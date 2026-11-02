package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Representa la puerta de escape de la oficina. Al ser alcanzada
 * por el empleado, concluye la partida con el estado de Victoria.
 */
public class Salida extends EntidadEstatica {

    /**
     * Define la posición física de la salida en el mapa.
     *
     * @param posicion Ubicación de la puerta.
     */
    public Salida(Posicion posicion) {
        super(posicion);
    }

    /**
     * Evalúa si el jugador ha alcanzado las coordenadas de la salida.
     *
     * @param posicionJugador Hitbox actual del empleado.
     * @return {@code true} si el jugador pisa la salida; {@code false} en caso contrario.
     */
    public Boolean esAlcanzada(Posicion posicionJugador) {
        return posicion.getX1() <= posicionJugador.getX2()
                && posicion.getX2() >= posicionJugador.getX1()
                && posicion.getY1() <= posicionJugador.getY2()
                && posicion.getY2() >= posicionJugador.getY1();
    }
}
