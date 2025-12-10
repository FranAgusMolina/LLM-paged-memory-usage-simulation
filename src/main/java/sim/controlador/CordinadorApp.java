package sim.controlador;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sim.modelo.Frame;
import sim.modelo.LLMProcess;
import sim.modelo.PhysicalMemory;
import sim.negocio.MMUService;
import sim.negocio.SimulationManager;
import sim.recorder.Auditador;
import sim.util.Constantes;

import java.util.Collections;

/**
 * Coordinador principal de la aplicación que integra la interfaz gráfica
 * con la lógica de simulación de memoria paginada.
 * Actúa como mediador entre la UI (JavaFX) y los componentes de negocio.
 */
public class CordinadorApp {
    private final Stage stage;
    private ControladorUI uiController;

    private SimulationManager simulador;
    private PhysicalMemory ram;
    private MMUService mmu;

    private Auditador auditador;

    /**
     * Crea un nuevo coordinador de aplicación.
     *
     * @param stage ventana principal de JavaFX
     */
    public CordinadorApp(Stage stage) {
        this.stage = stage;
    }

    /**
     * Inicializa y lanza la aplicación completa:
     * - Componentes de negocio (RAM, MMU)
     * - Sistema de auditoría
     * - Simulador
     * - Interfaz gráfica
     * - Conexiones entre lógica y UI
     */
    public void iniciarAplicacion() {
        inicializarNegocio();
        inicializarRecorder();
        inicilizarSimulacion();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/visualizacion.fxml"));
            Parent root = loader.load();

            this.uiController = loader.getController();
            if (this.uiController == null) throw new RuntimeException("Error loading controller");

            conectarLogicaConUI();

            Scene scene = new Scene(root);
            stage.setTitle("Simulador PagedAttention");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Inicializa los componentes de negocio principales:
     * - Memoria física (RAM)
     * - Unidad de gestión de memoria (MMU) con TLB
     */
    private void inicializarNegocio() {
        this.ram = new PhysicalMemory(Constantes.TOTAL_MARCOS_RAM);
        this.mmu = new MMUService(ram, Constantes.TAMANIO_TLB, Constantes.TAMANIO_PAGINA);
    }

    /**
     * Conecta los eventos de la UI con la lógica de simulación:
     * - Inyecta el grid visual de memoria
     * - Configura callbacks de botones (iniciar/detener)
     * - Establece listeners para actualización automática
     * - Configura resaltado visual al seleccionar procesos
     */
    private void conectarLogicaConUI() {
        sim.UI.MemoryGrid gridVisual = new sim.UI.MemoryGrid(ram.getSize());
        uiController.inyectarMemoryGrid(gridVisual);

        uiController.setOnIniciar(() -> simulador.iniciar());
        uiController.setOnDetener(() -> simulador.detener());

        simulador.setOnUpdate(() -> Platform.runLater(this::sincronizarSimulacion));

        uiController.getTablaProcesos().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    refrescarVistaVisual();
                });
    }

    /**
     * Sincroniza el estado de la simulación con la interfaz gráfica.
     * Se ejecuta en el hilo de JavaFX cada vez que la simulación avanza un ciclo.
     * - Actualiza lista de procesos activos
     * - Restaura la selección del proceso previamente seleccionado
     * - Actualiza estadísticas de TLB
     * - Refresca la visualización de memoria
     */
    private void sincronizarSimulacion() {
        LLMProcess seleccionadoPrevio = uiController.getTablaProcesos().getSelectionModel().getSelectedItem();
        int pidPrevio = (seleccionadoPrevio != null) ? seleccionadoPrevio.getPid() : -1;

        uiController.actualizarListaProcesos(simulador.getProcesosActivos());

        if (pidPrevio != -1) {
            for (LLMProcess p : uiController.getTablaProcesos().getItems()) {
                if (p.getPid() == pidPrevio) {
                    uiController.getTablaProcesos().getSelectionModel().select(p);
                    break;
                }
            }
        }

        uiController.actualizarEstadisticas(mmu.getTlb().getHits(), mmu.getTlb().getMisses());
        refrescarVistaVisual();
    }

    /**
     * Actualiza la visualización de la memoria física aplicando colores según el estado:
     * - Gris: marcos libres
     * - Azul: marcos ocupados por otros procesos
     * - Naranja: marcos del proceso seleccionado (resaltado)
     * También actualiza la tabla de páginas del proceso seleccionado.
     */
    private void refrescarVistaVisual() {
        LLMProcess procesoSeleccionado = uiController.getTablaProcesos().getSelectionModel().getSelectedItem();
        int pidSeleccionado = (procesoSeleccionado != null) ? procesoSeleccionado.getPid() : -1;

        for (int i = 0; i < ram.getSize(); i++) {
            Frame frame = ram.getFrame(i);
            Color colorPintar;

            if (!frame.isOcupado()) {
                colorPintar = Color.web(Constantes.COLOR_LIBRE);
            } else {
                if (frame.getProcessId() == pidSeleccionado) {
                    colorPintar = Color.web(Constantes.COLOR_DESTACADO);
                } else {
                    colorPintar = Color.web(Constantes.COLOR_OCUPADO);
                }
            }
            uiController.pintarBloqueMemoria(i, colorPintar);
        }

        if (procesoSeleccionado != null) {
            uiController.mostrarTablaPaginas(procesoSeleccionado.getPageTable().getMapa());
        } else {
            uiController.mostrarTablaPaginas(Collections.emptyMap());
        }
    }

    /**
     * Inicializa el gestor de simulación con los componentes de negocio
     * y el sistema de auditoría.
     */
    private void inicilizarSimulacion(){
        this.simulador = new SimulationManager(ram, mmu, auditador);
    }

    /**
     * Inicializa el sistema de auditoría para registro de métricas
     * y eventos de la simulación.
     */
    private void inicializarRecorder(){
        this.auditador = new Auditador();
    }
}
