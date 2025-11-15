package carlos.mejia.authservi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carlos.mejia.authservi.dto.ClienteRegistroRequestDto;
import carlos.mejia.authservi.dto.JwtResponseDto;
import carlos.mejia.authservi.dto.LoginRequestDto;
import carlos.mejia.authservi.dto.PerfilDto;
import carlos.mejia.authservi.dto.RegistroRequestDto;
import carlos.mejia.authservi.dto.UsuarioResponseDto;
import carlos.mejia.authservi.dto.UsuarioUpdateRequestDto;
import carlos.mejia.authservi.service.AuthService;
import carlos.mejia.authservi.service.PerfilRolService;
import carlos.mejia.authservi.service.PerfilService;
import carlos.mejia.authservi.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private static final Integer PERFIL_CLIENTE_ID = 5; // ID del Perfil "Cliente"

    private final AuthService authService;
    
    private final PerfilService perfilService;
    
    private final UsuarioService usuarioService;
    
    private final PerfilRolService perfilRolService;

    /**
     * Endpoint para la autenticación del usuario.
     * Recibe credenciales y devuelve un JWT si son válidas.
     * Endpoint: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto request) {
        
        // El servicio maneja la autenticación y la generación del token
        JwtResponseDto response = authService.login(request);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDto> registerUsuario(@RequestBody RegistroRequestDto request) {
        try {
            UsuarioResponseDto nuevoUsuario = authService.registrarUsuario(request);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Maneja errores como "Perfil no encontrado" o "Username ya existe"
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }    
    
    

    // 1. REGISTRO PÚBLICO (CLIENTE)
    @PostMapping("/registrar/cliente")
    public ResponseEntity<UsuarioResponseDto> registerCliente(@RequestBody ClienteRegistroRequestDto request) {
        // Creamos un DTO completo internamente, forzando el ID del perfil
        RegistroRequestDto registroCompleto = RegistroRequestDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .idPerfil(PERFIL_CLIENTE_ID) // Fijo: 5 (Cliente)
                .build();
                
        UsuarioResponseDto nuevoUsuario = authService.registrarUsuario(registroCompleto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    //2. REGISTRO ADMINISTRATIVO (STAFF)
    @PostMapping("/registrar/empleados") 
    public ResponseEntity<UsuarioResponseDto> registerStaff(@RequestBody RegistroRequestDto request) {
        UsuarioResponseDto nuevoUsuario = authService.registrarUsuario(request);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
    
    @GetMapping("/usuario/{username}") 
    public ResponseEntity<UsuarioResponseDto> getUsuarioByUsername(@PathVariable("username") String username) {
        try {
            UsuarioResponseDto usuario = usuarioService.getUsuarioByUsername(username);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Devuelve 404 NOT FOUND si el usuario no existe.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
    }    
    
    @GetMapping("/perfiles") 
    public ResponseEntity<List<PerfilDto>> getAllPerfiles() {
        
        List<PerfilDto> perfiles = perfilRolService.getAllPerfiles();
        
        // Si no hay perfiles, devuelve una lista vacía, pero un 200 OK.
        return new ResponseEntity<>(perfiles, HttpStatus.OK);
    }    
    
    
    //Actualizar usuario (Solo contrasena)
    @PutMapping("/actualiza/sincontra/usuario/{id}")
    public ResponseEntity<UsuarioResponseDto> updateUsuario(
            @PathVariable("id") Integer idUsuario,
            @RequestBody UsuarioUpdateRequestDto request) {
        try {
            UsuarioResponseDto usuarioActualizado = authService.updateUsuario(idUsuario, request);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK); // 200 OK para actualización exitosa
        } catch (RuntimeException e) {
            // Manejar errores como Usuario no encontrado o Perfil no encontrado
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request o 404 Not Found
        }
    }   
    
    @GetMapping("/usuario/buscaid/{id}") // <-- ¡NUEVO ENDPOINT!
    public ResponseEntity<UsuarioResponseDto> getUsuarioById(@PathVariable("id") Integer id) {
        try {
            UsuarioResponseDto usuario = usuarioService.getUsuarioById(id);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Devuelve 404 NOT FOUND si el usuario no existe.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
    }  
    
    
    
    @GetMapping("/estatus/{username}")
    public ResponseEntity<Integer> getEstatusByUsername(@PathVariable String username) {
        // Usamos 0 si no lo encuentra o si el servicio es UsuarioService
        Integer estatus = usuarioService.getEstatusByUsername(username); 
        return ResponseEntity.ok(estatus);
    }  
    
    
    @PatchMapping("/toggle/estatus/{idUsuario}")
    public ResponseEntity<UsuarioResponseDto> toggleEstatus(@PathVariable Integer idUsuario) {
        
        UsuarioResponseDto updatedDto = usuarioService.toggleEstatus(idUsuario);
        return ResponseEntity.ok(updatedDto);
    }    
    
}