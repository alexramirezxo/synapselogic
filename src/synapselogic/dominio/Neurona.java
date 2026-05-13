/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package synapselogic.dominio;


/**
 * Representa una neurona dentro de la red sináptica.
 */

public class Neurona {

    private String id;
    private boolean activa;

    public Neurona(String id) {
        this.id = id;
        this.activa = true;
    }

    public String getId() {
        return id;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }
}
/**
 *
 * @author alexramirez
 */
