package sim.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import sim.UI.MemoryGrid;
import sim.modelo.LLMProcess;

import java.util.List;

public class ControladorUI {

    // --- Elementos FXML ---
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private ScrollPane scrollMemoria;
    @FXML private Label lblHits, lblMisses;
    @FXML private TableView<LLMProcess> tablaProcesos;

    // Referencia al componente visual custom
    private MemoryGrid memoryGrid;

    // Acciones (Callbacks) que inyectará el AppController
    private Runnable onIniciarAction;
    private Runnable onDetenerAction;

    // Se llama automáticamente al cargar el FXML
    public void initialize() {
        // Configuraciones visuales iniciales (columnas de tabla, etc.)
    }

    // --- Métodos para que el AppController configure la UI ---

    public void setOnIniciar(Runnable action) { this.onIniciarAction = action; }
    public void setOnDetener(Runnable action) { this.onDetenerAction = action; }

    public void inyectarMemoryGrid(MemoryGrid grid) {
        this.memoryGrid = grid;
        this.scrollMemoria.setContent(grid);
    }

    // --- Métodos para ACTUALIZAR la UI (Pura cosmética) ---

    public void actualizarEstadisticas(long hits, long misses) {
        lblHits.setText("Hits: " + hits);
        lblMisses.setText("Misses: " + misses);
    }

    public void pintarBloqueMemoria(int index, Color color) {
        if (memoryGrid != null) {
            memoryGrid.pintarBloque(index, color);
        }
    }

    public void actualizarListaProcesos(List<LLMProcess> procesos) {
        tablaProcesos.getItems().setAll(procesos);
    }

    // --- Eventos de la UI ---

    @FXML
    private void onBtnStartClick() {
        if (onIniciarAction != null) {
            onIniciarAction.run(); // Delegamos al AppController
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