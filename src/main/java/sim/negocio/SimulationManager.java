package sim.negocio;

import sim.modelo.LLMProcess;
import sim.modelo.PhysicalMemory;
import sim.recorder.Auditador;
import sim.util.Constantes;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Administra la simulación de procesos LLM y la gestión de memoria.
 * Controla el ciclo de vida de los procesos, la asignación de memoria y el registro de auditoría.
 */
public class SimulationManager implements Runnable{
    private final PhysicalMemory ram;
    private final MMUService mmu;
    private final Auditador auditador;
    private final List<LLMProcess> procesosActivos;
    private boolean running = false;
    private final Random random = new Random();
    private Runnable onUpdateCallback;

    /**
     * Crea un nuevo SimulationManager con los componentes principales.
     *
     * @param ram memoria física a utilizar
     * @param mmu servicio de gestión de memoria
     * @param auditar auditor para registrar eventos de la simulación
     */
    public SimulationManager(PhysicalMemory ram, MMUService mmu, Auditador auditar) {
        this.ram = ram;
        this.mmu = mmu;
        this.auditador = auditar;
        this.procesosActivos = new CopyOnWriteArrayList<>();
    }

    /**
     * Inicia la simulación en un hilo separado.
     */
    public void iniciar() {
        if (running) return;
        running = true;
        new Thread(this).start();
        System.out.println("SIMULACIÓN: Iniciada.");
    }

    /**
     * Detiene la simulación y cierra el auditor.
     */
    public void detener() {
        running = false;
        auditador.cerrar();
        System.out.println("SIMULACIÓN: Detenida.");
    }

    /**
     * Establece un callback que se ejecuta tras cada ciclo de simulación.
     *
     * @param callback función a ejecutar en cada actualización
     */
    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    /**
     * Lógica principal del ciclo de simulación.
     */
    @Override
    public void run() {
        int ciclo = 0;

        while (running) {
            try {
                System.out.println("--- CICLO " + ciclo + " ---");

                if (random.nextDouble() < 0.3) {
                    crearNuevoProceso(ciclo);
                }

                for (LLMProcess proceso : procesosActivos) {
                    try {
                        mmu.asignarMemoriaParaToken(proceso);
                        mmu.traducirDireccion(proceso, proceso.getContadorTokens() - 1);
                    } catch (Exception e) {
                        System.err.println("Error con proceso " + proceso.getPid() + ": " + e.getMessage());
                        eliminarProceso(proceso);
                    }
                }

                int procesosActivos = this.procesosActivos.size();
                int marcosOcupados = ram.getMarcosOcupados();
                int tlbHits = mmu.getTlbHits();
                int tlbMisses = mmu.getTlbMisses();

                auditador.registrar(ciclo, procesosActivos, marcosOcupados, tlbHits, tlbMisses);

                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }

                ciclo++;
                Thread.sleep(Constantes.VELOCIDAD_SIMULACION_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        auditador.cerrar();
    }

    /**
     * Crea y agrega un nuevo proceso LLM a la simulación.
     *
     * @param id identificador del proceso
     */
    private void crearNuevoProceso(int id) {
        String color = String.format("#%06x", random.nextInt(0xffffff + 1));
        LLMProcess nuevo = new LLMProcess(id, "User-" + id, color);
        procesosActivos.add(nuevo);
        System.out.println("NUEVO PROCESO: " + nuevo.getNombre() + " ha llegado.");
    }

    /**
     * Elimina un proceso de la simulación y libera su memoria.
     *
     * @param proceso proceso a eliminar
     */
    private void eliminarProceso(LLMProcess proceso) {
        mmu.liberarMemoria(proceso);
        procesosActivos.remove(proceso);
        System.out.println("PROCESO TERMINADO: " + proceso.getNombre() + " (Memoria liberada)");
    }

    /**
     * Obtiene la lista de procesos activos en la simulación.
     *
     * @return lista de procesos LLM activos
     */
    public List<LLMProcess> getProcesosActivos() {
        return procesosActivos;
    }
}
