package sim.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import sim.datos.CargarPerfiles;
import sim.modelo.Perfil;

import java.util.Map;

/**
 * Controlador de la ventana de configuración.
 * Gestiona los parámetros ajustables de la simulación: perfil, tamaño TLB y tamaño de marcos.
 */
public class ControladorConfig {

    @FXML private ComboBox<String> comboPerfil;
    @FXML private Spinner<Integer> spinnerTamanioTLB;
    @FXML private Spinner<Integer> spinnerTamanioMarco;

    @FXML private Button btnAplicar;
    @FXML private Button btnCancelar;

    private Map<String, Perfil> perfiles;
    private Runnable onAplicarCallback;

    /**
     * Inicializa los componentes de la ventana.
     * Configura los valores iniciales del ComboBox y los Spinners.
     */
    @FXML
    public void initialize() {
        // PRIMERO: Configurar Spinner de TLB (rango: 4 a 512, step: 4)
        spinnerTamanioTLB.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 512, 32, 4)
        );

        // PRIMERO: Configurar Spinner de Tamaño de Marco (rango: 16 a 256, step: 16)
        spinnerTamanioMarco.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(16, 256, 64, 16)
        );

        // SEGUNDO: Cargar perfiles desde archivo perfiles.txt
        perfiles = CargarPerfiles.cargar();

        if (perfiles.isEmpty()) {
            System.err.println("⚠️ No se cargaron perfiles. Usando valores por defecto.");
            comboPerfil.setDisable(true);
        } else {
            // Agregar nombres de perfiles al ComboBox (mantiene orden de lectura)
            comboPerfil.getItems().addAll(perfiles.keySet());

            // Seleccionar el primer perfil por defecto
            String primerPerfil = perfiles.keySet().iterator().next();
            comboPerfil.setValue(primerPerfil);

            // Cargar valores del primer perfil (ahora los Spinners ya tienen ValueFactory)
            cargarValoresDePerfil(perfiles.get(primerPerfil));
        }


        // Listener para actualizar spinners cuando cambia el perfil
        comboPerfil.setOnAction(event -> {
            String nombrePerfil = comboPerfil.getValue();
            if (nombrePerfil != null && perfiles.containsKey(nombrePerfil)) {
                cargarValoresDePerfil(perfiles.get(nombrePerfil));
            }
        });
    }

    /**
     * Carga los valores de un perfil en los spinners.
     */
    private void cargarValoresDePerfil(Perfil perfil) {
        spinnerTamanioTLB.getValueFactory().setValue(perfil.getTLBSize());
        spinnerTamanioMarco.getValueFactory().setValue(perfil.getPageSize());
    }

    /**
     * Establece el callback que se ejecutará cuando se aplique la configuración.
     * Permite al coordinador recibir la notificación sin violar el patrón MVC.
     *
     * @param callback función a ejecutar cuando se presione Aplicar
     */
    public void setOnAplicar(Runnable callback) {
        this.onAplicarCallback = callback;
    }

    /**
     * Obtiene el perfil actualmente seleccionado en el ComboBox.
     *
     * @return nombre del perfil seleccionado
     */
    public String getPerfilSeleccionado() {
        return comboPerfil.getValue();
    }

    /**
     * Obtiene el tamaño de TLB configurado en el spinner.
     *
     * @return tamaño de TLB
     */
    public int getTamanioTLB() {
        return spinnerTamanioTLB.getValue();
    }

    /**
     * Obtiene el tamaño de marco configurado en el spinner.
     *
     * @return tamaño de marco de página
     */
    public int getTamanioMarco() {
        return spinnerTamanioMarco.getValue();
    }

    /**
     * Maneja el clic en el botón Aplicar.
     * Aplica los cambios realizados en la configuración ejecutando el callback configurado.
     */
    @FXML
    private void onBtnAplicarClick() {
        String perfilSeleccionado = comboPerfil.getValue();
        int tamanioTLB = spinnerTamanioTLB.getValue();
        int tamanioMarco = spinnerTamanioMarco.getValue();

        System.out.println("✅ Configuración aplicada:");
        System.out.println("   Perfil: " + perfilSeleccionado);
        System.out.println("   Tamaño TLB: " + tamanioTLB);
        System.out.println("   Tamaño Marco: " + tamanioMarco);

        // Ejecutar callback si está configurado
        if (onAplicarCallback != null) {
            onAplicarCallback.run();
        }

        cerrarVentana();
    }


    /**
     * Maneja el clic en el botón Cancelar.
     * Cierra la ventana sin guardar cambios.
     */
    @FXML
    private void onBtnCancelarClick() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}

