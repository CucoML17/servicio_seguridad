package carlos.mejia.authservi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroRequestDto {
    private String username;
    private String password;
    
    
    private Integer idPerfil; 
}