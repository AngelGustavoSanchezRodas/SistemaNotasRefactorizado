package soft.notes.IU.dashboard.maestro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.asignacionMaestro.AsignacionMaestroSalidaDto;
import soft.notes.dto.calificacion.CalificacionRegistroDto;
import soft.notes.dto.calificacion.CalificacionSalidaDto;
import soft.notes.service.AsignacionMaestroService;
import soft.notes.service.CalificacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MaestroPanel extends JPanel {

    private final AsignacionMaestroService asignacionService;
    private final CalificacionService calificacionService;

    // UI
    private JComboBox<CursoItem> cbCursos;
    private JTable tablaNotas;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;

    // Datos en memoria para saber qué estamos editando
    private List<CalificacionSalidaDto> listaNotasActuales;

    @Autowired
    public MaestroPanel(AsignacionMaestroService asignacionService, CalificacionService calificacionService) {
        this.asignacionService = asignacionService;
        this.calificacionService = calificacionService;

        setLayout(new BorderLayout());
        inicializarUI();
    }

    private void inicializarUI() {
        // --- 1. BARRA SUPERIOR (Selector de Curso) ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.setBackground(new Color(245, 245, 245));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelTop.add(new JLabel("Seleccione Curso a Calificar:"));

        cbCursos = new JComboBox<>();
        cbCursos.setPreferredSize(new Dimension(400, 30));
        panelTop.add(cbCursos);

        JButton btnCargar = new JButton("Cargar Alumnos");
        btnCargar.setBackground(new Color(33, 150, 243));
        btnCargar.setForeground(Color.WHITE);
        btnCargar.addActionListener(e -> cargarAlumnos());
        panelTop.add(btnCargar);

        add(panelTop, BorderLayout.NORTH);

        // --- 2. TABLA CENTRAL (Editable) ---
        // Columnas: ID Oculto, Nombre Alumno, T1, T2, P1, P2, Final, Total
        String[] columnas = {"ID_CALIF", "Alumno", "Tarea 1 (10)", "Tarea 2 (10)", "Parcial 1 (25)", "Parcial 2 (25)", "Final (30)", "TOTAL"};

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo son editables las columnas de notas (2 a 6)
                return column >= 2 && column <= 6;
            }

            // Forzamos que las columnas de notas sean Double
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2) return Double.class;
                return String.class;
            }
        };

        tablaNotas = new JTable(modeloTabla);
        tablaNotas.setRowHeight(30);
        tablaNotas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaNotas.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); // Guardar al hacer clic fuera

        // Ocultar la columna ID_CALIF (índice 0)
        tablaNotas.getColumnModel().getColumn(0).setMinWidth(0);
        tablaNotas.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaNotas.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(tablaNotas), BorderLayout.CENTER);

        // --- 3. BARRA INFERIOR (Guardar) ---
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblEstado = new JLabel("Listo.");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JButton btnGuardar = new JButton("GUARDAR NOTAS");
        btnGuardar.setBackground(new Color(0, 150, 136)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.addActionListener(e -> guardarCambios());

        panelBottom.add(lblEstado);
        panelBottom.add(Box.createHorizontalStrut(20));
        panelBottom.add(btnGuardar);

        add(panelBottom, BorderLayout.SOUTH);
    }

    // --- LÓGICA ---

    public void cargarCursosMaestro(Integer idUsuario) {
        cbCursos.removeAllItems();
        modeloTabla.setRowCount(0);

        List<AsignacionMaestroSalidaDto> cursos = asignacionService.obtenerCursosPorMaestroUsuario(idUsuario);

        if (cursos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tiene cursos asignados.");
            return;
        }

        for (AsignacionMaestroSalidaDto c : cursos) {
            String texto = c.getMateria().getNombre() + " - " + c.getGrado().getNombreGrado() + " " + c.getGrado().getSeccion();
            // Guardamos ID Materia y ID Grado en el objeto
            cbCursos.addItem(new CursoItem(c.getMateria().getIdMateria(), c.getGrado().getIdGrado(), texto));
        }
    }

    private void cargarAlumnos() {
        CursoItem cursoSel = (CursoItem) cbCursos.getSelectedItem();
        if (cursoSel == null) return;

        modeloTabla.setRowCount(0);
        listaNotasActuales = calificacionService.obtenerSabanaDeNotas(cursoSel.idMateria, cursoSel.idGrado);

        if (listaNotasActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay alumnos inscritos en este curso.");
            return;
        }

        for (CalificacionSalidaDto n : listaNotasActuales) {
            double total = (n.getNotaTarea1() != null ? n.getNotaTarea1() : 0) +
                           (n.getNotaTarea2() != null ? n.getNotaTarea2() : 0) +
                           (n.getNotaParcial1() != null ? n.getNotaParcial1() : 0) +
                           (n.getNotaParcial2() != null ? n.getNotaParcial2() : 0) +
                           (n.getNotaFinal() != null ? n.getNotaFinal() : 0);

            Object[] fila = {
                n.getIdCalificacion(),
                n.getIdAlumno().getUsuario().getNombre() + " " + n.getIdAlumno().getUsuario().getApellido(),
                n.getNotaTarea1() != null ? n.getNotaTarea1() : 0.0,
                n.getNotaTarea2() != null ? n.getNotaTarea2() : 0.0,
                n.getNotaParcial1() != null ? n.getNotaParcial1() : 0.0,
                n.getNotaParcial2() != null ? n.getNotaParcial2() : 0.0,
                n.getNotaFinal() != null ? n.getNotaFinal() : 0.0,
                total // Columna Total (No editable)
            };
            modeloTabla.addRow(fila);
        }
        lblEstado.setText("Se cargaron " + listaNotasActuales.size() + " alumnos.");
    }

    private void guardarCambios() {
        if (tablaNotas.isEditing()) {
            tablaNotas.getCellEditor().stopCellEditing();
        }

        CursoItem cursoSel = (CursoItem) cbCursos.getSelectedItem();
        if (cursoSel == null) return;

        try {
            int filas = modeloTabla.getRowCount();
            int guardados = 0;

            for (int i = 0; i < filas; i++) {
                // Recuperar valores de la tabla
                Integer idCalificacion = (Integer) modeloTabla.getValueAt(i, 0);

                // Convertir object a Double con seguridad
                Double t1 = objectToDouble(modeloTabla.getValueAt(i, 2));
                Double t2 = objectToDouble(modeloTabla.getValueAt(i, 3));
                Double p1 = objectToDouble(modeloTabla.getValueAt(i, 4));
                Double p2 = objectToDouble(modeloTabla.getValueAt(i, 5));
                Double fn = objectToDouble(modeloTabla.getValueAt(i, 6));

                // Obtener datos originales para saber IDs
                // Ojo: Esto asume que el orden no cambió. Lo más seguro es buscar en listaNotasActuales por ID
                CalificacionSalidaDto original = buscarEnLista(idCalificacion);

                if (original != null) {
                    CalificacionRegistroDto dto = new CalificacionRegistroDto();
                    dto.setIdAlumno(original.getIdAlumno().getIdAlumno());
                    dto.setIdMateria(cursoSel.idMateria); // Del combo seleccionado

                    dto.setNotaTarea1(t1);
                    dto.setNotaTarea2(t2);
                    dto.setNotaParcial1(p1);
                    dto.setNotaParcial2(p2);
                    dto.setNotaFinal(fn);

                    // Solo si quieres pedir PIN para editar, aquí iría.
                    // Para simplificar, asumiremos que el profe puede editar libremente o pasamos el PIN
                    dto.setPinAdministrativo("1234"); // HARDCODED TEMPORAL PARA PRUEBAS SI EL SERVICE PIDE PIN

                    calificacionService.editarCalificacion(dto);
                    guardados++;
                }
            }

            JOptionPane.showMessageDialog(this, "Se actualizaron las notas de " + guardados + " alumnos.");
            cargarAlumnos(); // Recargar para ver totales calculados

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error guardando: " + ex.getMessage());
        }
    }

    private Double objectToDouble(Object obj) {
        if (obj == null) return 0.0;
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private CalificacionSalidaDto buscarEnLista(Integer idCalificacion) {
        for (CalificacionSalidaDto c : listaNotasActuales) {
            if (c.getIdCalificacion().equals(idCalificacion)) return c;
        }
        return null;
    }

    // Clase auxiliar para el combo
    private record CursoItem(Integer idMateria, Integer idGrado, String texto) {
        @Override public String toString() { return texto; }
    }
}