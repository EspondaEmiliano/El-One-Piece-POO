package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

public class Muro extends EntidadEstatica implements IColisionable {

    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return null;
    }
}
