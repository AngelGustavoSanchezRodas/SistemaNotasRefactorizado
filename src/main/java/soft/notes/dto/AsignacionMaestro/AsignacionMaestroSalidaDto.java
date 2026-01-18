package soft.notes.dto.AsignacionMaestro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.Grado.GradoSalidaDto;
import soft.notes.dto.Maestro.MaestroSalidaDto;
import soft.notes.dto.Materia.MateriaSalidaDto;

import java.io.Serializable;

/**
 * DTO for {@link soft.notes.entities.AsignacionMaestro}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionMaestroSalidaDto implements Serializable {
    private Integer idAsignacion;
    private MaestroSalidaDto maestro;
    private MateriaSalidaDto materia;
    private GradoSalidaDto grado;
}