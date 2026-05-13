/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package synapselogic.estructuras;

/**
 *
 * @author alexramirez
 */

import java.util.*;
import synapselogic.dominio.Neurona;
import synapselogic.dominio.Sinapsis;

/**
 * Grafo dirigido por lista de adyacencia.
 */

public class GrafoSinaptico {

    private Map<String, Neurona> neuronas;
    private Map<String, List<Sinapsis>> adyacencias;

    public GrafoSinaptico() {
        neuronas = new HashMap<>();
        adyacencias = new HashMap<>();
    }

    public void agregarNeurona(Neurona n) {
        neuronas.put(n.getId(), n);
        adyacencias.putIfAbsent(n.getId(), new ArrayList<>());
    }

    public void eliminarNeurona(String id) {
        neuronas.remove(id);
        adyacencias.remove(id);

        for (List<Sinapsis> lista : adyacencias.values()) {
            lista.removeIf(s -> s.getDestinoId().equals(id));
        }
    }

    public void agregarSinapsis(Sinapsis s) {
        if (!neuronas.containsKey(s.getOrigenId())) {
            agregarNeurona(new Neurona(s.getOrigenId()));
        }

        if (!neuronas.containsKey(s.getDestinoId())) {
            agregarNeurona(new Neurona(s.getDestinoId()));
        }

        adyacencias.get(s.getOrigenId()).add(s);
    }

    public void eliminarSinapsis(String origenId, String destinoId) {
        if (adyacencias.containsKey(origenId)) {
            adyacencias.get(origenId).removeIf(s -> s.getDestinoId().equals(destinoId));
        }
    }

    public Neurona obtenerNeurona(String id) {
        return neuronas.get(id);
    }

    public List<Neurona> obtenerVecinos(String id) {
        List<Neurona> vecinos = new ArrayList<>();

        for (Sinapsis s : obtenerSinapsisSalientes(id)) {
            Neurona destino = neuronas.get(s.getDestinoId());
            if (destino != null) {
                vecinos.add(destino);
            }
        }

        return vecinos;
    }

    public List<Sinapsis> obtenerSinapsisSalientes(String id) {
        return adyacencias.getOrDefault(id, new ArrayList<>());
    }

    public List<Neurona> obtenerTodasLasNeuronas() {
        return new ArrayList<>(neuronas.values());
    }

    public List<Sinapsis> obtenerTodasLasSinapsis() {
        List<Sinapsis> todas = new ArrayList<>();

        for (List<Sinapsis> lista : adyacencias.values()) {
            todas.addAll(lista);
        }

        return todas;
    }

    public void limpiar() {
        neuronas.clear();
        adyacencias.clear();
    }
}
