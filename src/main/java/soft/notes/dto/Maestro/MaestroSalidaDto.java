package soft.notes.dto.Maestro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Usuario.UsuarioSalidaDto;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Maestro}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MaestroSalidaDto implements Serializable {

    private Integer idMaestro;

    private UsuarioSalidaDto usuario;

    private String codigoEmpleado;
}