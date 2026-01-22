package soft.notes.dto.Maestro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Usuario.UsuarioSalidaDto;
import soft.notes.entities.Maestro;

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

    private Boolean activo;

    public MaestroSalidaDto(Maestro maestro) {
        this.idMaestro = maestro.getIdMaestro();
        this.usuario = new UsuarioSalidaDto(maestro.getUsuario());
        this.codigoEmpleado = maestro.getCodigoEmpleado();
        this.activo = maestro.getActivo();
    }

}