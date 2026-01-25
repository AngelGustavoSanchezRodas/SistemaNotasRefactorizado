package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.calificacion.CalificacionRegistroDto;
import soft.notes.dto.calificacion.CalificacionSalidaDto;
import soft.notes.entities.*;
import soft.notes.repositories.*;

import java.util.List;

@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private GradoRepository gradoRepository;

    @Autowired
    private AsignacionMaestroRepository asignacionMaestroRepository;

    @Value("${app.security.admin-pin}")
    private String PIN_SECRET;

    private void desactivarNotasAnteriores(Alumno alumno) {
        List<Calificacion> notasViejas = calificacionRepository.findByAlumno(alumno);
        for (Calificacion c : notasViejas) {
            c.setActivo(false);
            calificacionRepository.save(c);
        }
    }

    @Transactional(readOnly = true)
    public List<CalificacionSalidaDto> obtenerSabanaDeNotas(Integer idMateria, Integer idGrado) {
        Materia materia = materiaRepository.findById(idMateria).orElseThrow();
        soft.notes.entities.Grado grado = gradoRepository.findById(idGrado).orElseThrow();

        return calificacionRepository.findByMateriaAndAlumno_GradoOrderByAlumno_Usuario_Apellido(materia, grado)
                .stream()
                .map(CalificacionSalidaDto::new)
                .toList();
    }

    @Transactional
    public void inscribirAlumnoEnGrado(Integer idAlumno, Integer idGrado) {

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Grado gradoNuevo = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("Grado no encontrado"));

        // --- PASO NUEVO: LIMPIEZA DE GRADO ANTERIOR ---
        // Si el alumno ya tenía un grado diferente asignado, desactivamos sus notas anteriores.
        if (alumno.getGrado() != null && !alumno.getGrado().getIdGrado().equals(idGrado)) {
            desactivarNotasAnteriores(alumno);
        }

        // Actualizamos al nuevo grado
        alumno.setGrado(gradoNuevo);
        alumnoRepository.save(alumno);

        // --- PASO ESTÁNDAR: CREAR NUEVAS BOLETAS ---
        List<AsignacionMaestro> asignacionesDelGrado = asignacionMaestroRepository.findByGradoAndActivoTrue(gradoNuevo);

        if (asignacionesDelGrado.isEmpty()) {
            throw new RuntimeException("Este grado no tiene cursos configurados (Pensum vacío).");
        }

        for (AsignacionMaestro asignacion : asignacionesDelGrado) {
            Materia materia = asignacion.getMateria();

            // Buscamos si ya existía una boleta (incluso inactiva) para reactivarla o crear nueva
            Calificacion boleta = calificacionRepository.findByAlumnoAndMateria(alumno, materia)
                    .orElse(new Calificacion());

            // Configuramos/Reseteamos la boleta
            boleta.setAlumno(alumno);
            boleta.setMateria(materia);
            boleta.setActivo(true);

            calificacionRepository.save(boleta);
        }
    }
    
   @Transactional(readOnly = true)
    public List<CalificacionSalidaDto> obtenerNotasPorUsuario(Integer idUsuario) {

        // 1. Encontrar quién es el alumno
        Alumno alumno = alumnoRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("No se encontró perfil de alumno para este usuario"));

        // 2. Buscar todas sus calificaciones (Trae activas e inactivas)
        List<Calificacion> calificaciones = calificacionRepository.findByAlumno(alumno);

        // 3. Convertir a DTOs FILTRANDO solo las activas
        return calificaciones.stream()
                .filter(Calificacion::getActivo)
                .map(CalificacionSalidaDto::new)
                .toList();
    }


    @Transactional
    public CalificacionSalidaDto asignarCalificacion(CalificacionRegistroDto dto) {

        // Validamos existencias
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Materia materia = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Validamos que NO exista ya una calificación para evitar duplicados
        if (calificacionRepository.findByAlumnoAndMateria(alumno, materia).isPresent()) {
            throw new RuntimeException("El alumno ya tiene un registro de notas en esta materia. Use la opción de editar.");
        }

        // Creamos la nueva entidad
        Calificacion nuevaCalificacion = new Calificacion();
        nuevaCalificacion.setAlumno(alumno);
        nuevaCalificacion.setMateria(materia);
        
        // Asignamos valores iniciales (sin pedir PIN, porque es nuevo)
        asignarValores(nuevaCalificacion, dto);

        Calificacion guardada = calificacionRepository.save(nuevaCalificacion);

        // Retorno directo usando el constructor del DTO
        return new CalificacionSalidaDto(guardada);
    }

    // MÉTODO EXCLUSIVO PARA ACTUALIZAR
    @Transactional
    public CalificacionSalidaDto editarCalificacion(CalificacionRegistroDto dto) {

        // Buscamos las entidades para poder encontrar la calificación
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Materia materia = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Buscamos la calificación existente
        Calificacion calificacion = calificacionRepository.findByAlumnoAndMateria(alumno, materia)
                .orElseThrow(() -> new RuntimeException("No existe registro de notas para editar. Primero debe asignarlas."));

        // Lógica de Seguridad (PIN)
        validarPinSiEsNecesario(calificacion, dto);

        // Actualizamos valores
        asignarValores(calificacion, dto);

        Calificacion actualizada = calificacionRepository.save(calificacion);

        return new CalificacionSalidaDto(actualizada);
    }

    // --- Métodos Privados de Apoyo  ---
    private void asignarValores(Calificacion c, CalificacionRegistroDto dto) {

        if (dto.getNotaTarea1() != null) c.setNotaTarea1(dto.getNotaTarea1());
        if (dto.getNotaTarea2() != null) c.setNotaTarea2(dto.getNotaTarea2());
        if (dto.getNotaParcial1() != null) c.setNotaParcial1(dto.getNotaParcial1());
        if (dto.getNotaParcial2() != null) c.setNotaParcial2(dto.getNotaParcial2());
        if (dto.getNotaFinal() != null) c.setNotaFinal(dto.getNotaFinal());
    }

    private void validarPinSiEsNecesario(Calificacion entidad, CalificacionRegistroDto dto) {
        // Si intenta cambiar una nota que ya tiene valor, pedimos PIN
        boolean requierePin = 
            (dto.getNotaTarea1() != null && entidad.getNotaTarea1() > 0 && !dto.getNotaTarea1().equals(entidad.getNotaTarea1())) ||
            (dto.getNotaTarea2() != null && entidad.getNotaTarea2() > 0 && !dto.getNotaTarea2().equals(entidad.getNotaTarea2())) ||
            (dto.getNotaParcial1() != null && entidad.getNotaParcial1() > 0 && !dto.getNotaParcial1().equals(entidad.getNotaParcial1())) ||
            (dto.getNotaParcial2() != null && entidad.getNotaParcial2() > 0 && !dto.getNotaParcial2().equals(entidad.getNotaParcial2())) ||
            (dto.getNotaFinal() != null && entidad.getNotaFinal() > 0 && !dto.getNotaFinal().equals(entidad.getNotaFinal()));

        if (requierePin) {
            if (dto.getPinAdministrativo() == null || !dto.getPinAdministrativo().equals(PIN_SECRET)) {
                throw new RuntimeException("PIN Administrativo requerido o incorrecto para modificar notas existentes.");
            }
        }
    }
}