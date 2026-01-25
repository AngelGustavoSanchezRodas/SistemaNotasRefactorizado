package soft.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soft.notes.entities.Alumno;
import soft.notes.entities.Grado;
import soft.notes.entities.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {

    List<Alumno> findByUsuario_NombreContainingIgnoreCaseOrUsuario_ApellidoContainingIgnoreCase(String nombre, String apellido);

    Optional<Alumno> findByUsuario_IdUsuario(Integer idUsuario);

    // Para buscar alumno por idUsuario y idGrado
    Optional<Alumno> findByUsuarioAndGrado(Usuario idUsuario, Grado idGrado);

    // Obtener alumno por Carnet
   boolean existsByCarnet(String carnet);

}
