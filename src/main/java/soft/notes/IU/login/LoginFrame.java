package soft.notes.IU.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import soft.notes.IU.dashboard.MainFrame;
import soft.notes.IU.inicio.InicioFrame;
import soft.notes.dto.usuario.UsuarioSalidaDto;
import soft.notes.service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component
public class LoginFrame extends JFrame {

    private final UsuarioService usuarioService;
    private final ApplicationContext context;

    // Componentes de la UI
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    @Autowired
    public LoginFrame(UsuarioService usuarioService, ApplicationContext context) {
        this.usuarioService = usuarioService;
        this.context = context;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("SoftNotes - Iniciar Sesión");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TÍTULO ---
        JLabel lblTitulo = new JLabel("Bienvenido");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // --- CORREO ---
        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Correo Electrónico:"), gbc);

        txtCorreo = new JTextField(20);
        gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(txtCorreo, gbc);

        // --- PASSWORD ---
        gbc.gridwidth = 1; gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(txtPassword, gbc);

        // --- BOTÓN INGRESAR ---
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(33, 150, 243)); // Azul
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnIngresar, gbc);

        // --- BOTÓN CANCELAR (VOLVER) ---
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(117, 117, 117)); // Gris
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Acción directa para cancelar
        btnCancelar.addActionListener(e -> {
            this.setVisible(false);
            context.getBean(InicioFrame.class).setVisible(true);
            limpiarCampos();
        });

        gbc.gridy = 6;
        panel.add(btnCancelar, gbc);

        add(panel);

        configurarEventos();
    }

    private void configurarEventos() {
        btnIngresar.addActionListener(this::accionIngresar);
        txtPassword.addActionListener(this::accionIngresar);
    }

    private void accionIngresar(ActionEvent e) {
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor llene todos los campos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. LLAMADA REAL AL SERVICIO
            UsuarioSalidaDto usuario = usuarioService.login(correo, password);

            // 2. OCULTAR LOGIN
            this.setVisible(false);
            limpiarCampos();

            // 3. ABRIR EL DASHBOARD PRINCIPAL (Ya conectado)
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.iniciarSesion(usuario); // Pasamos el usuario logueado para que sepa qué panel mostrar
            mainFrame.setVisible(true);

        } catch (Exception ex) {
            mostrarMensaje("Error de acceso: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtCorreo.setText("");
        txtPassword.setText("");
    }

    private void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, "Sistema Escolar", tipo);
    }
}