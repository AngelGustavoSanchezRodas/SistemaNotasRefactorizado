package soft.notes.dto.administrativo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.usuario.UsuarioRegistroDto; // Asegúrate de importar el del usuario
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdministrativoRegistroDto implements Serializable {

    @NotNull(message = "Los datos del usuario son obligatorios")
    private UsuarioRegistroDto usuario;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @NotBlank(message = "El turno es obligatorio")
    private String turno;
}