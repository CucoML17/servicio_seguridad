package carlos.mejia.authservi.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="perfil")
public class Perfil {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="perfil", unique = true, nullable = false)
    private String perfil; 

    // Mapeo inverso: Un perfil puede tener muchos usuarios.
    // 'perfil' es el campo en la clase Usuario que referencia a Perfil.
    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL)
    private List<Usuario> usuarios;
}