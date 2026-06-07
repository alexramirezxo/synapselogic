/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.servicios;

import synapselogic.dominio.Neurona;
import synapselogic.dominio.Sinapsis;
import synapselogic.estructuras.GrafoSinaptico;

/**
 * Servicio responsable de modificar la red para simular lesiones o fatiga.
 */
public class SimuladorRedSinaptica {

    /**
     * Simula fatiga multiplicando todos los coeficientes k por 1.2.
     *
     * @param grafo grafo a modificar
     */
    public void aplicarFatiga(GrafoSinaptico grafo) {
        if (grafo == null) {
            return;
        }

        for (Sinapsis s : grafo.obtenerTodasLasSinapsis()) {
            s.setCoeficienteEficiencia(s.getCoeficienteEficiencia() * 1.2);
        }
    }

    public void agregarNeurona(GrafoSinaptico grafo, Neurona n) {
        if (grafo != null) {
            grafo.agregarNeurona(n);
        }
    }

    public void eliminarNeurona(GrafoSinaptico grafo, String id) {
        if (grafo != null) {
            grafo.eliminarNeurona(id);
        }
    }

    public void agregarSinapsis(GrafoSinaptico grafo, Sinapsis s) {
        if (grafo != null) {
            grafo.agregarSinapsis(s);
        }
    }

    public void eliminarSinapsis(GrafoSinaptico grafo, String origenId, String destinoId) {
        if (grafo != null) {
            grafo.eliminarSinapsis(origenId, destinoId);
        }
    }
}