package soft.notes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soft.notes.repositories.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

}
