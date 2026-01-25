package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Alumno;
import soft.notes.entities.Calificacion;
import soft.notes.entities.Grado;
import soft.notes.entities.Materia;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    Optional<Calificacion> findByAlumnoAndMateria(Alumno alumno, Materia materia);

    List<Calificacion> findByAlumno(Alumno alumno);

    List<Calificacion> findByMateriaAndAlumno_GradoOrderByAlumno_Usuario_Apellido(Materia materia, Grado grado);
}