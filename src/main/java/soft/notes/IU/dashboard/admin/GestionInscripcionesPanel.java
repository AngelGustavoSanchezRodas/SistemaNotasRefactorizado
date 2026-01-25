package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soft.notes.dto.grado.GradoSalidaDto;
import soft.notes.entities.Alumno;
import soft.notes.repositories.AlumnoRepository;
import soft.notes.service.CalificacionService;
import soft.notes.service.GradoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@Component
public class GestionInscripcionesPanel extends JPanel {

    private final AlumnoRepository alumnoRepository;
    private final GradoService gradoService;
    private final CalificacionService calificacionService;

    // Componentes
    private JTextField txtBuscar;
    private JTable tablaResultados;
    private DefaultTableModel modeloAlumnos;
    private JComboBox<ComboItem> cbGrados;
    private JTextArea areaLog;

    @Autowired
    public GestionInscripcionesPanel(AlumnoRepository alumnoRepository,
                                     GradoService gradoService,
                                     CalificacionService calificacionService) {
        this.alumnoRepository = alumnoRepository;
        this.gradoService = gradoService;
        this.calificacionService = calificacionService;

        setLayout(new BorderLayout(10, 10));
        inicializarUI();
    }

    private void inicializarUI() {
        // --- PANEL IZQUIERDO: BUSCADOR DE ALUMNOS ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("1. Buscar Alumno"));
        panelIzquierdo.setPreferredSize(new Dimension(400, 0));

        // Buscador
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        txtBuscar = new JTextField();
        txtBuscar.putClientProperty("JTextField.placeholderText", "Escribe nombre o apellido...");
        JButton btnBuscar = new JButton("Buscar");

        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);
        panelIzquierdo.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla de resultados
        String[] colAlumnos = {"ID", "Carnet", "Nombre Completo", "Grado Actual"};
        modeloAlumnos = new DefaultTableModel(colAlumnos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaResultados = new JTable(modeloAlumnos);
        panelIzquierdo.add(new JScrollPane(tablaResultados), BorderLayout.CENTER);

        // Eventos de búsqueda
        btnBuscar.addActionListener(e -> buscarAlumnos());
        txtBuscar.addActionListener(e -> buscarAlumnos()); // Enter key
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarAlumnos();
            }
        });

        // --- PANEL DERECHO: SELECCIÓN DE GRADO Y ACCIÓN ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("2. Asignar Grado"));

        JPanel formGrado = new JPanel(new GridLayout(4, 1, 5, 5));
        cbGrados = new JComboBox<>();

        JButton btnInscribir = new JButton("INSCRIBIR ALUMNO");
        btnInscribir.setBackground(new Color(0, 150, 136));
        btnInscribir.setForeground(Color.WHITE);
        btnInscribir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnInscribir.addActionListener(e -> accionInscribir());

        formGrado.add(new JLabel("Seleccione el Grado destino:"));
        formGrado.add(cbGrados);
        formGrado.add(new JLabel("")); // Espacio
        formGrado.add(btnInscribir);

        panelDerecho.add(formGrado, BorderLayout.NORTH);

        // Log
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setBorder(BorderFactory.createTitledBorder("Log de Inscripción"));
        panelDerecho.add(new JScrollPane(areaLog), BorderLayout.CENTER);

        // --- SPLIT PANE ---
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        split.setDividerLocation(450);
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);
    }

    public void cargarCombos() {
        cbGrados.removeAllItems();
        List<GradoSalidaDto> grados = gradoService.obtenerTodosLosGrados();
        for (GradoSalidaDto g : grados) {
            cbGrados.addItem(new ComboItem(g.getIdGrado(), g.getNombreGrado() + " - " + g.getSeccion()));
        }
        buscarAlumnos(); // Carga inicial de todos
    }

    private void buscarAlumnos() {
        String texto = txtBuscar.getText().trim();
        modeloAlumnos.setRowCount(0);

        List<Alumno> resultados;
        if (texto.isEmpty()) {
            resultados = alumnoRepository.findAll();
        } else {
            resultados = alumnoRepository.findByUsuario_NombreContainingIgnoreCaseOrUsuario_ApellidoContainingIgnoreCase(texto, texto);
        }

        for (Alumno a : resultados) {
            String gradoActual = (a.getGrado() != null)
                    ? a.getGrado().getNombreGrado() + " " + a.getGrado().getSeccion()
                    : "--- SIN GRADO ---";

            modeloAlumnos.addRow(new Object[]{
                a.getIdAlumno(),
                a.getCarnet(),
                a.getUsuario().getNombre() + " " + a.getUsuario().getApellido(),
                gradoActual
            });
        }
    }

    private void accionInscribir() {
        int fila = tablaResultados.getSelectedRow();
        ComboItem gradoSel = (ComboItem) cbGrados.getSelectedItem();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un alumno de la tabla de la izquierda.");
            return;
        }
        if (gradoSel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un grado.");
            return;
        }

        Integer idAlumno = (Integer) modeloAlumnos.getValueAt(fila, 0);
        String nombreAlumno = (String) modeloAlumnos.getValueAt(fila, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Inscribir a: " + nombreAlumno + "\nEn el grado: " + gradoSel.texto + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                calificacionService.inscribirAlumnoEnGrado(idAlumno, gradoSel.id);

                areaLog.append( nombreAlumno + " -> " + gradoSel.texto + "\n");
                buscarAlumnos();

            } catch (Exception ex) {
                areaLog.append(" Error: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private record ComboItem(Integer id, String texto) {
        @Override public String toString() { return texto; }
    }
}