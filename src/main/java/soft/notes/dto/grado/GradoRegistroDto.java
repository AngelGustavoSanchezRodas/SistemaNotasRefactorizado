package soft.notes.dto.grado;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link soft.notes.entities.Grado}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GradoRegistroDto implements Serializable {

    @NotBlank(message = "El nombre del grado no puede estar vacío")
    private String nombreGrado;

    @NotBlank(message = "La sección no puede estar vacía")
    private String seccion;

    private List<Integer> idsMaterias;
}