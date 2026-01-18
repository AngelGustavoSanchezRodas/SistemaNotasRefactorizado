package soft.notes.dto.Grado;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Grado}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GradoSalidaDto implements Serializable {
    private Integer idGrado;
    private String nombreGrado;
    private String seccion;
}