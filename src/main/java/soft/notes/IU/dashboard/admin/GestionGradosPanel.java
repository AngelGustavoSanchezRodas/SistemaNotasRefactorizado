package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.grado.GradoRegistroDto;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.service.GradoService;
import soft.notes.service.MateriaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class GestionGradosPanel extends JPanel {

    private final GradoService gradoService;
    private final MateriaService materiaService;

    private JTable tablaGrados;
    private DefaultTableModel modeloTabla;

    @Autowired
    public GestionGradosPanel(GradoService gradoService, MateriaService materiaService) {
        this.gradoService = gradoService;
        this.materiaService = materiaService;
        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        // --- BARRA SUPERIOR ---
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(Color.WHITE);

        JButton btnNuevo = new JButton("Nuevo Grado");
        btnNuevo.setBackground(new Color(0, 150, 136));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(211, 47, 47));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);

        btnNuevo.addActionListener(e -> abrirFormulario());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        barra.add(btnNuevo);
        barra.add(btnEliminar);
        add(barra, BorderLayout.NORTH);

        // --- TABLA ---
        String[] cols = {"ID", "Nombre del Grado", "Sección"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaGrados = new JTable(modeloTabla);
        tablaGrados.setRowHeight(25);
        tablaGrados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(tablaGrados), BorderLayout.CENTER);
    }

    public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<GradoSalidaDto> lista = gradoService.obtenerTodosLosGrados();
        for (GradoSalidaDto g : lista) {
            modeloTabla.addRow(new Object[]{g.getIdGrado(), g.getNombreGrado(), g.getSeccion()});
        }
    }

    private void abrirFormulario() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Crear Nuevo Grado", true);
        d.setSize(450, 500); // Más alto para que quepan las materias
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        // --- PANEL PRINCIPAL (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Campos de Texto
        JTextField txtNombre = new JTextField(20);
        JTextField txtSeccion = new JTextField(5);

        gbc.gridx=0; gbc.gridy=0;
        formPanel.add(new JLabel("Nombre Grado (Ej: Primero Primaria):"), gbc);

        gbc.gridx=0; gbc.gridy=1;
        formPanel.add(txtNombre, gbc);

        gbc.gridx=0; gbc.gridy=2;
        formPanel.add(new JLabel("Sección (Ej: A):"), gbc);

        gbc.gridx=0; gbc.gridy=3;
        formPanel.add(txtSeccion, gbc);

        // 2. Título de Materias
        gbc.gridx=0; gbc.gridy=4;
        gbc.insets = new Insets(20, 10, 5, 10);
        JLabel lblMaterias = new JLabel("Seleccione el Pensum (Materias):");
        lblMaterias.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblMaterias, gbc);

        // 3. Lista de Checkboxes (Dinámica)
        JPanel panelCheckboxes = new JPanel();
        panelCheckboxes.setLayout(new BoxLayout(panelCheckboxes, BoxLayout.Y_AXIS));
        panelCheckboxes.setBackground(Color.WHITE);

        List<JCheckBox> checksMaterias = new ArrayList<>();
        List<MateriaSalidaDto> materiasDisponibles = materiaService.obtenerTodasLasMaterias();

        if (materiasDisponibles.isEmpty()) {
            panelCheckboxes.add(new JLabel("No hay materias creadas aún."));
        } else {
            for (MateriaSalidaDto m : materiasDisponibles) {
                JCheckBox chk = new JCheckBox(m.getNombre());
                chk.setBackground(Color.WHITE);
                // Guardamos el ID en el nombre del componente o usamos un mapa,
                // pero por simplicidad usaremos ClientProperty
                chk.putClientProperty("ID_MATERIA", m.getIdMateria());

                checksMaterias.add(chk);
                panelCheckboxes.add(chk);
            }
        }

        JScrollPane scrollMaterias = new JScrollPane(panelCheckboxes);
        scrollMaterias.setPreferredSize(new Dimension(300, 150));
        scrollMaterias.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        gbc.gridx=0; gbc.gridy=5;
        gbc.insets = new Insets(0, 10, 20, 10);
        gbc.weighty = 1.0; // Que se estire
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollMaterias, gbc);

        // --- BOTÓN GUARDAR ---
        JButton btnGuardar = new JButton("Guardar Grado y Pensum");
        btnGuardar.setBackground(new Color(0, 150, 136));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(200, 40));

        btnGuardar.addActionListener(e -> {
            try {
                if(txtNombre.getText().isEmpty() || txtSeccion.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Nombre y Sección son obligatorios");
                    return;
                }

                GradoRegistroDto dto = new GradoRegistroDto();
                dto.setNombreGrado(txtNombre.getText());
                dto.setSeccion(txtSeccion.getText());

                // Recolectar IDs seleccionados
                List<Integer> idsSeleccionados = new ArrayList<>();
                for (JCheckBox chk : checksMaterias) {
                    if (chk.isSelected()) {
                        idsSeleccionados.add((Integer) chk.getClientProperty("ID_MATERIA"));
                    }
                }
                dto.setIdsMaterias(idsSeleccionados);

                gradoService.guardarGrado(dto);
                cargarDatosTabla();
                d.dispose();
                JOptionPane.showMessageDialog(this, "Grado creado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });

        d.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0,0,10,0));
        btnPanel.add(btnGuardar);
        d.add(btnPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private void eliminarSeleccionado() {
        int fila = tablaGrados.getSelectedRow();
        if (fila == -1) return;
        Integer id = (Integer) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Borrar grado?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                gradoService.eliminarGrado(id);
                cargarDatosTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}