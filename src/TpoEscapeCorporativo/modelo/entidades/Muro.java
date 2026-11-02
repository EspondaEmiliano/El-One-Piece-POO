package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Bloque sólido estático e impenetrable del laberinto que bloquea
 * el avance del jugador y destruye proyectiles.
 */
public class Muro extends EntidadEstatica implements IColisionable {

    /**
     * Construye un muro con su posición de celda.
     *
     * @param posicion Coordenadas del bloque.
     */
    public Muro(Posicion posicion) {
        super(posicion); // Herencia de la clase Super(EntidadEstatica)
    }

    /**
     * Comprueba si otra posición rectangular colisiona contra este muro.
     *
     * @param otraPosicion Hitbox a evaluar.
     * @return {@code true} si hay solapamiento geométrico; {@code false} en caso contrario.
     */
    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return posicion.getX1() < otraPosicion.getX2() &&
               posicion.getX2() > otraPosicion.getX1() &&
               posicion.getY1() < otraPosicion.getY2() &&
               posicion.getY2() > otraPosicion.getY1();
    }
}
