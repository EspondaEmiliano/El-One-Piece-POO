package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Representa la puerta de escape de la oficina. Al ser alcanzada
 * por el empleado, concluye la partida con el estado de Victoria.
 */
public class Salida extends EntidadEstatica implements IColisionable {

    /**
     * Define la posición física de la salida en el mapa.
     *
     * @param posicion Ubicación de la puerta.
     */
    public Salida(Posicion posicion) {
        super(posicion);
    }

    /**
     * Evalúa si otra entidad colisiona con las coordenadas de la salida.
     *
     * @param otraPosicion Hitbox a evaluar contra la puerta de salida.
     * @return {@code true} si hay superposición física; {@code false} en caso contrario.
     */
    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        if (otraPosicion == null) {
            return false;
        }
        return posicion.getX1() <= otraPosicion.getX2()
                && posicion.getX2() >= otraPosicion.getX1()
                && posicion.getY1() <= otraPosicion.getY2()
                && posicion.getY2() >= otraPosicion.getY1();
    }

    /**
     * Evalúa si el jugador ha alcanzado las coordenadas de la salida.
     *
     * @param posicionJugador Hitbox actual del empleado.
     * @return {@code true} si el jugador pisa la salida; {@code false} en caso contrario.
     */
    public Boolean esAlcanzada(Posicion posicionJugador) {
        return verificarColision(posicionJugador);
    }
}
