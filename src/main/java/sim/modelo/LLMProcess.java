package sim.modelo;

/**
 * Representa un proceso de conversación activa con la IA.
 * Agrupa la tabla de páginas propia y metadatos como ID, nombre, color para la UI y cantidad de tokens generados.
 */
public class LLMProcess {
    private final int pid;
    private final String nombre;
    private final String colorHex;
    private final PageTable pageTable;
    private int contadorTokens;

    /**
     * Crea un nuevo proceso LLM con los metadatos especificados.
     *
     * @param pid identificador único del proceso
     * @param nombre nombre del proceso
     * @param colorHex color en formato hexadecimal para la UI
     */
    public LLMProcess(int pid, String nombre, String colorHex) {
        this.pid = pid;
        this.nombre = nombre;
        this.colorHex = colorHex;
        this.pageTable = new PageTable();
        this.contadorTokens = 0;
    }

    /**
     * Incrementa el contador de tokens generados por el proceso.
     */
    public void agregarToken() {
        this.contadorTokens++;
    }

    /**
     * Obtiene el identificador único del proceso.
     *
     * @return ID del proceso
     */
    public int getPid() {
        return pid;
    }

    /**
     * Obtiene el nombre del proceso.
     *
     * @return nombre del proceso
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el color hexadecimal asociado al proceso para la UI.
     *
     * @return color en formato hexadecimal
     */
    public String getColorHex() {
        return colorHex;
    }

    /**
     * Obtiene la tabla de páginas del proceso.
     *
     * @return instancia de PageTable
     */
    public PageTable getPageTable() {
        return pageTable;
    }

    /**
     * Obtiene la cantidad de tokens generados por el proceso.
     *
     * @return contador de tokens
     */
    public int getContadorTokens() {
        return contadorTokens;
    }

    /**
     * Devuelve una representación en texto del proceso, mostrando el nombre y la cantidad de tokens.
     *
     * @return cadena descriptiva del proceso
     */
    @Override
    public String toString() {
        return nombre + " (Tokens: " + contadorTokens + ")";
    }
}
