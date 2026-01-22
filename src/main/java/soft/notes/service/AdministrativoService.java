package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.administrativo.AdministrativoRegistroDto;
import soft.notes.dto.administrativo.AdministrativoSalidaDto;
import soft.notes.entities.Administrativo;
import soft.notes.entities.Usuario;
import soft.notes.repositories.AdministrativoRepository;

import java.util.List;

@Service
public class AdministrativoService {

    @Autowired
    private AdministrativoRepository administrativoRepository;

    @Autowired
    private UsuarioService usuarioService;

    // 1. LISTAR
    @Transactional(readOnly = true)
    public List<AdministrativoSalidaDto> obtenerAdministrativos() {
        return administrativoRepository.findAll().stream()
                .filter(Administrativo::getActivo)
                .map(AdministrativoSalidaDto::new)
                .toList();
    }

    // 2. GUARDAR
    @Transactional
    public AdministrativoSalidaDto guardarAdministrativo(AdministrativoRegistroDto dto) {

        // 1. DELEGAMOS la creación del usuario (Rol forzado: ADMINISTRATIVO)
        Usuario usuarioGuardado = usuarioService.crearUsuarioBase(dto.getUsuario(), "ADMINISTRATIVO");

        // 2. Crear Perfil Administrativo vinculado
        Administrativo nuevoAdmin = new Administrativo();
        nuevoAdmin.setUsuario(usuarioGuardado);
        nuevoAdmin.setCargo(dto.getCargo());
        nuevoAdmin.setTurno(dto.getTurno());
        nuevoAdmin.setActivo(true);

        Administrativo adminGuardado = administrativoRepository.save(nuevoAdmin);

        return new AdministrativoSalidaDto(adminGuardado);
    }

    // 3. EDITAR
    @Transactional
    public AdministrativoSalidaDto editarAdministrativo(Integer id, AdministrativoRegistroDto dto) {

        Administrativo admin = administrativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrativo no encontrado"));

        // Actualizamos solo datos específicos del puesto
        admin.setCargo(dto.getCargo());
        admin.setTurno(dto.getTurno());

        Administrativo actualizado = administrativoRepository.save(admin);
        return new AdministrativoSalidaDto(actualizado);
    }

    // 4. ELIMINAR
    @Transactional
    public void eliminarAdministrativo(Integer id) {
        Administrativo admin = administrativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrativo no encontrado"));

        admin.setActivo(false);
        admin.getUsuario().setActivo(false);

        administrativoRepository.save(admin);
    }
}