/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.interfaz;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import synapselogic.dominio.Neurona;
import synapselogic.dominio.Neurotransmisor;
import synapselogic.dominio.Sinapsis;
import synapselogic.estructuras.GrafoSinaptico;
import synapselogic.estructuras.TablaHashNeurotransmisores;
import synapselogic.servicios.GestorArchivosCSV;
import synapselogic.servicios.MotorAnalisisSinaptico;
import synapselogic.servicios.SimuladorRedSinaptica;

/**
 * Ventana principal de SynapseLogic.
 * Integra carga de archivos, visualizacion, analisis BFS/DFS/Dijkstra y simulacion.
 */
public class InterfazUsuario extends JFrame {

    private GrafoSinaptico grafo;
    private TablaHashNeurotransmisores tabla;
    private GestorArchivosCSV gestorCSV;
    private MotorAnalisisSinaptico motor;
    private SimuladorRedSinaptica simulador;
    private VisualizadorGrafo visualizador;

    private JLabel lblRedCargada;
    private JLabel lblDiccionarioCargado;
    private JLabel lblNeuronas;
    private JLabel lblSinapsis;
    private JLabel lblCambios;
    private JLabel lblNeurotransmisores;

    private JTextField txtFuente;
    private JTextArea txtResultadoAnalisis;

    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JTextArea txtResultadoRuta;

    private JTextArea txtResumenRed;

    private List<Neurona> ultimaRutaCalculada;
    private List<String> ultimasZonasAisladas;

    /**
     * Crea la interfaz y los servicios de la aplicacion.
     */
    public InterfazUsuario() {
        grafo = new GrafoSinaptico();
        tabla = new TablaHashNeurotransmisores();
        gestorCSV = new GestorArchivosCSV();
        motor = new MotorAnalisisSinaptico();
        simulador = new SimuladorRedSinaptica();
        visualizador = new VisualizadorGrafo();

        ultimaRutaCalculada = new ArrayList<>();
        ultimasZonasAisladas = new ArrayList<>();
    }

