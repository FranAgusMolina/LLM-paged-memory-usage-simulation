package sim.modelo;

/**
 * un "Proceso" representa una conversación activa con la IA.
 * Este objeto agrupa su Tabla de Páginas propia  y sus metadatos
 * (ID, color para la UI, cantidad de tokens generados)
 */
public class LLMProcess {
    private final int pid;               //Proceso de ID unico
    private final String nombre;
    private final String colorHex;
    private final PageTable pageTable;

    private int contadorTokens;         //Cuantos tokens se han generado

    public LLMProcess(int pid, String nombre, String colorHex){
        this.pid = pid;
        this.nombre = nombre;
        this.colorHex = colorHex;
        this.pageTable = new PageTable();
        this.contadorTokens = 0;
    }

    public void agregarToken() {
        this.contadorTokens++;
    }

    public int getPid() {
        return pid;

    }
    public String getNombre() {
        return nombre;
    }
    public String getColorHex() {
        return colorHex;
    }
    public PageTable getPageTable() {
        return pageTable;
    }
    public int getContadorTokens() {
        return contadorTokens;
    }

    @Override
    public String toString() {
        return nombre + " (Tokens: " + contadorTokens + ")";
    }
}
