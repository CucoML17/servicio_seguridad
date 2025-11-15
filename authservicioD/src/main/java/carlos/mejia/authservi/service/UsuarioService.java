package carlos.mejia.authservi.service;

import carlos.mejia.authservi.dto.UsuarioResponseDto;

public interface UsuarioService {
	UsuarioResponseDto getUsuarioByUsername(String username);
	
	UsuarioResponseDto getUsuarioById(Integer id);
	
	Integer getEstatusByUsername(String username);
	
	UsuarioResponseDto toggleEstatus(Integer idUsuario);
}
