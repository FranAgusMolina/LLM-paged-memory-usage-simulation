package sim.controlador;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import sim.UI.MemoryGrid;
import sim.modelo.LLMProcess;
import sim.recorder.RScriptRunner;
import java.util.List;
import java.util.Map;

/**
 * Controlador principal de la interfaz gráfica JavaFX.
 * Coordina la visualización de la simulación de memoria paginada.
 */
public class ControladorUI {

    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private Button btnReporte;
    @FXML private Button btnReiniciar;
    @FXML private Button btnAyuda;
    @FXML private ScrollPane scrollMemoria;

    @FXML private Label lblHits;
    @FXML private Label lblMisses;
    @FXML private Label lblRate;

    @FXML private TableView<LLMProcess> tablaProcesos;
    @FXML private TableColumn<LLMProcess, Integer> colPid;
    @FXML private TableColumn<LLMProcess, String> colNombre;
    @FXML private TableColumn<LLMProcess, Integer> colTokens;
    @FXML private TableColumn<LLMProcess, Integer> colMarcos;

    @FXML private TableView<Map.Entry<Integer, Integer>> tablaPaginas;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> colPaginaVirtual;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> colMarcoFisico;

    @FXML private TableView<Map.Entry<String, Integer>> tablaTLB;
    @FXML private TableColumn<Map.Entry<String, Integer>, String> colTLBPID;
    @FXML private TableColumn<Map.Entry<String, Integer>, Integer> colTLBPagina;
    @FXML private TableColumn<Map.Entry<String, Integer>, Integer> colTLBMarcoFisico;



    private MemoryGrid memoryGrid;
    private Runnable onIniciarAction;
    private Runnable onDetenerAction;
    private Runnable onReiniciarAction;
    private ReportController reporController;

    /**
     * Inicializa las tablas al cargar el FXML.
     */
    @FXML
    public void initialize() {
        configurarTablaProcesos();
        configurarTablaPaginas();
        configurarTablaTLB();
    }

