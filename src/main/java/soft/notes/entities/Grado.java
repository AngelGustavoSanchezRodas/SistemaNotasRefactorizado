package soft.notes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "grados")
public class Grado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grado")
    private Integer idGrado;

    @Column(name = "nombre_grado")
    private String nombreGrado;

    @Column(name = "seccion")
    private String seccion;

    @Column(name = "activo")
    private Boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "grado_materias",
        joinColumns = @JoinColumn(name = "id_grado"),
        inverseJoinColumns = @JoinColumn(name = "id_materia")
    )
    private List<Materia> materiasDelGrado;
}
