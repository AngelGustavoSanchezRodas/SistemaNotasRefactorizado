package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.Maestro.MaestroRegistroDto;
import soft.notes.dto.Maestro.MaestroSalidaDto;
import soft.notes.dto.Materia.MateriaSalidaDto;
import soft.notes.dto.Usuario.UsuarioSalidaDto;
import soft.notes.entities.Maestro;
import soft.notes.entities.Usuario;
import soft.notes.repositories.MaestroRepository;
import soft.notes.repositories.UsuarioRepository;

import java.time.Year;
import java.util.List;
import java.util.Random;

@Service
public class MaestroService {

    @Autowired
    private MaestroRepository maestroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Generador de Código de Empleado (Ej: PROF-2026-839)
    private String generarCodigoEmpleado() {
        int year = Year.now().getValue();
        int randomNum = new Random().nextInt(9000) + 1000; // 4 dígitos
        return "PROF-" + year + "-" + randomNum;
    }

    // Listar Maestros (Solo Activos)
    @Transactional(readOnly = true)
    public List<MaestroSalidaDto> obtenerMaestros() {
        return maestroRepository.findAll().stream()
                .filter(Maestro::getActivo) // Filtro Soft Delete
                .map(maestro -> new MaestroSalidaDto(
                        maestro.getIdMaestro(),
                        new UsuarioSalidaDto(maestro.getUsuario()), // Constructor Mapper
                        maestro.getCodigoEmpleado(),
                        maestro.getActivo()
                ))
                .toList();
    }

    // Guardar Maestro (Usuario + Perfil Maestro)
    @Transactional
    public MaestroSalidaDto guardarMaestro(MaestroRegistroDto dto) {

        // 1. Validar correo
        if (usuarioRepository.existsByCorreo(dto.getUsuario().getCorreo())) {
            throw new RuntimeException("El correo ya existe");
        }

        // 2. Crear Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getUsuario().getNombre());
        nuevoUsuario.setApellido(dto.getUsuario().getApellido());
        nuevoUsuario.setTelefono(dto.getUsuario().getTelefono());
        nuevoUsuario.setCorreo(dto.getUsuario().getCorreo());
        nuevoUsuario.setRol("MAESTRO");
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getUsuario().getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 3. Crear Maestro
        Maestro nuevoMaestro = new Maestro();
        nuevoMaestro.setUsuario(usuarioGuardado);
        nuevoMaestro.setActivo(true);
        nuevoMaestro.setCodigoEmpleado(generarCodigoEmpleado());

        Maestro maestroGuardado = maestroRepository.save(nuevoMaestro);

        // 4. Retornar
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

        maestro.getUsuario().setActivo(false);

        maestroRepository.save(maestro);
    }
}