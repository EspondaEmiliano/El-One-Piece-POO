package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Posicion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Representa al personaje principal controlado por el jugador.
 * Hereda el comportamiento de movimiento de EntidadMovil y administra su estado
 * de salud y los modificadores temporales (Buffos) activos.
 */
public class Empleado extends EntidadMovil {

    public static final int SALUD_DEFAULT = 100;
    private int salud;
    private final List<Buffo> buffosActivos;

    /**
     * Crea un empleado con su posición y velocidad iniciales.
     * Asigna la salud inicial por defecto (100) y prepara la lista de buffos.
     *
     * @param posicion  Posición inicial del jugador.
     * @param velocidad Velocidad de movimiento del jugador.
     */
    public Empleado(Posicion posicion, int velocidad) {
        super(posicion, velocidad);
        this.salud = SALUD_DEFAULT;
        this.buffosActivos = new ArrayList<>();
    }

    /**
     * Reduce la salud del empleado al ser impactado por un proyectil.
     * La salud nunca desciende por debajo de 0.
     *
     * @param danio Puntos de daño recibidos
     */
    public void recibirDanio(int danio) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        this.salud = Math.max(0, this.salud - danio);
    }

    /**
     * Registra un buffo activo en el empleado.
     *
     * @param buffo Potenciador a incorporar
     */
    public void agregarBuffo(Buffo buffo) {
        if (buffo != null) {
            buffo.aplicar(this);
            this.buffosActivos.add(buffo);
        }
    }

    /**
     * Elimina buffo que ya expiró de la lista
     *
     * @param buffo Potenciador a remover.
     */
    public void removerBuffo(Buffo buffo) {
        this.buffosActivos.remove(buffo);
    }

    /**
     * Actualiza el tiempo restante de todos los buffos activos y remueve
     * aquellos que hayan expirado, revirtiendo sus efectos.
     *
     * @param deltaTiempo Milisegundos transcurridos en el frame (ej. 15 ms).
     */
    public void actualizarBuffos(long deltaTiempo) {
        Iterator<Buffo> iterator = this.buffosActivos.iterator();
        while (iterator.hasNext()) {
            Buffo buffo = iterator.next();
            buffo.actualizar(deltaTiempo);

            if (buffo.estaExpirado()) {
                buffo.remover(this);
                iterator.remove();
            }
        }
    }

    /**
     * Indica si el empleado sigue vivo para continuar la partida.
     */
    public boolean estaVivo() {
        return this.salud > 0;
    }

    // --- Getters y Setters ---

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = Math.max(0, salud);
    }

    /**
     * Devuelve una vista de solo lectura de los buffos para proteger el encapsulamiento.
     */
    public List<Buffo> getBuffosActivos() {
        return Collections.unmodifiableList(buffosActivos);
    }
}
