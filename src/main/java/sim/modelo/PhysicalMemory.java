package sim.modelo;

/**
 * Representa la Memoria Principal (RAM) como un arreglo de marcos físicos.
 * Su tamaño total se define por la cantidad de marcos especificada al crear la instancia.
 */
public class PhysicalMemory {
    private Frame[] frames;
    private final int totalSize;

    /**
     * Crea una memoria física con la cantidad de marcos especificada.
     * Inicializa cada marco con su identificador correspondiente.
     *
     * @param cantidadMarcos número total de marcos en la memoria física
     */
    public PhysicalMemory(int cantidadMarcos) {
        this.totalSize = cantidadMarcos;
        this.frames = new Frame[cantidadMarcos];
        for (int i = 0; i < cantidadMarcos; i++) {
            frames[i] = new Frame(i);
        }
    }

    /**
     * Obtiene el marco físico correspondiente al número de marco dado.
     *
     * @param numeroDeMarco índice del marco físico
     * @return objeto Frame correspondiente al índice
     * @throws IndexOutOfBoundsException si el índice es inválido
     */
    public Frame getFrame(int numeroDeMarco) {
        if (numeroDeMarco < 0 || numeroDeMarco >= totalSize) {
            throw new IndexOutOfBoundsException("Error de Hardware: Dirección física inválida " + numeroDeMarco);
        }
        return frames[numeroDeMarco];
    }

    /**
     * Obtiene el tamaño total de la memoria física (cantidad de marcos).
     *
     * @return número total de marcos
     */
    public int getSize() {
        return totalSize;
    }

    /**
     * Calcula la cantidad de marcos libres en la memoria física.
     *
     * @return número de marcos libres
     */
    public int getMarcosLibres() {
        int libres = 0;
        for (Frame f : frames) {
            if (!f.isOcupado()) libres++;
        }
        return libres;
    }

    /**
     * Devuelve una representación en texto de la memoria física y sus marcos.
     *
     * @return cadena descriptiva de la memoria física
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Memoria Física (Total Frames: ").append(totalSize).append(")\n");
        for (Frame frame : frames) {
            sb.append(frame.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Calcula la cantidad de marcos ocupados en la memoria física.
     *
     * @return número de marcos ocupados
     */
    public int getMarcosOcupados() {
        int marcosOcupados = 0;
        for (Frame frame : frames) {
            if (frame.isOcupado()) marcosOcupados++;
        }
        return marcosOcupados;
    }
}
