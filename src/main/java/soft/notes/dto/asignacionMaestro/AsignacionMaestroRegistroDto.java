package soft.notes.dto.asignacionMaestro;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.AsignacionMaestro}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionMaestroRegistroDto implements Serializable {

    // CAMBIO CLAVE: Usamos Integer (IDs), no Objetos completos.

    @NotNull(message = "El ID del maestro es obligatorio")
    private Integer idMaestro;

    @NotNull(message = "El ID de la materia es obligatorio")
    private Integer idMateria;

    @NotNull(message = "El ID del grado es obligatorio")
    private Integer idGrado;

}