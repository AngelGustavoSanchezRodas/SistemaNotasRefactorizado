package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.grado.GradoRegistroDto; // Corregí el paquete a minúscula
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.entities.Grado;
import soft.notes.entities.Materia;
import soft.notes.repositories.GradoRepository;
import soft.notes.repositories.MateriaRepository;

import java.util.List;

@Service
public class GradoService {

    @Autowired
    private GradoRepository gradoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    // Obtener todos los grados (SOLO LOS ACTIVOS)
    @Transactional(readOnly = true)
    public List<GradoSalidaDto> obtenerTodosLosGrados() {
        List<Grado> grados = gradoRepository.findAll();

        return grados.stream()

                .filter(Grado::getActivo)
                .map(grado -> new GradoSalidaDto(
                        grado.getIdGrado(),
                        grado.getNombreGrado(),
                        grado.getSeccion(),
                        grado.getActivo()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<soft.notes.dto.materia.MateriaSalidaDto> obtenerMateriasPorGrado(Integer idGrado) {
        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("Grado no encontrado"));

        return grado.getMateriasDelGrado().stream()
                .filter(Materia::getActivo)
                .map(MateriaSalidaDto::new)
                .toList();
    }

    // Guardar un nuevo grado
    @Transactional
    public GradoSalidaDto guardarGrado(GradoRegistroDto dto) {

        if (gradoRepository.existsByNombreGradoAndSeccion(dto.getNombreGrado(), dto.getSeccion())) {
            throw new RuntimeException("El grado " + dto.getNombreGrado() + " sección " + dto.getSeccion() + " ya existe");
        }

        Grado nuevoGrado = new Grado();
        nuevoGrado.setNombreGrado(dto.getNombreGrado());
        nuevoGrado.setSeccion(dto.getSeccion());
        nuevoGrado.setActivo(true);

        if (dto.getIdsMaterias() != null && !dto.getIdsMaterias().isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(dto.getIdsMaterias());
            nuevoGrado.setMateriasDelGrado(materias);
        }

        Grado gradoGuardado = gradoRepository.save(nuevoGrado);

        return new GradoSalidaDto(
                gradoGuardado.getIdGrado(),
                gradoGuardado.getNombreGrado(),
                gradoGuardado.getSeccion(),
                gradoGuardado.getActivo()
        );
    }

    // Editar Grado
    @Transactional
    public GradoSalidaDto editarGrado(Integer idGrado, GradoRegistroDto dto) {

        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("El grado " + idGrado + " no existe"));

        grado.setNombreGrado(dto.getNombreGrado());
        grado.setSeccion(dto.getSeccion());

        Grado gradoActualizado = gradoRepository.save(grado);

        return new GradoSalidaDto(
                gradoActualizado.getIdGrado(),
                gradoActualizado.getNombreGrado(),
                gradoActualizado.getSeccion(),
                gradoActualizado.getActivo() // Nuevo campo
        );
    }

    // Eliminar Grado
    @Transactional
    public void eliminarGrado(Integer idGrado) {

        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("El grado " + idGrado + " no existe"));

        grado.setActivo(false);

        gradoRepository.save(grado);
    }
}