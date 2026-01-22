package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.Alumno.AlumnoRegistroDto;
import soft.notes.dto.Alumno.AlumnoSalidaDto;
import soft.notes.dto.Grado.GradoSalidaDto;
import soft.notes.dto.Usuario.UsuarioSalidaDto;
import soft.notes.entities.Alumno;
import soft.notes.entities.Grado;
import soft.notes.entities.Usuario;
import soft.notes.repositories.AlumnoRepository;
import soft.notes.repositories.GradoRepository;
import soft.notes.repositories.UsuarioRepository;

import java.time.Year;
import java.util.List;
import java.util.Random;

@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GradoRepository gradoRepository;

    // Generador del carnet (Privado)
    private String generarCarnet(String nombre, String apellido) {
        int year = Year.now().getValue();
        char letraN = nombre.toUpperCase().charAt(0);
        char letraA = apellido.toUpperCase().charAt(0);

        int randomNum = new Random().nextInt(900) + 100;

        return String.valueOf(year) + letraN + letraA + randomNum;
    }

    // Obtenemos todos los alumnos (SOLO ACTIVOS)
    @Transactional(readOnly = true)
    public List<AlumnoSalidaDto> obtenerAlumnos() {
        List<Alumno> alumnos = alumnoRepository.findAll();

        return alumnos.stream()
                // 1. FILTRO: Solo alumnos activos
                .filter(Alumno::getActivo)
                .map(alumno -> new AlumnoSalidaDto(
                        alumno.getIdAlumno(),
                        new UsuarioSalidaDto(alumno.getUsuario()),
                        new GradoSalidaDto(alumno.getGrado()),
                        alumno.getCarnet(),
                        alumno.getActivo()
                ))
                .toList();
    }

    // Guardar Alumno
    @Transactional
    public AlumnoSalidaDto guardarAlumno(AlumnoRegistroDto dto) {

        if (usuarioRepository.existsByCorreo(dto.getUsuario().getCorreo())) {
            throw new RuntimeException("El correo " + dto.getUsuario().getCorreo() + " ya está registrado.");
        }

        // Buscamos el grado por el id
        Grado gradoExistente = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new RuntimeException("Grado no existe"));

        // Traemos los datos de usuario y los ingresamos
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getUsuario().getNombre());
        nuevoUsuario.setApellido(dto.getUsuario().getApellido());
        nuevoUsuario.setTelefono(dto.getUsuario().getTelefono());
        nuevoUsuario.setCorreo(dto.getUsuario().getCorreo());
        nuevoUsuario.setRol("ALUMNO");
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getUsuario().getPassword()));

        Usuario guardarUsuario = usuarioRepository.save(nuevoUsuario);

        // Creamos el Alumno
        Alumno nuevoAlumno = new Alumno();
        nuevoAlumno.setUsuario(guardarUsuario);
        nuevoAlumno.setGrado(gradoExistente);
        nuevoAlumno.setActivo(true); // Nos aseguramos que nazca activo

        String carnetGenerado = generarCarnet(guardarUsuario.getNombre(), guardarUsuario.getApellido());
        nuevoAlumno.setCarnet(carnetGenerado);

        Alumno guardarAlumno = alumnoRepository.save(nuevoAlumno);

        return new AlumnoSalidaDto(
                guardarAlumno.getIdAlumno(),
                new UsuarioSalidaDto(guardarAlumno.getUsuario()),
                new GradoSalidaDto(guardarAlumno.getGrado()),
                guardarAlumno.getCarnet(),
                guardarAlumno.getActivo() // 5to parámetro
        );
    }

    // Editamos la informacion academica del alumno (Cambio de Grado)
    @Transactional
    public AlumnoSalidaDto cambiarGrado(Integer idAlumno, Integer idNuevoGrado) {

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("El alumno con ID " + idAlumno + " no existe"));

        if (!alumno.getGrado().getIdGrado().equals(idNuevoGrado)) {

            Grado nuevoGrado = gradoRepository.findById(idNuevoGrado)
                    .orElseThrow(() -> new RuntimeException("El grado con ID " + idNuevoGrado + " no existe"));

            alumno.setGrado(nuevoGrado);
            alumnoRepository.save(alumno);
        }

        return new AlumnoSalidaDto(
                alumno.getIdAlumno(),
                new UsuarioSalidaDto(alumno.getUsuario()),
                new GradoSalidaDto(alumno.getGrado()),
                alumno.getCarnet(),
                alumno.getActivo() // 5to parámetro
        );
    }

    // 3. IMPLEMENTACIÓN DE SOFT DELETE
    @Transactional
    public void eliminarAlumno(Integer idAlumno) {

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // Opción A: Desactivar solo el registro académico
        alumno.setActivo(false);

        // Opción B (Opcional): Si quieres que TAMPOCO pueda entrar al sistema, descomenta esto:
        alumno.getUsuario().setActivo(false);

        alumnoRepository.save(alumno);
    }
}