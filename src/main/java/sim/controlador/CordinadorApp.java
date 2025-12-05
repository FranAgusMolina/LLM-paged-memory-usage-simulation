package sim.controlador;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sim.UI.MemoryGrid;
import sim.modelo.Frame;
import sim.modelo.PhysicalMemory;
import sim.negocio.MMUService;
import sim.negocio.SimulationManager;

import java.io.IOException;

public class CordinadorApp {
    private final Stage stage;
    private ControladorUI uiController; // Referencia a la UI

    // Negocio
    private SimulationManager simulador;
    private PhysicalMemory ram;
    private MMUService mmu;

    public CordinadorApp(Stage stage) {
        this.stage = stage;
    }

    public void iniciarAplicacion() {
        try {
            // 1. Inicializar el NEGOCIO (Hardware simulado)
            inicializarNegocio();

            // 2. Cargar la VISTA (FXML)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("src/main/resources/visualizacion.fxml"));
            Parent root = loader.load();

            // 3. Obtener el controlador de UI y configurarlo
            this.uiController = loader.getController();
            conectarLogicaConUI();

            // 4. Mostrar ventana
            Scene scene = new Scene(root);
            stage.setTitle("Simulador PagedAttention");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarNegocio() {
        this.ram = new PhysicalMemory(1024);
        this.mmu = new MMUService(ram, 16, 16);
        this.simulador = new SimulationManager(ram, mmu);
    }

    private void conectarLogicaConUI() {
        // A. Inyectar componentes visuales complejos
        uiController.inyectarMemoryGrid(new MemoryGrid(ram.getSize()));

        // B. Definir qué pasa cuando tocan los botones
        uiController.setOnIniciar(() -> simulador.iniciar());
        uiController.setOnDetener(() -> simulador.detener());

        // C. Definir qué pasa cuando el simulador avisa cambios
        simulador.setOnUpdate(() -> {
            // Usamos Platform.runLater AQUÍ, centralizado
            Platform.runLater(this::sincronizarDatosConVista);
        });
    }

    // Este método pasa los datos crudos del Modelo a la Vista
    private void sincronizarDatosConVista() {
        // 1. Actualizar colores de la RAM
        for (int i = 0; i < ram.getSize(); i++) {
            Frame f = ram.getFrame(i);
            Color c = f.isOcupado() ? Color.TOMATO : Color.LIGHTGRAY;
            // (Podrías mejorar esto obteniendo el color real del proceso)
            uiController.pintarBloqueMemoria(i, c);
        }

        // 2. Actualizar Métricas
        uiController.actualizarEstadisticas(mmu.getTlb().getHits(), mmu.getTlb().getMisses());

        // 3. Actualizar Tabla
        uiController.actualizarListaProcesos(simulador.getProcesosActivos());
    }
}
