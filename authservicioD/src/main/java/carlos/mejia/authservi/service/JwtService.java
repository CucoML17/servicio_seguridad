package carlos.mejia.authservi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class JwtService {

    // MODIFICACIÓN CLAVE: Se añade el valor por defecto después de los dos puntos
    @Value("${jwt.secret:UnaClavePorDefectoDeEmergenciaParaQueArranqueElServicioYSeaLoSuficientementeLarga}")
    private String SECRET_KEY;

    // MODIFICACIÓN CLAVE: Se añade el valor por defecto (86400000ms = 24 horas)
    @Value("${jwt.expiration:86400000}")
    private long EXPIRATION_TIME; // Tiempo en milisegundos

    // --- Métodos de Generación del Token ---

    public String generateToken(UserDetails userDetails) {
        // Obtenemos los roles del usuario
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        // Creamos un mapa para incluir los roles en el payload del JWT (Claims)
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities);

        return buildToken(claims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject) // Nombre de usuario
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Métodos de Extracción/Validación del Token ---
    
    // Obtiene el username del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Verifica si el token es válido (no expirado y coincide el username)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Método genérico para extraer cualquier claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- Método de Clave Secreta ---

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    
    //El token es valido?
    
    public boolean isTokenValid(String token) {
        try {
            // Intenta extraer todos los claims. Si falla (firma inválida/expirado), lanzará una excepción.
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            // Log the exception if needed, but for simplicity, just return false
            return false;
        }
    }
    
    
}