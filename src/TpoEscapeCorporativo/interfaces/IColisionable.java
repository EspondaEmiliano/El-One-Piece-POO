package TpoEscapeCorporativo.interfaces;

import TpoEscapeCorporativo.modelo.datos.Posicion;

public interface IColisionable {
    Boolean verificarColision(Posicion otraPosicion);
}
