/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import synapselogic.dominio.Neurona;
import synapselogic.dominio.Neurotransmisor;
import synapselogic.dominio.Sinapsis;
import synapselogic.estructuras.GrafoSinaptico;
import synapselogic.estructuras.TablaHashNeurotransmisores;

/**
 * Servicio que implementa los algoritmos de analisis de la red sinaptica:
 * BFS, DFS, conectividad fuerte y Dijkstra.
 */
public class MotorAnalisisSinaptico {

    /**
     * Detecta neuronas no alcanzables desde una fuente usando BFS.
     *
     * @param grafo grafo sinaptico
     * @param fuenteId neurona fuente
     * @return lista de neuronas aisladas desde la fuente
     */
    public List<Neurona> detectarZonasAisladasBFS(GrafoSinaptico grafo, String fuenteId) {
        Set<String> visitados = new HashSet<>();

        if (grafo == null || fuenteId == null || grafo.obtenerNeurona(fuenteId) == null) {
            return obtenerNoVisitadas(grafo, visitados);
        }

        Queue<String> cola = new LinkedList<>();
        cola.add(fuenteId.trim());
        visitados.add(fuenteId.trim());

        while (!cola.isEmpty()) {
            String actual = cola.poll();

            for (Sinapsis s : grafo.obtenerSinapsisSalientes(actual)) {
                String destino = s.getDestinoId();

                if (!visitados.contains(destino)) {
                    visitados.add(destino);
                    cola.add(destino);
                }
            }
        }

        return obtenerNoVisitadas(grafo, visitados);
    }

    /**
     * Detecta neuronas no alcanzables desde una fuente usando DFS.
     *
     * @param grafo grafo sinaptico
     * @param fuenteId neurona fuente
     * @return lista de neuronas aisladas desde la fuente
     */
    public List<Neurona> detectarZonasAisladasDFS(GrafoSinaptico grafo, String fuenteId) {
        Set<String> visitados = new HashSet<>();

        if (grafo == null || fuenteId == null || grafo.obtenerNeurona(fuenteId) == null) {
            return obtenerNoVisitadas(grafo, visitados);
        }

        dfs(grafo, fuenteId.trim(), visitados);
        return obtenerNoVisitadas(grafo, visitados);
    }

    private void dfs(GrafoSinaptico grafo, String actual, Set<String> visitados) {
        visitados.add(actual);

        for (Sinapsis s : grafo.obtenerSinapsisSalientes(actual)) {
            String destino = s.getDestinoId();
            if (!visitados.contains(destino)) {
                dfs(grafo, destino, visitados);
            }
        }
    }

    private List<Neurona> obtenerNoVisitadas(GrafoSinaptico grafo, Set<String> visitados) {
        List<Neurona> aisladas = new ArrayList<>();

        if (grafo == null) {
            return aisladas;
        }

        for (Neurona n : grafo.obtenerTodasLasNeuronas()) {
            if (!visitados.contains(n.getId())) {
                aisladas.add(n);
            }
        }

        return aisladas;
    }

