package carlos.mejia.authservi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import carlos.mejia.authservi.dto.ClientePerfilDto;

@FeignClient(name = "restaurantespringf")
public interface ClienteClientFeign {
	@GetMapping("/api/cliente/perfil/usuario/{idUsuario}")
    ClientePerfilDto getClienteByUserId(@PathVariable("idUsuario") Integer idUsuario);
}
