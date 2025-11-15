package carlos.mejia.authservi.security;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import carlos.mejia.authservi.service.JwtService;
import carlos.mejia.authservi.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UsuarioService usuarioService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Obtener el encabezado de autorización
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final String rolesClaim;

        // 2. Verificar si el token existe y tiene el formato "Bearer <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extraer solo el token
        
        // 3. Validar el token y extraer el username y los roles (Claims)
        if (jwtService.isTokenValid(jwt)) {
            
            // 3.1. Extracción
            username = jwtService.extractUsername(jwt);
            
            
            // --- VERIFICACIÓN DE ESTATUS CRÍTICA (NUEVA LÓGICA) ---
            Integer estatus = usuarioService.getEstatusByUsername(username);
            System.out.println("DEBUG: Usuario: " + username + " - Estatus Obtenido: " + estatus);
            
            if (estatus == 0) {
                // Si el usuario está inactivo, rechazamos la petición
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Usuario inactivo o suspendido.");
               
                return; // Detenemos la cadena de filtros
            }
            // --------------------------------------------------------            
            
            
            rolesClaim = jwtService.extractAllClaims(jwt).get("roles", String.class);

            // 3.2. Crear una lista de autoridades (roles)
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesClaim.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 3.3. Crear el objeto de autenticación
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, // El Principal (username)
                    null,     // Las credenciales son nulas en JWT
                    authorities // Las autoridades (roles)
            );

            // 3.4. Asignar detalles de la petición y establecer el contexto de seguridad
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 4. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}