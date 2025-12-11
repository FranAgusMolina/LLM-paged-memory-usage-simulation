package sim.recorder;

import java. io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejecutor de scripts de R con los datos de la simulación.
 * Se encarga de buscar, validar y ejecutar scripts de análisis estadístico
 * sobre los archivos CSV generados por el sistema de auditoría.
 * No maneja interfaz gráfica, solo lógica de ejecución.
 */
public class RScriptRunner {
    private final String archivoCSV;
    private static final String CARPETA_SCRIPTS = "src/main/resources/scripts_r";

    /**
     * Crea un nuevo ejecutor de scripts de R.
     *
     * @param archivoCSV ruta del archivo CSV con los datos de la simulación
     */
    public RScriptRunner(String archivoCSV) {
        this.archivoCSV = archivoCSV;
    }

    /**
     * Obtiene la lista de scripts de R disponibles en la carpeta de scripts.
     * Solo busca archivos con extensión .R o .r
     *
     * @return lista de nombres de archivos de scripts encontrados
     */
    public List<String> obtenerScriptsDisponibles() {
        List<String> scripts = new ArrayList<>();
        File carpeta = new File(CARPETA_SCRIPTS);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
            return scripts;
        }

        File[] archivos = carpeta.listFiles((dir, name) ->
                name.endsWith(".R") || name.endsWith(".r")
        );

        if (archivos != null) {
            for (File f : archivos) {
                scripts.add(f.getName());
            }
        }

        return scripts;
    }

    /**
     * Ejecuta un script de R seleccionado pasándole el archivo CSV como argumento.
     * El script recibe la ruta del CSV como primer argumento de línea de comandos.
     *
     * @param nombreScript nombre del archivo del script a ejecutar
     * @return resultado de la ejecución con estado y mensaje
     */
    public ResultadoEjecucion ejecutarScript(String nombreScript) {
        String rutaCompleta = CARPETA_SCRIPTS + File. separator + nombreScript;

        try {
            if (! verificarRInstalado()) {
                return new ResultadoEjecucion(false,
                        "R no está instalado o Rscript no está en el PATH del sistema");
            }

            ProcessBuilder pb = new ProcessBuilder("Rscript", rutaCompleta, archivoCSV);
            pb.redirectErrorStream(true);

            Process proceso = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proceso.getInputStream())
            );

            StringBuilder salida = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                salida. append(linea).append("\n");
                System.out.println("[R] " + linea);
            }

            int exitCode = proceso.waitFor();

            if (exitCode == 0) {
                return new ResultadoEjecucion(true, salida.toString());
            } else {
                return new ResultadoEjecucion(false,
                        "El script falló con código: " + exitCode + "\n" + salida.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoEjecucion(false,
                    "Error al ejecutar el script: " + e. getMessage());
        }
    }

    /**
     * Verifica si R está instalado en el sistema y Rscript está disponible.
     *
     * @return true si Rscript está disponible, false en caso contrario
     */
    private boolean verificarRInstalado() {
        try {
            Process proceso = new ProcessBuilder("Rscript", "--version").start();
            int exitCode = proceso.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Encapsula el resultado de la ejecución de un script de R.
     * Contiene el estado de éxito y el mensaje de salida o error.
     */
    public static class ResultadoEjecucion {
        private final boolean exitoso;
        private final String mensaje;

        /**
         * Crea un nuevo resultado de ejecución.
         *
         * @param exitoso true si el script se ejecutó correctamente
         * @param mensaje mensaje de salida o error del script
         */
        public ResultadoEjecucion(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        /**
         * Verifica si la ejecución fue exitosa.
         *
         * @return true si el script se ejecutó sin errores
         */
        public boolean isExitoso() {
            return exitoso;
        }

        /**
         * Obtiene el mensaje de salida o error del script.
         *
         * @return texto con la salida estándar del script
         */
        public String getMensaje() {
            return mensaje;
        }
    }
}