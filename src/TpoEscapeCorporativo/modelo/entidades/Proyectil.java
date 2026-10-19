package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;

public class Proyectil extends EntidadMovil implements IColisionable {
    private Boolean estaActivo;

    public Proyectil(Posicion posicion, int velocidad) {
        super(posicion, velocidad);
        this.estaActivo = true;
    }

    public void impacto() {
        this.estaActivo = false;
    }

    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        return posicion.getX1() <= otraPosicion.getX2()
                && posicion.getX2() >= otraPosicion.getX1()
                && posicion.getY1() <= otraPosicion.getY2()
                && posicion.getY2() >= otraPosicion.getY1();
    }
}
