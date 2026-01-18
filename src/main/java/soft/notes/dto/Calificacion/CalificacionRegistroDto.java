package soft.notes.dto.Calificacion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import soft.notes.dto.Materia.MateriaRegistroDto;
import soft.notes.entities.Alumno;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Calificacion}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalificacionRegistroDto implements Serializable {

    @NotNull(message = "El alumno no puede ser nulo")
    private Alumno idAlumno;

    @NotNull(message = "La materia no puede ser nula")
    private MateriaRegistroDto idMateria;

    @Min(0) @Max(10)
    private Double notaTarea1;

    @Min(0) @Max(10)
    private Double notaTarea2;

    @Min(0) @Max(25)
    private Double notaParcial1;

    @Min(0) @Max(25)
    private Double notaParcial2;

    @Min(0) @Max(30)
    private Double notaFinal;
}