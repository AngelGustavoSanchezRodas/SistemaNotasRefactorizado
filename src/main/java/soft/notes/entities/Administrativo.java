package soft.notes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "administrativos")
public class Administrativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_administrativos")
    private Integer idAdministrativos;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "cargo", nullable = false)
    private String cargo;

    @Column(name = "turno", nullable = false)
    private String turno;

     @Column(name = "activo")
    private Boolean activo = true;

}