    /**
     * Determina si cada neurona puede alcanzar a todas las demas.
     *
     * @param grafo grafo sinaptico
     * @return true si es fuertemente conexo
     */
    public boolean esFuertementeConexa(GrafoSinaptico grafo) {
        if (grafo == null || grafo.estaVacio()) {
            return false;
        }

        for (Neurona n : grafo.obtenerTodasLasNeuronas()) {
            List<Neurona> aisladas = detectarZonasAisladasBFS(grafo, n.getId());

            if (!aisladas.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calcula la ruta mas rapida entre dos neuronas usando Dijkstra.
     * El peso de cada arista se calcula como (distancia / velocidad) * k.
     *
     * @param grafo grafo sinaptico
     * @param tabla tabla hash de neurotransmisores
     * @param origenId neurona origen
     * @param destinoId neurona destino
     * @return lista de neuronas que componen la ruta; vacia si no hay ruta
     */
    public List<Neurona> calcularRutaMasRapida(GrafoSinaptico grafo,
                                                TablaHashNeurotransmisores tabla,
                                                String origenId,
                                                String destinoId) {
        List<Neurona> ruta = new ArrayList<>();

        if (grafo == null || tabla == null || origenId == null || destinoId == null) {
            return ruta;
        }

        origenId = origenId.trim();
        destinoId = destinoId.trim();

        if (grafo.obtenerNeurona(origenId) == null || grafo.obtenerNeurona(destinoId) == null) {
            return ruta;
        }

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> anteriores = new HashMap<>();
        Set<String> noVisitados = new HashSet<>();

        for (Neurona n : grafo.obtenerTodasLasNeuronas()) {
            distancias.put(n.getId(), Double.POSITIVE_INFINITY);
            noVisitados.add(n.getId());
        }

        distancias.put(origenId, 0.0);

        while (!noVisitados.isEmpty()) {
            String actual = extraerNoVisitadoConMenorDistancia(noVisitados, distancias);
            if (actual == null) {
                break;
            }

            noVisitados.remove(actual);

            if (actual.equals(destinoId)) {
                break;
            }

            double distanciaActual = distancias.get(actual);
            if (Double.isInfinite(distanciaActual)) {
                break;
            }

            for (Sinapsis s : grafo.obtenerSinapsisSalientes(actual)) {
                String vecino = s.getDestinoId();
                if (!noVisitados.contains(vecino)) {
                    continue;
                }

                double peso = calcularPeso(s, tabla);
                if (Double.isInfinite(peso) || peso == Double.MAX_VALUE || peso < 0) {
                    continue;
                }

                double nuevaDistancia = distanciaActual + peso;
                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    anteriores.put(vecino, actual);
                }
            }
        }

        if (Double.isInfinite(distancias.get(destinoId))) {
            return ruta;
        }

        LinkedList<String> idsRuta = new LinkedList<>();
        String actual = destinoId;
        idsRuta.addFirst(actual);

        while (anteriores.containsKey(actual)) {
            actual = anteriores.get(actual);
            idsRuta.addFirst(actual);
        }

        if (!idsRuta.isEmpty() && idsRuta.getFirst().equals(origenId)) {
            for (String id : idsRuta) {
                Neurona neurona = grafo.obtenerNeurona(id);
                if (neurona != null) {
                    ruta.add(neurona);
                }
            }
        }

        return ruta;
    }

    private String extraerNoVisitadoConMenorDistancia(Set<String> noVisitados,
                                                       Map<String, Double> distancias) {
        String menorId = null;
        double menorDistancia = Double.POSITIVE_INFINITY;

        for (String id : noVisitados) {
            double distancia = distancias.get(id);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                menorId = id;
            }
        }

        return menorId;
    }

    /**
     * Calcula el peso real de una sinapsis para Dijkstra.
     * Peso = (distancia / velocidad del neurotransmisor) * k.
     *
     * @param s sinapsis
     * @param tabla tabla hash de neurotransmisores
     * @return peso calculado; infinito si el neurotransmisor no existe o es invalido
     */
    public double calcularPeso(Sinapsis s, TablaHashNeurotransmisores tabla) {
        if (s == null || tabla == null) {
            return Double.POSITIVE_INFINITY;
        }

        Neurotransmisor nt = tabla.buscar(s.getIdNeurotransmisor());

        if (nt == null || nt.getVelocidad() <= 0) {
            return Double.POSITIVE_INFINITY;
        }

        return (s.getDistancia() / nt.getVelocidad()) * s.getCoeficienteEficiencia();
    }

    /**
     * Calcula el tiempo total de una ruta ya obtenida.
     *
     * @param grafo grafo sinaptico
     * @param tabla tabla hash de neurotransmisores
     * @param ruta ruta de neuronas
     * @return tiempo total calculado
     */
    public double calcularTiempoRuta(GrafoSinaptico grafo,
                                     TablaHashNeurotransmisores tabla,
                                     List<Neurona> ruta) {
        if (grafo == null || tabla == null || ruta == null || ruta.size() < 2) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Sinapsis s = grafo.obtenerSinapsis(ruta.get(i).getId(), ruta.get(i + 1).getId());
            if (s == null) {
                return Double.POSITIVE_INFINITY;
            }
            total += calcularPeso(s, tabla);
        }

        return total;
    }
}