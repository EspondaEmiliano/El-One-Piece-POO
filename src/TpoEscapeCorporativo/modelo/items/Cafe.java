package TpoEscapeCorporativo.modelo.items;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.entidades.Empleado;

/**
 * Representa un potenciador temporal de Café.
 * Incrementa la velocidad de movimiento del empleado durante un lapso de tiempo
 * y otorga puntos de bonificación al recolectarse.
 */
public class Cafe implements Buffo {

    private static final int DURACION_DEFAULT_MS = 5000;
    private static final int INCREMENTO_VELOCIDAD_DEFAULT = 3;
    private static final int PUNTOS_BONUS_DEFAULT = 50;

    private final long duracionInicialMs;
    private final int incrementoVelocidad;
    private final int puntosBonus;
    private long tiempoRestanteMs;
    private boolean aplicado;

    /**
     * Constructor por defecto con 5 segundos de duración, +3 de velocidad y 50 puntos bonus.
     */
    public Cafe() {
        this(DURACION_DEFAULT_MS, INCREMENTO_VELOCIDAD_DEFAULT, PUNTOS_BONUS_DEFAULT);
    }

    /**
     * Constructor configurable de duración e incremento de velocidad.
     *
     * @param duracionMs          Tiempo que dura el efecto en milisegundos.
     * @param incrementoVelocidad Unidades de velocidad extra que recibe el empleado.
     */
    public Cafe(long duracionMs, int incrementoVelocidad) {
        this(duracionMs, incrementoVelocidad, PUNTOS_BONUS_DEFAULT);
    }

    /**
     * Constructor principal completo.
     *
     * @param duracionMs          Tiempo que dura el efecto en milisegundos.
     * @param incrementoVelocidad Unidades de velocidad extra que recibe el empleado.
     * @param puntosBonus         Puntos adicionales otorgados al recolectarlo.
     */
    public Cafe(long duracionMs, int incrementoVelocidad, int puntosBonus) {
        this.duracionInicialMs = Math.max(0, duracionMs);
        this.tiempoRestanteMs = this.duracionInicialMs;
        this.incrementoVelocidad = incrementoVelocidad;
        this.puntosBonus = puntosBonus;
        this.aplicado = false;
    }

    @Override
    public void aplicar(Empleado empleado) {
        if (!aplicado && empleado != null) {
            empleado.setVelocidad(empleado.getVelocidad() + this.incrementoVelocidad);
            this.aplicado = true;
        }
    }

    @Override
    public void remover(Empleado empleado) {
        if (aplicado && empleado != null) {
            empleado.setVelocidad(Math.max(1, empleado.getVelocidad() - this.incrementoVelocidad));
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

    @Override
    public int getPuntosBonus() {
        return puntosBonus;
    }

    @Override
    public boolean esRegenerable() {
        return true;
    }

    @Override
    public Buffo crearCopia() {
        return new Cafe(this.duracionInicialMs, this.incrementoVelocidad, this.puntosBonus);
    }
}
