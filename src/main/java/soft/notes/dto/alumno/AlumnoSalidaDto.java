package soft.notes.dto.alumno;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.usuario.UsuarioSalidaDto;
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