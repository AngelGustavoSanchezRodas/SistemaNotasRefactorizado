package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Para buscar correos del usuario
    Optional<Usuario> findByCorreo(String correo);

    // Para verificar si un correo ya existe
    boolean existsByCorreo(String correo);

}
