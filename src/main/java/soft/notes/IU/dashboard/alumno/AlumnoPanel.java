package soft.notes.IU.dashboard.alumno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.calificacion.CalificacionSalidaDto;
import soft.notes.service.CalificacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class AlumnoPanel extends JPanel {

    private final CalificacionService calificacionService;
    private JTable tablaNotas;
    private DefaultTableModel modeloTabla;

    @Autowired
    public AlumnoPanel(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;

        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        // --- TÍTULO ---
        JLabel titulo = new JLabel("Mis Calificaciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // --- TABLA ---
        String[] columnas = {
            "Materia",
            "Tarea 1 (10)",
            "Tarea 2 (10)",
            "Parcial 1 (25)",
            "Parcial 2 (25)",
            "Final (30)",
            "TOTAL (100)"
        };

        // Modelo no editable
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaNotas = new JTable(modeloTabla);
        tablaNotas.setRowHeight(30);
        tablaNotas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaNotas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Alinear columnas de números al centro
        // (Opcional, pero se ve mejor)

        add(new JScrollPane(tablaNotas), BorderLayout.CENTER);

        // Botón Refrescar (Opcional, por si el profe sube nota en ese momento)
        JButton btnRefrescar = new JButton("Actualizar Notas");
        btnRefrescar.addActionListener(e -> {
             // Nota: Necesitamos guardar el ID usuario en algún lado si queremos refrescar manual
             // Por ahora lo dejaremos simple.
             JOptionPane.showMessageDialog(this, "Para actualizar, vuelve a iniciar sesión o cambia de pestaña.");
        });
        add(btnRefrescar, BorderLayout.SOUTH);
    }

    public void cargarNotasAlumno(Integer idUsuario) {
        modeloTabla.setRowCount(0); // Limpiar tabla

        try {
            System.out.println("Cargando notas para usuario ID: " + idUsuario);
            List<CalificacionSalidaDto> notas = calificacionService.obtenerNotasPorUsuario(idUsuario);

            if (notas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aún no tienes cursos inscritos o notas asignadas.");
                return;
            }

            for (CalificacionSalidaDto n : notas) {
                // Manejo de nulos (si el profe no ha calificado, es 0)
                double t1 = n.getNotaTarea1() != null ? n.getNotaTarea1() : 0.0;
                double t2 = n.getNotaTarea2() != null ? n.getNotaTarea2() : 0.0;
                double p1 = n.getNotaParcial1() != null ? n.getNotaParcial1() : 0.0;
                double p2 = n.getNotaParcial2() != null ? n.getNotaParcial2() : 0.0;
                double fn = n.getNotaFinal() != null ? n.getNotaFinal() : 0.0;

                double total = t1 + t2 + p1 + p2 + fn;

                Object[] fila = {
                    n.getIdMateria().getNombre(), // Nombre de la materia
                    t1, t2, p1, p2, fn,
                    total // Columna calculada
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando notas: " + e.getMessage());
        }
    }
}