package carlos.mejia.authservi.inter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

// Asegúrate de que esta clase sea escaneada por Spring
@Configuration 
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        // 1. Obtener la petición HTTP actual
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // 2. Extraer el header Authorization
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            // 3. Si existe, propagar el header a la petición de Feign
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
            }
        }
        // Si no hay token (ej. si la llamada viene de un scheduler o es pública), no se añade nada.
    }
}