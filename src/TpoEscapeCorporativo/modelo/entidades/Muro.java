package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Bloque sólido estático e impenetrable del laberinto que bloquea
 * el avance del jugador.
 */
public class Muro extends EntidadEstatica implements IColisionable {

    /**
     * Construye un muro con su posición de celda.
     *
     * @param posicion Coordenadas del bloque.
     */
    public Muro(Posicion posicion) {
        super(posicion);
    }

    /**
     * Comprueba si otra posición rectangular colisiona contra este muro
     * evaluando intersección estricta (sin considerar roce de bordes).
     *
     * @param otraPosicion Hitbox a evaluar.
     * @return {@code true} si hay solapamiento geométrico; {@code false} en caso contrario.
     */
    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return this.posicion.seSuperpone(otraPosicion, false);
    }
}
