package sim.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import java.util.List;
import java.util.Optional;

/**
 * Gestiona la creación y presentación de diálogos y alertas en la aplicación.
 */
public class DialogManager {

    /**
     * Muestra un diálogo de alerta genérico.
     *
     * @param titulo título de la ventana
     * @param mensaje contenido del mensaje
     * @param tipo tipo de alerta
     */
    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de selección de scripts.
     *
     * @param scripts lista de scripts disponibles
     * @return script seleccionado o vacío si se cancela
     */
    public Optional<String> mostrarDialogoSeleccionScript(List<String> scripts) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(scripts.get(0), scripts);
        dialog.setTitle("Generar Reporte");
        dialog.setHeaderText("Selecciona el tipo de análisis");
        dialog.setContentText("Script de R:");
        dialog.getDialogPane().setPrefWidth(400);
        return dialog.showAndWait();
    }

    /**
     * Muestra un diálogo de espera durante operaciones largas.
     *
     * @return referencia al diálogo para poder cerrarlo después
     */
    public Alert mostrarDialogoEspera() {
        Alert esperaDialog = new Alert(Alert.AlertType.INFORMATION);
        esperaDialog.setTitle("Ejecutando...");
        esperaDialog.setHeaderText("Ejecutando script de R");
        esperaDialog.setContentText("Por favor espera...");
        esperaDialog.show();
        return esperaDialog;
    }
}