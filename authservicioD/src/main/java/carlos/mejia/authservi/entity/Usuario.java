package carlos.mejia.authservi.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="password", nullable = false)
    private String password; //Debe estar codificado
    
    @Column(name="username", unique = true, nullable = false)
    private String username;
    
    @Column(name="estatus", nullable = false)
    private Integer estatus; // 1 = activo, 0 = inactivo
    
    @Column(name="fecha_registro")
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
    

    //Relación Uno a Muchos (ManyToOne)
    //Cada Usuario tiene UN solo Perfil (la clave foránea 'id_perfil' estará aquí)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_perfil", nullable = false) //Nombre de la columna FK en la tabla 'usuario'
    private Perfil perfil;
}