package sim.modelo;

/**
 * Representa una entrada en el log de accesos a memoria.
 * Contiene información detallada sobre cada traducción de dirección virtual a física.
 */
public class MemoryAccessLog {
    private final int direccionVirtual;
    private final int direccionFisica;
    private final int numeroPagina;
    private final int numeroMarco;
    private final boolean tlbHit;
    private final int pid;
    private final String nombreProceso;

    /**
     * Crea un nuevo registro de acceso a memoria.
     *
     * @param direccionVirtual dirección virtual solicitada
     * @param direccionFisica dirección física calculada
     * @param numeroPagina número de página virtual
     * @param numeroMarco número de marco físico
     * @param tlbHit true si fue un acierto en TLB, false si fue un miss
     * @param pid identificador del proceso
     * @param nombreProceso nombre del proceso
     */
    public MemoryAccessLog(int direccionVirtual, int direccionFisica, int numeroPagina,
                          int numeroMarco, boolean tlbHit, int pid, String nombreProceso) {
        this.direccionVirtual = direccionVirtual;
        this.direccionFisica = direccionFisica;
        this.numeroPagina = numeroPagina;
        this.numeroMarco = numeroMarco;
        this.tlbHit = tlbHit;
        this.pid = pid;
        this.nombreProceso = nombreProceso;
    }

    public int getDireccionVirtual() {
        return direccionVirtual;
    }

    public int getDireccionFisica() {
        return direccionFisica;
    }

    public int getNumeroPagina() {
        return numeroPagina;
    }

    public int getNumeroMarco() {
        return numeroMarco;
    }

    public boolean isTlbHit() {
        return tlbHit;
    }

    public String getTlbResultado() {
        return tlbHit ? "HIT" : "MISS";
    }

    public int getPid() {
        return pid;
    }

    public String getNombreProceso() {
        return nombreProceso;
    }
}

