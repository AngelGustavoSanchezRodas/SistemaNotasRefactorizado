package soft.notes.dto.Alumno;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Grado.GradoSalidaDto;
import soft.notes.dto.Usuario.UsuarioSalidaDto;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.Alumno}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlumnoSalidaDto implements Serializable {
    private Integer idAlumno;
    private UsuarioSalidaDto usuario;
    private GradoSalidaDto grado;
    private String carnet;
}