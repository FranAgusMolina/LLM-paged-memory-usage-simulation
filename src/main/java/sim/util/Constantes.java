package sim.util;

public class Constantes {
    // --- Configuración de Hardware ---
    public static final int COLUMNAS_GRILLA = 46;
    public static final int FILAS_GRILLA = 18;
    public static final int TOTAL_MARCOS_RAM = COLUMNAS_GRILLA * FILAS_GRILLA; // 512

    public static final int TAMANIO_PAGINA = 64;
    public static final int TAMANIO_TLB = 32;

    // --- Configuración de Simulación ---
    public static final int VELOCIDAD_SIMULACION_MS = 100; // Más rápido para ver acción

    // --- Colores Semánticos ---
    public static final String COLOR_LIBRE = "#E0E0E0";      // Gris
    public static final String COLOR_OCUPADO = "#708090";    // Azul Acero (Ocupado normal)
    public static final String COLOR_DESTACADO = "#FF8C00";  // Naranja (Seleccionado)
    public static final String COLOR_BORDE = "#2b2b2b";
}