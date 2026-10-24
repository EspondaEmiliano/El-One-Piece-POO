package TpoEscapeCorporativo.modelo.items;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.entidades.Empleado;

/**
 * Representa un potenciador temporal de Café.
 * Incrementa la velocidad de movimiento del empleado durante un lapso de tiempo.
 */
public class Cafe implements Buffo {

    private static final int DURACION_DEFAULT_MS = 5000;        // 5 segundos de duración
    private static final int INCREMENTO_VELOCIDAD_DEFAULT = 3;  // +3 a la velocidad

    private final int icrementoVelocidad;
    private long tiempoRestanteMs;
    private boolean aplicado;

    /**
     * Constructor por defecto con 5 segundos de duración y + 3 de velocidad.
     */
    public Cafe() {
        this(DURACION_DEFAULT_MS, INCREMENTO_VELOCIDAD_DEFAULT);
    }

    /**
     * Constructor configurable.
     *
     * @param duracionMs          Tiempo que dura el efecto en milisegundos.
     * @param incrementoVelocidad Unidades de velocidad extra que recibe el empleado.
     */
    public Cafe(long duracionMs, int incrementoVelocidad) {
        this.tiempoRestanteMs = Math.max(0, duracionMs);
        this.icrementoVelocidad = incrementoVelocidad;
        this.aplicado = false;
    }

    @Override
    public void aplicar(Empleado empleado) {
        if (!aplicado && empleado != null) {
            empleado.setVelocidad(empleado.getVelocidad() + this.icrementoVelocidad);
            this.aplicado = true;
        }
    }

    @Override
    public void remover(Empleado empleado) {
        if (aplicado && empleado != null) {
            empleado.setVelocidad(Math.max(1, empleado.getVelocidad() - this.icrementoVelocidad));
            this.aplicado = false;
        }

    }

    @Override
    public void actualizar(long deltaTiempo) {
        if (tiempoRestanteMs > 0) {
            tiempoRestanteMs = Math.max(0, tiempoRestanteMs - deltaTiempo);
        }
    }

    @Override
    public boolean estaExpirado() {
        return tiempoRestanteMs <= 0;
    }

    @Override
    public String getNombre() {
        return "Cafe";
    }

    public long getTiempoRestanteMs() {
        return tiempoRestanteMs;
    }

    public int  getIncrementoVelocidad() {
        return icrementoVelocidad;
    }
}
