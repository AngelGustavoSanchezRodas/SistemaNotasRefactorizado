package soft.notes.dto.Materia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.entities.Materia;

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
    private Boolean activo;

    public MateriaSalidaDto(Materia materia) {
        this.idMateria = materia.getIdMateria();
        this.nombre = materia.getNombre();
        this.descripcion = materia.getDescripcion();
        this.activo = materia.getActivo();
    }
}