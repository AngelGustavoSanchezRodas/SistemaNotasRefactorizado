package soft.notes.dto.administrativo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soft.notes.dto.usuario.UsuarioSalidaDto;
import soft.notes.entities.Administrativo;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdministrativoSalidaDto implements Serializable {

    private Integer idAdministrativo;
    private UsuarioSalidaDto usuario;
    private String cargo;
    private String turno;
    private Boolean activo;

    public AdministrativoSalidaDto(Administrativo entidad) {
        this.idAdministrativo = entidad.getIdAdministrativos();
        this.usuario = new UsuarioSalidaDto(entidad.getUsuario());
        this.cargo = entidad.getCargo();
        this.turno = entidad.getTurno();
        this.activo = entidad.getActivo();
    }
}