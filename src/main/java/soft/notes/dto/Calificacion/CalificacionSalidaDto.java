package soft.notes.dto.Calificacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Alumno.AlumnoSalidaDto;
import soft.notes.dto.Materia.MateriaSalidaDto;

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
}