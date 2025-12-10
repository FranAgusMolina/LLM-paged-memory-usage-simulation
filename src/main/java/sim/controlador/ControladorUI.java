package sim.controlador;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import sim.UI.MemoryGrid;
import sim.modelo.LLMProcess;

import java.util.List;
import java.util.Map;

/**
 * Controlador de la interfaz gráfica JavaFX que gestiona la visualización
 * de la simulación de memoria paginada.
 * Coordina la actualización de tablas, estadísticas y el grid de memoria visual.
 */
public class ControladorUI {

    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
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

    /**
     * Metodo llamado automáticamente por JavaFX después de cargar el FXML.
     * Configura el binding de datos para las tablas de procesos y páginas.
     */
    @FXML
    public void initialize() {
        configurarTablaProcesos();
        configurarTablaPaginas();
    }

    /**
     * Configura el binding de columnas de la tabla de procesos activos.
     * Enlaza cada columna con las propiedades del objeto LLMProcess.
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
     * Configura el binding de columnas de la tabla de páginas.
     * Muestra las traducciones de página virtual a marco físico.
     */
    private void configurarTablaPaginas() {
        colPaginaVirtual.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getKey()).asObject());

        colMarcoFisico.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getValue()).asObject());
    }

    /**
     * Establece la acción a ejecutar al presionar el botón Iniciar.
     *
     * @param action callback que inicia la simulación
     */
    public void setOnIniciar(Runnable action) {
        this.onIniciarAction = action;
    }

    /**
     * Establece la acción a ejecutar al presionar el botón Detener.
     *
     * @param action callback que detiene la simulación
     */
    public void setOnDetener(Runnable action) {
        this.onDetenerAction = action;
    }

    /**
     * Inyecta el componente visual de la grilla de memoria en el ScrollPane.
     *
     * @param grid componente personalizado que visualiza la memoria física
     */
    public void inyectarMemoryGrid(MemoryGrid grid) {
        this.memoryGrid = grid;
        this.scrollMemoria.setContent(grid);
    }

    /**
     * Actualiza las estadísticas de la TLB en la interfaz.
     * Calcula y muestra el porcentaje de aciertos (hit rate).
     *
     * @param hits número de aciertos en la TLB
     * @param misses número de fallos en la TLB
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
     * Cambia el color de un bloque específico en la grilla de memoria.
     *
     * @param index índice del marco físico a pintar
     * @param color color que representa el estado del marco
     */
    public void pintarBloqueMemoria(int index, Color color) {
        if (memoryGrid != null) {
            memoryGrid.pintarBloque(index, color);
        }
    }

    /**
     * Actualiza la lista de procesos activos en la tabla.
     *
     * @param procesos lista de procesos LLM actualmente en ejecución
     */
    public void actualizarListaProcesos(List<LLMProcess> procesos) {
        tablaProcesos.getItems().setAll(procesos);
    }

    /**
     * Actualiza la tabla de páginas con las traducciones del proceso seleccionado.
     *
     * @param mapaPaginas mapa de traducciones (página virtual → marco físico)
     */
    public void mostrarTablaPaginas(Map<Integer, Integer> mapaPaginas) {
        tablaPaginas.getItems().setAll(mapaPaginas.entrySet());
    }

    /**
     * Obtiene la referencia a la tabla de procesos.
     * Permite al coordinador agregar listeners de selección.
     *
     * @return tabla de procesos activos
     */
    public TableView<LLMProcess> getTablaProcesos() {
        return tablaProcesos;
    }

    /**
     * Manejador del evento del botón Iniciar.
     * Ejecuta el callback configurado y ajusta el estado de los botones.
     */
    @FXML
    private void onBtnStartClick() {
        if (onIniciarAction != null) {
            onIniciarAction.run();
            btnIniciar.setDisable(true);
            btnDetener.setDisable(false);
        }
    }

    /**
     * Manejador del evento del botón Detener.
     * Ejecuta el callback configurado y ajusta el estado de los botones.
     */
    @FXML
    private void onBtnStopClick() {
        if (onDetenerAction != null) {
            onDetenerAction.run();
            btnIniciar.setDisable(false);
            btnDetener.setDisable(true);
        }
    }
}
