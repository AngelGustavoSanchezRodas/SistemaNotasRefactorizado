package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Alumno;
import soft.notes.entities.Calificacion;
import soft.notes.entities.Materia;

import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    // Para buscar calificacion por idAlumno y idMateria
    Optional<Calificacion> findByAlumnoAndMateria(Alumno idAlumno, Materia idMateria);

}
