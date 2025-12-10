package sim.recorder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase responsable de auditar y registrar los datos de la simulación en un archivo CSV.
 * Cada instancia crea un archivo único para almacenar los ciclos, procesos activos,
 * marcos ocupados, TLB hits y misses.
 */
public class Auditador {
    private PrintWriter writer;
    private String nombreArchivo;
    private static final String CARPETA_DATOS = "src/main/resources/datos";

    /**
     * Crea un nuevo auditor y prepara el archivo de registro.
     */
    public Auditador() {
        this.nombreArchivo = obtenerNombreArchivo();
        inicializarArchivo();
    }

    /**
     * Genera un nombre de archivo único para el registro de datos.
     *
     * @return nombre del archivo CSV a utilizar
     */
    private String obtenerNombreArchivo() {
        String nombreBase = "data";
        String extension = ".csv";
        Path path = Paths.get(CARPETA_DATOS, nombreBase + extension);

        if (!Files.exists(path)) {
            return Paths.get(CARPETA_DATOS, nombreBase + extension).toString();
        }

        int contador = 1;
        while (Files.exists(Paths.get(CARPETA_DATOS, nombreBase + "(" + contador + ")" + extension))) {
            contador++;
        }

        return Paths.get(CARPETA_DATOS, nombreBase + "(" + contador + ")" + extension).toString();
    }

    /**
     * Inicializa el archivo de registro y escribe la cabecera.
     */
    private void inicializarArchivo() {
        try {
            writer = new PrintWriter(new FileWriter(nombreArchivo, true));
            writer.println("Ciclo,Procesos_Activos,Marcos_Ocupados,TLB_Hits,TLB_Misses");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error al crear el archivo: " + e.getMessage());
        }
    }

    /**
     * Registra una línea de datos en el archivo CSV.
     *
     * @param ciclo número de ciclo de la simulación
     * @param procesosActivos cantidad de procesos activos
     * @param marcosOcupados cantidad de marcos ocupados
     * @param tlbHits cantidad de aciertos en la TLB
     * @param tlbMisses cantidad de fallos en la TLB
     */
    public void registrar(int ciclo, int procesosActivos, int marcosOcupados, int tlbHits, int tlbMisses) {
        if (writer != null) {
            writer.println(ciclo + "," + procesosActivos + "," + marcosOcupados + "," + tlbHits + "," + tlbMisses);
            writer.flush();
        }
    }

    /**
     * Cierra el archivo de registro.
     */
    public void cerrar() {
        if (writer != null) {
            writer.close();
        }
    }

    /**
     * Obtiene el nombre del archivo de registro utilizado.
     *
     * @return nombre del archivo CSV
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
