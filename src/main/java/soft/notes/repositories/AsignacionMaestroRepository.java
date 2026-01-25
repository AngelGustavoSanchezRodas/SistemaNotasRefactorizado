package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.AsignacionMaestro;
import soft.notes.entities.Grado;
import soft.notes.entities.Maestro;
import soft.notes.entities.Materia;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionMaestroRepository extends JpaRepository<AsignacionMaestro, Integer> {

    // Usamos esto antes de guardar para evitar duplicados.
    boolean existsByMaestroAndMateriaAndGradoAndActivoTrue(Maestro maestro, Materia materia, Grado grado);

    // Devuelve una LISTA, porque el profe puede dar esa materia en varios grados distintos.
    List<AsignacionMaestro> findByMaestroAndMateriaAndActivoTrue(Maestro maestro, Materia materia);

    // Sirve para: Mostrarle al profe Juan todas las clases que tiene asignadas.
    List<AsignacionMaestro> findByMaestroAndActivoTrue(Maestro maestro);

    // Sirve para: Ver qué materias se dan en 5to Bachillerato.
    List<AsignacionMaestro> findByGradoAndActivoTrue(Grado grado);

    // Si alguna vez necesitas encontrar LA asignación única para editarla, usaremos 3 parametros
    Optional<AsignacionMaestro> findByMaestroAndMateriaAndGradoAndActivoTrue(Maestro maestro, Materia materia, Grado grado);

    List<AsignacionMaestro> findByMaestro_Usuario_IdUsuarioAndActivoTrue(Integer idUsuario);

}