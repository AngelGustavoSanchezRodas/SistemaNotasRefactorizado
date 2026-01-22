package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.grado.GradoRegistroDto; // Corregí el paquete a minúscula
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.entities.Grado;
import soft.notes.repositories.GradoRepository;

import java.util.List;

@Service
public class GradoService {

    @Autowired
    private GradoRepository gradoRepository;

    // Obtener todos los grados (SOLO LOS ACTIVOS)
    @Transactional(readOnly = true)
    public List<GradoSalidaDto> obtenerTodosLosGrados() {
        List<Grado> grados = gradoRepository.findAll();

        return grados.stream()
                // 1. FILTRO: Solo mostramos los grados activos
                .filter(Grado::getActivo)
                .map(grado -> new GradoSalidaDto(
                        grado.getIdGrado(),
                        grado.getNombreGrado(),
                        grado.getSeccion(),
                        grado.getActivo() // 2. ACTUALIZACIÓN: Agregamos el campo al constructor
                ))
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
        // nuevoGrado.setActivo(true); // La entidad lo pone por defecto, pero no está de más saberlo.

        Grado gradoGuardado = gradoRepository.save(nuevoGrado);

        return new GradoSalidaDto(
                gradoGuardado.getIdGrado(),
                gradoGuardado.getNombreGrado(),
                gradoGuardado.getSeccion(),
                gradoGuardado.getActivo() // Nuevo campo
        );
    }

    // Editar Grado
    @Transactional
    public GradoSalidaDto editarGrado(Integer idGrado, GradoRegistroDto dto) {

        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("El grado " + idGrado + " no existe"));

        grado.setNombreGrado(dto.getNombreGrado());
        grado.setSeccion(dto.getSeccion());
        // Al editar no tocamos el estado activo

        Grado gradoActualizado = gradoRepository.save(grado);

        return new GradoSalidaDto(
                gradoActualizado.getIdGrado(),
                gradoActualizado.getNombreGrado(),
                gradoActualizado.getSeccion(),
                gradoActualizado.getActivo() // Nuevo campo
        );
    }

    // 3. SOFT DELETE: Eliminar Grado
    @Transactional
    public void eliminarGrado(Integer idGrado) {

        Grado grado = gradoRepository.findById(idGrado)
                .orElseThrow(() -> new RuntimeException("El grado " + idGrado + " no existe"));

        // En lugar de borrar, desactivamos
        grado.setActivo(false);

        gradoRepository.save(grado);
    }
}