package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

public class Muro extends EntidadEstatica implements IColisionable {

    public Muro(Posicion posicion) {
        super(posicion); // Herencia de la clase Super(EntidadEstatica)
    }

    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return posicion.getX1() < otraPosicion.getX2() &&
               posicion.getX2() > otraPosicion.getX1() &&
               posicion.getY1() < otraPosicion.getY2() &&
               posicion.getY2() > otraPosicion.getY1();
    }
}
