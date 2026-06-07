/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package synapselogic.servicios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import synapselogic.dominio.Neurona;
import synapselogic.dominio.Neurotransmisor;
import synapselogic.dominio.Sinapsis;
import synapselogic.estructuras.GrafoSinaptico;
import synapselogic.estructuras.TablaHashNeurotransmisores;

/**
 * Gestiona la carga y guardado de archivos CSV para la red sinaptica
 * y el diccionario de neurotransmisores.
 */
public class GestorArchivosCSV {

    private boolean hayCambiosNoGuardados;

    private static class RegistroCSV {
        String texto;
        int lineaInicial;

        RegistroCSV(String texto, int lineaInicial) {
            this.texto = texto;
            this.lineaInicial = lineaInicial;
        }
    }

    /**
     * Carga un archivo CSV de red sinaptica.
     *
     * Formato esperado:
     * origen,destino,distancia,ID_Neurotransmisor,coheficiente_eficiencia_sinaptica
     *
     * Tambien acepta separador punto y coma:
     * origen;destino;distancia;ID_Neurotransmisor;coheficiente_eficiencia_sinaptica
     *
     * @param ruta ruta del archivo CSV
     * @param grafo grafo destino
     * @throws IOException si ocurre un error de lectura o formato
     */
    public void cargarRed(String ruta, GrafoSinaptico grafo) throws IOException {
        if (grafo == null) {
            throw new IOException("El grafo no esta inicializado.");
        }

        GrafoSinaptico grafoTemporal = new GrafoSinaptico();
        List<RegistroCSV> registros = leerRegistrosCSV(ruta);

        boolean datosEncontrados = false;

        for (RegistroCSV registro : registros) {
            String linea = removerBOM(registro.texto).trim();

            if (linea.isEmpty() || esLineaIgnorable(linea) || esCabeceraRed(linea)) {
                continue;
            }

            char separador = detectarSeparador(linea);
            List<String> partes = parsearLineaCSV(linea, separador);

            if (partes.size() < 5) {
                if (!datosEncontrados) {
                    continue;
                }

                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": se esperaban 5 columnas para la red. "
                        + "Revise si el archivo usa coma, punto y coma o tabulador como separador. "
                        + "Contenido leido: " + linea
                );
            }

            String origen = limpiarCampo(partes.get(0));
            String destino = limpiarCampo(partes.get(1));
            String distanciaTexto = limpiarCampo(partes.get(2));
            String idNeuro = limpiarCampo(partes.get(3));
            String coeficienteTexto = limpiarCampo(partes.get(4));

            if (origen.isEmpty() || destino.isEmpty() || idNeuro.isEmpty()) {
                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": origen, destino e ID_Neurotransmisor son obligatorios."
                );
            }

            double distancia = parsearDouble(distanciaTexto, "distancia", registro.lineaInicial);
            double k = parsearDouble(coeficienteTexto, "coeficiente k", registro.lineaInicial);

