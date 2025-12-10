package sim.Aplicacion;

import javafx.application.Application;
import javafx.stage.Stage;
import sim.controlador.CordinadorApp;

/**
 * Clase principal de la aplicación JavaFX.
 * Inicializa y lanza la interfaz gráfica de usuario.
 */
public class App extends Application {

    /**
     * Método de inicio de la aplicación JavaFX.
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
     * Fuerza el cierre del sistema para finalizar todos los hilos.
     *
     * @throws Exception si ocurre un error al detener la aplicación
     */
    @Override
    public void stop() throws Exception {
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
