package sim.modelo;

/**
 *
 * La Memoria Principal (Mp) se ve como un vector (array) de marcos.
 * Su tamaño total se define por 2^nf marcos, donde nf son los bits del número de marco.
 */
public class PhysicalMemory {
    //La RAM es un arreglo de marcos
    private Frame[] frames;
    private final int totalSize;

    public PhysicalMemory(int cantidadMarcos) {
        this.totalSize = cantidadMarcos;
        this.frames = new Frame[cantidadMarcos];
        for (int i = 0; i < cantidadMarcos; i++) {
            frames[i] = new Frame(i); //Inicializamos cada marco, {0}, {1}, {2}, ...
        }
    }

    public Frame getFrame(int numeroDeMarco) {
        if (numeroDeMarco < 0 || numeroDeMarco >= totalSize) {
            throw new IndexOutOfBoundsException("Error de Hardware: Dirección física inválida " + numeroDeMarco);
        }
        return frames[numeroDeMarco];
    }

    public int getSize() {
        return totalSize;
    }

    // Utilidad para saber cuánto espacio queda (Opcional, útil para estadísticas)
    public int getMarcosLibres() {
        int libres = 0;
        for (Frame f : frames) {
            if (!f.isOcupado()) libres++;
        }
        return libres;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Memoria Física (Total Frames: ").append(totalSize).append(")\n");
        for (Frame frame : frames) {
            sb.append(frame.toString()).append("\n");
        }
        return sb.toString();
    }
}
