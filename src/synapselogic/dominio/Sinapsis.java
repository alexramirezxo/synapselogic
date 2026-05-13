/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.dominio;

/**
 * Representa una conexión dirigida entre dos neuronas.
 */

public class Sinapsis {

    private String origenId;
    private String destinoId;
    private double distancia;
    private String idNeurotransmisor;
    private double coeficienteEficiencia;

    public Sinapsis(String origenId, String destinoId, double distancia,
                    String idNeurotransmisor, double coeficienteEficiencia) {
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.distancia = distancia;
        this.idNeurotransmisor = idNeurotransmisor;
        this.coeficienteEficiencia = coeficienteEficiencia;
    }

    public String getOrigenId() {
        return origenId;
    }

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
}
/**
 *
 * @author alexramirez
 */

