package sim.modelo;

public class Frame {
    private final int id;
    private boolean ocupado;
    private int processId;
    private String colorHex; // <--- NUEVO CAMPO

    public Frame(int id) {
        this.id = id;
        this.ocupado = false;
        this.processId = -1;
        this.colorHex = "#DDDDDD"; // Gris por defecto
    }

    // Modificamos asignar para recibir el color
    public void asignar(int pid, String colorHex) {
        this.ocupado = true;
        this.processId = pid;
        this.colorHex = colorHex; // <--- GUARDAMOS EL COLOR
    }

    public void liberar() {
        this.ocupado = false;
        this.processId = -1;
        this.colorHex = "#DDDDDD"; // Volver a gris
    }

    public boolean isOcupado() { return ocupado; }
    public int getId() { return id; }
    public int getProcessId() { return processId; }
    public String getColorHex() { return colorHex; } // <--- NUEVO GETTER
}