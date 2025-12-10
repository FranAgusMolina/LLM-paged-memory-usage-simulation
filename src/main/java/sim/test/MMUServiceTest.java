package sim.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sim.modelo.LLMProcess;
import sim.modelo.PhysicalMemory;
import sim.negocio.MMUService;

import static org.junit.jupiter.api.Assertions.*;

public class MMUServiceTest {

    private PhysicalMemory ram;
    private MMUService mmu;
    private LLMProcess proceso;

    @BeforeEach
    void setUp() {
        // Configuración de prueba: RAM pequeña (10 marcos), Paginas de 2 tokens
        ram = new PhysicalMemory(10);
        mmu = new MMUService(ram, 4, 2); // TLB tamaño 4, Pagina tamaño 2
        proceso = new LLMProcess(1, "TestUser", "#FFFFFF");
    }

    @Test
    void testAsignacionDeMemoria() throws Exception {
        // 1. El proceso nace con 0 tokens
        assertEquals(0, proceso.getContadorTokens());

        // 2. Asignamos memoria para el 1er token (Debería ocupar 1 marco)
        mmu.asignarMemoriaParaToken(proceso);
        assertEquals(1, proceso.getContadorTokens());
        assertEquals(1, proceso.getPageTable().getMapa().size(), "Debe tener 1 página asignada");

        // 3. Asignamos memoria para el 2do token (Cabe en la misma página, no consume marco nuevo)
        mmu.asignarMemoriaParaToken(proceso);
        assertEquals(2, proceso.getContadorTokens());
        assertEquals(1, proceso.getPageTable().getMapa().size(), "Sigue teniendo 1 página");

        // 4. Asignamos memoria para el 3er token (Nueva página -> Nuevo marco)
        mmu.asignarMemoriaParaToken(proceso);
        assertEquals(3, proceso.getContadorTokens());
        assertEquals(2, proceso.getPageTable().getMapa().size(), "Ahora tiene 2 páginas");
    }

    @Test
    void testTLBFunctionality() throws Exception {
        // Llenamos datos
        mmu.asignarMemoriaParaToken(proceso); // Token 0 (Pagina 0)

        // Primer acceso: Debe ser MISS en TLB (está vacía)
        mmu.traducirDireccion(proceso, 0);
        assertEquals(1, mmu.getTlb().getMisses(), "El primer acceso debe ser Miss");

        // Segundo acceso a la misma página: Debe ser HIT
        mmu.traducirDireccion(proceso, 0); // Token 0 (Pagina 0)
        assertEquals(1, mmu.getTlb().getHits(), "El segundo acceso debe ser Hit (Localidad Temporal)");
    }
}