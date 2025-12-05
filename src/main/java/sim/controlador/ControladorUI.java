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

public class ControladorUI {

    // --- Elementos FXML ---
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private ScrollPane scrollMemoria;

    // Etiquetas de Estadísticas
    @FXML private Label lblHits;
    @FXML private Label lblMisses;
    @FXML private Label lblRate; // Agregado para el porcentaje

    // Tabla de Procesos (Izquierda/Derecha según tu diseño)
    @FXML private TableView<LLMProcess> tablaProcesos;
    @FXML private TableColumn<LLMProcess, Integer> colPid;
    @FXML private TableColumn<LLMProcess, String> colNombre;
    @FXML private TableColumn<LLMProcess, Integer> colTokens;
    @FXML private TableColumn<LLMProcess, Integer> colMarcos; // Cantidad de marcos usados

    // Tabla de Páginas (Detalle del proceso seleccionado)
    @FXML private TableView<Map.Entry<Integer, Integer>> tablaPaginas;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> colPaginaVirtual;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> colMarcoFisico;

    // Referencia al componente visual custom (La grilla de cuadraditos)
    private MemoryGrid memoryGrid;

    // Acciones (Callbacks) que inyectará el AppController
    private Runnable onIniciarAction;
    private Runnable onDetenerAction;

    /**
     * Este método es llamado automáticamente por JavaFX después de cargar el FXML.
     * Aquí configuramos cómo las tablas leen los datos de los objetos.
     */
    @FXML
    public void initialize() {
        configurarTablaProcesos();
        configurarTablaPaginas();
    }

    private void configurarTablaProcesos() {
        // Enlazar columnas con los datos del objeto LLMProcess
        colPid.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPid()).asObject());

        colNombre.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNombre()));

        colTokens.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getContadorTokens()).asObject());

        // Calculamos cuántos marcos usa (tamaño del mapa de su PageTable)
        colMarcos.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPageTable().getMapa().size()).asObject());
    }

    private void configurarTablaPaginas() {
        // La tabla de páginas muestra entradas de un Mapa (Clave=Virtual -> Valor=Físico)
        colPaginaVirtual.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getKey()).asObject());

        colMarcoFisico.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getValue()).asObject());
    }

    // --- Métodos de Configuración (Llamados por CordinadorApp) ---

    public void setOnIniciar(Runnable action) { this.onIniciarAction = action; }
    public void setOnDetener(Runnable action) { this.onDetenerAction = action; }

    public void inyectarMemoryGrid(MemoryGrid grid) {
        this.memoryGrid = grid;
        this.scrollMemoria.setContent(grid);
    }

    // --- Métodos de Actualización Visual (Llamados durante la simulación) ---

    public void actualizarEstadisticas(long hits, long misses) {
        lblHits.setText("Hits: " + hits);
        lblMisses.setText("Misses: " + misses);

        // Calcular y mostrar porcentaje
        long total = hits + misses;
        if (total > 0) {
            double rate = (double) hits / total * 100;
            if (lblRate != null) {
                lblRate.setText(String.format("Hit Rate: %.1f%%", rate));
            }
        }
    }

    public void pintarBloqueMemoria(int index, Color color) {
        if (memoryGrid != null) {
            memoryGrid.pintarBloque(index, color);
        }
    }

    public void actualizarListaProcesos(List<LLMProcess> procesos) {
        // Actualizamos la lista. JavaFX se encarga de refrescar la tabla visualmente.
        tablaProcesos.getItems().setAll(procesos);
    }

    public void mostrarTablaPaginas(Map<Integer, Integer> mapaPaginas) {
        // Actualizamos la tabla de detalle inferior
        tablaPaginas.getItems().setAll(mapaPaginas.entrySet());
    }

    /**
     * Permite al Coordinador obtener la tabla para agregarle listeners de selección.
     */
    public TableView<LLMProcess> getTablaProcesos() {
        return tablaProcesos;
    }

    // --- Eventos de los Botones ---

    @FXML
    private void onBtnStartClick() {
        if (onIniciarAction != null) {
            onIniciarAction.run();
            btnIniciar.setDisable(true);
            btnDetener.setDisable(false);
        }
    }

    @FXML
    private void onBtnStopClick() {
        if (onDetenerAction != null) {
            onDetenerAction.run();
            btnIniciar.setDisable(false);
            btnDetener.setDisable(true);
        }
    }
}