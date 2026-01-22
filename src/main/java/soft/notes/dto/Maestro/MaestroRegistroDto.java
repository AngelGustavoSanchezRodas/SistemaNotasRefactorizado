package soft.notes.dto.Maestro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Usuario.UsuarioRegistroDto;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Maestro}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MaestroRegistroDto implements Serializable {

    @NotNull(message = "El idUsuario no puede ser nulo")
    private UsuarioRegistroDto usuario;

}