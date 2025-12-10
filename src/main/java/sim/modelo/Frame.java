package sim.modelo;

/**
 * Representa un marco físico en la memoria RAM.
 * Cada marco puede estar libre u ocupado por un proceso específico.
 * Mantiene información sobre su estado, el proceso que lo ocupa y su color visual.
 */
public class Frame {
    private final int id;
    private boolean ocupado;
    private int processId;
    private String colorHex;

    /**
     * Crea un nuevo marco físico en estado libre.
     * Por defecto se inicializa con color gris (#DDDDDD).
     *
     * @param id identificador único del marco en la memoria física
     */
    public Frame(int id) {
        this.id = id;
        this.ocupado = false;
        this.processId = -1;
        this.colorHex = "#DDDDDD";
    }

    /**
     * Asigna este marco a un proceso específico y establece su color visual.
     *
     * @param pid identificador del proceso que ocupará el marco
     * @param colorHex código hexadecimal del color para visualización (ej: "#FF5733")
     */
    public void asignar(int pid, String colorHex) {
        this.ocupado = true;
        this.processId = pid;
        this.colorHex = colorHex;
    }

    /**
     * Libera el marco, dejándolo disponible para otro proceso.
     * Restablece el color al gris por defecto.
     */
    public void liberar() {
        this.ocupado = false;
        this.processId = -1;
        this.colorHex = "#DDDDDD";
    }

    /**
     * Verifica si el marco está ocupado por algún proceso.
     *
     * @return true si el marco está ocupado, false si está libre
     */
    public boolean isOcupado() {
        return ocupado;
    }

    /**
     * Obtiene el identificador único del marco.
     *
     * @return id del marco físico
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el identificador del proceso que ocupa este marco.
     *
     * @return PID del proceso, o -1 si el marco está libre
     */
    public int getProcessId() {
        return processId;
    }

    /**
     * Obtiene el color hexadecimal asignado para visualización.
     *
     * @return código hexadecimal del color (ej: "#DDDDDD" para libre)
     */
    public String getColorHex() {
        return colorHex;
    }
}
