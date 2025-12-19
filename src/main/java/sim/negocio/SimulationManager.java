package sim.negocio;

import sim.modelo.LLMProcess;
import sim.modelo.Perfil;
import sim.modelo.PhysicalMemory;
import sim.recorder.Auditador;
import sim.datos.Constantes;

import java.util. List;
import java.util. Random;
import java.util. concurrent.CopyOnWriteArrayList;

/**
 * Administra la simulación de procesos LLM y la gestión de memoria.
 * Controla el ciclo de vida de los procesos, la asignación de memoria y el registro de auditoría.
 */
public class SimulationManager implements Runnable{
    private final PhysicalMemory ram;
    private final MMUService mmu;
    private final Auditador auditador;
    private final List<LLMProcess> procesosActivos;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private final Random random = new Random();
    private Runnable onUpdateCallback;
    private Thread simulationThread;
    private int ciclo = 0;
    private Perfil perfil;

    /**
     * Crea un nuevo SimulationManager con los componentes principales.
     *
     * @param ram memoria física a utilizar
     * @param mmu servicio de gestión de memoria
     * @param auditar auditor para registrar eventos de la simulación
     */
    public SimulationManager(PhysicalMemory ram, MMUService mmu, Auditador auditar, Perfil perfil) {
        this.ram = ram;
        this.mmu = mmu;
        this.auditador = auditar;
        this.procesosActivos = new CopyOnWriteArrayList<>();
        this.perfil = perfil;
    }

    /**
     * Inicia la simulación en un hilo separado.
     * Si la simulación está pausada, la reanuda.
     * Si no está corriendo, crea un nuevo hilo.
     */
    public void iniciar() {
        if (running && paused) {
            reanudar();
            return;
        }

        if (running) return;

        running = true;
        paused = false;
        simulationThread = new Thread(this);
        simulationThread.start();
        System.out.println("SIMULACIÓN: Iniciada.");
    }

    /**
     * Pausa la simulación sin detener el hilo.
     * Permite reanudar desde el mismo punto.
     */
    public void pausar() {
        if (!running || paused) return;
        paused = true;
        System.out.println("SIMULACIÓN: Pausada en ciclo " + ciclo);
    }

    /**
     * Reanuda la simulación desde el punto donde fue pausada.
     */
    public void reanudar() {
        if (!running || ! paused) return;
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
        System.out. println("SIMULACIÓN: Reanudada desde ciclo " + ciclo);
    }

    /**
     * Detiene completamente la simulación y cierra el auditor.
     */
    public void detener() {
        if (!running) return;
        running = false;
        paused = false;

        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }

        if (simulationThread != null) {
            simulationThread.interrupt();
        }

        auditador.cerrar();
        System.out.println("SIMULACIÓN: Detenida completamente en ciclo " + ciclo);
    }

    /**
     * Reinicia completamente la simulación, limpiando todos los recursos.
     * Resetea memoria, TLB, procesos activos y ciclos.
     */
    public void reiniciar() {
        // Primero detener la simulación si está corriendo
        detener();

        // Esperar a que el hilo termine
        if (simulationThread != null) {
            try {
                simulationThread.join(1000); // Esperar máximo 1 segundo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Limpiar todos los recursos
        procesosActivos.clear();
        mmu.getTlb().limpiar();
        ram.limpiar();
        ciclo = 0;

        System.out.println("SIMULACIÓN: Reiniciada completamente. Todos los recursos limpiados.");
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
     * Mantiene el contador de ciclos entre pausas.
     */
    @Override
    public void run() {
        while (running) {
            try {
                synchronized (pauseLock) {
                    while (paused && running) {
                        pauseLock.wait();
                    }
                }

                if (! running) break;

                System.out.println("--- CICLO " + ciclo + " ---");

                if (random.nextDouble() < 0.3) {
                    crearNuevoProceso(ciclo);
                }

                for (LLMProcess proceso : procesosActivos) {
                    try {
                        mmu. asignarMemoriaParaToken(proceso);
                        mmu.traducirDireccion(proceso, proceso.getContadorTokens() - 1);
                    } catch (Exception e) {
                        System.err. println("Error con proceso " + proceso.getPid() + ": " + e.getMessage());
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
                Thread.sleep(perfil.getSimSpeed());

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
        String color = String. format("#%06x", random. nextInt(0xffffff + 1));
        LLMProcess nuevo = new LLMProcess(id, "User-" + id, color);
        procesosActivos.add(nuevo);
        System.out.println("NUEVO PROCESO: " + nuevo. getNombre() + " ha llegado.");
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

    /**
     * Verifica si la simulación está en ejecución.
     *
     * @return true si la simulación está corriendo
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Verifica si la simulación está pausada.
     *
     * @return true si la simulación está en pausa
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Obtiene el número del ciclo actual.
     *
     * @return número de ciclo actual
     */
    public int getCicloActual() {
        return ciclo;
    }
}