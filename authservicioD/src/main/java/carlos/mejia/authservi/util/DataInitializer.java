package carlos.mejia.authservi.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import carlos.mejia.authservi.entity.Perfil;
import carlos.mejia.authservi.entity.Usuario;
import carlos.mejia.authservi.repository.PerfilRepository;
import carlos.mejia.authservi.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Lista de perfiles en el orden deseado
    private static final String[] PERFILES = {
        "Administrador", "Supervisor", "Cajero", "Mesero", "Cliente"
    };

    @Override
    @Transactional // Para asegurar que las operaciones se ejecuten en una transacción
    public void run(String... args) throws Exception {
        // Ejecutamos la inserción y luego, por seguridad, eliminamos el main de prueba
        initializePerfiles();
        initializeAdminUser();
        
        // La línea del GeneradorPassword ya no debe ejecutarse en el main, 
        // pero podemos imprimir el hash aquí si es necesario:
        // System.out.println("El hash BCrypt para 'adminpass' es: " + passwordEncoder.encode("adminpass"));
    }

    private void initializePerfiles() {
        if (perfilRepository.count() == 0) {
            System.out.println("Inicializando la tabla Perfil...");
            for (String nombrePerfil : PERFILES) {
                Perfil perfil = new Perfil();
                perfil.setPerfil(nombrePerfil);
                // NOTA: La clave autoincremental garantizará el orden 1, 2, 3...
                perfilRepository.save(perfil); 
            }
            System.out.println("Perfiles insertados correctamente.");
        }
    }

    private void initializeAdminUser() {
        if (usuarioRepository.count() == 0) {
            System.out.println("Inicializando el usuario Administrador...");

            // 1. Buscar el perfil 'Administrador' (ID=1)
            Perfil perfilAdmin = perfilRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Error: Perfil Administrador (ID 1) no encontrado."));

            // 2. Crear el usuario
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass")); // Encripta la contraseña
            admin.setEstatus(1); // Activo
            
            // Establecer la fecha actual
            LocalDate localDate = LocalDate.now();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            admin.setFechaRegistro(date);

            // 3. Asignar el perfil
            admin.setPerfil(perfilAdmin);

            usuarioRepository.save(admin);
            System.out.println("Usuario 'admin' insertado correctamente.");
        }
    }
}