package carlos.mejia.authservi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import carlos.mejia.authservi.dto.PerfilResponseDto;
import carlos.mejia.authservi.feign.ClienteClientFeign;
import carlos.mejia.authservi.feign.EmpleadoClientFeign;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final ClienteClientFeign clienteClient;
    private final EmpleadoClientFeign empleadoClient;

    public PerfilResponseDto getPerfilAgregado() {
        // 1. Obtener la información del usuario logueado del Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Asumiendo que el principal es tu UserDetails customizado
        // Necesitas una forma de obtener el ID del usuario y el rol del token validado
        // Esto depende de cómo extraes la información en tu JwtAuthenticationFilter.
        String username = authentication.getName();
        String rol = authentication.getAuthorities().iterator().next().getAuthority(); // Obtener el primer rol
        
        // *CRUCIAL*: Necesitas obtener el ID del usuario a partir del Username,
        // o si tu JwtAuthenticationFilter ya lo puso en el Principal, usarlo.
        // Asumiremos que el idUsuario se puede obtener del UserDetails o del contexto.
        // Si no está, debes hacer un paso de búsqueda a la base de datos de usuarios.
        
        Integer idUsuario = obtenerIdUsuarioDesdeElContexto(authentication); // Implementación pendiente

        String nombreCompleto;

        // 2. Orquestación: Llamar al microservicio de negocio basado en el rol
        if (rol.equals("Cliente")) {
            // ClienteClient solo devuelve nombrecliente
            nombreCompleto = clienteClient.getClienteByUserId(idUsuario).getNombrecliente();
        } else if (rol.equals("Administrador") || rol.equals("Supervisor") || rol.equals("Cajero") || rol.equals("Mesero")) {
            // EmpleadoClient solo devuelve nombre
            nombreCompleto = empleadoClient.getEmpleadoByUserId(idUsuario).getNombre();
        } else {
            nombreCompleto = "Usuario Desconocido"; // Rol sin entidad de negocio asociada
        }

        // 3. Devolver el DTO combinado
        return PerfilResponseDto.builder()
                .idUsuario(idUsuario)
                .username(username)
                .nombreCompleto(nombreCompleto)
                .rolPrincipal(rol)
                .build();
    }
    
    // **NOTA**: Este método es un placeholder. Debes implementar cómo obtener el ID
    // del usuario desde el principal autenticado (p. ej., si tu UserDetails tiene un ID, o si lo buscas por username)
    private Integer obtenerIdUsuarioDesdeElContexto(Authentication authentication) {
        // ... (Tu lógica para obtener el ID) ...
        // Podría ser casteando el principal a tu UserDetails custom
        return 1; // ID de ejemplo
    }
}