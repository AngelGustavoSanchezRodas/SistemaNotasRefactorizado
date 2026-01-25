package soft.notes.IU.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.IU.dashboard.admin.AdminPanel;
import soft.notes.IU.dashboard.alumno.AlumnoPanel;
import soft.notes.IU.dashboard.maestro.MaestroPanel;
import soft.notes.IU.inicio.InicioFrame;
import soft.notes.dto.usuario.UsuarioSalidaDto;

import javax.swing.*;
import java.awt.*;

@Component
public class MainFrame extends JFrame {

    // Inyectamos los 3 paneles posibles (Las "Vistas")
    @Autowired private AdminPanel adminPanel;
    @Autowired private MaestroPanel maestroPanel;
    @Autowired private AlumnoPanel alumnoPanel;

    @Autowired private InicioFrame inicioFrame; // Para cerrar sesión

    private UsuarioSalidaDto usuarioActual;

    public MainFrame() {
        setTitle("SoftNotes - Sistema de Gestión Escolar");
        setSize(1100, 750); // Un poco más grande para que quepan las tablas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * ESTE ES EL MÉTODO QUE DETECTA EL ROL
     * Se llama automáticamente desde el LoginFrame cuando la contraseña es correcta.
     */
    public void iniciarSesion(UsuarioSalidaDto usuario) {
        this.usuarioActual = usuario;

        // 1. Limpiamos la ventana (quitamos lo que hubiera antes)
        getContentPane().removeAll();

        // 2. Ponemos el Header (Barra superior con nombre y botón salir)
        add(crearHeader(), BorderLayout.NORTH);

        // 3. LÓGICA DE DETECCIÓN DE ROL (El "Switch" Maestro)
        String rol = usuario.getRol().toUpperCase(); // Convertimos a mayúsculas por seguridad

        System.out.println("Iniciando sesión con Rol: " + rol); // Log para depurar

        switch (rol) {
            case "ADMIN":
            case "ADMINISTRATIVO":
                // A. Si es Admin, mostramos el panel con las 6 pestañas
                add(adminPanel, BorderLayout.CENTER);
                adminPanel.inicializarDatos();
                break;

            case "MAESTRO":
                // B. Si es Maestro, mostramos su panel de cursos
                add(maestroPanel, BorderLayout.CENTER);
                // IMPORTANTE: Aquí cargaremos los cursos de ESTE maestro
                maestroPanel.cargarCursosMaestro(usuario.getIdUsuario());
                break;

            case "ALUMNO":
                // C. Si es Alumno, mostramos sus notas
                add(alumnoPanel, BorderLayout.CENTER);
                // IMPORTANTE: Aquí cargamos las notas de ESTE alumno
                alumnoPanel.cargarNotasAlumno(usuario.getIdUsuario());
                break;

            default:
                // Caso de error o rol nuevo
                JPanel panelError = new JPanel(new GridBagLayout());
                panelError.add(new JLabel("Rol no reconocido: " + rol));
                add(panelError, BorderLayout.CENTER);
        }

        // 4. Refrescamos la ventana para que se pinten los cambios
        revalidate();
        repaint();
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 33, 33)); // Gris oscuro elegante
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Info del Usuario
        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblRol = new JLabel("  [" + usuarioActual.getRol() + "]");
        lblRol.setForeground(new Color(200, 200, 200)); // Gris claro
        lblRol.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelInfo.setOpaque(false);
        panelInfo.add(lblUsuario);
        panelInfo.add(lblRol);

        // Botón Salir
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(198, 40, 40)); // Rojo
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnLogout.addActionListener(e -> cerrarSesion());

        header.add(panelInfo, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        return header;
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desea cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.usuarioActual = null;
            this.setVisible(false);
            getContentPane().removeAll(); // Limpiar paneles de memoria
            inicioFrame.setVisible(true); // Volver a la portada
        }
    }
}