package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.materia.MateriaRegistroDto;
import soft.notes.dto.materia.MateriaSalidaDto;
import soft.notes.service.MateriaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class GestionMateriasPanel extends JPanel {

    private final MateriaService materiaService;
    private JTable tablaMaterias;
    private DefaultTableModel modeloTabla;

    @Autowired
    public GestionMateriasPanel(MateriaService materiaService) {
        this.materiaService = materiaService;
        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(Color.WHITE);

        JButton btnNuevo = new JButton("Nueva Materia");
        btnNuevo.setBackground(new Color(0, 150, 136));
        btnNuevo.setForeground(Color.WHITE);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(211, 47, 47));
        btnEliminar.setForeground(Color.WHITE);

        btnNuevo.addActionListener(e -> abrirFormulario());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        barra.add(btnNuevo);
        barra.add(btnEliminar);
        add(barra, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre Materia", "Descripción"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMaterias = new JTable(modeloTabla);
        tablaMaterias.setRowHeight(25);
        add(new JScrollPane(tablaMaterias), BorderLayout.CENTER);
    }

    public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<MateriaSalidaDto> lista = materiaService.obtenerTodasLasMaterias();
        for (MateriaSalidaDto m : lista) {
            modeloTabla.addRow(new Object[]{m.getIdMateria(), m.getNombre(), m.getDescripcion()});
        }
    }

    private void abrirFormulario() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Crear Materia", true);
        d.setSize(350, 250);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(15);
        JTextArea txtDesc = new JTextArea(3, 15);
        txtDesc.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.gridx=0; gbc.gridy=0; d.add(new JLabel("Nombre:"), gbc);
        gbc.gridx=1; d.add(txtNombre, gbc);

        gbc.gridx=0; gbc.gridy=1; d.add(new JLabel("Descripción:"), gbc);
        gbc.gridx=1; d.add(txtDesc, gbc);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                MateriaRegistroDto dto = new MateriaRegistroDto();
                dto.setNombre(txtNombre.getText());
                dto.setDescripcion(txtDesc.getText());

                materiaService.guardarMateria(dto);
                cargarDatosTabla();
                d.dispose();
                JOptionPane.showMessageDialog(this, "Materia creada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });

        gbc.gridy=2; gbc.gridwidth=2; d.add(btnGuardar, gbc);
        d.setVisible(true);
    }

    private void eliminarSeleccionado() {
        int fila = tablaMaterias.getSelectedRow();
        if (fila == -1) return;
        Integer id = (Integer) modeloTabla.getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(this, "¿Borrar materia?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                materiaService.eliminarMateria(id);
                cargarDatosTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}