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
import sim.util.Constantes;

import java.util.Collections;

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
        // 1. Primero inicializamos el modelo y el negocio
        inicializarNegocio();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/visualizacion.fxml"));

        try {
            Parent root = loader.load();

            this.uiController = loader.getController();
            if (this.uiController == null) {
                throw new RuntimeException("Error: El FXML no tiene un fx:controller asignado o no se pudo cargar.");
            }

            // 2. Conectamos la lógica con la UI ahora que ambos existen
            conectarLogicaConUI();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void inicializarNegocio() {
        this.ram = new PhysicalMemory(1024);
        this.mmu = new MMUService(ram, 16, 16);
        this.simulador = new SimulationManager(ram, mmu);
    }

    private void conectarLogicaConUI() {
        // 1. Inyectar la Grilla de Memoria (Visual)
        sim.UI.MemoryGrid gridVisual = new sim.UI.MemoryGrid(ram.getSize());
        uiController.inyectarMemoryGrid(gridVisual);

        // 2. Conectar Botones (Vista -> Negocio)
        uiController.setOnIniciar(() -> {
            simulador.iniciar();
        });

        uiController.setOnDetener(() -> {
            simulador.detener();
        });

        // 3. Conectar Actualizaciones del Simulador (Negocio -> Vista)
        simulador.setOnUpdate(() -> {
            // Usamos Platform.runLater para volver al hilo gráfico
            Platform.runLater(this::sincronizarDatosConVista);
        });

        // 4. Configurar la Selección de Tabla (Interacción Maestro-Detalle)
        uiController.getTablaProcesos().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        // A. Actualizamos la tabla pequeña con la Tabla de Páginas del proceso
                        uiController.mostrarTablaPaginas(newSelection.getPageTable().getMapa());

                        // B. Forzamos un repintado inmediato de la memoria para iluminar bloques en Naranja
                        sincronizarDatosConVista();
                    } else {
                        uiController.mostrarTablaPaginas(Collections.emptyMap());
                    }
                });
    }

    // Este método debe estar FUERA de conectarLogicaConUI
    private void sincronizarDatosConVista() {
        // -------------------------------------------------------------
        // PASO 1: Capturar el estado actual de la UI (Qué está mirando el usuario)
        // -------------------------------------------------------------
        LLMProcess procesoSeleccionado = uiController.getTablaProcesos().getSelectionModel().getSelectedItem();
        int pidSeleccionado = (procesoSeleccionado != null) ? procesoSeleccionado.getPid() : -1;

        // -------------------------------------------------------------
        // PASO 2: Pintar el Mapa de Memoria (Lógica Semántica)
        // -------------------------------------------------------------
        for (int i = 0; i < ram.getSize(); i++) {
            Frame frame = ram.getFrame(i);
            Color colorPintar;

            if (!frame.isOcupado()) {
                // ESTADO: LIBRE -> Gris Claro
                colorPintar = Color.web(Constantes.COLOR_LIBRE);
            } else {
                if (frame.getProcessId() == pidSeleccionado) {
                    // ESTADO: DESTACADO -> Naranja (Páginas del proceso seleccionado)
                    colorPintar = Color.web(Constantes.COLOR_DESTACADO); // DarkOrange
                } else {
                    // ESTADO: OCUPADO POR OTROS -> Azul Acero
                    colorPintar = Color.web(Constantes.COLOR_OCUPADO); // SlateGray
                }
            }

            uiController.pintarBloqueMemoria(i, colorPintar);
        }

        // -------------------------------------------------------------
        // PASO 3: Actualizar la Tabla de Procesos (Sin romper la selección)
        // -------------------------------------------------------------
        uiController.actualizarListaProcesos(simulador.getProcesosActivos());

        // Truco: Restaurar la selección si el proceso sigue vivo
        if (pidSeleccionado != -1) {
            boolean procesoSigueVivo = false;
            for (LLMProcess p : uiController.getTablaProcesos().getItems()) {
                if (p.getPid() == pidSeleccionado) {
                    uiController.getTablaProcesos().getSelectionModel().select(p);
                    procesoSigueVivo = true;
                    break;
                }
            }
            // Si el proceso murió o terminó, limpiamos la tabla de detalle
            if (!procesoSigueVivo) {
                uiController.mostrarTablaPaginas(Collections.emptyMap());
            }
        }

        // -------------------------------------------------------------
        // PASO 4: Actualizar Estadísticas de la TLB
        // -------------------------------------------------------------
        long hits = mmu.getTlb().getHits();
        long misses = mmu.getTlb().getMisses();
        uiController.actualizarEstadisticas(hits, misses);
    }
}