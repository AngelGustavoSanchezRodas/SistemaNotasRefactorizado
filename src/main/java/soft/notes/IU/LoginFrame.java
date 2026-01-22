package soft.notes.IU;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.service.UsuarioService;

import javax.swing.*;

@Component
public class LoginFrame extends JFrame {

    @Autowired
    private UsuarioService usuarioService;

    
}
