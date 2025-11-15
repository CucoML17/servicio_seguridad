package carlos.mejia.authservi.mapper;

import java.util.List;
import java.util.stream.Collectors;

import carlos.mejia.authservi.dto.PerfilDto;
import carlos.mejia.authservi.entity.Perfil;

public class PerfilMapper {
	public static PerfilDto mapToPerfilDto(Perfil perfil) {
        if (perfil == null) return null;
        
        return new PerfilDto(
                perfil.getId(),
                perfil.getPerfil() // Usamos el campo 'perfil' de la entidad como 'nombre' en el DTO
        );
    }
    
    public static List<PerfilDto> mapToPerfilDtoList(List<Perfil> perfiles) {
        if (perfiles == null) return List.of();
        
        return perfiles.stream()
                .map(PerfilMapper::mapToPerfilDto)
                .collect(Collectors.toList());
    }
}
