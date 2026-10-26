package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;

public abstract class EntidadEstatica {
    protected Posicion posicion;

    public EntidadEstatica(Posicion posicion){
        this.posicion = posicion;
    }

    public Posicion getPosicion() {
        return posicion;
    }
}
