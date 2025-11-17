package carlos.mejia.authservi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateCredencialesRequestDto {
    // El ID del usuario se pasará en el PathVariable
    
    private String username;
    
    private String password; // La nueva contraseña (debe ser codificada en el servicio)
}