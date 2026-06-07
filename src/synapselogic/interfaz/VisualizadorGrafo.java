/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.interfaz;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import synapselogic.dominio.Neurona;
import synapselogic.dominio.Sinapsis;
import synapselogic.estructuras.GrafoSinaptico;

/**
 * Panel Swing que dibuja el grafo sinaptico sin dependencias externas.
 * Permite arrastrar nodos manualmente y resaltar rutas o zonas aisladas.
 */
public class VisualizadorGrafo extends JPanel {

    private static final int RADIO_NODO = 24;
    private static final int MARGEN = 70;

    private GrafoSinaptico grafo;
    private Map<String, Point> posiciones;
    private Set<String> zonasAisladas;
    private List<String> rutaActual;
    private String nodoArrastrado;
    private Point desplazamientoArrastre;

    /**
     * Crea el visualizador del grafo.
     */
    public VisualizadorGrafo() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 500));

        grafo = new GrafoSinaptico();
        posiciones = new HashMap<>();
        zonasAisladas = new HashSet<>();
        rutaActual = new ArrayList<>();

        configurarEventosMouse();
    }

    /**
     * Muestra el grafo indicado y refresca el panel.
     *
     * @param grafoSinaptico grafo a mostrar
     */
    public void mostrarGrafo(GrafoSinaptico grafoSinaptico) {
        if (grafoSinaptico == null) {
            this.grafo = new GrafoSinaptico();
        } else {
            this.grafo = grafoSinaptico;
        }

        limpiarPosicionesEliminadas();
        calcularPosicionesIniciales();
        zonasAisladas.clear();
        rutaActual.clear();
        refrescar();
    }

    /**
     * Resalta las neuronas aisladas detectadas por BFS o DFS.
     *
     * @param ids IDs de neuronas aisladas
     */
    public void resaltarZonasAisladas(List<String> ids) {
        zonasAisladas.clear();
        rutaActual.clear();

        if (ids != null) {
            zonasAisladas.addAll(ids);
        }

        refrescar();
    }

    /**
     * Resalta una ruta de neuronas calculada por Dijkstra.
     *
     * @param ruta lista de neuronas en orden
     */
    public void resaltarRuta(List<Neurona> ruta) {
        rutaActual.clear();
        zonasAisladas.clear();

        if (ruta != null) {
            for (Neurona n : ruta) {
                rutaActual.add(n.getId());
            }
        }

        refrescar();
    }

    /**
     * Limpia resaltados visuales.
     */
    public void limpiarResaltados() {
        zonasAisladas.clear();
        rutaActual.clear();
        refrescar();
    }

    /**
     * Fuerza el repintado del grafo.
     */
    public void refrescar() {
        revalidate();
        repaint();
    }

    private void configurarEventosMouse() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String id = obtenerNodoEnPunto(e.getPoint());
                if (id != null) {
                    nodoArrastrado = id;
                    Point p = posiciones.get(id);
                    desplazamientoArrastre = new Point(e.getX() - p.x, e.getY() - p.y);
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (nodoArrastrado != null) {
                    int x = e.getX() - desplazamientoArrastre.x;
                    int y = e.getY() - desplazamientoArrastre.y;
                    posiciones.put(nodoArrastrado, new Point(x, y));
                    refrescar();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                nodoArrastrado = null;
                desplazamientoArrastre = null;
                setCursor(Cursor.getDefaultCursor());
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private String obtenerNodoEnPunto(Point punto) {
        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            Point p = entry.getValue();
            double distancia = punto.distance(p);
            if (distancia <= RADIO_NODO) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void limpiarPosicionesEliminadas() {
        Set<String> idsActuales = new HashSet<>();
        for (Neurona n : grafo.obtenerTodasLasNeuronas()) {
            idsActuales.add(n.getId());
        }
        posiciones.keySet().removeIf(id -> !idsActuales.contains(id));
    }

    private void calcularPosicionesIniciales() {
        List<Neurona> neuronas = grafo.obtenerTodasLasNeuronas();
        int n = neuronas.size();
        if (n == 0) {
            return;
        }

        int ancho = Math.max(getWidth(), 700);
        int alto = Math.max(getHeight(), 500);
        int centroX = ancho / 2;
        int centroY = alto / 2;
        int radio = Math.max(120, Math.min(ancho, alto) / 2 - MARGEN);

        for (int i = 0; i < n; i++) {
            Neurona neurona = neuronas.get(i);
            if (posiciones.containsKey(neurona.getId())) {
                continue;
            }

            double angulo = (2 * Math.PI * i) / n;
            int x = (int) (centroX + radio * Math.cos(angulo));
            int y = (int) (centroY + radio * Math.sin(angulo));
            posiciones.put(neurona.getId(), new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (grafo == null || grafo.obtenerTodasLasNeuronas().isEmpty()) {
            dibujarMensajeVacio(g2);
            g2.dispose();
            return;
        }

        calcularPosicionesIniciales();
        dibujarSinapsis(g2);
        dibujarNeuronas(g2);
        dibujarLeyenda(g2);

        g2.dispose();
    }

    private void dibujarMensajeVacio(Graphics2D g2) {
        g2.setColor(new Color(90, 90, 90));
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String texto = "Cargue un archivo CSV o agregue neuronas para visualizar el grafo.";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        int y = Math.max(40, getHeight() / 2);
        g2.drawString(texto, x, y);
    }

    private void dibujarSinapsis(Graphics2D g2) {
        for (Sinapsis s : grafo.obtenerTodasLasSinapsis()) {
            Point origen = posiciones.get(s.getOrigenId());
            Point destino = posiciones.get(s.getDestinoId());

            if (origen == null || destino == null) {
                continue;
            }

            boolean resaltada = esAristaDeRuta(s.getOrigenId(), s.getDestinoId());
            if (resaltada) {
                g2.setColor(new Color(25, 130, 60));
                g2.setStroke(new BasicStroke(3.0f));
            } else {
                g2.setColor(new Color(70, 90, 120));
                g2.setStroke(new BasicStroke(1.5f));
            }

            dibujarFlecha(g2, origen, destino);
            dibujarEtiquetaSinapsis(g2, s, origen, destino, resaltada);
        }
    }

    private boolean esAristaDeRuta(String origenId, String destinoId) {
        if (rutaActual.size() < 2) {
            return false;
        }

        for (int i = 0; i < rutaActual.size() - 1; i++) {
            if (rutaActual.get(i).equals(origenId) && rutaActual.get(i + 1).equals(destinoId)) {
                return true;
            }
        }
        return false;
    }

    private void dibujarFlecha(Graphics2D g2, Point origen, Point destino) {
        double dx = destino.x - origen.x;
        double dy = destino.y - origen.y;
        double distancia = Math.sqrt(dx * dx + dy * dy);

        if (distancia == 0) {
            return;
        }

        double ux = dx / distancia;
        double uy = dy / distancia;

        int x1 = (int) (origen.x + ux * RADIO_NODO);
        int y1 = (int) (origen.y + uy * RADIO_NODO);
        int x2 = (int) (destino.x - ux * RADIO_NODO);
        int y2 = (int) (destino.y - uy * RADIO_NODO);

        g2.drawLine(x1, y1, x2, y2);

        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int largoFlecha = 12;
        int anchoFlecha = 7;

        int xA = (int) (x2 - largoFlecha * Math.cos(angulo) + anchoFlecha * Math.sin(angulo));
        int yA = (int) (y2 - largoFlecha * Math.sin(angulo) - anchoFlecha * Math.cos(angulo));
        int xB = (int) (x2 - largoFlecha * Math.cos(angulo) - anchoFlecha * Math.sin(angulo));
        int yB = (int) (y2 - largoFlecha * Math.sin(angulo) + anchoFlecha * Math.cos(angulo));

        Polygon flecha = new Polygon();
        flecha.addPoint(x2, y2);
        flecha.addPoint(xA, yA);
        flecha.addPoint(xB, yB);
        g2.fillPolygon(flecha);
    }

    private void dibujarEtiquetaSinapsis(Graphics2D g2, Sinapsis s, Point origen, Point destino, boolean resaltada) {
        int x = (origen.x + destino.x) / 2;
        int y = (origen.y + destino.y) / 2;

        String etiqueta = "d:" + formatear(s.getDistancia())
                + "  " + s.getIdNeurotransmisor()
                + "  k:" + formatear(s.getCoeficienteEficiencia());

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int ancho = fm.stringWidth(etiqueta) + 8;
        int alto = fm.getHeight() + 4;

        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(x - ancho / 2, y - alto / 2, ancho, alto, 8, 8);
        g2.setColor(resaltada ? new Color(20, 100, 45) : new Color(50, 50, 50));
        g2.drawString(etiqueta, x - ancho / 2 + 4, y + fm.getAscent() / 2 - 2);
    }

    private void dibujarNeuronas(Graphics2D g2) {
        for (Neurona n : grafo.obtenerTodasLasNeuronas()) {
            Point p = posiciones.get(n.getId());
            if (p == null) {
                continue;
            }

            boolean aislada = zonasAisladas.contains(n.getId());
            boolean enRuta = rutaActual.contains(n.getId());

            if (aislada) {
                g2.setColor(new Color(230, 80, 80));
            } else if (enRuta) {
                g2.setColor(new Color(110, 205, 130));
            } else {
                g2.setColor(new Color(135, 190, 235));
            }

            g2.fillOval(p.x - RADIO_NODO, p.y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);
            g2.setColor(new Color(40, 60, 80));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(p.x - RADIO_NODO, p.y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String id = n.getId();
            int tx = p.x - fm.stringWidth(id) / 2;
            int ty = p.y + fm.getAscent() / 2 - 2;
            g2.setColor(Color.BLACK);
            g2.drawString(id, tx, ty);
        }
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int x = 12;
        int y = 18;
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(new Color(60, 60, 60));
        g2.drawString("Arrastre los nodos con el mouse para reorganizar el grafo.", x, y);
        g2.drawString("Azul: normal   Rojo: aislada   Verde: ruta Dijkstra", x, y + 16);
    }

    private String formatear(double valor) {
        return String.format(java.util.Locale.US, "%.3f", valor);
    }
}