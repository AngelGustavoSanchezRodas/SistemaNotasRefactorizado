package soft.notes.dto.Materia;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Materia}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MateriaRegistroDto implements Serializable {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripcion no puede estar vacío")
    private String descripcion;

}