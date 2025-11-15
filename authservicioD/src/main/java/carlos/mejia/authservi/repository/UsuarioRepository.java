package carlos.mejia.authservi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import carlos.mejia.authservi.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Método clave que Spring Security usará para buscar por nombre de usuario
    Optional<Usuario> findByUsername(String username);
}
