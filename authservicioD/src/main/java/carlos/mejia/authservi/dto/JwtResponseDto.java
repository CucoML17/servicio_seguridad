package carlos.mejia.authservi.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDto {
    private String token;
    private Integer idUsuario;
    private String username;
    private List<String> perfiles;
}