package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.AsignacionMaestro;
import soft.notes.entities.Maestro;
import soft.notes.entities.Materia;

import java.util.Optional;

@Repository
public interface AsignacionMaestroRepository extends JpaRepository<AsignacionMaestro, Integer> {

    // Para buscar asignacion por idMaestro y idMateria
    Optional<AsignacionMaestro> findByMaestroAndMateria(Maestro idMaestro, Materia idMateria);
}
