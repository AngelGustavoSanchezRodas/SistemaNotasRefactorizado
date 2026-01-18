package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Maestro;

import java.util.Optional;

@Repository
public interface MaestroRepository extends JpaRepository<Maestro, Integer> {

    // Para verificar si un codigoEmpleado ya existe
    boolean existsByCodigoEmpleado (String codigoEmpleado);

}
