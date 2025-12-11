package sim.controlador;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import sim.UI.DialogManager;
import sim.recorder.RScriptRunner;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Controla la lógica de generación y visualización de reportes.
 */
public class ReportController {

    private final RScriptRunner rScriptRunner;
    private final DialogManager dialogManager;
    private final ReportViewer reporteViewer;

    /**
     * Constructor del controlador de reportes.
     *
     * @param rScriptRunner ejecutor de scripts de R
     */
    public ReportController(RScriptRunner rScriptRunner) {
        this.rScriptRunner = rScriptRunner;
        this.dialogManager = new DialogManager();
        this.reporteViewer = new ReportViewer();
    }

    /**
     * Inicia el flujo de generación de reportes.
     */
    public void generarReporte() {
        if (rScriptRunner == null) {
            dialogManager.mostrarAlerta("Error",
                    "Sistema de reportes no configurado",
                    Alert.AlertType.ERROR);
            return;
        }

        List<String> scripts = rScriptRunner.obtenerScriptsDisponibles();

        if (scripts.isEmpty()) {
            dialogManager.mostrarAlerta("No hay scripts",
                    "No se encontraron scripts de R.\nColoca archivos .R en: src/main/resources/scripts_r/",
                    Alert.AlertType.WARNING);
            return;
        }

        Optional<String> resultado = dialogManager.mostrarDialogoSeleccionScript(scripts);
        resultado.ifPresent(this::ejecutarScriptSeleccionado);
    }

    /**
     * Ejecuta el script seleccionado en un hilo separado.
     *
     * @param nombreScript nombre del archivo de script a ejecutar
     */
    private void ejecutarScriptSeleccionado(String nombreScript) {
        Alert esperaDialog = dialogManager.mostrarDialogoEspera();

        new Thread(() -> {
            RScriptRunner.ResultadoEjecucion resultado = rScriptRunner.ejecutarScript(nombreScript);
            List<File> archivosGenerados = rScriptRunner.obtenerArchivosGenerados(nombreScript);

            Platform.runLater(() -> {
                esperaDialog.close();

                if (resultado.isExitoso()) {
                    dialogManager.mostrarAlerta("Reporte Generado",
                            "Script ejecutado exitosamente:\n\n" + resultado.getMensaje(),
                            Alert.AlertType.INFORMATION);

                    if (!archivosGenerados.isEmpty()) {
                        reporteViewer.mostrarImagenesGeneradas(archivosGenerados);
                    }
                } else {
                    dialogManager.mostrarAlerta("Error en Script",
                            resultado.getMensaje(),
                            Alert.AlertType.ERROR);
                }
            });
        }).start();
    }
}
