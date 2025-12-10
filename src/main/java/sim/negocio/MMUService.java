package sim.negocio;

import sim.modelo.Frame;
import sim.modelo.LLMProcess;
import sim.modelo.PhysicalMemory;

import java.util.Map;

/**
 * La MMU (Memory Management Unit)
 * es el hardware que traduce direcciones virtuales a físicas.
 */
public class MMUService {
    private final PhysicalMemory ram;
    private final TLB tlb;
    private final int pageSize;

    /**
     * Crea una nueva instancia de MMUService.
     *
     * @param ram memoria física a gestionar
     * @param tlbSize tamaño de la TLB
     * @param PageSize cantidad de tokens por marco
     */
    public MMUService(PhysicalMemory ram, int tlbSize, int PageSize) {
        this.ram = ram;
        this.tlb = new TLB(tlbSize);
        this.pageSize = PageSize;
    }

    /**
     * Implementa el algoritmo de paginación para asignar espacio a un nuevo token.
     * Si la página actual está llena, busca un nuevo marco físico libre.
     *
     * @param proceso proceso de LLM
     * @throws Exception si la memoria está llena
     */
    public void asignarMemoriaParaToken(LLMProcess proceso) throws Exception {
        int totalTokens = proceso.getContadorTokens();
        boolean necesitaNuevoMarco = (totalTokens % pageSize) == 0;

        if (necesitaNuevoMarco) {
            int idMarcoLibre = buscarMarcoLibre();

            if (idMarcoLibre == -1) {
                throw new Exception("MEMORIA LLENA: No se pueden asignar más tokens.");
            }

            Frame frame = ram.getFrame(idMarcoLibre);
            frame.asignar(proceso.getPid(), proceso.getColorHex());
            int nuevaPaginaVirtual = totalTokens / pageSize;
            proceso.getPageTable().agregarEntrada(nuevaPaginaVirtual, idMarcoLibre);
            proceso.agregarToken();

            System.out.println("ASIGNACIÓN: Proceso " + proceso.getPid() +
                    " -> Página Virtual " + nuevaPaginaVirtual +
                    " mapeada a Marco Físico " + idMarcoLibre);
        } else {
            proceso.agregarToken();
        }
    }

    /**
     * Simula la traducción de una dirección virtual a física para un token específico.
     *
     * @param proceso proceso de LLM
     * @param tokenIndex índice del token
     * @return número de marco físico o -1 si no existe la traducción
     */
    public int traducirDireccion(LLMProcess proceso, int tokenIndex) {
        int paginaVirtual = tokenIndex / pageSize;
        Integer marcoFisico = tlb.buscar(proceso.getPid(), paginaVirtual);

        if (marcoFisico == null) {
            marcoFisico = proceso.getPageTable().getMarcoFisico(paginaVirtual);

            if (marcoFisico != null) {
                tlb.agregarEntrada(proceso.getPid(), paginaVirtual, marcoFisico);
            }
        }

        return (marcoFisico != null) ? marcoFisico : -1;
    }

    /**
     * Libera todos los marcos físicos ocupados por el proceso y limpia la TLB.
     *
     * @param proceso proceso de LLM
     */
    public void liberarMemoria(LLMProcess proceso) {
        Map<Integer, Integer> mapa = proceso.getPageTable().getMapa();
        for (Integer marcoId : mapa.values()) {
            ram.getFrame(marcoId).liberar();
        }
        tlb.invalidarPorProceso(proceso.getPid());
        proceso.getPageTable().limpiar();
    }

    /**
     * Busca un marco físico libre en la memoria RAM.
     *
     * @return índice del marco libre o -1 si no hay disponibles
     */
    private int buscarMarcoLibre() {
        for (int i = 0; i < ram.getSize(); i++) {
            if (!ram.getFrame(i).isOcupado()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Obtiene la TLB utilizada por la MMU.
     *
     * @return instancia de TLB
     */
    public TLB getTlb() {
        return tlb;
    }

    /**
     * Obtiene la cantidad de aciertos en la TLB.
     *
     * @return número de hits
     */
    public int getTlbHits() {
        return tlb.getHits();
    }

    /**
     * Obtiene la cantidad de fallos en la TLB.
     *
     * @return número de misses
     */
    public int getTlbMisses() {
        return tlb.getMisses();
    }
}
