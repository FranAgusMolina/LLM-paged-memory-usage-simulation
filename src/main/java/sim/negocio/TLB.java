package sim.negocio;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * La TLB (Translation Lookaside Buffer) es una pequeña memoria asociativa
 * dentro del procesador.
 * Su función es guardar las últimas traducciones de página que se usaron
 * para evitar consultar la lenta Tabla de Páginas en RAM.
 * Utiliza una política de reemplazo LRU (Least Recently Used).
 */
public class TLB {
    private final int capacidadMaxima;
    private final Map<String, Integer> cache;
    private int hits = 0;
    private int misses = 0;

    /**
     * Crea una nueva TLB con la capacidad especificada.
     * Utiliza un LinkedHashMap con orden de acceso para implementar LRU.
     *
     * @param capacidad número máximo de traducciones que puede almacenar
     */
    public TLB(int capacidad) {
        this.capacidadMaxima = capacidad;
        this.cache = new LinkedHashMap<>(capacidad, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > capacidadMaxima;
            }
        };
    }

    /**
     * Obtiene el número de fallos de TLB.
     *
     * @return cantidad de veces que no se encontró la traducción en cache
     */
    public int getMisses() {
        return misses;
    }

    /**
     * Obtiene el número de aciertos de TLB.
     *
     * @return cantidad de veces que se encontró la traducción en cache
     */
    public int getHits() {
        return hits;
    }

    /**
     * Obtiene el mapa interno de la cache.
     *
     * @return mapa con las traducciones almacenadas (clave: "PID:PaginaVirtual", valor: marco físico)
     */
    public Map<String, Integer> getCache() {
        return cache;
    }

    /**
     * Obtiene la capacidad máxima de la TLB.
     *
     * @return número máximo de entradas que puede almacenar
     */
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Simula la consulta de hardware a la TLB para traducir una dirección virtual.
     * Incrementa el contador de hits si encuentra la traducción, o misses si no.
     *
     * @param pid identificador del proceso
     * @param paginaVirtual número de página virtual a traducir
     * @return número de marco físico si está en cache, null en caso contrario
     */
    public Integer buscar(int pid, int paginaVirtual) {
        String key = generarClave(pid, paginaVirtual);

        if (cache.containsKey(key)) {
            hits++;
            return cache.get(key);
        }

        misses++;
        return null;
    }

    /**
     * Guarda una nueva traducción en la TLB.
     * Si la cache está llena, elimina la entrada menos recientemente usada.
     *
     * @param pid identificador del proceso
     * @param paginaVirtual número de página virtual
     * @param marcoFisico número de marco físico asignado
     */
    public void agregarEntrada(int pid, int paginaVirtual, int marcoFisico) {
        String key = generarClave(pid, paginaVirtual);
        cache.put(key, marcoFisico);
    }

    /**
     * Invalida todas las entradas de la TLB asociadas a un proceso específico.
     * Se utiliza al cambiar de proceso o liberar memoria para evitar incoherencias.
     *
     * @param pid identificador del proceso cuyas entradas se eliminarán
     */
    public void invalidarPorProceso(int pid) {
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(pid + ":"));
    }

    /**
     * Genera una clave única para identificar una traducción en la cache.
     *
     * @param pid identificador del proceso
     * @param paginaVirtual número de página virtual
     * @return clave en formato "PID:PaginaVirtual"
     */
    private String generarClave(int pid, int paginaVirtual) {
        return pid + ":" + paginaVirtual;
    }

    /**
     * Limpia completamente la TLB, borrando toda la cache y reseteando estadísticas.
     */
    public void limpiar() {
        cache.clear();
        hits = 0;
        misses = 0;
        System.out.println("TLB limpiada completamente.");
    }
}
