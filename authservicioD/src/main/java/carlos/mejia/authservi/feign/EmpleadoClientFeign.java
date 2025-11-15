package carlos.mejia.authservi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import carlos.mejia.authservi.dto.EmpleadoPerfilDto;

@FeignClient(name = "reservaciones")
public interface EmpleadoClientFeign {

	@GetMapping("/api/empleados/perfil/usuario/{idUsuario}")
    EmpleadoPerfilDto getEmpleadoByUserId(@PathVariable("idUsuario") Integer idUsuario);	
}
