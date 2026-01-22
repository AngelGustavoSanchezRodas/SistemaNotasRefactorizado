package soft.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soft.notes.dto.usuario.UsuarioRegistroDto;
import soft.notes.dto.usuario.UsuarioSalidaDto;
import soft.notes.entities.Usuario;
import soft.notes.repositories.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar SOLO usuarios activos (Soft Delete logic)
    @Transactional(readOnly = true)
    public List<UsuarioSalidaDto> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                // Filtramos por usuario activo
                .filter(Usuario::getActivo)
                .map(usuario -> new UsuarioSalidaDto(
                        usuario.getIdUsuario(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getTelefono(),
                        usuario.getCorreo(),
                        usuario.getRol(),
                        usuario.getActivo()
                ))
                .toList();
    }

    @Transactional
    public UsuarioSalidaDto registrarUsuario(UsuarioRegistroDto dto) {

        Usuario usuarioGuardado = crearUsuarioBase(dto, dto.getRol());

        return new UsuarioSalidaDto(usuarioGuardado);
    }

    @Transactional
    public Usuario crearUsuarioBase(UsuarioRegistroDto dto, String rolForzado) {

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo " + dto.getCorreo() + " ya existe.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setApellido(dto.getApellido());
        nuevoUsuario.setTelefono(dto.getTelefono());
        nuevoUsuario.setCorreo(dto.getCorreo());

        nuevoUsuario.setRol(rolForzado != null ? rolForzado : dto.getRol());

        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setActivo(true);

        return usuarioRepository.save(nuevoUsuario);
    }

    @Transactional
    public UsuarioSalidaDto editarUsuario(Integer idUsuario, UsuarioRegistroDto dto) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());

        // Aquí asumimos que siempre viene en el DTO de registro.
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return new UsuarioSalidaDto(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellido(),
                usuarioGuardado.getTelefono(),
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getRol(),
                usuarioGuardado.getActivo()
        );
    }

    // IMPLEMENTACIÓN DE SOFT DELETE
    @Transactional
    public void elimnarUsuario(Integer idUsuario) {

        // 1. Buscamos al usuario (Si no existe, fallamos)
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. En lugar de borrar, cambiamos el estado
        usuario.setActivo(false);

        // 3. Guardamos el cambio
        usuarioRepository.save(usuario);
    }
}