            if (distancia < 0) {
                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": la distancia no puede ser negativa."
                );
            }

            if (k <= 0) {
                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": el coeficiente k debe ser mayor que cero."
                );
            }

            grafoTemporal.agregarSinapsis(new Sinapsis(origen, destino, distancia, idNeuro, k));
            datosEncontrados = true;
        }

        if (!datosEncontrados) {
            throw new IOException(
                    "No se encontraron registros validos de red. "
                    + "El archivo debe tener columnas: origen, destino, distancia, ID_Neurotransmisor, coeficiente."
            );
        }

        grafo.limpiar();

        for (Neurona n : grafoTemporal.obtenerTodasLasNeuronas()) {
            grafo.agregarNeurona(n);
        }

        for (Sinapsis s : grafoTemporal.obtenerTodasLasSinapsis()) {
            grafo.agregarSinapsis(s);
        }

        marcarCambiosGuardados();
    }

    /**
     * Carga un diccionario de neurotransmisores.
     *
     * Formato esperado:
     * id,nombre,efecto,velocidad,descripcion
     *
     * Tambien acepta separador punto y coma.
     *
     * @param ruta ruta del archivo CSV
     * @param tabla tabla hash destino
     * @throws IOException si ocurre un error de lectura o formato
     */
    public void cargarDiccionario(String ruta, TablaHashNeurotransmisores tabla) throws IOException {
        if (tabla == null) {
            throw new IOException("La tabla hash no esta inicializada.");
        }

        TablaHashNeurotransmisores tablaTemporal = new TablaHashNeurotransmisores();
        List<RegistroCSV> registros = leerRegistrosCSV(ruta);

        boolean datosEncontrados = false;

        for (RegistroCSV registro : registros) {
            String linea = removerBOM(registro.texto).trim();

            if (linea.isEmpty() || esLineaIgnorable(linea) || esCabeceraDiccionario(linea)) {
                continue;
            }

            char separador = detectarSeparador(linea);
            List<String> partes = parsearLineaCSV(linea, separador);

            if (partes.size() < 5) {
                if (!datosEncontrados) {
                    continue;
                }

                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": se esperaban 5 columnas para el diccionario. "
                        + "Formato esperado: id,nombre,efecto,velocidad,descripcion. "
                        + "Contenido leido: " + linea
                );
            }

            String id = limpiarCampo(partes.get(0));
            String nombre = limpiarCampo(partes.get(1));
            String efecto = limpiarCampo(partes.get(2));
            String velocidadTexto = limpiarCampo(partes.get(3));
            String descripcion = limpiarCampo(partes.get(4));

            if (id.isEmpty() || nombre.isEmpty() || efecto.isEmpty()) {
                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": ID, nombre y efecto son obligatorios."
                );
            }

            double velocidad = parsearDouble(velocidadTexto, "velocidad", registro.lineaInicial);

            if (velocidad <= 0) {
                throw new IOException(
                        "Linea " + registro.lineaInicial
                        + ": la velocidad debe ser mayor que cero."
                );
            }

            tablaTemporal.insertar(new Neurotransmisor(id, nombre, efecto, velocidad, descripcion));
            datosEncontrados = true;
        }

        if (!datosEncontrados) {
            throw new IOException(
                    "No se encontraron registros validos en el diccionario. "
                    + "El archivo debe tener columnas: id, nombre, efecto, velocidad, descripcion."
            );
        }

        tabla.limpiar();

        for (Neurotransmisor nt : tablaTemporal.obtenerTodos()) {
            tabla.insertar(nt);
        }
    }

    /**
     * Guarda la red actual en formato CSV separado por coma.
     *
     * @param ruta ruta del archivo destino
     * @param grafo grafo a guardar
     * @throws IOException si ocurre un error de escritura
     */
    public void guardarRed(String ruta, GrafoSinaptico grafo) throws IOException {
        if (grafo == null) {
            throw new IOException("El grafo no esta inicializado.");
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ruta), StandardCharsets.UTF_8))) {

            bw.write("origen,destino,distancia,ID_Neurotransmisor,coheficiente_eficiencia_sináptica");
            bw.newLine();

            for (Sinapsis s : grafo.obtenerTodasLasSinapsis()) {
                bw.write(escaparCSV(s.getOrigenId()));
                bw.write(",");
                bw.write(escaparCSV(s.getDestinoId()));
                bw.write(",");
                bw.write(Double.toString(s.getDistancia()));
                bw.write(",");
                bw.write(escaparCSV(s.getIdNeurotransmisor()));
                bw.write(",");
                bw.write(Double.toString(s.getCoeficienteEficiencia()));
                bw.newLine();
            }
        }

        marcarCambiosGuardados();
    }

    public boolean hayCambiosPendientes() {
        return hayCambiosNoGuardados;
    }

    public void marcarCambiosPendientes() {
        hayCambiosNoGuardados = true;
    }

    public void marcarCambiosGuardados() {
        hayCambiosNoGuardados = false;
    }

    private List<RegistroCSV> leerRegistrosCSV(String ruta) throws IOException {
        List<RegistroCSV> registros = new ArrayList<>();
        StringBuilder acumulado = new StringBuilder();

        int numeroLinea = 0;
        int lineaInicial = 1;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(ruta), StandardCharsets.UTF_8))) {

            String linea;

            while ((linea = br.readLine()) != null) {
                numeroLinea++;

                if (acumulado.length() == 0) {
                    lineaInicial = numeroLinea;
                } else {
                    acumulado.append("\n");
                }

                acumulado.append(linea);

                if (comillasBalanceadas(acumulado.toString())) {
                    registros.add(new RegistroCSV(acumulado.toString(), lineaInicial));
                    acumulado.setLength(0);
                }
            }
        }

        if (acumulado.length() > 0) {
            throw new IOException(
                    "El archivo CSV tiene comillas sin cerrar cerca de la linea " + lineaInicial + "."
            );
        }

        return registros;
    }

    private boolean comillasBalanceadas(String texto) {
        boolean dentroDeComillas = false;

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (c == '"') {
                if (dentroDeComillas && i + 1 < texto.length() && texto.charAt(i + 1) == '"') {
                    i++;
                } else {
                    dentroDeComillas = !dentroDeComillas;
                }
            }
        }

        return !dentroDeComillas;
    }

    private boolean esLineaIgnorable(String linea) {
        String l = linea.trim().toLowerCase();

        return l.isEmpty()
                || l.equals("none")
                || l.equals("archivo:")
                || l.startsWith("archivo:")
                || l.startsWith("sep=")
                || l.startsWith("#");
    }

    private boolean esCabeceraRed(String linea) {
        String l = linea.toLowerCase();

        return (l.contains("origen") && l.contains("destino"))
                || l.contains("id_neurona_origen")
                || l.contains("id_neurotransmisor");
    }

    private boolean esCabeceraDiccionario(String linea) {
        String l = linea.toLowerCase();

        return l.startsWith("id,")
                || l.startsWith("id;")
                || l.contains("nombre") && l.contains("velocidad")
                || l.contains("descripcion") && l.contains("efecto");
    }

    private char detectarSeparador(String linea) {
        int comas = contarSeparadorFueraDeComillas(linea, ',');
        int puntoYComas = contarSeparadorFueraDeComillas(linea, ';');
        int tabs = contarSeparadorFueraDeComillas(linea, '\t');

        if (puntoYComas >= comas && puntoYComas >= tabs && puntoYComas > 0) {
            return ';';
        }

        if (tabs >= comas && tabs >= puntoYComas && tabs > 0) {
            return '\t';
        }

        return ',';
    }

    private int contarSeparadorFueraDeComillas(String linea, char separador) {
        boolean dentroDeComillas = false;
        int contador = 0;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                if (dentroDeComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    i++;
                } else {
                    dentroDeComillas = !dentroDeComillas;
                }
            } else if (c == separador && !dentroDeComillas) {
                contador++;
            }
        }

        return contador;
    }

    private List<String> parsearLineaCSV(String linea, char separador) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean dentroDeComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                if (dentroDeComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
                    dentroDeComillas = !dentroDeComillas;
                    actual.append(c);
                }
            } else if (c == separador && !dentroDeComillas) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }

        campos.add(actual.toString());
        return campos;
    }

    private String limpiarCampo(String campo) {
        if (campo == null) {
            return "";
        }

        String valor = campo.trim();

        if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
        }

        return valor.replace("\"\"", "\"").trim();
    }

    private double parsearDouble(String texto, String campo, int numeroLinea) throws IOException {
        try {
            String normalizado = texto.trim()
                    .replace("\u00A0", "")
                    .replace(",", ".");

            return Double.parseDouble(normalizado);
        } catch (NumberFormatException ex) {
            throw new IOException(
                    "Linea " + numeroLinea
                    + ": el campo " + campo
                    + " debe ser numerico. Valor recibido: " + texto
            );
        }
    }

    private String removerBOM(String texto) {
        if (texto != null && texto.startsWith("\uFEFF")) {
            return texto.substring(1);
        }

        return texto;
    }

    private String escaparCSV(String valor) {
        if (valor == null) {
            return "";
        }

        boolean necesitaComillas = valor.contains(",")
                || valor.contains(";")
                || valor.contains("\"")
                || valor.contains("\n");

        String resultado = valor.replace("\"", "\"\"");

        if (necesitaComillas) {
            resultado = "\"" + resultado + "\"";
        }

        return resultado;
    }
}