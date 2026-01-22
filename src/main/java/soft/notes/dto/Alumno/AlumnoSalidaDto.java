package soft.notes.dto.Alumno;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Grado.GradoSalidaDto;
import soft.notes.dto.Usuario.UsuarioSalidaDto;
import soft.notes.entities.Alumno;

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
    private Boolean alumnoActivo;

    public AlumnoSalidaDto(Alumno alumno) {
        this.idAlumno = alumno.getIdAlumno();
        this.usuario = new UsuarioSalidaDto(alumno.getUsuario());
        this.grado = new GradoSalidaDto(alumno.getGrado());
        this.carnet = alumno.getCarnet();
        this.alumnoActivo = alumno.getActivo();
    }
}