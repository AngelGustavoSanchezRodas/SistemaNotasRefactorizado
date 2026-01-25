package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.alumno.AlumnoRegistroDto;
import soft.notes.dto.alumno.AlumnoSalidaDto;
import soft.notes.dto.usuario.UsuarioRegistroDto;
import soft.notes.service.AlumnoService;
import soft.notes.service.GradoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class GestionAlumnosPanel extends JPanel {

    private final AlumnoService alumnoService;
    private final GradoService gradoService;

    private JTable tablaAlumnos;
    private DefaultTableModel modeloTabla;

    @Autowired
    public GestionAlumnosPanel(AlumnoService alumnoService, GradoService gradoService) {
        this.alumnoService = alumnoService;
        this.gradoService = gradoService;

        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        // --- 1. BARRA SUPERIOR (BOTONES) ---
        JPanel barraHerramientas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraHerramientas.setBackground(Color.WHITE);

        JButton btnNuevo = crearBoton("Nuevo Alumno", new Color(0, 150, 136));
        JButton btnEliminar = crearBoton("Eliminar", new Color(211, 47, 47));
        JButton btnRefrescar = crearBoton("Refrescar", new Color(33, 150, 243));

        btnNuevo.addActionListener(e -> abrirFormularioCreacion());
        btnEliminar.addActionListener(e -> eliminarAlumnoSeleccionado());
        btnRefrescar.addActionListener(e -> cargarDatosTabla());

        barraHerramientas.add(btnNuevo);
        barraHerramientas.add(btnEliminar);
        barraHerramientas.add(btnRefrescar);

        add(barraHerramientas, BorderLayout.NORTH);

        // --- 2. TABLA ---
        String[] columnas = {"ID", "Carnet", "Nombre", "Apellido", "Correo", "Grado", "Sección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAlumnos = new JTable(modeloTabla);
        tablaAlumnos.setRowHeight(25);
        tablaAlumnos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        add(new JScrollPane(tablaAlumnos), BorderLayout.CENTER);
    }

    // --- LÓGICA DE NEGOCIO ---

   public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<AlumnoSalidaDto> alumnos = alumnoService.obtenerAlumnos();

        for (AlumnoSalidaDto a : alumnos) {

            // VALIDACIÓN VISUAL:
            String nombreGrado = (a.getGrado() != null) ? a.getGrado().getNombreGrado() : "--- SIN ASIGNAR ---";
            String seccion = (a.getGrado() != null) ? a.getGrado().getSeccion() : "";

            Object[] fila = {
                a.getIdAlumno(),
                a.getCarnet(),
                a.getUsuario().getNombre(),
                a.getUsuario().getApellido(),
                a.getUsuario().getCorreo(),
                nombreGrado,
                seccion
            };
            modeloTabla.addRow(fila);
        }
    }

    private void abrirFormularioCreacion() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Alumno (Admisión)", true);
        dialogo.setSize(400, 400); // Más pequeño ahora
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(15);
        JTextField txtApellido = new JTextField(15);
        JTextField txtTelefono = new JTextField(15);
        JTextField txtCorreo = new JTextField(15);
        JPasswordField txtPassword = new JPasswordField(15);

        agregarCampo(dialogo, "Nombre:", txtNombre, gbc, 0);
        agregarCampo(dialogo, "Apellido:", txtApellido, gbc, 1);
        agregarCampo(dialogo, "Teléfono:", txtTelefono, gbc, 2);
        agregarCampo(dialogo, "Correo:", txtCorreo, gbc, 3);
        agregarCampo(dialogo, "Contraseña:", txtPassword, gbc, 4);

        JButton btnGuardar = new JButton("Registrar Alumno");
        btnGuardar.setBackground(new Color(0, 150, 136));
        btnGuardar.setForeground(Color.WHITE);

        btnGuardar.addActionListener(e -> {
            try {
                UsuarioRegistroDto userDto = new UsuarioRegistroDto();
                userDto.setNombre(txtNombre.getText());
                userDto.setApellido(txtApellido.getText());
                userDto.setTelefono(txtTelefono.getText());
                userDto.setCorreo(txtCorreo.getText());
                userDto.setPassword(new String(txtPassword.getPassword()));
                userDto.setRol("ALUMNO");

                AlumnoRegistroDto alumnoDto = new AlumnoRegistroDto();
                alumnoDto.setUsuario(userDto);

                alumnoService.guardarAlumno(alumnoDto);

                JOptionPane.showMessageDialog(dialogo, "Alumno registrado. Recuerde inscribirlo en un grado.");
                dialogo.dispose();
                cargarDatosTabla();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });

        gbc.gridy = 5; gbc.gridwidth = 2;
        dialogo.add(btnGuardar, gbc);
        dialogo.setVisible(true);
    }

    private void eliminarAlumnoSeleccionado() {
        int filaSeleccionada = tablaAlumnos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un alumno para eliminar");
            return;
        }

        // Obtener ID (Columna 0)
        Integer idAlumno = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar al alumno " + nombre + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                alumnoService.eliminarAlumno(idAlumno);
                cargarDatosTabla();
                JOptionPane.showMessageDialog(this, "Alumno eliminado");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // --- HELPERS ---
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void agregarCampo(JDialog dialog, String label, JComponent campo, GridBagConstraints gbc, int fila) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        dialog.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        dialog.add(campo, gbc);
    }

    // Clase auxiliar para el ComboBox
    private record GradoItem(Integer id, String nombre) {
        @Override
        public String toString() { return nombre; }
    }
}