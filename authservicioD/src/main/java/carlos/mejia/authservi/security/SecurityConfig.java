package carlos.mejia.authservi.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // INYECCIÓN DEL FILTRO JWT
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        //Permite cualquier origen (el equivalente a @CrossOrigin("*"))
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        
        //Permite los métodos (GET, POST, PUT, DELETE, y CRUCIAL: OPTIONS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        //permite el header Authorization y Content-Type
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); 
        
        //Permite enviar cookies
        configuration.setAllowCredentials(false); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //Aplica esta configuración a todas las rutas
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }    

    //1. Define cómo se cifran las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //2. Define el AuthenticationManager, que es el orquestador principal
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //3. Define el proveedor de autenticación: dónde buscar usuarios y qué encoder usar
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //4. Configura las reglas de seguridad HTTP (SecurityFilterChain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        
		    //Aplicar la configuración CORS
		    .cors(cors -> cors.configurationSource(corsConfigurationSource()))        
            //4.1. Deshabilita la protección CSRF (necesaria para APIs REST)
            .csrf(csrf -> csrf.disable())
            
            //4.2. Configura las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                
                // PÚBLICO: Login y registro de clientes
                .requestMatchers("/api/auth/login", "/api/auth/registrar/cliente").permitAll()
                .requestMatchers("/api/auth/usuario/**").permitAll()
                
                // PROTEGIDO: Registro de empleados (Requiere token de Admin/Supervisor)
                .requestMatchers("/api/auth/registrar/empleados").hasAnyAuthority("Administrador", "Supervisor")
                
                .requestMatchers("/api/auth/perfiles").hasAnyAuthority("Administrador", "Supervisor", "Cajero", "Mesero")
                .requestMatchers("/api/auth/actualiza/sincontra/usuario/**").hasAnyAuthority("Administrador", "Supervisor", "Cliente", "Cajero", "Mesero")
                
                .requestMatchers("/api/auth/registrar").hasAnyAuthority("Administrador", "Supervisor")
                
                .requestMatchers("/api/auth/usuario/buscaid/**").hasAnyAuthority("Administrador", "Supervisor")
                
                .requestMatchers("/api/auth/estatus/**").hasAnyAuthority("Cliente", "Mesero", "Cajero", "Supervisor", "Administrador")
                .requestMatchers("/api/auth/toggle/estatus/**").hasAnyAuthority("Administrador", "Supervisor")
                
                // Cualquier otra petición debe estar autenticada 
                .anyRequest().authenticated()
            )
            
            // 4.3. Establece la política de sesión a STATELESS
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4.4. Agrega nuestro proveedor de autenticación
            .authenticationProvider(authenticationProvider())

            //AÑADIR EL FILTRO JWT
            // Esto asegura que cada petición con token sea validada ANTES de llegar al controlador.
            .addFilterBefore(
                jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}