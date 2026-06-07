/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package synapselogic;

import javax.swing.SwingUtilities;
import synapselogic.interfaz.InterfazUsuario;

/**
 * Clase principal del proyecto SynapseLogic.
 */
public class main {

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param args argumentos de linea de comandos no utilizados
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazUsuario interfaz = new InterfazUsuario();
            interfaz.iniciar();
        });
    }
}