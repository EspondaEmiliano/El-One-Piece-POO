package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Clase base abstracta para todos los elementos del juego cuya posición
 * permanece inalterable a lo largo de la partida (muros, salida, ítems).
 */
public abstract class EntidadEstatica {
    /** Ubicación espacial fija del elemento en el mapa. */
    protected Posicion posicion;

    /**
     * Construye una entidad estática fijando sus coordenadas iniciales.
     * @param posicion Hitbox fija del elemento.
     */
    public EntidadEstatica(Posicion posicion){
        this.posicion = posicion;
    }

    /**
     * Obtiene la posición de la entidad estática.
     *
     * @return Instancia de {@link Posicion}
     */
    public Posicion getPosicion() {
        return posicion;
    }
}
