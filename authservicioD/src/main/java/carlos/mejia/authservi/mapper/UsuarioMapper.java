package carlos.mejia.authservi.mapper;

import carlos.mejia.authservi.dto.UsuarioResponseDto;
import carlos.mejia.authservi.entity.Usuario;

public class UsuarioMapper {
	
	public static UsuarioResponseDto mapToResponseDto(Usuario usuario) {
        return UsuarioResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .estatus(usuario.getEstatus())
                .fechaRegistro(usuario.getFechaRegistro())
                .perfil(usuario.getPerfil().getPerfil()) // Extraemos solo el nombre
                .build();
    }

}
