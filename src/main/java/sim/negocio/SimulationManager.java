package sim.negocio;

import sim.modelo.LLMProcess;
import sim.modelo.PhysicalMemory;
import sim.util.Constantes;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulationManager implements Runnable{
    private final PhysicalMemory ram;
    private final MMUService mmu;

    private final List<LLMProcess> procesosActivos;

    private boolean running = false;
    private final Random random = new Random();

    // Para notificar a la GUI (Patrón Observador simplificado)
    private Runnable onUpdateCallback;

    public SimulationManager(PhysicalMemory ram, MMUService mmu) {
        this.ram = ram;
        this.mmu = mmu;
        this.procesosActivos = new CopyOnWriteArrayList<>();
    }

    public void iniciar() {
        if (running) return;
        running = true;
        // Iniciar el hilo de simulación
        new Thread(this).start();
        System.out.println("SIMULACIÓN: Iniciada.");
    }

    public void detener() {
        running = false;
        System.out.println("SIMULACIÓN: Detenida.");
    }

    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    @Override
    public void run() {
        int ciclo = 0;

        while (running) {
            try {
                System.out.println("--- CICLO " + ciclo + " ---");

                // 1. ¿Llega un nuevo usuario? (Probabilidad del 30% cada ciclo)
                if (random.nextDouble() < 0.3) {
                    crearNuevoProceso(ciclo);
                }

                // 2. Todos los procesos activos generan 1 token (necesitan memoria)
                for (LLMProcess proceso : procesosActivos) {
                    try {
                        // Aquí ocurre la magia: El proceso pide memoria a la MMU
                        mmu.asignarMemoriaParaToken(proceso);

                        // (Opcional) Simular lectura para probar la TLB
                        mmu.traducirDireccion(proceso, proceso.getContadorTokens() - 1);

                    } catch (Exception e) {
                        System.err.println("Error con proceso " + proceso.getPid() + ": " + e.getMessage());
                        // En la vida real, aquí mataríamos el proceso u liberaríamos memoria
                        // liberamos memoria para que siga la simulación
                        eliminarProceso(proceso);
                    }
                }

                // 3. Notificar a la GUI que algo cambió
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
    }

    // --- Lógica Privada ---

    private void crearNuevoProceso(int id) {
        // Generar un color aleatorio hexadecimal para la GUI
        String color = String.format("#%06x", random.nextInt(0xffffff + 1));
        LLMProcess nuevo = new LLMProcess(id, "User-" + id, color);
        procesosActivos.add(nuevo);
        System.out.println("NUEVO PROCESO: " + nuevo.getNombre() + " ha llegado.");
    }

    private void eliminarProceso(LLMProcess proceso) {
        mmu.liberarMemoria(proceso);
        procesosActivos.remove(proceso);
        System.out.println("PROCESO TERMINADO: " + proceso.getNombre() + " (Memoria liberada)");
    }

    public List<LLMProcess> getProcesosActivos() {
        return procesosActivos;
    }


}
