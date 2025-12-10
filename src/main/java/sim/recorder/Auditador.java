package sim.recorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase responsable de auditar y registrar los datos de la simulación en un archivo CSV temporal.
 * Cada instancia crea un archivo único que se elimina automáticamente al finalizar el programa.
 */
public class Auditador {
    private PrintWriter writer;
    private String nombreArchivo;
    private static final String CARPETA_DATOS = "src/main/resources/datos";

    /**
     * Crea un nuevo auditor y prepara el archivo de registro temporal.
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
     * Inicializa el archivo de registro, escribe la cabecera y marca el archivo para eliminación al salir.
     */
    private void inicializarArchivo() {
        try {
            File archivo = new File(nombreArchivo);
            archivo.deleteOnExit();

            writer = new PrintWriter(new FileWriter(archivo, true));
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
