package carlos.mejia.authservi.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDto {
    private Integer id;
    private String username;
    private Integer estatus;
    private Date fechaRegistro;
    private String perfil; // Solo el nombre del perfil
}