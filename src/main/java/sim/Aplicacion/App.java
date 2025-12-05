package sim.Aplicacion;

import javafx.application.Application;
import javafx.stage.Stage;
import sim.controlador.CordinadorApp;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Instanciamos el "Director de Orquesta" (AppController)
        // Le pasamos el escenario (Stage) para que él decida qué mostrar.
        CordinadorApp appController = new CordinadorApp(stage);

        // 2. Le damos la orden de arranque
        appController.iniciarAplicacion();
    }

    @Override
    public void stop() throws Exception {
        // Este método se ejecuta cuando cierras la ventana (X)
        super.stop();

        // Forzamos el cierre del sistema para matar los hilos de simulación
        // que podrían quedar corriendo en segundo plano.
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}