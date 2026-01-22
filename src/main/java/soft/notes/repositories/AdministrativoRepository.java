package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Administrativo;

@Repository
public interface AdministrativoRepository extends JpaRepository<Administrativo, Integer> {
    // No necesitamos métodos especiales por ahora, el CRUD básico basta
}