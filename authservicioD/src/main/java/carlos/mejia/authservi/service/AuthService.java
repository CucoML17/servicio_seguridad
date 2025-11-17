package carlos.mejia.authservi.service;



import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import carlos.mejia.authservi.dto.JwtResponseDto;
import carlos.mejia.authservi.dto.LoginRequestDto;
import carlos.mejia.authservi.dto.RegistroRequestDto;
import carlos.mejia.authservi.dto.UsuarioResponseDto;
import carlos.mejia.authservi.dto.UsuarioUpdateCredencialesRequestDto;
import carlos.mejia.authservi.dto.UsuarioUpdateRequestDto;
import carlos.mejia.authservi.entity.Perfil;
import carlos.mejia.authservi.entity.Usuario;
import carlos.mejia.authservi.mapper.UsuarioMapper;
import carlos.mejia.authservi.repository.PerfilRepository;
import carlos.mejia.authservi.repository.UsuarioRepository;
import carlos.mejia.authservi.serviceimpl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService; // Para cargar datos adicionales
    private final UsuarioRepository usuarioRepository; // Para obtener el ID del usuario

    
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    

    //Procesa la solicitud de login, autentica al usuario y genera el JWT.
    //request Las credenciales (username y password)
    //JwtResponseDto con el token y los datos del usuario.
    public JwtResponseDto login(LoginRequestDto request) {
    	
    	// 1. BUSCAR USUARIO Y VERIFICAR ESTATUS MANUALMENTE (ANTES DE LA AUTENTICACIÓN PESADA)
        carlos.mejia.authservi.entity.Usuario usuario = 
                usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas o usuario no encontrado.")); // Usamos una excepción genérica por seguridad

        // 1.1. VERIFICACIÓN CRÍTICA DEL ESTATUS
        if (usuario.getEstatus() == 0) {
            // Lanza la misma excepción que fallaría la autenticación para no dar pistas al atacante.
            throw new RuntimeException("Credenciales inválidas o usuario inactivo."); 
        }    	
        
        // 1. Autentica al usuario usando el AuthenticationManager
        // Esto activará el UserDetailsServiceImpl para cargar el usuario y luego
        // usará el PasswordEncoder para verificar la contraseña.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        //2. Si la autenticación fue exitosa (no se lanzó excepción):
        
        //2.1. Carga los detalles del usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        //2.2. Genera el token JWT
        String token = jwtService.generateToken(userDetails);
        
        //2.3. Prepara la respuesta extrayendo roles e ID
        
        //Se utiliza el userDetails para obtener los perfiles/roles
        List<String> perfiles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        //Busca el ID del usuario desde la DB (necesario para el frontend)
        carlos.mejia.authservi.entity.Usuario usuario2 = 
                usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Error fatal: Usuario autenticado no encontrado en DB"));

        return JwtResponseDto.builder()
                .token(token)
                .idUsuario(usuario2.getId())
                .username(usuario2.getUsername())
                .perfiles(perfiles)
                .build();
    }
    
  

    //Registro de usuario:
    public UsuarioResponseDto registrarUsuario(RegistroRequestDto request) {
        
        // 1. Buscar el perfil (debe existir)
        Perfil perfil = perfilRepository.findById(request.getIdPerfil())
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado con ID: " + request.getIdPerfil()));

        // 2. Construir la entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        
        // CRÍTICO: Encriptar la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); 
        
        usuario.setPerfil(perfil);
        usuario.setEstatus(1); // Activo por defecto
        usuario.setFechaRegistro(new Date());
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 3. Guardar en la base de datos
        return UsuarioMapper.mapToResponseDto(usuarioGuardado);
    }    
    
    
    
    
    //Actualizar usuario sin contraseña:
    public UsuarioResponseDto updateUsuario(Integer idUsuario, UsuarioUpdateRequestDto request) {
        
        // 1. Buscar el Usuario por ID (debe existir)
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        
        // 2. Buscar el nuevo Perfil (debe existir)
        Perfil nuevoPerfil = perfilRepository.findById(request.getIdPerfil())
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado con ID: " + request.getIdPerfil()));
        
        // 3. Aplicar los cambios
        usuario.setUsername(request.getUsername());
        usuario.setPerfil(nuevoPerfil);
        
        // 4. Guardar los cambios
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        // 5. Mapear y retornar
        return UsuarioMapper.mapToResponseDto(usuarioActualizado);
    }
    
    
    public UsuarioResponseDto updateUsuarioCredenciales(Integer idUsuario, UsuarioUpdateCredencialesRequestDto request) {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        
        // 1. Validar que el nuevo username no esté ya en uso por otro usuario
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(request.getUsername());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(idUsuario)) {
            throw new RuntimeException("El nombre de usuario '" + request.getUsername() + "' ya está en uso.");
        }
        
        // 2. Aplicar los cambios
        usuario.setUsername(request.getUsername());
        
        // 3. Codificar y aplicar la nueva contraseña
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        usuario.setPassword(encodedPassword);
        
        // 4. Guardar y retornar
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return UsuarioMapper.mapToResponseDto(usuarioActualizado);
    }       
    
}