package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;

public class Salida extends EntidadEstatica {

    public Salida(Posicion posicion) {
        super(posicion);
    }

    public Boolean esAlcanzada(Posicion posicionJugador) {
        return posicion.getX1() <= posicionJugador.getX2()
                && posicion.getX2() >= posicionJugador.getX1()
                && posicion.getY1() <= posicionJugador.getY2()
                && posicion.getY2() >= posicionJugador.getY1();
    }
}
