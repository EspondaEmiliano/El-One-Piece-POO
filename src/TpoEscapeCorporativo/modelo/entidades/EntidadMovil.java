package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.datos.Direccion;

/**
 * Clase base abstracta para todos los actores del juego capaces de desplazarse
 * (Empleado, Jefe, Proyectil), administrando su velocidad y dirección actual.
 */
public abstract class EntidadMovil {
    /** Coordenadas espaciales actuales de la entidad. */
    protected Posicion posicion;
    /** Dirección hacia donde se mueve en el frame actual. */
    protected Direccion direccionActual;
    /** Cantidad de píxeles que recorre por frame. */
    protected int velocidad;

    /**
     * Inicializa la entidad móvil con posición y velocidad.
     *
     * @param posicion Ubicación inicial en el mapa.
     * @param velocidad Magnitud de desplazamiento por ciclo.
     */
    public EntidadMovil(Posicion posicion, int velocidad) {
        this.posicion = posicion;
        this.velocidad = velocidad;
        this.direccionActual = Direccion.NINGUNA;
    }

    /**
     * Aplica el desplazamiento vectorial a las coordenadas según su dirección y velocidad.
     */
    public void mover() {
        int despX = direccionActual.getDx() * velocidad;
        int despY = direccionActual.getDy() * velocidad;

        posicion.setX1(posicion.getX1() + despX);
        posicion.setY1(posicion.getY1() + despY);
        posicion.setX2(posicion.getX2() + despX);
        posicion.setY2(posicion.getY2() + despY);
    }
    /** @return Hitbox actual de la entidad. */
    public Posicion getPosicion() { return posicion; }

    /** @param posicion Nueva ubicación de la entidad. */
    public void setPosicion(Posicion posicion) { this.posicion = posicion; }

    /** @return Sentido actual de movimiento. */
    public Direccion getDireccionActual() { return direccionActual; }

    /** @param direccionActual Nuevo sentido de movimiento. */
    public void setDireccionActual(Direccion direccionActual) { this.direccionActual = direccionActual; }

    /** @return Velocidad de desplazamiento en píxeles por frame. */
    public int getVelocidad() { return velocidad; }

    /** @param velocidad Nueva velocidad de movimiento. */
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
}
