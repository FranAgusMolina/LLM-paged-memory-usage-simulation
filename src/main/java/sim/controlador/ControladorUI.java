package sim.controlador;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx. beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx. scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx. scene.image.Image;
import javafx. scene.image.ImageView;
import javafx.scene.layout. VBox;
import javafx. stage.Stage;
import java.io.File;
import sim.UI.MemoryGrid;
import sim.modelo.LLMProcess;
import sim.recorder.RScriptRunner;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador de la interfaz gráfica JavaFX que gestiona la visualización
 * de la simulación de memoria paginada.
 * Coordina la actualización de tablas, estadísticas y el grid de memoria visual.
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

    @FXML private TableView<Map. Entry<Integer, Integer>> tablaPaginas;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> colPaginaVirtual;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> colMarcoFisico;

    private MemoryGrid memoryGrid;
    private Runnable onIniciarAction;
    private Runnable onDetenerAction;
    private RScriptRunner rScriptRunner;


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
                new SimpleIntegerProperty(cell. getValue().getPid()).asObject());
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
     * Configura el ejecutor de scripts de R para el sistema de reportes.
     *
     * @param runner instancia del ejecutor de scripts
     */
    public void setRScriptRunner(RScriptRunner runner) {
        this.rScriptRunner = runner;
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
     * Habilita o deshabilita el botón de reporte.
     *
     * @param disable true para deshabilitar, false para habilitar
     */
    public void setReporteDisable(boolean disable) {
        if (btnReporte != null) btnReporte.setDisable(disable);
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
        tablaPaginas. getItems().setAll(mapaPaginas. entrySet());
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
            btnReporte.setDisable(true);
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
            btnReporte.setDisable(false);
        }
    }

    /**
     * Manejador del evento del botón Reporte.
     * Muestra diálogo de selección y ejecuta el script de R elegido.
     */
    @FXML
    private void onBtnReporteClick() {
        if (rScriptRunner == null) {
            mostrarAlerta("Error", "Sistema de reportes no configurado", Alert.AlertType.ERROR);
            return;
        }

        List<String> scripts = rScriptRunner.obtenerScriptsDisponibles();

        if (scripts.isEmpty()) {
            mostrarAlerta("No hay scripts",
                    "No se encontraron scripts de R.\nColoca archivos .R en:  src/main/resources/scripts_r/",
                    Alert.AlertType. WARNING);
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(scripts.get(0), scripts);
        dialog.setTitle("Generar Reporte");
        dialog.setHeaderText("Selecciona el tipo de análisis");
        dialog.setContentText("Script de R:");
        dialog.getDialogPane().setPrefWidth(400);

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            ejecutarScriptSeleccionado(resultado.get());
        }
    }

    /**
     * Ejecuta el script seleccionado en un hilo separado y muestra el resultado.
     * Si el script genera imágenes, las muestra en un diálogo visual.
     *
     * @param nombreScript nombre del archivo de script a ejecutar
     */
    private void ejecutarScriptSeleccionado(String nombreScript) {
        Alert esperaDialog = new Alert(Alert.AlertType.INFORMATION);
        esperaDialog.setTitle("Ejecutando.. .");
        esperaDialog.setHeaderText("Ejecutando script de R");
        esperaDialog.setContentText("Por favor espera...");
        esperaDialog.show();

        new Thread(() -> {
            RScriptRunner. ResultadoEjecucion resultado = rScriptRunner.ejecutarScript(nombreScript);
            List<File> archivosGenerados = rScriptRunner.obtenerArchivosGenerados(nombreScript);

            Platform.runLater(() -> {
                esperaDialog.close();

                if (resultado.isExitoso()) {
                    // Mostrar mensaje de éxito
                    mostrarAlerta("Reporte Generado",
                            "Script ejecutado exitosamente:\n\n" + resultado.getMensaje(),
                            Alert.AlertType.INFORMATION);

                    // Si hay imágenes, mostrarlas
                    if (!archivosGenerados.isEmpty()) {
                        mostrarImagenesGeneradas(archivosGenerados);
                    }
                } else {
                    mostrarAlerta("Error en Script",
                            resultado.getMensaje(),
                            Alert.AlertType.ERROR);
                }
            });
        }).start();
    }

    /**
     * Muestra las imágenes generadas por el script en una nueva ventana.
     *
     * @param archivos lista de archivos de imagen a mostrar
     */
    private void mostrarImagenesGeneradas(List<File> archivos) {
        Stage stage = new Stage();
        stage.setTitle("Gráficos Generados");

        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(15));
        contenedor.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(contenedor);
        scrollPane.setFitToWidth(true);

        for (File archivo : archivos) {
            try {
                Image imagen = new Image(archivo.toURI().toString());
                ImageView imageView = new ImageView(imagen);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(900);

                Label titulo = new Label("📊 " + archivo.getName());
                titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                VBox bloqueImagen = new VBox(5, titulo, imageView);
                bloqueImagen.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");

                contenedor. getChildren().add(bloqueImagen);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + archivo.getName());
            }
        }

        Scene scene = new Scene(scrollPane, 950, 700);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Muestra un diálogo de alerta genérico.
     *
     * @param titulo título de la ventana del diálogo
     * @param mensaje contenido del mensaje
     * @param tipo tipo de alerta (INFORMATION, WARNING, ERROR)
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}