    /**
     * Inicializa y muestra la ventana principal.
     */
    public void iniciar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Si no se puede aplicar el look and feel del sistema, se usa el valor por defecto de Swing.
        }

        setTitle("SynapseLogic - Analisis de Conectividad y Transmision Neuronal");
        setSize(1250, 820);
        setMinimumSize(new Dimension(1050, 680));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                salir();
            }
        });

        actualizarEstado();
        setVisible(true);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel titulo = new JLabel("SynapseLogic");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Grafo dirigido, tabla hash, BFS, DFS, Dijkstra y simulacion de fatiga");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel titulos = new JPanel(new BorderLayout());
        titulos.add(titulo, BorderLayout.NORTH);
        titulos.add(subtitulo, BorderLayout.SOUTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAyuda = new JButton("Ayuda");
        JButton btnGuardar = new JButton("Guardar Red");
        JButton btnLimpiar = new JButton("Limpiar resaltado");
        JButton btnSalir = new JButton("Salir");

        btnAyuda.addActionListener(e -> mostrarAyuda());
        btnGuardar.addActionListener(e -> guardarRed());
        btnLimpiar.addActionListener(e -> limpiarResaltados());
        btnSalir.addActionListener(e -> salir());

        botones.add(btnAyuda);
        botones.add(btnGuardar);
        botones.add(btnLimpiar);
        botones.add(btnSalir);

        panel.add(titulos, BorderLayout.WEST);
        panel.add(botones, BorderLayout.EAST);

        return panel;
    }

    private JSplitPane crearPanelPrincipal() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.24);
        split.setLeftComponent(crearPanelIzquierdo());
        split.setRightComponent(crearPanelCentral());
        split.setDividerLocation(310);
        return split;
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(320, 500));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Datos y configuracion"));

        JButton btnCargarRed = new JButton("Cargar Red CSV");
        JButton btnCargarDiccionario = new JButton("Cargar Diccionario CSV");
        JButton btnAgregarNeurona = new JButton("Agregar Neurona");
        JButton btnEliminarNeurona = new JButton("Eliminar Neurona");
        JButton btnAgregarSinapsis = new JButton("Agregar Sinapsis");
        JButton btnEliminarSinapsis = new JButton("Eliminar Sinapsis");
        JButton btnAgregarNeurotransmisor = new JButton("Agregar Neurotransmisor");
        JButton btnFatiga = new JButton("Aplicar Fatiga");
        JButton btnVerResumen = new JButton("Actualizar Resumen");

        configurarBotonPanel(btnCargarRed);
        configurarBotonPanel(btnCargarDiccionario);
        configurarBotonPanel(btnAgregarNeurona);
        configurarBotonPanel(btnEliminarNeurona);
        configurarBotonPanel(btnAgregarSinapsis);
        configurarBotonPanel(btnEliminarSinapsis);
        configurarBotonPanel(btnAgregarNeurotransmisor);
        configurarBotonPanel(btnFatiga);
        configurarBotonPanel(btnVerResumen);

        lblRedCargada = new JLabel("Red: no cargada");
        lblDiccionarioCargado = new JLabel("Diccionario: no cargado");
        lblNeuronas = new JLabel("Neuronas: 0");
        lblSinapsis = new JLabel("Sinapsis: 0");
        lblNeurotransmisores = new JLabel("Neurotransmisores: 0");
        lblCambios = new JLabel("Cambios pendientes: no");

        btnCargarRed.addActionListener(e -> cargarArchivoRed());
        btnCargarDiccionario.addActionListener(e -> cargarArchivoDiccionario());
        btnAgregarNeurona.addActionListener(e -> agregarNeurona());
        btnEliminarNeurona.addActionListener(e -> eliminarNeurona());
        btnAgregarSinapsis.addActionListener(e -> agregarSinapsis());
        btnEliminarSinapsis.addActionListener(e -> eliminarSinapsis());
        btnAgregarNeurotransmisor.addActionListener(e -> agregarNeurotransmisor());
        btnFatiga.addActionListener(e -> aplicarFatiga());
        btnVerResumen.addActionListener(e -> actualizarResumenRed());

        panel.add(btnCargarRed);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnCargarDiccionario);
        panel.add(Box.createVerticalStrut(15));

        panel.add(lblRedCargada);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblDiccionarioCargado);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblNeuronas);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblSinapsis);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblNeurotransmisores);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblCambios);

        panel.add(Box.createVerticalStrut(20));
        panel.add(btnAgregarNeurona);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnEliminarNeurona);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnAgregarSinapsis);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnEliminarSinapsis);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnAgregarNeurotransmisor);

        panel.add(Box.createVerticalStrut(20));
        panel.add(btnFatiga);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnVerResumen);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Grafo de transmision sinaptica"));

        txtResumenRed = new JTextArea(6, 40);
        txtResumenRed.setEditable(false);
        txtResumenRed.setLineWrap(true);
        txtResumenRed.setWrapStyleWord(true);

        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.78);
        splitVertical.setTopComponent(visualizador);
        splitVertical.setBottomComponent(new JScrollPane(txtResumenRed));
        splitVertical.setDividerLocation(520);

        panel.add(splitVertical, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        panel.add(crearPanelAnalisis());
        panel.add(crearPanelRutaRapida());

        return panel;
    }

    private JPanel crearPanelAnalisis() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Analisis de conectividad"));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtFuente = new JTextField(8);
        JButton btnBFS = new JButton("BFS");
        JButton btnDFS = new JButton("DFS");
        JButton btnVerGrafo = new JButton("Ver aisladas");

        btnBFS.addActionListener(e -> ejecutarBFS());
        btnDFS.addActionListener(e -> ejecutarDFS());
        btnVerGrafo.addActionListener(e -> verZonasAisladasEnGrafo());

        controles.add(new JLabel("Fuente:"));
        controles.add(txtFuente);
        controles.add(btnBFS);
        controles.add(btnDFS);
        controles.add(btnVerGrafo);

        txtResultadoAnalisis = new JTextArea(7, 35);
        txtResultadoAnalisis.setEditable(false);
        txtResultadoAnalisis.setLineWrap(true);
        txtResultadoAnalisis.setWrapStyleWord(true);

        panel.add(controles, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtResultadoAnalisis), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelRutaRapida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ruta de mayor activacion / Dijkstra"));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtOrigen = new JTextField(8);
        txtDestino = new JTextField(8);
        JButton btnDijkstra = new JButton("Dijkstra");
        JButton btnVerGrafo = new JButton("Ver ruta");

        btnDijkstra.addActionListener(e -> ejecutarDijkstra());
        btnVerGrafo.addActionListener(e -> verRutaEnGrafo());

        controles.add(new JLabel("Origen:"));
        controles.add(txtOrigen);
        controles.add(new JLabel("Destino:"));
        controles.add(txtDestino);
        controles.add(btnDijkstra);
        controles.add(btnVerGrafo);

        txtResultadoRuta = new JTextArea(7, 35);
        txtResultadoRuta.setEditable(false);
        txtResultadoRuta.setLineWrap(true);
        txtResultadoRuta.setWrapStyleWord(true);

        panel.add(controles, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtResultadoRuta), BorderLayout.CENTER);

        return panel;
    }

    private void configurarBotonPanel(JButton boton) {
        boton.setMaximumSize(new Dimension(275, 30));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Permite cargar un archivo CSV de red mediante JFileChooser.
     */
    public void cargarArchivoRed() {
        if (!grafo.estaVacio()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "Ya existe una red en memoria. Si tiene cambios, guardelos antes de reemplazarla.\n¿Desea cargar otra red?",
                    "Reemplazar red actual",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JFileChooser chooser = crearChooserCSV();
        int resultado = chooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();

            try {
                gestorCSV.cargarRed(archivo.getAbsolutePath(), grafo);
                visualizador.mostrarGrafo(grafo);
                lblRedCargada.setText("Red: " + archivo.getName());
                ultimaRutaCalculada.clear();
                ultimasZonasAisladas.clear();
                txtResultadoAnalisis.setText("");
                txtResultadoRuta.setText("");
                actualizarEstado();
                actualizarResumenRed();
                mostrarMensaje("Red cargada correctamente.");
            } catch (Exception ex) {
                mostrarMensaje("Error al cargar red: " + ex.getMessage());
            }
        }
    }

    /**
     * Permite cargar un diccionario de neurotransmisores mediante JFileChooser.
     */
    public void cargarArchivoDiccionario() {
        JFileChooser chooser = crearChooserCSV();
        int resultado = chooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();

            try {
                gestorCSV.cargarDiccionario(archivo.getAbsolutePath(), tabla);
                lblDiccionarioCargado.setText("Diccionario: " + archivo.getName());
                actualizarEstado();
                actualizarResumenRed();
                mostrarMensaje("Diccionario cargado correctamente.");
            } catch (Exception ex) {
                mostrarMensaje("Error al cargar diccionario: " + ex.getMessage());
            }
        }
    }

    /**
     * Ejecuta BFS desde la neurona fuente indicada.
     */
    public void ejecutarBFS() {
        String fuente = txtFuente.getText().trim();

        if (!validarGrafoCargado()) {
            return;
        }

        if (!validarFuente(fuente)) {
            return;
        }

        List<Neurona> aisladas = motor.detectarZonasAisladasBFS(grafo, fuente);
        mostrarResultadoAisladas("BFS", fuente, aisladas);
    }

    /**
     * Ejecuta DFS desde la neurona fuente indicada.
     */
    public void ejecutarDFS() {
        String fuente = txtFuente.getText().trim();

        if (!validarGrafoCargado()) {
            return;
        }

        if (!validarFuente(fuente)) {
            return;
        }

        List<Neurona> aisladas = motor.detectarZonasAisladasDFS(grafo, fuente);
        mostrarResultadoAisladas("DFS", fuente, aisladas);
    }

    private boolean validarFuente(String fuente) {
        if (fuente.isEmpty()) {
            mostrarMensaje("Debe ingresar una neurona fuente.");
            return false;
        }

        if (grafo.obtenerNeurona(fuente) == null) {
            mostrarMensaje("La neurona fuente no existe en el grafo.");
            return false;
        }

        return true;
    }

    private void mostrarResultadoAisladas(String algoritmo, String fuente, List<Neurona> aisladas) {
        ultimasZonasAisladas.clear();
        ultimaRutaCalculada.clear();

        txtResultadoAnalisis.setText("Resultado " + algoritmo + "\n");
        txtResultadoAnalisis.append("Fuente: " + fuente + "\n");
        txtResultadoAnalisis.append("Zonas aisladas: " + aisladas.size() + "\n");

        if (aisladas.isEmpty()) {
            txtResultadoAnalisis.append("No hay neuronas aisladas desde la fuente indicada.\n");
        } else {
            for (Neurona n : aisladas) {
                txtResultadoAnalisis.append("- Neurona " + n.getId() + "\n");
                ultimasZonasAisladas.add(n.getId());
            }
        }

        boolean fuertementeConexa = motor.esFuertementeConexa(grafo);
        txtResultadoAnalisis.append("\nRed fuertemente conexa: " + (fuertementeConexa ? "si" : "no"));
        visualizador.resaltarZonasAisladas(ultimasZonasAisladas);
    }

    /**
     * Ejecuta Dijkstra entre origen y destino.
     */
    public void ejecutarDijkstra() {
        String origen = txtOrigen.getText().trim();
        String destino = txtDestino.getText().trim();

        if (!validarGrafoCargado()) {
            return;
        }

        if (!validarDiccionarioCargado()) {
            return;
        }

        if (origen.isEmpty() || destino.isEmpty()) {
            mostrarMensaje("Debe ingresar origen y destino.");
            return;
        }

        if (grafo.obtenerNeurona(origen) == null) {
            mostrarMensaje("La neurona origen no existe en el grafo.");
            return;
        }

        if (grafo.obtenerNeurona(destino) == null) {
            mostrarMensaje("La neurona destino no existe en el grafo.");
            return;
        }

        List<Neurona> ruta = motor.calcularRutaMasRapida(grafo, tabla, origen, destino);

        ultimaRutaCalculada.clear();
        ultimasZonasAisladas.clear();
        txtResultadoRuta.setText("Resultado Dijkstra\n");

        if (ruta == null || ruta.isEmpty()) {
            txtResultadoRuta.append("No existe ruta valida entre " + origen + " y " + destino
                    + ". Revise tambien que los neurotransmisores de las sinapsis existan en el diccionario.");
            visualizador.limpiarResaltados();
            return;
        }

        ultimaRutaCalculada.addAll(ruta);

        txtResultadoRuta.append("Ruta mas rapida:\n");

        for (int i = 0; i < ruta.size(); i++) {
            txtResultadoRuta.append(ruta.get(i).getId());

            if (i < ruta.size() - 1) {
                txtResultadoRuta.append(" -> ");
            }
        }

        double tiempo = motor.calcularTiempoRuta(grafo, tabla, ruta);
        txtResultadoRuta.append("\nTiempo total estimado: " + formatear(tiempo));
        txtResultadoRuta.append("\n\nDetalle de aristas:\n");

        for (int i = 0; i < ruta.size() - 1; i++) {
            Sinapsis s = grafo.obtenerSinapsis(ruta.get(i).getId(), ruta.get(i + 1).getId());
            if (s != null) {
                txtResultadoRuta.append("- " + s.getOrigenId() + " -> " + s.getDestinoId()
                        + " | d=" + formatear(s.getDistancia())
                        + " | nt=" + s.getIdNeurotransmisor()
                        + " | k=" + formatear(s.getCoeficienteEficiencia())
                        + " | peso=" + formatear(motor.calcularPeso(s, tabla)) + "\n");
            }
        }

        visualizador.resaltarRuta(ultimaRutaCalculada);
    }

    /**
     * Agrega una neurona al grafo.
     */
    public void agregarNeurona() {
        String id = JOptionPane.showInputDialog(this, "Ingrese ID de la nueva neurona:");

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        id = id.trim();

        if (grafo.obtenerNeurona(id) != null) {
            mostrarMensaje("Ya existe una neurona con ese ID.");
            return;
        }

        simulador.agregarNeurona(grafo, new Neurona(id));
        gestorCSV.marcarCambiosPendientes();
        visualizador.mostrarGrafo(grafo);
        actualizarEstado();
        actualizarResumenRed();
        mostrarMensaje("Neurona agregada correctamente.");
    }

    /**
     * Elimina una neurona del grafo.
     */
    public void eliminarNeurona() {
        String id = JOptionPane.showInputDialog(this, "Ingrese ID de la neurona a eliminar:");

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        id = id.trim();

        if (grafo.obtenerNeurona(id) == null) {
            mostrarMensaje("No existe una neurona con ese ID.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "Se eliminara la neurona " + id + " y todas sus sinapsis entrantes y salientes. ¿Continuar?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        simulador.eliminarNeurona(grafo, id);
        gestorCSV.marcarCambiosPendientes();
        visualizador.mostrarGrafo(grafo);
        actualizarEstado();
        actualizarResumenRed();
        mostrarMensaje("Neurona eliminada correctamente.");
    }

    private void agregarSinapsis() {
        JTextField txtOrigenSinapsis = new JTextField();
        JTextField txtDestinoSinapsis = new JTextField();
        JTextField txtDistancia = new JTextField();
        JTextField txtNeurotransmisor = new JTextField();
        JTextField txtCoeficiente = new JTextField("1");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Origen:"));
        panel.add(txtOrigenSinapsis);
        panel.add(new JLabel("Destino:"));
        panel.add(txtDestinoSinapsis);
        panel.add(new JLabel("Distancia:"));
        panel.add(txtDistancia);
        panel.add(new JLabel("ID Neurotransmisor:"));
        panel.add(txtNeurotransmisor);
        panel.add(new JLabel("Coeficiente k:"));
        panel.add(txtCoeficiente);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Agregar Sinapsis",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String origen = txtOrigenSinapsis.getText().trim();
            String destino = txtDestinoSinapsis.getText().trim();
            double distancia = Double.parseDouble(txtDistancia.getText().trim().replace(",", "."));
            String idNeuro = txtNeurotransmisor.getText().trim();
            double k = Double.parseDouble(txtCoeficiente.getText().trim().replace(",", "."));

            if (origen.isEmpty() || destino.isEmpty() || idNeuro.isEmpty()) {
                mostrarMensaje("Origen, destino y neurotransmisor son obligatorios.");
                return;
            }

            if (distancia < 0 || k <= 0) {
                mostrarMensaje("La distancia debe ser mayor o igual a cero y k debe ser mayor que cero.");
                return;
            }

            if (tabla.getCantidad() > 0 && !tabla.contiene(idNeuro)) {
                int continuar = JOptionPane.showConfirmDialog(this,
                        "El neurotransmisor " + idNeuro + " no existe en el diccionario cargado.\n"
                                + "La sinapsis se puede agregar, pero Dijkstra no la usara hasta que exista el neurotransmisor.\n¿Desea continuar?",
                        "Neurotransmisor no encontrado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (continuar != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Sinapsis sinapsis = new Sinapsis(origen, destino, distancia, idNeuro, k);
            simulador.agregarSinapsis(grafo, sinapsis);

            gestorCSV.marcarCambiosPendientes();
            visualizador.mostrarGrafo(grafo);
            actualizarEstado();
            actualizarResumenRed();
            mostrarMensaje("Sinapsis agregada correctamente.");

        } catch (NumberFormatException ex) {
            mostrarMensaje("Distancia y coeficiente k deben ser numeros validos.");
        }
    }

    private void eliminarSinapsis() {
        JTextField txtOrigenSinapsis = new JTextField();
        JTextField txtDestinoSinapsis = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Origen:"));
        panel.add(txtOrigenSinapsis);
        panel.add(new JLabel("Destino:"));
        panel.add(txtDestinoSinapsis);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Eliminar Sinapsis",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String origen = txtOrigenSinapsis.getText().trim();
        String destino = txtDestinoSinapsis.getText().trim();

        if (origen.isEmpty() || destino.isEmpty()) {
            mostrarMensaje("Debe indicar origen y destino.");
            return;
        }

        if (grafo.obtenerSinapsis(origen, destino) == null) {
            mostrarMensaje("No existe una sinapsis desde " + origen + " hacia " + destino + ".");
            return;
        }

        simulador.eliminarSinapsis(grafo, origen, destino);
        gestorCSV.marcarCambiosPendientes();
        visualizador.mostrarGrafo(grafo);
        actualizarEstado();
        actualizarResumenRed();
        mostrarMensaje("Sinapsis eliminada correctamente.");
    }

    private void agregarNeurotransmisor() {
        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtEfecto = new JTextField();
        JTextField txtVelocidad = new JTextField();
        JTextField txtDescripcion = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("ID:"));
        panel.add(txtId);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Efecto:"));
        panel.add(txtEfecto);
        panel.add(new JLabel("Velocidad:"));
        panel.add(txtVelocidad);
        panel.add(new JLabel("Descripcion:"));
        panel.add(txtDescripcion);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Agregar Neurotransmisor",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String efecto = txtEfecto.getText().trim();
            double velocidad = Double.parseDouble(txtVelocidad.getText().trim().replace(",", "."));
            String descripcion = txtDescripcion.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || efecto.isEmpty()) {
                mostrarMensaje("ID, nombre y efecto son obligatorios.");
                return;
            }

            if (velocidad <= 0) {
                mostrarMensaje("La velocidad debe ser mayor que cero.");
                return;
            }

            Neurotransmisor neurotransmisor = new Neurotransmisor(
                    id,
                    nombre,
                    efecto,
                    velocidad,
                    descripcion
            );

            tabla.insertar(neurotransmisor);
            actualizarEstado();
            actualizarResumenRed();
            mostrarMensaje("Neurotransmisor agregado correctamente.");

        } catch (NumberFormatException ex) {
            mostrarMensaje("La velocidad debe ser un numero valido.");
        }
    }

    private void aplicarFatiga() {
        if (!validarGrafoCargado()) {
            return;
        }

        simulador.aplicarFatiga(grafo);
        gestorCSV.marcarCambiosPendientes();
        visualizador.mostrarGrafo(grafo);
        actualizarEstado();
        actualizarResumenRed();
        mostrarMensaje("Fatiga aplicada. Los coeficientes k fueron multiplicados por 1.2.");
    }

    private void guardarRed() {
        if (!validarGrafoCargado()) {
            return;
        }

        JFileChooser chooser = crearChooserCSV();
        int resultado = chooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = asegurarExtensionCSV(chooser.getSelectedFile());

            try {
                gestorCSV.guardarRed(archivo.getAbsolutePath(), grafo);
                actualizarEstado();
                mostrarMensaje("Red guardada correctamente.");
            } catch (Exception ex) {
                mostrarMensaje("Error al guardar red: " + ex.getMessage());
            }
        }
    }

    private JFileChooser crearChooserCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        return chooser;
    }

    private File asegurarExtensionCSV(File archivo) {
        if (archivo == null) {
            return null;
        }

        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".csv")) {
            return new File(ruta + ".csv");
        }
        return archivo;
    }

    private void mostrarAyuda() {
        String mensaje =
                "SynapseLogic\n\n" +
                "1. Cargue primero la red sinaptica desde un archivo CSV.\n" +
                "2. Cargue el diccionario de neurotransmisores desde CSV.\n" +
                "3. Use BFS o DFS para detectar zonas aisladas desde una neurona fuente.\n" +
                "4. Use Dijkstra para calcular la ruta mas rapida entre dos neuronas.\n" +
                "5. Use Aplicar Fatiga para multiplicar los coeficientes k por 1.2.\n" +
                "6. Puede agregar o eliminar neuronas y sinapsis.\n" +
                "7. Guarde la red si realizo modificaciones.\n\n" +
                "Formato de red:\n" +
                "origen,destino,distancia,ID_Neurotransmisor,coheficiente_eficiencia_sinaptica\n\n" +
                "Formato de diccionario:\n" +
                "id,nombre,efecto,velocidad,descripcion\n\n" +
                "En el grafo puede arrastrar los nodos con el mouse para reorganizar la visualizacion.";

        JOptionPane.showMessageDialog(this, mensaje, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void salir() {
        if (gestorCSV.hayCambiosPendientes()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "Hay cambios pendientes. ¿Desea salir sin guardar?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        dispose();
        System.exit(0);
    }

    private void actualizarEstado() {
        lblNeuronas.setText("Neuronas: " + grafo.obtenerTodasLasNeuronas().size());
        lblSinapsis.setText("Sinapsis: " + grafo.obtenerTodasLasSinapsis().size());
        lblNeurotransmisores.setText("Neurotransmisores: " + tabla.getCantidad());
        lblCambios.setText("Cambios pendientes: " + (gestorCSV.hayCambiosPendientes() ? "si" : "no"));
    }

    private void actualizarResumenRed() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resumen de la red\n");
        sb.append("Neuronas: ").append(grafo.obtenerTodasLasNeuronas().size()).append("\n");
        sb.append("Sinapsis: ").append(grafo.obtenerTodasLasSinapsis().size()).append("\n");
        sb.append("Neurotransmisores en diccionario: ").append(tabla.getCantidad()).append("\n");
        sb.append("Fuertemente conexa: ").append(grafo.estaVacio() ? "no aplica" : (motor.esFuertementeConexa(grafo) ? "si" : "no")).append("\n\n");

        sb.append("Sinapsis registradas:\n");
        if (grafo.obtenerTodasLasSinapsis().isEmpty()) {
            sb.append("- No hay sinapsis registradas.\n");
        } else {
            for (Sinapsis s : grafo.obtenerTodasLasSinapsis()) {
                sb.append("- ").append(s.getOrigenId()).append(" -> ").append(s.getDestinoId())
                        .append(" | d=").append(formatear(s.getDistancia()))
                        .append(" | nt=").append(s.getIdNeurotransmisor())
                        .append(" | k=").append(formatear(s.getCoeficienteEficiencia()));
                if (tabla.getCantidad() > 0 && !tabla.contiene(s.getIdNeurotransmisor())) {
                    sb.append(" | ADVERTENCIA: neurotransmisor no encontrado");
                }
                sb.append("\n");
            }
        }

        txtResumenRed.setText(sb.toString());
        txtResumenRed.setCaretPosition(0);
    }

    private boolean validarGrafoCargado() {
        if (grafo.obtenerTodasLasNeuronas().isEmpty()) {
            mostrarMensaje("Debe cargar o crear una red primero.");
            return false;
        }

        return true;
    }

    private boolean validarDiccionarioCargado() {
        if (tabla.getCantidad() == 0) {
            mostrarMensaje("Debe cargar el diccionario de neurotransmisores primero.");
            return false;
        }

        return true;
    }

    private void verZonasAisladasEnGrafo() {
        if (ultimasZonasAisladas.isEmpty()) {
            mostrarMensaje("No hay zonas aisladas calculadas para resaltar.");
            return;
        }

        visualizador.resaltarZonasAisladas(ultimasZonasAisladas);
    }

    private void verRutaEnGrafo() {
        if (ultimaRutaCalculada.isEmpty()) {
            mostrarMensaje("No hay ruta calculada para resaltar.");
            return;
        }

        visualizador.resaltarRuta(ultimaRutaCalculada);
    }

    private void limpiarResaltados() {
        ultimasZonasAisladas.clear();
        ultimaRutaCalculada.clear();
        visualizador.limpiarResaltados();
    }

    /**
     * Muestra un mensaje modal al usuario.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private String formatear(double valor) {
        if (Double.isInfinite(valor)) {
            return "infinito";
        }
        return String.format(java.util.Locale.US, "%.4f", valor);
    }

    /**
     * Ejecuta la interfaz en el hilo de eventos de Swing.
     * Util para pruebas directas de la ventana.
     *
     * @param args argumentos de linea de comandos no utilizados
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazUsuario interfaz = new InterfazUsuario();
            interfaz.iniciar();
        });
    }
}