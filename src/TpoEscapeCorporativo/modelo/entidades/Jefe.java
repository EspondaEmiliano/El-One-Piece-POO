package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;

public class Jefe extends EntidadMovil{
    private String nombre;
    private int intervalAtaque;

    public Jefe(Posicion posicion, int velocidad, String nombre, int intervalAtaque) {
        super(posicion, velocidad);
        this.nombre = nombre;
        this.intervalAtaque = intervalAtaque;
    }

    public void atacar(Posicion posicionObjetivo) {
        // Implementar después de ver la lógica de Proyectil
    }


}
