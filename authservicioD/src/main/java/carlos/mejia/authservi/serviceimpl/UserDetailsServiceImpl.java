package carlos.mejia.authservi.serviceimpl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import carlos.mejia.authservi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        carlos.mejia.authservi.entity.Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        
        // 1. Obtener el único perfil como un String
        // Nota: Solo se necesita el nombre del perfil para Spring Security
        String perfilName = usuario.getPerfil().getPerfil(); // Accedemos al único perfil

        // 2. Usar la clase User de Spring Security
        return org.springframework.security.core.userdetails.User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword()) // Spring Security luego verifica el hash
            .authorities(perfilName)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(usuario.getEstatus() != 1) // 1 = activo
            .build();
    }
}