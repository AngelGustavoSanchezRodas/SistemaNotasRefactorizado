package soft.notes.IU.inicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import soft.notes.IU.login.LoginFrame;
import soft.notes.IU.registro.RegistroFrame;

import javax.swing.*;
import java.awt.*;

@Component
public class InicioFrame extends JFrame {

    private final ApplicationContext context;

    @Value("${app.security.admin-pin}")
    private String PIN_SECRET;

    @Autowired
    public InicioFrame(ApplicationContext context) {
        this.context = context;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("SoftNotes - Bienvenido");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // --- LOGO / TÍTULO ---
        JLabel lblTitulo = new JLabel("SoftNotes Escolar");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Gestión Académica Simplificada");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // --- BOTONES ---
        JButton btnLogin = new JButton("Iniciar Sesión");
        estilarBoton(btnLogin, new Color(33, 150, 243)); // Azul

        JButton btnRegistro = new JButton("Registrar Nuevo Usuario");
        estilarBoton(btnRegistro, new Color(76, 175, 80)); // Verde

        // --- ACCIONES ---

        // 1. Login (Acceso público)
        btnLogin.addActionListener(e -> {
            this.setVisible(false);
            context.getBean(LoginFrame.class).setVisible(true);
        });

        // 2. Registro (PROTEGIDO CON PIN)
        btnRegistro.addActionListener(e -> {
            // Pedimos el PIN
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(
                    this,
                    pf,
                    "Ingrese PIN Administrativo para continuar:",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());

                // Verificamos contra el valor de application.properties
                if (password.equals(PIN_SECRET)) {
                    // PIN CORRECTO: Abrimos la ventana de registro
                    this.setVisible(false);
                    context.getBean(RegistroFrame.class).setVisible(true); // <--- CONEXIÓN FINAL

                } else {
                    // PIN INCORRECTO
                    JOptionPane.showMessageDialog(this, "PIN Incorrecto. Acceso denegado.", "Seguridad", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- AGREGAR AL PANEL ---
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblSubtitulo);
        panel.add(Box.createVerticalStrut(50));
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnRegistro);

        add(panel);
    }

   private void estilarBoton(JButton btn, Color color) {

        btn.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        btn.setMaximumSize(new Dimension(250, 45));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }
}