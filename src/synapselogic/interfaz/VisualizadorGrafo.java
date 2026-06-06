/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package synapselogic.interfaz;

import java.awt.*;
import javax.swing.*;
import synapselogic.dominio.Sinapsis;
import synapselogic.estructuras.GrafoSinaptico;

public class VisualizadorGrafo extends JPanel {

    private GrafoSinaptico grafo;

    public void mostrarGrafo(GrafoSinaptico grafo) {
        this.grafo = grafo;
        repaint();
    }

    public void resaltarZonasAisladas(java.util.List<String> ids) {
        repaint();
    }

    public void resaltarRuta(java.util.List<synapselogic.dominio.Neurona> ruta) {
        repaint();
    }

    public void refrescar() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (grafo == null) {
            g.drawString("No hay grafo cargado.", 30, 30);
            return;
        }

        int y = 30;
        g.drawString("Sinapsis cargadas:", 30, y);
        y += 25;

        for (Sinapsis s : grafo.obtenerTodasLasSinapsis()) {
            g.drawString(
                s.getOrigenId() + " -> " + s.getDestinoId() +
                " | d=" + s.getDistancia() +
                " | nt=" + s.getIdNeurotransmisor() +
                " | k=" + s.getCoeficienteEficiencia(),
                30,
                y
            );
            y += 20;
        }
    }
}