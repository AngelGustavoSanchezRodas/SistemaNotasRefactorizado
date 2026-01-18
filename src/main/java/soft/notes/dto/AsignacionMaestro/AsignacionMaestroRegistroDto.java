package soft.notes.dto.AsignacionMaestro;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Grado.GradoRegistroDto;
import soft.notes.dto.Maestro.MaestroRegistroDto;
import soft.notes.dto.Materia.MateriaRegistroDto;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.AsignacionMaestro}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionMaestroRegistroDto implements Serializable {

    @NotNull(message = "El maestro no puede ser nulo")
    private MaestroRegistroDto maestro;

    @NotNull(message = "La materia no puede ser nula")
    private MateriaRegistroDto materia;

    @NotNull(message = "El grado no puede ser nulo")
    private GradoRegistroDto grado;
}