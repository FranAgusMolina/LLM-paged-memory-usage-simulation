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
    private final int pageSize; // Cuántos tokens caben en un marco

    public MMUService(PhysicalMemory ram, int tlbSize, int PageSize) {
        this.ram = ram;
        this.tlb = new TLB(tlbSize);
        this.pageSize = PageSize;
    }

    /**
     * Implementacion de algoritmo de paginacion de atencion de memoria
     * Asigna espacio para un nuevo token. Si la página actual está llena,
     * busca un nuevo marco físico libre
     * @param proceso
     * @throws Exception
     */
    public void asignarMemoriaParaToken(LLMProcess proceso) throws Exception {
        int totalTokens = proceso.getContadorTokens();

        // Calculamos si necesitamos un NUEVO marco
        // Si (tokens % tamaño_pagina == 0), significa que llenamos el anterior y necesitamos uno nuevo.
        boolean necesitaNuevoMarco = (totalTokens % pageSize) == 0;

        if (necesitaNuevoMarco) {
            // 1. Buscar hueco libre (Algoritmo First-Fit)
            int idMarcoLibre = buscarMarcoLibre();

            if (idMarcoLibre == -1) {
                // Simulación de "Out of Memory" (Fallo de Página fatal)
                throw new Exception("MEMORIA LLENA: No se pueden asignar más tokens.");
            }

            // 2. Ocupar el marco físico
            Frame frame = ram.getFrame(idMarcoLibre);
            frame.asignar(proceso.getPid());

            // 3. Actualizar la Tabla de Páginas del proceso (Lógica -> Física)
            int nuevaPaginaVirtual = totalTokens / pageSize;
            proceso.getPageTable().agregarEntrada(nuevaPaginaVirtual, idMarcoLibre);

            // 4. Agregar token al contador del proceso
            proceso.agregarToken();

            System.out.println("ASIGNACIÓN: Proceso " + proceso.getPid() +
                    " -> Página Virtual " + nuevaPaginaVirtual +
                    " mapeada a Marco Físico " + idMarcoLibre);
        } else {
            // Si todavía hay espacio en el marco actual, solo sumamos el token
            proceso.agregarToken();
        }
    }

    /**
     * Simula la lectura de un token especifico
     * (traduccion de direcciones)
     * @param proceso
     * @param tokenIndex
     * @return
     */
    public int traducirDireccion(LLMProcess proceso, int tokenIndex) {
        // Calcular en qué página virtual está el token
        int paginaVirtual = tokenIndex / pageSize;

        // 1. Consultar TLB
        Integer marcoFisico = tlb.buscar(proceso.getPid(), paginaVirtual);

        if (marcoFisico == null) {
            // 2. Fallo de TLB -> Ir a Tabla de Páginas
            marcoFisico = proceso.getPageTable().getMarcoFisico(paginaVirtual);

            if (marcoFisico != null) {
                // 3. Cargar en TLB para la próxima
                tlb.agregarEntrada(proceso.getPid(), paginaVirtual, marcoFisico);
            }
        }

        return (marcoFisico != null) ? marcoFisico : -1; // -1 = Segmentation Fault
    }

    /**
     * Recorrer la tabla de páginas y liberar los marcos físicos
     * @param proceso
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
     * Busca un marco físico libre en la RAM
     * La primera vez que encuentra uno, lo retorna.
     * @return
     */
    private int buscarMarcoLibre() {
        for (int i = 0; i < ram.getSize(); i++) {
            if (!ram.getFrame(i).isOcupado()) {
                return i;
            }
        }
        return -1; // Memoria llena
    }

    public TLB getTlb() {
        return tlb;
    }
}
