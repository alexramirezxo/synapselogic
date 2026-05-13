/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.dominio;

/**
 * Representa un neurotransmisor usado por una sinapsis.
 */
public class Neurotransmisor {

    private String id;
    private String nombre;
    private String efecto;
    private double velocidad;
    private String descripcion;

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
}

/**
 *
 * @author alexramirez
 */

