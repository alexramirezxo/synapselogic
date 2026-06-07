/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.estructuras;

import java.util.ArrayList;
import java.util.List;
import synapselogic.dominio.Neurotransmisor;

/**
 * Tabla Hash propia para almacenar neurotransmisores.
 * Usa encadenamiento separado para resolver colisiones.
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
    private static final double FACTOR_CARGA_MAXIMO = 0.75;

    /**
     * Crea una tabla hash con capacidad inicial por defecto.
     * 
     * El 31 como multiplicar sirve al ser primo evitando colisiones
     * Referencia: https://medium.com/@skniyajali/the-secret-behind-kotlins-hashcode-why-31-is-the-magic-number-c2bb8c8e98f1
     * 
     */
    public TablaHashNeurotransmisores() {
        this.capacidad = 101;
        this.cantidad = 0;
        this.tabla = new NodoHash[capacidad];
    }

    private int hash(String clave) {
        if (clave == null) {
            return 0;
        }

        int hash = 0;
        for (int i = 0; i < clave.length(); i++) {
            hash = 31 * hash + clave.charAt(i);
        }

        return (hash & 0x7fffffff) % capacidad;
    }

    /**
     * Inserta o reemplaza un neurotransmisor usando su ID como clave.
     *
     * @param n neurotransmisor que se desea insertar
     */
    public void insertar(Neurotransmisor n) {
        if (n == null || n.getId() == null || n.getId().trim().isEmpty()) {
            return;
        }

        if ((cantidad + 1.0) / capacidad > FACTOR_CARGA_MAXIMO) {
            redimensionar();
        }

        String claveNormalizada = normalizarClave(n.getId());
        int indice = hash(claveNormalizada);
        NodoHash actual = tabla[indice];

        while (actual != null) {
            if (actual.clave.equals(claveNormalizada)) {
                actual.valor = n;
                return;
            }
            actual = actual.siguiente;
        }

        NodoHash nuevo = new NodoHash(claveNormalizada, n);
        nuevo.siguiente = tabla[indice];
        tabla[indice] = nuevo;
        cantidad++;
    }

    /**
     * Busca un neurotransmisor por ID.
     *
     * @param id ID del neurotransmisor
     * @return neurotransmisor encontrado, o null si no existe
     */
    public Neurotransmisor buscar(String id) {
        if (id == null) {
            return null;
        }

        String claveNormalizada = normalizarClave(id);
        int indice = hash(claveNormalizada);
        NodoHash actual = tabla[indice];

        while (actual != null) {
            if (actual.clave.equals(claveNormalizada)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }

        return null;
    }

    /**
     * Indica si un neurotransmisor existe en la tabla.
     *
     * @param id ID a consultar
     * @return true si existe; false en caso contrario
     */
    public boolean contiene(String id) {
        return buscar(id) != null;
    }

    /**
     * Elimina un neurotransmisor por ID.
     *
     * @param id ID a eliminar
     */
    public void eliminar(String id) {
        if (id == null) {
            return;
        }

        String claveNormalizada = normalizarClave(id);
        int indice = hash(claveNormalizada);
        NodoHash actual = tabla[indice];
        NodoHash anterior = null;

        while (actual != null) {
            if (actual.clave.equals(claveNormalizada)) {
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

    /**
     * Vacía la tabla hash.
     */
    public void limpiar() {
        tabla = new NodoHash[capacidad];
        cantidad = 0;
    }

    /**
     * Devuelve la cantidad de neurotransmisores almacenados.
     *
     * @return cantidad de registros
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Obtiene todos los neurotransmisores almacenados.
     *
     * @return lista con los neurotransmisores
     */
    public List<Neurotransmisor> obtenerTodos() {
        List<Neurotransmisor> lista = new ArrayList<>();
        for (NodoHash nodo : tabla) {
            NodoHash actual = nodo;
            while (actual != null) {
                lista.add(actual.valor);
                actual = actual.siguiente;
            }
        }        return lista;
    }

    private String normalizarClave(String clave) {
        return clave.trim().toUpperCase();
    }

    private void redimensionar() {
        NodoHash[] tablaAnterior = tabla;
        capacidad = capacidad * 2 + 1;
        tabla = new NodoHash[capacidad];
        cantidad = 0;

        for (NodoHash nodo : tablaAnterior) {
            NodoHash actual = nodo;
            while (actual != null) {
                insertar(actual.valor);
                actual = actual.siguiente;
            }
        }
    }
}