package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Grado;

import java.util.Optional;

@Repository
public interface GradoRepository extends JpaRepository<Grado,Integer> {

    // Para verificar si un nombreGrado ya existe
    boolean existsByNombreGrado(String nombreGrado);

    // Para verificar si una seccion ya existe
    boolean existsBySeccion(String seccion);

    boolean existsByNombreGradoAndSeccion(String nombreGrado, String seccion);

}
