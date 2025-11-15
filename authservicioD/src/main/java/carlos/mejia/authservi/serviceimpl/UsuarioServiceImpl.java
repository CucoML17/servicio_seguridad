package carlos.mejia.authservi.serviceimpl;

import org.springframework.stereotype.Service;

import carlos.mejia.authservi.dto.UsuarioResponseDto;
import carlos.mejia.authservi.entity.Usuario;
import carlos.mejia.authservi.mapper.UsuarioMapper;
import carlos.mejia.authservi.repository.UsuarioRepository;
import carlos.mejia.authservi.service.UsuarioService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository usuarioRepository;
	
	
	@Override
    public UsuarioResponseDto getUsuarioByUsername(String username) {
        // 1. Buscar la entidad por username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));

        // 2. Mapear la entidad al DTO de respuesta
        return UsuarioMapper.mapToResponseDto(usuario);
    }	
	
	@Override
    public UsuarioResponseDto getUsuarioById(Integer id) { // <-- ¡NUEVO MÉTODO!
        
        // 1. Buscar la entidad por ID.
        // JpaRepository ya tiene findById, que devuelve un Optional.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. Mapear y retornar el DTO.
        return UsuarioMapper.mapToResponseDto(usuario);
    }	
	
	@Override
	public Integer getEstatusByUsername(String username) {
	    // Asumiendo que tienes UsuarioRepository inyectado
	    return usuarioRepository.findByUsername(username)
	            .map(Usuario::getEstatus)
	            .orElse(0); // 0 si no existe (por seguridad)
	}
	
	
	@Override
    public UsuarioResponseDto toggleEstatus(Integer idUsuario) {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        
        // Determinar el nuevo estatus
        Integer nuevoEstatus = (usuario.getEstatus() == 1) ? 0 : 1;
        
        // Aplicar el cambio
        usuario.setEstatus(nuevoEstatus);
        
        // Guardar y retornar
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        // NOTA IMPORTANTE: Como el estatus se cambia en la DB, el siguiente
        // intento de uso del token por parte del usuario fallará en el JwtFilter.
        
        return UsuarioMapper.mapToResponseDto(usuarioActualizado);
    }
	
	
}
