package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.Materia.MateriaRegistroDto; // Corregí mayúsculas del paquete
import soft.notes.dto.Materia.MateriaSalidaDto;
import soft.notes.entities.Materia;
import soft.notes.repositories.MateriaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    // Obtener todas las materias (SOLO LAS ACTIVAS)
    @Transactional(readOnly = true)
    public List<MateriaSalidaDto> obtenerTodasLasMaterias(){
        List<Materia> materias = materiaRepository.findAll();

        return materias.stream()
                // 1. FILTRO SOFT DELETE: Solo mostramos las que tienen activo = true
                .filter(Materia::getActivo)
                .map(materia -> new MateriaSalidaDto(
                            materia.getIdMateria(),
                            materia.getNombre(),
                            materia.getDescripcion(),
                            materia.getActivo() // 2. ACTUALIZACIÓN: Agregamos el campo al DTO
                ))
                .collect(Collectors.toList());
    }

    // Guardar una nueva materia
    @Transactional
    public MateriaSalidaDto guardarMateria(MateriaRegistroDto dto){

        if(materiaRepository.existsByNombre(dto.getNombre())){
            throw new RuntimeException(dto.getNombre() + " ya existe");
        }

        Materia nuevaMateria = new Materia();
        nuevaMateria.setNombre(dto.getNombre());
        nuevaMateria.setDescripcion(dto.getDescripcion());
        // nuevaMateria.setActivo(true); // Opcional: La entidad ya lo pone en true por defecto

        Materia materiaGuardada = materiaRepository.save(nuevaMateria);

        return  new MateriaSalidaDto(
                materiaGuardada.getIdMateria(),
                materiaGuardada.getNombre(),
                materiaGuardada.getDescripcion(),
                materiaGuardada.getActivo() // Nuevo campo
        );
    }

    // Editar una materia existente
    @Transactional
    public MateriaSalidaDto editarMateria(Integer idMateria, MateriaRegistroDto dto){

        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new RuntimeException("La materia no existe")); // Mensaje corregido

        materia.setNombre(dto.getNombre());
        materia.setDescripcion(dto.getDescripcion());
        // Nota: Al editar no tocamos el "activo", se mantiene como estaba.

        Materia materiaActualizada = materiaRepository.save(materia);

        return new MateriaSalidaDto(
                materiaActualizada.getIdMateria(),
                materiaActualizada.getNombre(),
                materiaActualizada.getDescripcion(),
                materiaActualizada.getActivo() // Nuevo campo
        );
    }

    // 3. SOFT DELETE: Eliminar (desactivar) una materia
    @Transactional
    public void eliminarMateria(Integer idMateria){

        // Buscamos la materia
        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        materia.setActivo(false);

        materiaRepository.save(materia);
    }
}