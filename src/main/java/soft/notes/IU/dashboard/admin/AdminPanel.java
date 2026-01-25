package soft.notes.IU.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class AdminPanel extends JPanel {

    // Paneles inyectados (Ahora son 6)
    private final GestionGradosPanel gestionGradosPanel;
    private final GestionMateriasPanel gestionMateriasPanel;
    private final GestionAlumnosPanel gestionAlumnosPanel;
    private final GestionMaestrosPanel gestionMaestrosPanel;
    private final GestionAsignacionesPanel gestionAsignacionesPanel;
    private final GestionInscripcionesPanel gestionInscripcionesPanel;

    @Autowired
    public AdminPanel(GestionGradosPanel gestionGradosPanel,
                      GestionMateriasPanel gestionMateriasPanel,
                      GestionAlumnosPanel gestionAlumnosPanel,
                      GestionMaestrosPanel gestionMaestrosPanel,
                      GestionAsignacionesPanel gestionAsignacionesPanel,
                      GestionInscripcionesPanel gestionInscripcionesPanel) {

        this.gestionGradosPanel = gestionGradosPanel;
        this.gestionMateriasPanel = gestionMateriasPanel;
        this.gestionAlumnosPanel = gestionAlumnosPanel;
        this.gestionMaestrosPanel = gestionMaestrosPanel;
        this.gestionAsignacionesPanel = gestionAsignacionesPanel;
        this.gestionInscripcionesPanel = gestionInscripcionesPanel;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // --- ORDEN LÓGICO DE CREACIÓN ---
        // 1. Primero creas Grados y Materias (Catálogos)
        tabbedPane.addTab("1. Grados", gestionGradosPanel);
        tabbedPane.addTab("2. Materias", gestionMateriasPanel);

        // 2. Luego creas Personas
        tabbedPane.addTab("3. Alumnos", gestionAlumnosPanel);
        tabbedPane.addTab("4. Maestros", gestionMaestrosPanel);

        // 3. Finalmente Asignas
        tabbedPane.addTab("5. Asignar Prof. (Horario)", gestionAsignacionesPanel);
        tabbedPane.addTab("6. Inscribir Alumnos", gestionInscripcionesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // --- LISTENER DE CARGA ---
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: gestionGradosPanel.cargarDatosTabla(); break;
                case 1: gestionMateriasPanel.cargarDatosTabla(); break;
                case 2: gestionAlumnosPanel.cargarDatosTabla(); break;
                case 3: gestionMaestrosPanel.cargarDatosTabla(); break;
                case 4: gestionAsignacionesPanel.cargarDatosTabla(); break;
                case 5: gestionInscripcionesPanel.cargarCombos(); break;
            }
        });
    }

    public void inicializarDatos() {
        gestionGradosPanel.cargarDatosTabla();
    }
}