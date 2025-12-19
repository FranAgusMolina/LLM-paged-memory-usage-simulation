package sim.controlador;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene. Scene;
import javafx.scene.paint.Color;
import javafx. stage.Stage;
import sim.UI.MemoryGrid;
import sim.datos.CargarPerfiles;
import sim. modelo.Frame;
import sim.modelo.LLMProcess;
import sim.modelo.Perfil;
import sim. modelo.PhysicalMemory;
import sim.negocio.MMUService;
import sim.negocio.SimulationManager;
import sim.recorder. Auditador;
import sim.recorder.RScriptRunner;
import sim.datos.Constantes;

import java.util.Collections;
import java.util.LinkedHashMap;

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

    private LinkedHashMap<String, Perfil> perfiles;
    private Perfil perfil;

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
        inicializarPerfiles("Servidor Estándar (ChatGPT)");
        inicializarNegocio();
        inicializarRecorder();
        inicializarSimulacion();

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

    private void inicializarPerfiles(String perfilSeleccionado) {
        // Cargar perfiles solo la primera vez (singleton)
        if (perfiles == null) {
            perfiles = CargarPerfiles.cargar();
        }

        // Validar que el perfil seleccionado exista
        if (perfilSeleccionado != null && perfiles.containsKey(perfilSeleccionado)) {
            perfil = perfiles.get(perfilSeleccionado);
        } else {
            // Usar el primer perfil disponible como fallback
            perfil = perfiles.values().iterator().next();
            System.out.println("⚠️ Perfil '" + perfilSeleccionado + "' no encontrado. Usando: " + perfil.getNombre());
        }
    }

    /**
     * Inicializa los componentes de negocio principales:
     * - Memoria física (RAM)
     * - Unidad de gestión de memoria (MMU) con TLB
     */
    private void inicializarNegocio() {
        this.ram = new PhysicalMemory(perfil.getTotalMarcosRam());
        this.mmu = new MMUService(ram, perfil.getTLBSize(), perfil.getPageSize());
    }

    /**
     * Conecta los eventos de la UI con la lógica de simulación:
     * - Inyecta el grid visual de memoria
     * - Configura callbacks de botones (iniciar/detener/reiniciar)
     * - Configura sistema de reportes con R
     * - Establece listeners para actualización automática
     * - Configura resaltado visual al seleccionar procesos
     */
    private void conectarLogicaConUI() {
        MemoryGrid gridVisual = new MemoryGrid(ram.getSize(), perfil);
        uiController.inyectarMemoryGrid(gridVisual);

        uiController.setOnIniciar(() -> simulador.iniciar());
        uiController.setOnDetener(() -> simulador.pausar());
        uiController.setOnReiniciar(this::reiniciarAplicacion);
        uiController.setOnAplicarConfig(this::reiniciarConPerfil);

        String nombreCSV = auditador.getNombreArchivo();
        RScriptRunner rRunner = new RScriptRunner(nombreCSV);
        uiController.setRScriptRunner(rRunner);

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
        uiController.actualizarTablaTLB(mmu.getTlb().getCache());
        refrescarVistaVisual();
    }

    /**
     * Actualiza la visualización de la memoria física aplicando colores según el estado:
     * - blanco:  marcos libres
     * - gris: marcos ocupados por otros procesos
     * - Naranja: marcos del proceso seleccionado (resaltado)
     * También actualiza la tabla de páginas del proceso seleccionado.
     */
    private void refrescarVistaVisual() {
        LLMProcess procesoSeleccionado = uiController.getTablaProcesos().getSelectionModel().getSelectedItem();
        int pidSeleccionado = (procesoSeleccionado != null) ? procesoSeleccionado.getPid() : -1;

        for (int i = 0; i < ram.getSize(); i++) {
            Frame frame = ram.getFrame(i);
            Color colorPintar;

            if (! frame.isOcupado()) {
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
            uiController. mostrarTablaPaginas(Collections.emptyMap());
        }
    }

    /**
     * Inicializa el gestor de simulación con los componentes de negocio
     * y el sistema de auditoría.
     */
    private void inicializarSimulacion(){
        this.simulador = new SimulationManager(ram, mmu, auditador, perfil);
    }

    /**
     * Inicializa el sistema de auditoría para registro de métricas
     * y eventos de la simulación.
     */
    private void inicializarRecorder(){
        this.auditador = new Auditador();
    }

    /**
     * Reinicia completamente la aplicación:
     * - Detiene y limpia la simulación actual
     * - Limpia archivos temporales (datos e imágenes)
     * - Reinicia todos los componentes
     * - Refresca la interfaz gráfica
     */
    public void reiniciarAplicacion() {
        System.out.println("=== INICIANDO REINICIO COMPLETO ===");

        // 1. Detener y limpiar simulación
        simulador.reiniciar();

        // 2. Limpiar archivos temporales
        Auditador.limpiarArchivosTemporales();
        RScriptRunner tempRunner = new RScriptRunner("dummy");
        tempRunner.limpiarArchivosTemporales();

        // 3. Reiniciar componentes
        inicializarNegocio();
        inicializarRecorder();

        // 4. Crear nuevo simulador
        this.simulador = new SimulationManager(ram, mmu, auditador, perfil);

        // 5. Reconectar UI (mantener callbacks)
        simulador.setOnUpdate(() -> Platform.runLater(this::sincronizarSimulacion));

        // 6. Actualizar RScriptRunner con nuevo CSV
        String nombreCSV = auditador.getNombreArchivo();
        RScriptRunner rRunner = new RScriptRunner(nombreCSV);
        uiController.setRScriptRunner(rRunner);

        // 7. Refrescar visualización completa
        Platform.runLater(() -> {
            uiController.actualizarListaProcesos(simulador.getProcesosActivos());
            uiController.actualizarEstadisticas(0, 0);
            uiController.actualizarTablaTLB(Collections.emptyMap());
            uiController.mostrarTablaPaginas(Collections.emptyMap());
            refrescarVistaVisual();
        });

        System.out.println("=== REINICIO COMPLETO FINALIZADO ===");
    }

    /**
     * Reinicia la aplicación con un perfil diferente.
     * Carga el nuevo perfil y reinicia todos los componentes con la nueva configuración.
     *
     * @param nombrePerfil nombre del perfil a cargar
     */
    public void reiniciarConPerfil(String nombrePerfil) {
        System.out.println("=== REINICIANDO CON PERFIL: " + nombrePerfil + " ===");

        // 1. Detener simulación actual
        simulador.reiniciar();

        // 2. Limpiar archivos temporales
        Auditador.limpiarArchivosTemporales();
        RScriptRunner tempRunner = new RScriptRunner("dummy");
        tempRunner.limpiarArchivosTemporales();

        // 3. Cargar nuevo perfil
        inicializarPerfiles(nombrePerfil);

        // 4. Reiniciar componentes con nueva configuración
        inicializarNegocio();
        inicializarRecorder();

        // 5. Crear nuevo simulador
        this.simulador = new SimulationManager(ram, mmu, auditador, perfil);

        // 6. Recrear grilla visual con nuevo tamaño
        MemoryGrid nuevaGrilla = new MemoryGrid(ram.getSize(), perfil);
        uiController.inyectarMemoryGrid(nuevaGrilla);

        // 7. Reconectar callbacks
        simulador.setOnUpdate(() -> Platform.runLater(this::sincronizarSimulacion));

        // 8. Actualizar RScriptRunner con nuevo CSV
        String nombreCSV = auditador.getNombreArchivo();
        RScriptRunner rRunner = new RScriptRunner(nombreCSV);
        uiController.setRScriptRunner(rRunner);

        // 9. Refrescar visualización completa
        Platform.runLater(() -> {
            uiController.actualizarListaProcesos(simulador.getProcesosActivos());
            uiController.actualizarEstadisticas(0, 0);
            uiController.actualizarTablaTLB(Collections.emptyMap());
            uiController.mostrarTablaPaginas(Collections.emptyMap());
            refrescarVistaVisual();
        });

        System.out.println("=== REINICIO CON NUEVO PERFIL FINALIZADO ===");
    }

}