package soft.notes.dto.alumno;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.usuario.UsuarioRegistroDto;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Alumno}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlumnoRegistroDto implements Serializable {

    @Valid
    @NotNull(message = "El usuario no puede ser nulo")
    private UsuarioRegistroDto usuario;

   @NotNull(message = "El ID del grado es obligatorio")
    private Integer idGrado;

}