    /**
     * Configura el binding de la tabla de procesos.
     */
    private void configurarTablaProcesos() {
        colPid.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPid()).asObject());
        colNombre.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNombre()));
        colTokens.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getContadorTokens()).asObject());
        colMarcos.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPageTable().getMapa().size()).asObject());
    }

    /**
     * Configura el binding de la tabla de páginas.
     */
    private void configurarTablaPaginas() {
        colPaginaVirtual.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getKey()).asObject());
        colMarcoFisico.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getValue()).asObject());
    }

    /**
     * Configura el binding de la tabla TLB.
     */
    private void configurarTablaTLB() {
        colTLBPID.setCellValueFactory(cell -> {
            String[] parts = cell.getValue().getKey().split(":");
            return new SimpleStringProperty(parts.length > 0 ? parts[0] : "?");
        });

        colTLBPagina.setCellValueFactory(cell -> {
            String[] parts = cell.getValue().getKey().split(":");
            int pagina = parts.length > 1 ? Integer.parseInt(parts[1]) : -1;
            return new SimpleIntegerProperty(pagina).asObject();
        });

        colTLBMarcoFisico.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getValue()).asObject());
    }

    /**
     * Establece la acción del botón Iniciar.
     *
     * @param action callback de inicio
     */
    public void setOnIniciar(Runnable action) {
        this.onIniciarAction = action;
    }

    /**
     * Establece la acción del botón Detener.
     *
     * @param action callback de detención
     */
    public void setOnDetener(Runnable action) {
        this.onDetenerAction = action;
    }

    /**
     * Establece la acción del botón Reiniciar.
     *
     * @param action callback de reinicio
     */
    public void setOnReiniciar(Runnable action) {
        this.onReiniciarAction = action;
    }

    /**
     * Configura el sistema de reportes.
     *
     * @param runner instancia del ejecutor de scripts
     */
    public void setRScriptRunner(RScriptRunner runner) {
        this.reporController = new ReportController(runner);
    }

    /**
     * Inyecta el grid de memoria en el ScrollPane.
     *
     * @param grid componente visual de memoria
     */
    public void inyectarMemoryGrid(MemoryGrid grid) {
        this.memoryGrid = grid;
        this.scrollMemoria.setContent(grid);
    }

    /**
     * Habilita o deshabilita el botón de reporte.
     *
     * @param disable true para deshabilitar
     */
    public void setReporteDisable(boolean disable) {
        if (btnReporte != null) btnReporte.setDisable(disable);
    }

    /**
     * Actualiza las estadísticas de la TLB.
     *
     * @param hits aciertos en TLB
     * @param misses fallos en TLB
     */
    public void actualizarEstadisticas(int hits, int misses) {
        lblHits.setText("Hits: " + hits);
        lblMisses.setText("Misses: " + misses);
        int total = hits + misses;
        if (total > 0) {
            double rate = (double) hits / total * 100;
            if (lblRate != null) {
                lblRate.setText(String.format("Hit Rate: %.1f%%", rate));
            }
        }
    }

    /**
     * Pinta un bloque de memoria.
     *
     * @param index índice del marco
     * @param color color a aplicar
     */
    public void pintarBloqueMemoria(int index, Color color) {
        if (memoryGrid != null) {
            memoryGrid.pintarBloque(index, color);
        }
    }

    /**
     * Actualiza la lista de procesos en la tabla.
     *
     * @param procesos lista de procesos activos
     */
    public void actualizarListaProcesos(List<LLMProcess> procesos) {
        tablaProcesos.getItems().setAll(procesos);
    }

    /**
     * Muestra la tabla de páginas del proceso seleccionado.
     *
     * @param mapaPaginas mapa de traducciones
     */
    public void mostrarTablaPaginas(Map<Integer, Integer> mapaPaginas) {
        tablaPaginas.getItems().setAll(mapaPaginas.entrySet());
    }

    /**
     * Actualiza el contenido de la tabla TLB con las entradas del cache.
     *
     * @param cacheTLB mapa con las entradas de la TLB (clave: "PID:Pagina", valor: marco físico)
     */
    public void actualizarTablaTLB(Map<String, Integer> cacheTLB) {
        if (tablaTLB != null) {
            tablaTLB.getItems().clear();
            tablaTLB.getItems().addAll(cacheTLB.entrySet());
        }
    }

    /**
     * Obtiene la referencia a la tabla de procesos.
     *
     * @return tabla de procesos
     */
    public TableView<LLMProcess> getTablaProcesos() {
        return tablaProcesos;
    }

    /**
     * Maneja el clic en el botón Iniciar.
     */
    @FXML
    private void onBtnStartClick() {
        if (onIniciarAction != null) {
            onIniciarAction.run();
            btnIniciar.setDisable(true);
            btnDetener.setDisable(false);
            btnReporte.setDisable(true);
        }
    }

    /**
     * Maneja el clic en el botón Detener.
     */
    @FXML
    private void onBtnStopClick() {
        if (onDetenerAction != null) {
            onDetenerAction.run();
            btnIniciar.setDisable(false);
            btnDetener.setDisable(true);
            btnReporte.setDisable(false);
        }
    }

    /**
     * Maneja el clic en el boton Reiniciar
     */
    @FXML
    private void onBtnReiniciarClick() {
        if (onReiniciarAction != null) {
            onReiniciarAction.run();
            btnIniciar.setDisable(false);
            btnDetener.setDisable(true);
            btnReporte.setDisable(true);
        }
    }

    /**
     * Alias para onBtnReiniciarClick (usado en el nuevo FXML)
     */
    @FXML
    private void onBtnResetClick() {
        onBtnReiniciarClick();
    }

    /**
     * Maneja el clic en el botón de Ayuda.
     * Muestra información sobre la simulación y cómo usarla.
     */
    @FXML
    private void onBtnAyudaClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ayuda - Simulador PagedAttention");
        alert.setHeaderText("Información del Simulador");

        String contenido = """
                📚 SOBRE LA SIMULACIÓN:
                Este simulador demuestra el funcionamiento de la memoria paginada con TLB (Translation Lookaside Buffer).
                Simula múltiples procesos LLM (conversaciones) compitiendo por memoria RAM.
                
                🎮 CONTROLES:
                • ▶ INICIAR: Comienza/reanuda la simulación
                • ⏸ PAUSAR: Pausa la simulación temporalmente
                • ↺ REINICIAR: Resetea todo a estado inicial
                • 📊 Generar Reporte: Crea análisis estadísticos con R
                
                📊 VISUALIZACIÓN:
                • Grid de Memoria: Muestra marcos físicos (gris=libre, color=ocupado)
                • Conversaciones Activas: Lista de procesos en ejecución
                • Tabla de Páginas: Mapeo virtual→físico del proceso seleccionado
                • Contenido TLB: Cache de traducciones de direcciones
                
                📈 MÉTRICAS TLB:
                • Hits: Traducciones encontradas en cache (rápido)
                • Misses: Traducciones no encontradas (lento, consulta RAM)
                • Hit Rate: Porcentaje de eficiencia de la TLB
                
                💡 CONCEPTOS:
                • Paginación: División de memoria en bloques de tamaño fijo
                • TLB: Cache pequeña y rápida para traducciones frecuentes
                • Page Fault: Cuando una página no está en memoria
                • PagedAttention: Técnica de optimización para LLMs
                
                👥 Arquitectura de Computadoras - 2025
                """;

        alert.setContentText(contenido);
        alert.getDialogPane().setPrefWidth(600);
        alert.showAndWait();
    }

    /**
     * Maneja el clic en el botón Reporte.
     */
    @FXML
    private void onBtnReporteClick() {
        if (reporController != null) {
            reporController.generarReporte();
        }
    }
}
