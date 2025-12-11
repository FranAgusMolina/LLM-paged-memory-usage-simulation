package sim.controlador;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

/**
 * Gestiona la visualización de reportes e imágenes generadas por scripts de R.
 */
public class ReportViewer {

    /**
     * Muestra las imágenes generadas por el script en una nueva ventana.
     *
     * @param archivos lista de archivos de imagen a mostrar
     */
    public void mostrarImagenesGeneradas(List<File> archivos) {
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

                contenedor.getChildren().add(bloqueImagen);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + archivo.getName());
            }
        }

        Scene scene = new Scene(scrollPane, 950, 700);
        stage.setScene(scene);
        stage.show();
    }
}
