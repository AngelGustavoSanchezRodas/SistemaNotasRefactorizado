package soft.notes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Integer idCalificacion;

    @ManyToOne
    @JoinColumn(name = "id_alumno", referencedColumnName = "id_alumno")
    private Alumno idAlumno;

    @ManyToOne
    @JoinColumn(name = "id_materia", referencedColumnName = "id_materia")
    private Materia idMateria;

    @Column(name = "nota_tarea1")
    private Double notaTarea1;

    @Column(name = "nota_tarea2")
    private Double notaTarea2;

    @Column(name = "nota_parcial1")
    private Double notaParcial1;

    @Column(name = "nota_parcial2")
    private Double notaParcial2;

    @Column(name = "nota_final")
    private Double notaFinal;

}
