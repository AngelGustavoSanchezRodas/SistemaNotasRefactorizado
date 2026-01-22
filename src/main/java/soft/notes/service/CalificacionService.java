package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.calificacion.CalificacionRegistroDto;
import soft.notes.dto.calificacion.CalificacionSalidaDto;
import soft.notes.entities.Alumno;
import soft.notes.entities.Calificacion;
import soft.notes.entities.Materia;
import soft.notes.repositories.AlumnoRepository;
import soft.notes.repositories.CalificacionRepository;
import soft.notes.repositories.MateriaRepository;

@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private MateriaRepository materiaRepository;

    @Value("${app.security.admin-pin}")
    private String PIN_SECRET;


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

    // 2. MÉTODO EXCLUSIVO PARA ACTUALIZAR
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

    // --- Métodos Privados de Apoyo (Para no repetir lógica de seteo) ---
    private void asignarValores(Calificacion c, CalificacionRegistroDto dto) {
        if (dto.getNotaTarea1() != null) c.setNotaTarea1(dto.getNotaTarea1());
        if (dto.getNotaTarea2() != null) c.setNotaTarea2(dto.getNotaTarea2());
        if (dto.getNotaParcial1() != null) c.setNotaParcial1(dto.getNotaParcial1());
        if (dto.getNotaParcial2() != null) c.setNotaParcial2(dto.getNotaParcial2());
        if (dto.getNotaFinal() != null) c.setNotaFinal(dto.getNotaFinal());
    }

    private void validarPinSiEsNecesario(Calificacion entidad, CalificacionRegistroDto dto) {
        // Si intenta cambiar una nota que ya tiene valor (>0) y es diferente a la nueva, pedimos PIN
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