package sim.Aplicacion;

import javafx.application.Application;
import javafx.stage.Stage;
import sim.controlador.CordinadorApp;
import sim.recorder.Auditador;
import sim.recorder.RScriptRunner;

/**
 * Clase principal de la aplicación JavaFX.
 * Inicializa y lanza la interfaz gráfica de usuario.
 */
public class App extends Application {


    /**
     * Metodo de inicio de la aplicación JavaFX.
     * Instancia el coordinador de la aplicación y le pasa el escenario principal.
     *
     * @param stage escenario principal de la aplicación
     */
    @Override
    public void start(Stage stage) {
        CordinadorApp appController = new CordinadorApp(stage);
        appController.iniciarAplicacion();
    }

    /**
     * Método que se ejecuta al cerrar la ventana principal.
     * Limpia los archivos temporales (imágenes y datos CSV).
     * Fuerza el cierre del sistema para finalizar todos los hilos.
     *
     * @throws Exception si ocurre un error al detener la aplicación
     */
    @Override
    public void stop() throws Exception {
        // Limpiar archivos temporales
        System.out.println("Limpiando archivos temporales...");

        // Limpiar imágenes temporales generadas por R
        RScriptRunner tempRunner = new RScriptRunner("dummy");
        tempRunner.limpiarArchivosTemporales();

        // Limpiar archivos CSV de datos
        Auditador.limpiarArchivosTemporales();

        System.out.println("Archivos temporales eliminados. Cerrando aplicación...");

        super.stop();
        System.exit(0);
    }

    /**
     * Método principal que lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch();
    }
}
