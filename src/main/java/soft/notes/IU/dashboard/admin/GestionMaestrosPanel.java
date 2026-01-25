package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.maestro.MaestroRegistroDto;
import soft.notes.dto.maestro.MaestroSalidaDto;
import soft.notes.dto.usuario.UsuarioRegistroDto;
import soft.notes.service.MaestroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class GestionMaestrosPanel extends JPanel {

    private final MaestroService maestroService;

    private JTable tablaMaestros;
    private DefaultTableModel modeloTabla;

    @Autowired
    public GestionMaestrosPanel(MaestroService maestroService) {
        this.maestroService = maestroService;
        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        // --- 1. BARRA SUPERIOR ---
        JPanel barraHerramientas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraHerramientas.setBackground(Color.WHITE);

        JButton btnNuevo = crearBoton("Nuevo Maestro", new Color(0, 150, 136));
        JButton btnEliminar = crearBoton("Eliminar", new Color(211, 47, 47));
        JButton btnRefrescar = crearBoton("Refrescar", new Color(33, 150, 243));

        btnNuevo.addActionListener(e -> abrirFormularioCreacion());
        btnEliminar.addActionListener(e -> eliminarMaestroSeleccionado());
        btnRefrescar.addActionListener(e -> cargarDatosTabla());

        barraHerramientas.add(btnNuevo);
        barraHerramientas.add(btnEliminar);
        barraHerramientas.add(btnRefrescar);

        add(barraHerramientas, BorderLayout.NORTH);

        // --- 2. TABLA ---
        // Columnas: ID, Código, Nombre, Apellido, Correo, Teléfono
        String[] columnas = {"ID", "Código", "Nombre", "Apellido", "Correo", "Teléfono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMaestros = new JTable(modeloTabla);
        tablaMaestros.setRowHeight(25);
        tablaMaestros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        add(new JScrollPane(tablaMaestros), BorderLayout.CENTER);
    }

    // --- LÓGICA DE NEGOCIO ---

    public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<MaestroSalidaDto> maestros = maestroService.obtenerMaestros();

        for (MaestroSalidaDto m : maestros) {
            Object[] fila = {
                    m.getIdMaestro(),
                    m.getCodigoEmpleado(), // Generado automáticamente ej: PROF-2026-1234
                    m.getUsuario().getNombre(),
                    m.getUsuario().getApellido(),
                    m.getUsuario().getCorreo(),
                    m.getUsuario().getTelefono()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void abrirFormularioCreacion() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Maestro", true);
        dialogo.setSize(400, 400);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(0, 150, 136));
        btnGuardar.setForeground(Color.WHITE);

        btnGuardar.addActionListener(e -> {
            try {
                // 1. Armar DTO Usuario
                UsuarioRegistroDto userDto = new UsuarioRegistroDto();
                userDto.setNombre(txtNombre.getText().trim());
                userDto.setApellido(txtApellido.getText().trim());
                userDto.setTelefono(txtTelefono.getText().trim());
                userDto.setCorreo(txtCorreo.getText().trim());
                userDto.setPassword(new String(txtPassword.getPassword()).trim());
                // Rol se fuerza en el service, pero lo ponemos por completitud
                userDto.setRol("MAESTRO");

                // 2. Armar DTO Maestro
                MaestroRegistroDto maestroDto = new MaestroRegistroDto();
                maestroDto.setUsuario(userDto);

                // 3. Guardar
                maestroService.guardarMaestro(maestroDto);

                JOptionPane.showMessageDialog(dialogo, "Maestro creado exitosamente");
                dialogo.dispose();
                cargarDatosTabla();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 5; gbc.gridwidth = 2;
        dialogo.add(btnGuardar, gbc);

        dialogo.setVisible(true);
    }

    private void eliminarMaestroSeleccionado() {
        int filaSeleccionada = tablaMaestros.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un maestro para eliminar");
            return;
        }

        Integer idMaestro = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar al maestro " + nombre + "?\nEsto desactivará su acceso al sistema.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                maestroService.eliminarMaestro(idMaestro);
                cargarDatosTabla();
                JOptionPane.showMessageDialog(this, "Maestro eliminado");
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
}