package sim.UI;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sim.util.Constantes;

/**
 * Componente visual que representa la memoria física como una grilla de bloques.
 * Permite visualizar el estado de cada marco físico y actualizar su color según la ocupación.
 */
public class MemoryGrid extends GridPane {
    private Rectangle[] bloquesVisuales;

    /**
     * Crea una nueva grilla de memoria con la cantidad de marcos especificada.
     *
     * @param totalMarcos cantidad total de marcos físicos a mostrar
     */
    public MemoryGrid(int totalMarcos) {
        this.setHgap(2);
        this.setVgap(2);
        this.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 10;");

        inicializarGrilla(totalMarcos);
    }

    /**
     * Inicializa la grilla visual con los bloques correspondientes a cada marco físico.
     *
     * @param total cantidad total de marcos físicos
     */
    private void inicializarGrilla(int total) {
        bloquesVisuales = new Rectangle[total];
        int columnas = Constantes.COLUMNAS_GRILLA;

        for (int i = 0; i < total; i++) {
            Rectangle rect = new Rectangle(18, 18);
            rect.setFill(Color.web(Constantes.COLOR_LIBRE));
            rect.setStroke(Color.web(Constantes.COLOR_BORDE));
            rect.setArcWidth(5);
            rect.setArcHeight(5);

            Tooltip.install(rect, new Tooltip("Marco Físico: " + (i + 1)));

            bloquesVisuales[i] = rect;
            int fila = i / columnas;
            int col = i % columnas;
            this.add(rect, col, fila);
        }
    }

    /**
     * Cambia el color de un bloque de la grilla para reflejar el estado del marco físico.
     *
     * @param idMarco índice del marco físico a actualizar
     * @param color color a aplicar al bloque
     */
    public void pintarBloque(int idMarco, Color color) {
        if (idMarco >= 0 && idMarco < bloquesVisuales.length) {
            if (!bloquesVisuales[idMarco].getFill().equals(color)) {
                bloquesVisuales[idMarco].setFill(color);

                if (!color.toString().equals(Color.web(Constantes.COLOR_LIBRE).toString())) {
                    bloquesVisuales[idMarco].setStroke(color.brighter());
                } else {
                    bloquesVisuales[idMarco].setStroke(Color.web(Constantes.COLOR_BORDE));
                }
            }
        }
    }
}
