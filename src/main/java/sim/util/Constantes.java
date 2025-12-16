package sim.util;

public class Constantes {
    // ============================================================================================
    // PERFIL 1: SERVIDOR ESTÁNDAR (ACTIVO)
    // Simulación de una IA tipo ChatGPT atendiendo usuarios concurrentes con hardware balanceado
    // pero con cuello de botella en la TLB por la alta concurrencia.
    // ============================================================================================
    public static final int COLUMNAS_GRILLA = 46;
    public static final int FILAS_GRILLA = 18;
    public static final int TOTAL_MARCOS_RAM = COLUMNAS_GRILLA * FILAS_GRILLA; // ~828 marcos

    public static final int TAMANIO_PAGINA = 64; // Huge Pages para tensores
    public static final int TAMANIO_TLB = 32;    // TLB limitada (3-4% de la RAM) -> Provoca fallos

    public static final int VELOCIDAD_SIMULACION_MS = 100;

    /*
    // ============================================================================================
    // PERFIL 2: CLUSTER DE ALTO RENDIMIENTO (H100 / ENTERPRISE)
    // Hardware masivo diseñado para minimizar fallos. Mucha RAM y TLB grande.
    // Ideal para ver cómo el sistema escala sin sufrir thrashing temprano.
    // ============================================================================================
    public static final int COLUMNAS_GRILLA = 64;
    public static final int FILAS_GRILLA = 32;
    public static final int TOTAL_MARCOS_RAM = COLUMNAS_GRILLA * FILAS_GRILLA; // ~2048 marcos

    public static final int TAMANIO_PAGINA = 128; // Páginas gigantes
    public static final int TAMANIO_TLB = 256;    // TLB masiva, casi sin fallos

    public static final int VELOCIDAD_SIMULACION_MS = 50; // Simulación rápida
    */

    /*
    // ============================================================================================
    // PERFIL 3: EDGE AI / DISPOSITIVO MÓVIL
    // Simula correr un LLM local en un teléfono o Raspberry Pi.
    // Poca RAM, TLB minúscula. El sistema debería colapsar rápido (Thrashing agresivo).
    // ============================================================================================
    public static final int COLUMNAS_GRILLA = 16;
    public static final int FILAS_GRILLA = 16;
    public static final int TOTAL_MARCOS_RAM = COLUMNAS_GRILLA * FILAS_GRILLA; // 256 marcos

    public static final int TAMANIO_PAGINA = 16; // Páginas pequeñas estándar
    public static final int TAMANIO_TLB = 4;     // TLB crítica, fallo constante

    public static final int VELOCIDAD_SIMULACION_MS = 200; // Lento para ver el sufrimiento
    */

    // --- Colores Semánticos ---
    public static final String COLOR_LIBRE = "#E0E0E0";      // Gris
    public static final String COLOR_OCUPADO = "#708090";    // Azul Acero (Ocupado normal)
    public static final String COLOR_DESTACADO = "#FF8C00";  // Naranja (Seleccionado)
    public static final String COLOR_BORDE = "#2b2b2b";
}