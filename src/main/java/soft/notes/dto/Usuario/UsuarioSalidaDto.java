package soft.notes.dto.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Usuario}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioSalidaDto implements Serializable {
    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String rol;
    private Boolean activo;
}