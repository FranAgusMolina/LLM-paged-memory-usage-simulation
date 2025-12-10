package sim.modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa una tabla de páginas que traduce un Número de Página Virtual (VPN)
 * a un Número de Marco Físico (PFN) para un proceso.
 */
public class PageTable {
    private final Map<Integer, Integer> mapping;

    /**
     * Crea una nueva tabla de páginas vacía.
     */
    public PageTable() {
        this.mapping = new HashMap<>();
    }

    /**
     * Agrega una entrada de traducción de página virtual a marco físico.
     *
     * @param paginaVirtual número de página virtual (VPN)
     * @param marcoFisico número de marco físico (PFN)
     */
    public void agregarEntrada(int paginaVirtual, int marcoFisico) {
        mapping.put(paginaVirtual, marcoFisico);
    }

    /**
     * Obtiene el marco físico correspondiente a una página virtual.
     *
     * @param paginaVirtual número de página virtual (VPN)
     * @return número de marco físico (PFN), o null si no existe la entrada
     */
    public Integer getMarcoFisico(int paginaVirtual) {
        return mapping.get(paginaVirtual);
    }

    /**
     * Devuelve una copia del mapa de traducciones de la tabla de páginas.
     *
     * @return mapa de página virtual a marco físico
     */
    public Map<Integer, Integer> getMapa() {
        return new HashMap<>(mapping);
    }

    /**
     * Elimina todas las entradas de la tabla de páginas.
     */
    public void limpiar() {
        mapping.clear();
    }
}
