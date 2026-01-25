package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.maestro.MaestroRegistroDto;
import soft.notes.dto.maestro.MaestroSalidaDto;
import soft.notes.dto.usuario.UsuarioSalidaDto;
import soft.notes.entities.Maestro;
import soft.notes.entities.Usuario;
import soft.notes.repositories.MaestroRepository;

import java.time.Year;
import java.util.List;
import java.util.Random;

@Service
public class MaestroService {

    @Autowired
    private MaestroRepository maestroRepository;

    @Autowired
    private UsuarioService usuarioService;

    // Generador de Código de Empleado
    private String generarCodigoEmpleado() {
        int year = Year.now().getValue();
        int randomNum = new Random().nextInt(9000) + 1000;
        return "PROF-" + year + "-" + randomNum;
    }

    // Listar Maestros (Solo Activos)
    @Transactional(readOnly = true)
    public List<MaestroSalidaDto> obtenerMaestros() {
        return maestroRepository.findAll().stream()
                .filter(Maestro::getActivo)
                .map(maestro -> new MaestroSalidaDto(
                        maestro.getIdMaestro(),
                        new UsuarioSalidaDto(maestro.getUsuario()),
                        maestro.getCodigoEmpleado(),
                        maestro.getActivo()
                ))
                .toList();
    }

    // Guardar Maestro
    @Transactional
    public MaestroSalidaDto guardarMaestro(MaestroRegistroDto dto) {

        // 1. DELEGAMOS la creación del usuario (Rol forzado: MAESTRO)
        Usuario usuarioGuardado = usuarioService.crearUsuarioBase(dto.getUsuario(), "MAESTRO");

        // 2. Crear Maestro vinculado
        Maestro nuevoMaestro = new Maestro();
        nuevoMaestro.setUsuario(usuarioGuardado);
        nuevoMaestro.setActivo(true);
        nuevoMaestro.setCodigoEmpleado(generarCodigoEmpleado());

        Maestro maestroGuardado = maestroRepository.save(nuevoMaestro);

        return new MaestroSalidaDto(
                maestroGuardado.getIdMaestro(),
                new UsuarioSalidaDto(maestroGuardado.getUsuario()),
                maestroGuardado.getCodigoEmpleado(),
                maestroGuardado.getActivo()
        );
    }

    // Eliminar Maestro
    @Transactional
    public void eliminarMaestro(Integer idMaestro) {
        Maestro maestro = maestroRepository.findById(idMaestro)
                .orElseThrow(() -> new RuntimeException("Maestro no encontrado"));

        maestro.setActivo(false);
        // Soft delete del usuario (para que no pueda loguearse)
        maestro.getUsuario().setActivo(false);

        maestroRepository.save(maestro);
    }
}