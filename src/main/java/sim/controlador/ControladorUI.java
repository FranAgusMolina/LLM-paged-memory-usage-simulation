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

    private MemoryGrid memoryGrid;
    private Runnable onIniciarAction;
    private Runnable onDetenerAction;
    private ReportController reporController;

    /**
     * Inicializa las tablas al cargar el FXML.
     */
    @FXML
    public void initialize() {
        configurarTablaProcesos();
        configurarTablaPaginas();
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
     * Maneja el clic en el botón Reporte.
     */
    @FXML
    private void onBtnReporteClick() {
        if (reporController != null) {
            reporController.generarReporte();
        }
    }
}
