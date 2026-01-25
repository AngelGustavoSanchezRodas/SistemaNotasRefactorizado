package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.asignacionMaestro.AsignacionMaestroSalidaDto;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.maestro.MaestroSalidaDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.entities.*;
import soft.notes.repositories.*;
import soft.notes.dto.asignacionMaestro.AsignacionMaestroRegistroDto;
import soft.notes.entities.AsignacionMaestro;

import java.util.List;

@Service
public class AsignacionMaestroService {

    @Autowired
    private AsignacionMaestroRepository asignacionMaestroRepository;

    @Autowired
    private GradoRepository gradoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private MaestroRepository maestroRepository;


    @Transactional(readOnly = true)
    public List<AsignacionMaestroSalidaDto> obtenerAsignaciones(){
        List<AsignacionMaestro> asignacionMaestros = asignacionMaestroRepository.findAll();

        return asignacionMaestros.stream()
                .filter(AsignacionMaestro::getActivo)
                .map(asignacionMaestro -> new AsignacionMaestroSalidaDto(
                        asignacionMaestro.getIdAsignacion(),
                        new MaestroSalidaDto(asignacionMaestro.getMaestro()),
                        new MateriaSalidaDto(asignacionMaestro.getMateria()),
                        new GradoSalidaDto(asignacionMaestro.getGrado()),
                        asignacionMaestro.getActivo()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsignacionMaestroSalidaDto> obtenerCursosPorMaestroUsuario(Integer idUsuario) {
        return asignacionMaestroRepository.findByMaestro_Usuario_IdUsuarioAndActivoTrue(idUsuario)
                .stream()
                .map(AsignacionMaestroSalidaDto::new)
                .toList();
    }

    // Para ver la carga académica de un profesor específico
    @Transactional(readOnly = true)
    public List<AsignacionMaestroSalidaDto> obtenerPorMaestro(Integer idMaestro) {

        Maestro maestro = maestroRepository.findById(idMaestro)
                .orElseThrow(() -> new RuntimeException("Maestro no encontrado"));

        return asignacionMaestroRepository.findByMaestroAndActivoTrue(maestro).stream()
                .map(AsignacionMaestroSalidaDto::new)
                .toList();
    }

    // Para ver el pensum de un grado
    @Transactional(readOnly = true)
    public List<AsignacionMaestroSalidaDto> obtenerPorGrado(Integer idGrado) {

        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("Grado no encontrado"));

        return asignacionMaestroRepository.findByGradoAndActivoTrue(grado).stream()
                .map(AsignacionMaestroSalidaDto::new)
                .toList();
    }

    // Guardamos a que maestros le asignamos el curso, grado, etc.
    @Transactional
    public AsignacionMaestroSalidaDto guardarAsignacion(AsignacionMaestroRegistroDto dto){

        Maestro maestro = maestroRepository.findById(dto.getIdMaestro())
                .orElseThrow(() -> new RuntimeException("El maestro no existe"));

        Materia materia = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new RuntimeException("La materia no existe"));

        Grado grado = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new RuntimeException("El grado no existe"));

       if (asignacionMaestroRepository.existsByMaestroAndMateriaAndGradoAndActivoTrue(maestro, materia, grado)) {
            throw new RuntimeException("Este maestro ya tiene asignada esa materia en ese grado.");
        }

        AsignacionMaestro nuevaAsignacion = new AsignacionMaestro();
        nuevaAsignacion.setMaestro(maestro);
        nuevaAsignacion.setMateria(materia);
        nuevaAsignacion.setGrado(grado);
        nuevaAsignacion.setActivo(true);

        AsignacionMaestro asignacionGuardada = asignacionMaestroRepository.save(nuevaAsignacion);

        return new AsignacionMaestroSalidaDto(asignacionGuardada);
    }

    // Editar Asignación
    @Transactional
    public AsignacionMaestroSalidaDto editarAsignacion(Integer idAsignacion, AsignacionMaestroRegistroDto dto) {

        //  Buscamos la asignación actual
        AsignacionMaestro asignacion = asignacionMaestroRepository.findById(idAsignacion)
                .orElseThrow(() -> new RuntimeException("La asignación no existe"));

        // Buscamos las nuevas
        Maestro nuevoMaestro = maestroRepository.findById(dto.getIdMaestro())
                .orElseThrow(() -> new RuntimeException("El maestro no existe"));

        Materia nuevaMateria = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new RuntimeException("La materia no existe"));

        Grado nuevoGrado = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new RuntimeException("El grado no existe"));

        boolean cambioAlgo = !asignacion.getMaestro().getIdMaestro().equals(dto.getIdMaestro()) ||
                             !asignacion.getMateria().getIdMateria().equals(dto.getIdMateria()) ||
                             !asignacion.getGrado().getIdGrado().equals(dto.getIdGrado());

        if (cambioAlgo && asignacionMaestroRepository.existsByMaestroAndMateriaAndGradoAndActivoTrue(nuevoMaestro, nuevaMateria, nuevoGrado)) {
            throw new RuntimeException("Ya existe una asignación idéntica para estos datos.");
        }

        asignacion.setMaestro(nuevoMaestro);
        asignacion.setMateria(nuevaMateria);
        asignacion.setGrado(nuevoGrado);

        AsignacionMaestro asignacionActualizada = asignacionMaestroRepository.save(asignacion);

        return new AsignacionMaestroSalidaDto(asignacionActualizada);
    }

     @Transactional
    public void eliminarAsignacion(Integer idAsignacion) {

        AsignacionMaestro asignacion = asignacionMaestroRepository.findById(idAsignacion)
                .orElseThrow(() -> new RuntimeException("Asignacion no encontrada"));

        asignacion.setActivo(false);

        asignacionMaestroRepository.save(asignacion);
    }

}
