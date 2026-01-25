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
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "id_materia", referencedColumnName = "id_materia")
    private Materia materia;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "nota_tarea1")
    private Double notaTarea1 = 0.0;

    @Column(name = "nota_tarea2")
    private Double notaTarea2 = 0.0;

    @Column(name = "nota_parcial1")
    private Double notaParcial1 = 0.0;

    @Column(name = "nota_parcial2")
    private Double notaParcial2 = 0.0;

    @Column(name = "nota_final")
    private Double notaFinal = 0.0;


}