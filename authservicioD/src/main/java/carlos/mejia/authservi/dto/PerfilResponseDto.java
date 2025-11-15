package carlos.mejia.authservi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfilResponseDto {
    private Integer idUsuario;
    private String username;
    private String nombreCompleto; // El nombre del Cliente o Empleado
    private String rolPrincipal;   // El rol/perfil principal
}