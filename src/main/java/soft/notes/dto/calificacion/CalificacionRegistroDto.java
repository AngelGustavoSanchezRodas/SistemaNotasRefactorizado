package soft.notes.dto.calificacion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalificacionRegistroDto implements Serializable {

    @NotNull(message = "El ID del alumno es obligatorio")
    private Integer idAlumno;

    @NotNull(message = "El ID de la materia es obligatorio")
    private Integer idMateria;


    @Min(0) @Max(10) private Double notaTarea1;
    @Min(0) @Max(10) private Double notaTarea2;
    @Min(0) @Max(25) private Double notaParcial1;
    @Min(0) @Max(25) private Double notaParcial2;
    @Min(0) @Max(30) private Double notaFinal;


    private String pinAdministrativo;
}