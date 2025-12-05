package sim.negocio;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * La TLB (Translation Lookaside Buffer) es una pequeña memoria asociativa
 * dentro del procesador.
 * Su función es guardar las últimas traducciones de página que se usaron
 * para evitar consultar la lenta Tabla de Páginas en RAM.
 */
public class TLB {
    private final int capacidadMaxima;

    // Clave (String): "PID:PaginaVirtual" -> Valor (Integer): MarcoFisico
    private final Map<String, Integer> cache;

    //Metricas de eficiencia
    private long hits = 0;
    private long misses = 0;

    public TLB(int capacidad) {
        this.capacidadMaxima = capacidad;

        // El tercer parámetro 'true' activa el "Access Order".
        // Cada vez que leemos un dato, este se mueve al final de la lista.
        // El primero de la lista es el "Menos Recientemente Usado" (LRU).
        this.cache = new LinkedHashMap<>(capacidad, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                // Si superamos el tamaño, borramos el más viejo automáticamente
                return size() > capacidadMaxima;
            }
        };
    }

    public long getMisses() {
        return misses;
    }

    public long getHits() {
        return hits;
    }

    public Map<String, Integer> getCache() {
        return cache;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Simulac la consulta de hardware a la TLB
     * @param pid
     * @param paginaVirtual
     * @return
     */
    public Integer buscar(int pid, int paginaVirtual) {
        String key = generarClave(pid, paginaVirtual);

        if (cache.containsKey(key)) {
            hits++; // ¡Éxito! Ahorramos un viaje a la RAM
            return cache.get(key);
        }

        misses++; // Fallo. Tendremos que ir a la Tabla de Páginas.
        return null;
    }

    /**
     * Gaurda una nueva traduccion (se trae de la tabla de paginas)
     * @param pid
     * @param paginaVirtual
     * @param marcoFisico
     */
    public void agregarEntrada(int pid, int paginaVirtual, int marcoFisico) {
        String key = generarClave(pid, paginaVirtual);
        cache.put(key, marcoFisico);
    }

    /**
     * En sistemas reales, al cambiar de proceso o liberar memoria,
     * a veces se limpia la TLB para evitar incoherencias
     * @param pid
     */
    public void invalidarPorProceso(int pid) {
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(pid + ":"));
    }

    /**
     * Genera una clave unica
     * @param pid
     * @param paginaVirtual
     * @return
     */
    private String generarClave(int pid, int paginaVirtual) {
        return pid + ":" + paginaVirtual;
    }
}
