package soft.notes.dto.asignacionMaestro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.maestro.MaestroSalidaDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.entities.AsignacionMaestro;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionMaestroSalidaDto implements Serializable {

    private Integer idAsignacion;
    private MaestroSalidaDto maestro;
    private MateriaSalidaDto materia;
    private GradoSalidaDto grado;
    private Boolean activo;

    // Constructor Mapper
    public AsignacionMaestroSalidaDto(AsignacionMaestro entidad) {
        this.idAsignacion = entidad.getIdAsignacion();
        this.activo = entidad.getActivo();

        // Mapeo anidado
        this.maestro = new MaestroSalidaDto(
            entidad.getMaestro().getIdMaestro(),
            new soft.notes.dto.usuario.UsuarioSalidaDto(entidad.getMaestro().getUsuario()),
            entidad.getMaestro().getCodigoEmpleado(),
            entidad.getMaestro().getActivo()
        );

        this.materia = new MateriaSalidaDto(entidad.getMateria());
        this.grado = new GradoSalidaDto(entidad.getGrado());
    }
}