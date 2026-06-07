/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.estructuras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import synapselogic.dominio.Neurona;
import synapselogic.dominio.Sinapsis;

/**
 * Grafo dirigido implementado mediante lista de adyacencia.
 * Las neuronas son nodos y las sinapsis son aristas dirigidas.
 */
public class GrafoSinaptico {

    private Map<String, Neurona> neuronas;
    private Map<String, List<Sinapsis>> adyacencias;

    /**
     * Crea un grafo sinaptico vacio.
     */
    public GrafoSinaptico() {
        neuronas = new LinkedHashMap<>();
        adyacencias = new LinkedHashMap<>();
    }

    /**
     * Agrega una neurona si no existe. Si existe, conserva la registrada.
     *
     * @param n neurona que se desea agregar
     */
    public void agregarNeurona(Neurona n) {
        if (n == null || n.getId() == null || n.getId().trim().isEmpty()) {
            return;
        }

        String id = n.getId().trim();
        if (!neuronas.containsKey(id)) {
            neuronas.put(id, n);
        }
        adyacencias.putIfAbsent(id, new ArrayList<Sinapsis>());
    }

    /**
     * Elimina una neurona y todas sus sinapsis entrantes y salientes.
     *
     * @param id identificador de la neurona
     */
    public void eliminarNeurona(String id) {
        if (id == null) {
            return;
        }

        id = id.trim();
        neuronas.remove(id);
        adyacencias.remove(id);

        for (List<Sinapsis> lista : adyacencias.values()) {
            for (int i = lista.size() - 1; i >= 0; i--) {
                if (lista.get(i).getDestinoId().equals(id)) {
                    lista.remove(i);
                }
            }
        }
    }

    /**
     * Agrega una sinapsis dirigida. Si las neuronas no existen, las crea automaticamente.
     *
     * @param s sinapsis a agregar
     */
    public void agregarSinapsis(Sinapsis s) {
        if (s == null || s.getOrigenId() == null || s.getDestinoId() == null) {
            return;
        }

        String origen = s.getOrigenId().trim();
        String destino = s.getDestinoId().trim();

        if (!neuronas.containsKey(origen)) {
            agregarNeurona(new Neurona(origen));
        }

        if (!neuronas.containsKey(destino)) {
            agregarNeurona(new Neurona(destino));
        }

        adyacencias.putIfAbsent(origen, new ArrayList<Sinapsis>());
        adyacencias.get(origen).add(s);
    }

    /**
     * Elimina todas las sinapsis que conectan un origen con un destino.
     *
     * @param origenId ID de origen
     * @param destinoId ID de destino
     */
    public void eliminarSinapsis(String origenId, String destinoId) {
        if (origenId == null || destinoId == null) {
            return;
        }

        origenId = origenId.trim();
        destinoId = destinoId.trim();

        if (adyacencias.containsKey(origenId)) {
            List<Sinapsis> lista = adyacencias.get(origenId);
            for (int i = lista.size() - 1; i >= 0; i--) {
                if (lista.get(i).getDestinoId().equals(destinoId)) {
                    lista.remove(i);
                }
            }
        }
    }

    /**
     * Obtiene una neurona por ID.
     *
     * @param id ID de la neurona
     * @return neurona encontrada, o null si no existe
     */
    public Neurona obtenerNeurona(String id) {
        if (id == null) {
            return null;
        }
        return neuronas.get(id.trim());
    }

    /**
     * Obtiene los vecinos alcanzables por sinapsis salientes.
     *
     * @param id ID de la neurona origen
     * @return lista de neuronas vecinas
     */
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

    /**
     * Obtiene las sinapsis salientes desde una neurona.
     *
     * @param id ID de la neurona origen
     * @return lista de sinapsis salientes
     */
    public List<Sinapsis> obtenerSinapsisSalientes(String id) {
        if (id == null) {
            return Collections.emptyList();
        }
        List<Sinapsis> lista = adyacencias.get(id.trim());
        if (lista == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(lista);
    }

    /**
     * Obtiene una sinapsis especifica entre dos neuronas.
     * Si existen varias, retorna la primera registrada.
     *
     * @param origenId ID de origen
     * @param destinoId ID de destino
     * @return sinapsis encontrada, o null si no existe
     */
    public Sinapsis obtenerSinapsis(String origenId, String destinoId) {
        if (origenId == null || destinoId == null) {
            return null;
        }

        for (Sinapsis s : obtenerSinapsisSalientes(origenId)) {
            if (s.getDestinoId().equals(destinoId.trim())) {
                return s;
            }
        }

        return null;
    }

    /**
     * Obtiene todas las neuronas del grafo.
     *
     * @return lista de neuronas
     */
    public List<Neurona> obtenerTodasLasNeuronas() {
        return new ArrayList<>(neuronas.values());
    }

    /**
     * Obtiene todas las sinapsis del grafo.
     *
     * @return lista de sinapsis
     */
    public List<Sinapsis> obtenerTodasLasSinapsis() {
        List<Sinapsis> todas = new ArrayList<>();

        for (List<Sinapsis> lista : adyacencias.values()) {
            todas.addAll(lista);
        }

        return todas;
    }

    /**
     * Vacia el grafo.
     */
    public void limpiar() {
        neuronas.clear();
        adyacencias.clear();
    }

    /**
     * Indica si el grafo esta vacio.
     *
     * @return true si no contiene neuronas
     */
    public boolean estaVacio() {
        return neuronas.isEmpty();
    }
}
