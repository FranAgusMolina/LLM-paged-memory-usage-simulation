package sim.modelo;

import java.util.Objects;

public class Perfil {
    private String nombre;
    private int colGrilla;
    private int filasGrilla;
    private int totalMarcosRam;

    private int pageSize;
    private int TLBSize;

    private int simSpeed;

    public Perfil(String nombre, int colGrilla, int filasGrilla, int pageSize, int simSpeed, int TLBSize) {
        this.nombre = nombre;
        this.colGrilla = colGrilla;
        this.filasGrilla = filasGrilla;
        this.pageSize = pageSize;
        this.simSpeed = simSpeed;
        this.TLBSize = TLBSize;
        this.totalMarcosRam = colGrilla * filasGrilla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getColGrilla() {
        return colGrilla;
    }

    public void setColGrilla(int colGrilla) {
        this.colGrilla = colGrilla;
    }

    public int getFilasGrilla() {
        return filasGrilla;
    }

    public void setFilasGrilla(int filasGrilla) {
        this.filasGrilla = filasGrilla;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSimSpeed() {
        return simSpeed;
    }

    public void setSimSpeed(int simSpeed) {
        this.simSpeed = simSpeed;
    }

    public int getTLBSize() {
        return TLBSize;
    }

    public void setTLBSize(int TLBSize) {
        this.TLBSize = TLBSize;
    }

    public int getTotalMarcosRam() {
        return totalMarcosRam;
    }

    public void setTotalMarcosRam(int totalMarcosRam) {
        this.totalMarcosRam = totalMarcosRam;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Perfil perfil = (Perfil) o;
        return colGrilla == perfil.colGrilla && filasGrilla == perfil.filasGrilla && totalMarcosRam == perfil.totalMarcosRam && pageSize == perfil.pageSize && TLBSize == perfil.TLBSize && simSpeed == perfil.simSpeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colGrilla, filasGrilla, totalMarcosRam, pageSize, TLBSize, simSpeed);
    }

    @Override
    public String toString() {
        return "Perfil{" +
                "colGrilla=" + colGrilla +
                ", filasGrilla=" + filasGrilla +
                ", totalMarcosRam=" + totalMarcosRam +
                ", pageSize=" + pageSize +
                ", TLBSize=" + TLBSize +
                ", simSpeed=" + simSpeed +
                '}';
    }
}
