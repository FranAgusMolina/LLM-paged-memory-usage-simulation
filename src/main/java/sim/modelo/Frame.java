package sim.modelo;


import sim.util.Constantes;

/**
 * Un marco físico es un contenedor.
 * En la vida real, guarda bits.
 * En nuestra simulación de IA, diremos que guarda "tokens",
 * pero para el gestor de memoria, lo único que importa es
 * ¿Está libre o está ocupado? y ¿De quién es?.
 */
public class Frame {
    private int id;         // El "número de marco" físico (ej. 0, 1, 2...)
    private boolean ocupado; // ¿Está en uso?
    private int processID;  // ¿Qué conversación (ID) es dueña de este marco?

    public Frame(int id) {
        this.id = id;
        this.ocupado = false;                    //Al incio, la RAM esta libre
        this.processID = Constantes.NO_OCUPADO; //No tiene dueño al inciar
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public int getProcessID() {
        return processID;
    }

    public void asignar(int pid){
        this.ocupado = true;
        this.processID = pid;
    }


    public void liberar(){
        this.ocupado = false;
        this.processID = Constantes.NO_OCUPADO;
    }

    public String toString(){
        return "Frame ID: " + id + ", Estado: " + (ocupado ? "Ocupado" : "Libre") + ", Process ID: " + processID;
    }
}
