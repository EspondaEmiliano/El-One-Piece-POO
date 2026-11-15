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
     * @param posicion  Hitbox inicial.
     * @param velocidad Rapidez de caída vertical.
     */
    public Proyectil(Posicion posicion, int velocidad) {
        super(posicion, velocidad);
        this.estaActivo = true;
    }

    /**
     * Desactiva el proyectil tras colisionar con el jugador o salir del tablero.
     */
    public void impacto() {
        this.estaActivo = false;
    }

    /**
     * Indica si el proyectil continúa activo en juego.
     *
     * @return {@code true} si el proyectil sigue en juego; {@code false} si fue desactivado.
     */
    public Boolean getEstaActivo() {
        return estaActivo;
    }

    /**
     * Evalúa si el proyectil se superpone con la hitbox objetivo indicada.
     *
     * @param otraPosicion Hitbox de la entidad evaluada.
     * @return {@code true} si hay contacto o superposición; {@code false} en caso contrario.
     */
    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return this.posicion.seSuperpone(otraPosicion, true);
    }
}
