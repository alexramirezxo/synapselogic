/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.dominio;

/**
 * Representa un neurotransmisor usado por una o varias sinapsis.
 */
public class Neurotransmisor {

    private String id;
    private String nombre;
    private String efecto;
    private double velocidad;
    private String descripcion;

    /**
     * Crea un neurotransmisor.
     *
     * @param id identificador del neurotransmisor
     * @param nombre nombre comun o cientifico
     * @param efecto efecto principal: excitatorio, inhibitorio o modulador
     * @param velocidad factor de velocidad para el calculo del peso de Dijkstra
     * @param descripcion descripcion breve
     */
    public Neurotransmisor(String id, String nombre, String efecto,
                           double velocidad, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.efecto = efecto;
        this.velocidad = velocidad;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEfecto() {
        return efecto;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (v=" + velocidad + ")";
    }
}