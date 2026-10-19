package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.datos.Direccion;

public abstract class EntidadMovil {
    protected Posicion posicion;
    protected Direccion direccionActual;
    protected int velocidad;

    public EntidadMovil(Posicion posicion, int velocidad) {
        this.posicion = posicion;
        this.velocidad = velocidad;
        this.direccionActual = Direccion.NINGUNA;
    }
    public void mover() {
        int despX = direccionActual.getDx() * velocidad;
        int despY = direccionActual.getDy() * velocidad;

        posicion.setX1(posicion.getX1() + despX);
        posicion.setY1(posicion.getY1() + despY);
        posicion.setX2(posicion.getX2() + despX);
        posicion.setY2(posicion.getY2() + despY);
    }
    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public Direccion getDireccionActual() {
        return direccionActual;
    }

    public void setDireccionActual(Direccion direccionActual) {
        this.direccionActual = direccionActual;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }
}
