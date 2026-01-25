package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.asignacionMaestro.AsignacionMaestroRegistroDto;
import soft.notes.dto.asignacionMaestro.AsignacionMaestroSalidaDto;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.maestro.MaestroSalidaDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.service.AsignacionMaestroService;
import soft.notes.service.GradoService;
import soft.notes.service.MaestroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class GestionAsignacionesPanel extends JPanel {

    private final AsignacionMaestroService asignacionService;
    private final MaestroService maestroService;
    private final GradoService gradoService;

    private JTable tablaAsignaciones;
    private DefaultTableModel modeloTabla;

    private JComboBox<ComboItem> cbGrados;
    private JComboBox<ComboItem> cbMaterias;

    @Autowired
    public GestionAsignacionesPanel(AsignacionMaestroService asignacionService,
                                    MaestroService maestroService,
                                    GradoService gradoService) {
        this.asignacionService = asignacionService;
        this.maestroService = maestroService;
        this.gradoService = gradoService;

        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        // --- BARRA SUPERIOR ---
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(Color.WHITE);

        JButton btnNuevo = crearBoton("Nueva Asignación", new Color(0, 150, 136));
        JButton btnEliminar = crearBoton("Eliminar", new Color(211, 47, 47));
        JButton btnRefrescar = crearBoton("Refrescar Tabla", new Color(33, 150, 243));

        btnNuevo.addActionListener(e -> abrirFormularioCreacion());
        btnEliminar.addActionListener(e -> eliminarAsignacionSeleccionada());
        btnRefrescar.addActionListener(e -> cargarDatosTabla());

        barra.add(btnNuevo);
        barra.add(btnEliminar);
        barra.add(btnRefrescar);
        add(barra, BorderLayout.NORTH);

        // --- TABLA ---
        String[] cols = {"ID", "Maestro", "Grado", "Materia Asignada"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaAsignaciones = new JTable(modeloTabla);
        tablaAsignaciones.setRowHeight(25);
        tablaAsignaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(tablaAsignaciones), BorderLayout.CENTER);
    }

    public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<AsignacionMaestroSalidaDto> lista = asignacionService.obtenerAsignaciones();
        for (AsignacionMaestroSalidaDto a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdAsignacion(),
                a.getMaestro().getUsuario().getNombre() + " " + a.getMaestro().getUsuario().getApellido(),
                a.getGrado().getNombreGrado() + " " + a.getGrado().getSeccion(),
                a.getMateria().getNombre()
            });
        }
    }

    private void abrirFormularioCreacion() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Asignar Carga Académica", true);
        d.setSize(450, 350);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. MAESTRO
        JComboBox<ComboItem> cbMaestros = new JComboBox<>();
        List<MaestroSalidaDto> maestros = maestroService.obtenerMaestros();
        for (MaestroSalidaDto m : maestros) {
            cbMaestros.addItem(new ComboItem(m.getIdMaestro(), m.getUsuario().getNombre() + " " + m.getUsuario().getApellido()));
        }

        // 2. GRADO (Con Listener)
        cbGrados = new JComboBox<>();
        List<GradoSalidaDto> grados = gradoService.obtenerTodosLosGrados();
        for (GradoSalidaDto g : grados) {
            cbGrados.addItem(new ComboItem(g.getIdGrado(), g.getNombreGrado() + " - " + g.getSeccion()));
        }

        // 3. MATERIA (Empieza vacía y deshabilitada)
        cbMaterias = new JComboBox<>();
        cbMaterias.setEnabled(false);

        // --- EVENTO MÁGICO: Cuando cambie el Grado, cargar sus materias ---
        cbGrados.addActionListener(e -> actualizarComboMaterias());

        // Agregar al UI
        agregarCampo(d, "1. Seleccione Maestro:", cbMaestros, gbc, 0);
        agregarCampo(d, "2. Seleccione Grado:", cbGrados, gbc, 1);
        agregarCampo(d, "3. Seleccione Materia del Pensum:", cbMaterias, gbc, 2);

        JButton btnGuardar = new JButton("Asignar Curso");
        btnGuardar.setBackground(new Color(0, 150, 136));
        btnGuardar.setForeground(Color.WHITE);

        btnGuardar.addActionListener(e -> {
            try {
                ComboItem mSel = (ComboItem) cbMaestros.getSelectedItem();
                ComboItem gSel = (ComboItem) cbGrados.getSelectedItem();
                ComboItem matSel = (ComboItem) cbMaterias.getSelectedItem();

                if (mSel == null || gSel == null || matSel == null) {
                    JOptionPane.showMessageDialog(d, "Debe completar todos los campos");
                    return;
                }

                AsignacionMaestroRegistroDto dto = new AsignacionMaestroRegistroDto();
                dto.setIdMaestro(mSel.id);
                dto.setIdGrado(gSel.id);
                dto.setIdMateria(matSel.id);

                asignacionService.guardarAsignacion(dto);
                JOptionPane.showMessageDialog(d, "Asignación exitosa.");
                cargarDatosTabla();
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });

        gbc.gridy = 3; gbc.gridwidth = 2;
        d.add(btnGuardar, gbc);

        // Disparar la carga inicial si hay grados
        if (cbGrados.getItemCount() > 0) {
            cbGrados.setSelectedIndex(0);
            // Esto dispara el ActionListener automáticamente y carga las materias del primer grado
        }

        d.setVisible(true);
    }

    // Método que busca las materias del grado seleccionado
    private void actualizarComboMaterias() {
        cbMaterias.removeAllItems();
        ComboItem gradoSel = (ComboItem) cbGrados.getSelectedItem();

        if (gradoSel == null) {
            cbMaterias.setEnabled(false);
            return;
        }

        try {
            // Llamamos al nuevo método del servicio
            List<MateriaSalidaDto> materiasDelGrado = gradoService.obtenerMateriasPorGrado(gradoSel.id);

            if (materiasDelGrado.isEmpty()) {
                cbMaterias.addItem(new ComboItem(null, "--- Este grado no tiene materias ---"));
                cbMaterias.setEnabled(false);
            } else {
                for (MateriaSalidaDto m : materiasDelGrado) {
                    cbMaterias.addItem(new ComboItem(m.getIdMateria(), m.getNombre()));
                }
                cbMaterias.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void eliminarAsignacionSeleccionada() {
        int fila = tablaAsignaciones.getSelectedRow();
        if (fila == -1) return;
        Integer id = (Integer) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar asignación?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
            asignacionService.eliminarAsignacion(id);
            cargarDatosTabla();
        }
    }

    private void agregarCampo(JDialog d, String l, JComponent c, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        d.add(new JLabel(l), gbc);
        gbc.gridx = 1; d.add(c, gbc);
    }

    private JButton crearBoton(String t, Color c) {
        JButton b = new JButton(t); b.setBackground(c); b.setForeground(Color.WHITE);
        return b;
    }

    private record ComboItem(Integer id, String texto) {
        @Override public String toString() { return texto; }
    }
}