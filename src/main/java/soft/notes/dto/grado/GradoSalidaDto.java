package soft.notes.dto.grado;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.entities.Grado;

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
    private Boolean activo;

    public GradoSalidaDto(Grado grado) {
        this.idGrado = grado.getIdGrado();
        this.nombreGrado = grado.getNombreGrado();
        this.seccion = grado.getSeccion();
        this.activo = grado.getActivo();
    }
}