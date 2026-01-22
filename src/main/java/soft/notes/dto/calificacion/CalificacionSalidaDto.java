package soft.notes.dto.calificacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.alumno.AlumnoSalidaDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.entities.Calificacion;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Calificacion}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalificacionSalidaDto implements Serializable {

    private Integer idCalificacion;

    private AlumnoSalidaDto idAlumno;
    private MateriaSalidaDto idMateria;

    private Double notaTarea1;
    private Double notaTarea2;
    private Double notaParcial1;
    private Double notaParcial2;
    private Double notaFinal;

public CalificacionSalidaDto(Calificacion c) {
    this.idCalificacion = c.getIdCalificacion();
    this.idAlumno = new AlumnoSalidaDto(c.getAlumno());
    this.idMateria = new MateriaSalidaDto(c.getMateria());
    this.notaTarea1 = c.getNotaTarea1();
    this.notaTarea2 = c.getNotaTarea2();
    this.notaParcial1 = c.getNotaParcial1();
    this.notaParcial2 = c.getNotaParcial2();
    this.notaFinal = c.getNotaFinal();
}
}