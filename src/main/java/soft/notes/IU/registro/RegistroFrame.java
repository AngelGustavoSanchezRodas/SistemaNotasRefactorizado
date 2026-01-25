package soft.notes.IU.registro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import soft.notes.IU.inicio.InicioFrame;
import soft.notes.dto.usuario.UsuarioRegistroDto;
import soft.notes.service.UsuarioService;

import javax.swing.*;
import java.awt.*;

@Component
public class RegistroFrame extends JFrame {

    private final UsuarioService usuarioService;
    private final ApplicationContext context;

    private JTextField txtNombre, txtApellido, txtTelefono, txtCorreo;
    private JPasswordField txtPassword;

    @Autowired
    public RegistroFrame(UsuarioService usuarioService, ApplicationContext context) {
        this.usuarioService = usuarioService;
        this.context = context;
        configurarVentana();
        inicializarUI();
    }

    private void configurarVentana() {
        setTitle("Registro de Usuario");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Espacio entre elementos
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. TÍTULO (Fila 0) ---
        JLabel titulo = new JLabel("Crear Cuenta Administrativa");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(new Color(33, 33, 33));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa todo el ancho
        panel.add(titulo, gbc);

        // Reset para los campos
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alinear etiquetas a la izquierda

        // --- 2. CAMPOS (Filas 1 a 5) ---
        // Usamos una variable para controlar la fila manualmente y no perdernos
        int fila = 1;

        txtNombre = agregarFila(panel, "Nombre:", fila++, gbc);
        txtApellido = agregarFila(panel, "Apellido:", fila++, gbc);
        txtTelefono = agregarFila(panel, "Teléfono:", fila++, gbc);
        txtCorreo = agregarFila(panel, "Correo:", fila++, gbc); // <--- AHORA SÍ APARECERÁ

        // --- PASSWORD (Fila 5 - Manual porque es JPasswordField) ---
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        fila++; // Incrementamos fila

        // --- 3. BOTÓN REGISTRAR (Fila 6) ---
        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.setBackground(new Color(46, 125, 50)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGuardar.addActionListener(e -> accionRegistrar());

        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2; // Ocupa todo el ancho
        gbc.insets = new Insets(20, 10, 10, 10); // Más margen arriba
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnGuardar, gbc);

        // --- 4. BOTÓN VOLVER (Fila 7) ---
        JButton btnVolver = new JButton("Volver al Inicio");
        btnVolver.setBackground(new Color(117, 117, 117)); // Gris
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);

        btnVolver.addActionListener(e -> {
            this.setVisible(false);
            limpiarCampos();
            context.getBean(InicioFrame.class).setVisible(true);
        });

        gbc.gridy = fila;
        gbc.insets = new Insets(5, 10, 10, 10); // Menos margen
        panel.add(btnVolver, gbc);

        add(panel);
    }

    // Método auxiliar corregido para evitar solapamientos
    private JTextField agregarFila(JPanel p, String labelTexto, int fila, GridBagConstraints gbc) {
        // 1. Etiqueta (Columna 0)
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0; // No se estira
        p.add(new JLabel(labelTexto), gbc);

        // 2. Campo de Texto (Columna 1)
        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.weightx = 1.0;
        JTextField campo = new JTextField(15);
        p.add(campo, gbc);

        return campo;
    }

    private void accionRegistrar() {
        // Validar campos vacíos
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty() ||
            txtTelefono.getText().isEmpty() || txtCorreo.getText().isEmpty() ||
            txtPassword.getPassword().length == 0) {

            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UsuarioRegistroDto dto = new UsuarioRegistroDto();
            dto.setNombre(txtNombre.getText().trim());
            dto.setApellido(txtApellido.getText().trim());
            dto.setTelefono(txtTelefono.getText().trim());
            dto.setCorreo(txtCorreo.getText().trim());
            dto.setPassword(new String(txtPassword.getPassword()).trim());

            // Rol por defecto para esta prueba
            dto.setRol("ADMINISTRATIVO");

            usuarioService.registrarUsuario(dto);

            JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!");
            this.setVisible(false);
            limpiarCampos();
            context.getBean(InicioFrame.class).setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtPassword.setText("");
    }
}