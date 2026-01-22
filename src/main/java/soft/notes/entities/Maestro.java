package soft.notes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "maestros")
public class Maestro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maestro")
    private Integer idMaestro;

    @OneToOne
    @JoinColumn(name = "id_usuario", unique = true) // CORREGIDO
    private Usuario usuario;

    @Column(name = "codigo_empleado")
    private String codigoEmpleado;

    @Column(name = "activo")
    private Boolean activo = true;
}