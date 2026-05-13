/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package synapselogic.estructuras;

/**
 *
 * @author alexramirez
 */

import synapselogic.dominio.Neurotransmisor;

/**
 * Tabla Hash propia para almacenar neurotransmisores.
 */

public class TablaHashNeurotransmisores {

    private static class NodoHash {
        String clave;
        Neurotransmisor valor;
        NodoHash siguiente;

        NodoHash(String clave, Neurotransmisor valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private int capacidad;
    private int cantidad;
    private NodoHash[] tabla;

    public TablaHashNeurotransmisores() {
        this.capacidad = 101;
        this.cantidad = 0;
        this.tabla = new NodoHash[capacidad];
    }

    private int hash(String clave) {
        int hash = 0;

        for (int i = 0; i < clave.length(); i++) {
            hash = 31 * hash + clave.charAt(i);
        }

        return Math.abs(hash) % capacidad;
    }

    public void insertar(Neurotransmisor n) {
        int indice = hash(n.getId());
        NodoHash actual = tabla[indice];

        while (actual != null) {
            if (actual.clave.equals(n.getId())) {
                actual.valor = n;
                return;
            }
            actual = actual.siguiente;
        }

        NodoHash nuevo = new NodoHash(n.getId(), n);
        nuevo.siguiente = tabla[indice];
        tabla[indice] = nuevo;
        cantidad++;
    }

    public Neurotransmisor buscar(String id) {
        int indice = hash(id);
        NodoHash actual = tabla[indice];

        while (actual != null) {
            if (actual.clave.equals(id)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }

        return null;
    }

    public boolean contiene(String id) {
        return buscar(id) != null;
    }

    public void eliminar(String id) {
        int indice = hash(id);
        NodoHash actual = tabla[indice];
        NodoHash anterior = null;

        while (actual != null) {
            if (actual.clave.equals(id)) {
                if (anterior == null) {
                    tabla[indice] = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                cantidad--;
                return;
            }

            anterior = actual;
            actual = actual.siguiente;
        }
    }

    public void limpiar() {
        tabla = new NodoHash[capacidad];
        cantidad = 0;
    }

    public int getCantidad() {
        return cantidad;
    }
}
