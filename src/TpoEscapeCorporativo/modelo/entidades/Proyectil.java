package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Representa una tarea o expediente arrojado por el Jefe que viaja
 * verticalmente hacia abajo dañando al empleado en caso de impacto.
 */
public class Proyectil extends EntidadMovil implements IColisionable {
    private Boolean estaActivo;

    /**
     * Construye un nuevo proyectil activo con posición y velocidad.
     *
     * @param posicion Hitbox inicial.
     * @param velocidad Rapidez de caída vertical.
     */
    public Proyectil(Posicion posicion, int velocidad) {
        super(posicion, velocidad);
        this.estaActivo = true;
    }

    /**
     * Desactiva el proyectil tras colisionar con un muro o con el jugador.
     */
    public void impacto() {
        this.estaActivo = false;
    }

    /** @return {@code true} si el proyectil sigue en juego; {@code false} si fue destruido. */
    public Boolean getEstaActivo() {
        // Indica si el proyectil continúa activo
        return estaActivo;
    }

    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return posicion.getX1() <= otraPosicion.getX2()
                && posicion.getX2() >= otraPosicion.getX1()
                && posicion.getY1() <= otraPosicion.getY2()
                && posicion.getY2() >= otraPosicion.getY1();
    }
}
