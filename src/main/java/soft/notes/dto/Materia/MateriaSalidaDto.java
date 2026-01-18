package soft.notes.dto.Materia;

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
public class MateriaSalidaDto implements Serializable {

    private Integer idMateria;
    private String nombre;
    private String descripcion;

}