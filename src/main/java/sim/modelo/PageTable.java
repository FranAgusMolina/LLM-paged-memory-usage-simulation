package sim.modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * La tabla traduce un Número de Página Virtual (VPN) a un Número de Marco Físico (PFN).
 */
public class PageTable {
    // K: Página Virtual (ID lógico) -> V: Marco Físico (ID real en RAM)
    private final Map<Integer, Integer> mapping;

    public PageTable() {
        this.mapping = new HashMap<>();
    }

    public void agregarEntrada(int paginaVirtual, int marcoFisico) {
        mapping.put(paginaVirtual, marcoFisico);
    }

    public Integer getMarcoFisico(int paginaVirtual) {
        // Retorna null si no existe (Fallo de Página)
        return mapping.get(paginaVirtual);
    }

    // Útil para mostrar la tabla en la interfaz gráfica después
    public Map<Integer, Integer> getMapa() {
        return new HashMap<>(mapping);
    }

    public void limpiar() {
        mapping.clear();
    }
}
