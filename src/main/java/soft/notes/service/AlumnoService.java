package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.alumno.AlumnoRegistroDto;
import soft.notes.dto.alumno.AlumnoSalidaDto;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.usuario.UsuarioSalidaDto;
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
    private UsuarioService usuarioService;

    @Autowired
    private GradoRepository gradoRepository;

    // Generador del carnet
    private String generarCarnet(String nombre, String apellido) {
        int year = Year.now().getValue();
        char letraN = nombre.toUpperCase().charAt(0);
        char letraA = apellido.toUpperCase().charAt(0);

        int randomNum = new Random().nextInt(900) + 100;

        return String.valueOf(year) + letraN + letraA + randomNum;
    }

    // Obtenemos todos los alumnos
    @Transactional(readOnly = true)
    public List<AlumnoSalidaDto> obtenerAlumnos() {
        List<Alumno> alumnos = alumnoRepository.findAll();

        return alumnos.stream()
                // Solo alumnos activos
                .filter(Alumno::getActivo)
                .map(alumno -> new AlumnoSalidaDto(
                        alumno.getIdAlumno(),
                        new UsuarioSalidaDto(alumno.getUsuario()),
                        alumno.getGrado() != null ? new GradoSalidaDto(alumno.getGrado()) : null,
                        alumno.getCarnet(),
                        alumno.getActivo()
                ))
                .toList();
    }

    // Guardar Alumno
    @Transactional
    public AlumnoSalidaDto guardarAlumno(AlumnoRegistroDto dto) {

        // 1. Buscamos el grado (Validación propia de Alumno)
       Usuario usuarioGuardado = usuarioService.crearUsuarioBase(dto.getUsuario(), "ALUMNO");

       Alumno nuevoAlumno = new Alumno();
        nuevoAlumno.setUsuario(usuarioGuardado);
        nuevoAlumno.setActivo(true);
        nuevoAlumno.setGrado(null);

        // Generamos carnet usando los datos del usuario creado
        String carnetGenerado = generarCarnet(usuarioGuardado.getNombre(), usuarioGuardado.getApellido());
        nuevoAlumno.setCarnet(carnetGenerado);

        Alumno guardarAlumno = alumnoRepository.save(nuevoAlumno);

        return new AlumnoSalidaDto(
                guardarAlumno.getIdAlumno(),
                new UsuarioSalidaDto(guardarAlumno.getUsuario()),
                guardarAlumno.getGrado() != null ? new GradoSalidaDto(guardarAlumno.getGrado()) : null,
                guardarAlumno.getCarnet(),
                guardarAlumno.getActivo()
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


    @Transactional
    public void eliminarAlumno(Integer idAlumno) {

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        alumno.setActivo(false);

        //Evitamos que pueda acceder a la plataforma
        alumno.getUsuario().setActivo(false);

        alumnoRepository.save(alumno);
    }
}