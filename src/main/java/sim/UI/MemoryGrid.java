package sim.UI;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MemoryGrid extends GridPane {
    private Rectangle[] bloquesVisuales;
    private final int columnas = 32; // Ancho fijo de la matriz visual

    public MemoryGrid(int totalMarcos) {
        this.setHgap(2); // Espacio horizontal entre bloques (pixeles)
        this.setVgap(2); // Espacio vertical
        this.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 10;");

        inicializarGrilla(totalMarcos);
    }

    private void inicializarGrilla(int total) {
        bloquesVisuales = new Rectangle[total];

        for (int i = 0; i < total; i++) {
            // 1. Crear el rectángulo (Marco Físico)
            Rectangle rect = new Rectangle(18, 18);
            rect.setFill(Color.web("#444444")); // Gris oscuro (Libre)
            rect.setStroke(Color.web("#2b2b2b")); // Borde sutil
            rect.setArcWidth(5);
            rect.setArcHeight(5);

            // 2. Tooltip: Para ver el ID al pasar el mouse (¡Muy útil para debug!)
            Tooltip.install(rect, new Tooltip("Marco Físico: " + i));

            // 3. Guardar referencia para poder pintarlo después
            bloquesVisuales[i] = rect;

            // 4. Ubicar en la grilla (Matemática: fila = i / cols, col = i % cols)
            int fila = i / columnas;
            int col = i % columnas;
            this.add(rect, col, fila);
        }
    }

    // Método rápido para cambiar el color de un bloque específico
    public void pintarBloque(int idMarco, Color color) {
        if (idMarco >= 0 && idMarco < bloquesVisuales.length) {
            bloquesVisuales[idMarco].setFill(color);

            // Efecto visual opcional: Borde brillante si está ocupado
            if (!color.equals(Color.web("#444444"))) {
                bloquesVisuales[idMarco].setStroke(color.brighter());
            } else {
                bloquesVisuales[idMarco].setStroke(Color.web("#2b2b2b"));
            }
        }
    }
}
