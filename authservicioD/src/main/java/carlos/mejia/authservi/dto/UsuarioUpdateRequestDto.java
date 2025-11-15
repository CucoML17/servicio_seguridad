package carlos.mejia.authservi.dto;

import org.antlr.v4.runtime.misc.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequestDto {
    
    // El nuevo (o el mismo) username
    private String username;
    
    // El nuevo ID del perfil/rol
    private Integer idPerfil;
    
    // NOTA: La contraseña (password) NO se incluye aquí intencionalmente.
}