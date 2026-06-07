/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.dominio;

/**
 * Representa una conexion dirigida entre dos neuronas.
 * Una sinapsis contiene la distancia, el neurotransmisor asociado y el coeficiente k.
 */
public class Sinapsis {
    private String origenId;
    private String destinoId;
    private double distancia;
    private String idNeurotransmisor;
    private double coeficienteEficiencia;

    /**
     * Crea una sinapsis dirigida.
     *
     * @param origenId identificador de la neurona origen
     * @param destinoId identificador de la neurona destino
     * @param distancia distancia sinaptica
     * @param idNeurotransmisor ID del neurotransmisor asociado
     * @param coeficienteEficiencia coeficiente de eficiencia sinaptica k
     */
    public Sinapsis(String origenId, String destinoId, double distancia,
                    String idNeurotransmisor, double coeficienteEficiencia) {
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.distancia = distancia;
        this.idNeurotransmisor = idNeurotransmisor;
        this.coeficienteEficiencia = coeficienteEficiencia;
    }

    public String getOrigenId() {
        return origenId;}

    public String getDestinoId() {
        return destinoId;
    }

    public double getDistancia() {
        return distancia;
    }

    public String getIdNeurotransmisor() {
        return idNeurotransmisor;
    }

    public double getCoeficienteEficiencia() {
        return coeficienteEficiencia;
    }

    public void setCoeficienteEficiencia(double k) {
        this.coeficienteEficiencia = k;
    }

    @Override
    public String toString() {
        return origenId + " -> " + destinoId + " (d=" + distancia
                + ", nt=" + idNeurotransmisor + ", k=" + coeficienteEficiencia + ")";
    }
}