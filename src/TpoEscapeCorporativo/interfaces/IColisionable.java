package TpoEscapeCorporativo.interfaces;

import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Define el contrato de detección de colisiones entre entidades
 * dentro del plano bidimensional del juego.
 */
public interface IColisionable {

    /**
     * Evalúa si la hitbox actual se superpone con la posición de otra entidad.
     *
     * @param otraPosicion Coordenadas del objeto contra el cual se evalúa la colisión.
     * @return {@code true} si hay superposición física; {@code false} en caso contrario.
     */
    Boolean verificarColision(Posicion otraPosicion);
}
