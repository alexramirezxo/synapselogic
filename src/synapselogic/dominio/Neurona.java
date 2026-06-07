/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package synapselogic.dominio;

/*
 * Representa una neurona dentro de la red sinaptica.
 * En el grafo, cada neurona funciona como un nodo identificado por un ID unico.
 */
public class Neurona {

    private String id;
    private boolean activa;

    /*
     * Crea una neurona activa con el identificador indicado.
     *
     * @param id identificador unico de la neurona
     */
    public Neurona(String id) {
        this.id = id;
        this.activa = true;
    }

    /*
     * Obtiene el identificador de la neurona.
     *
     * @return ID de la neurona
     */
    public String getId() {
        return id;
    }

    /*
     * Indica si la neurona esta activa.
     *
     * @return true si esta activa; false en caso contrario
     */
    public boolean estaActiva() {
        return activa;
    }

    /**
     * Activa la neurona.
     */
    public void activar() {
        this.activa = true;
    }

    /*
     * Desactiva la neurona.
     */
    public void desactivar() {
        this.activa = false;
    }

    @Override
    public String toString() {
        return id;
    }
}