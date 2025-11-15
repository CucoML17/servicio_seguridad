package carlos.mejia.authservi.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import carlos.mejia.authservi.dto.PerfilDto;
import carlos.mejia.authservi.entity.Perfil;
import carlos.mejia.authservi.mapper.PerfilMapper;
import carlos.mejia.authservi.repository.PerfilRepository;
import carlos.mejia.authservi.repository.UsuarioRepository;
import carlos.mejia.authservi.service.PerfilRolService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PerfilRolServiceImpl implements PerfilRolService {
	private PerfilRepository perfilRepository;

    @Override
    public List<PerfilDto> getAllPerfiles() {
        // 1. Obtener todas las entidades Perfil
        List<Perfil> perfiles = perfilRepository.findAll();
        
        // 2. Mapear la lista de entidades a la lista de DTOs
        return PerfilMapper.mapToPerfilDtoList(perfiles);
    }
}
