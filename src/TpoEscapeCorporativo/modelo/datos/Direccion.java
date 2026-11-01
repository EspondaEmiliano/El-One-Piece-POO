package TpoEscapeCorporativo.modelo.datos;

/**
 * Enumerador que define los sentidos vectoriales de desplazamiento
 * para las entidades móviles del mapa.
 */
public enum Direccion {
    /** Desplazamiento vertical ascendente. */
    ARRIBA(0, -1),
    /** Desplazamiento vertical descendente. */
    ABAJO(0, 1),
    /** Desplazamiento horizontal hacia la izquierda. */
    IZQUIERDA(-1, 0),
    /** Desplazamiento horizontal hacia la derecha. */
    DERECHA(1, 0),
    /** Entidad en reposo / sin desplazamiento. */
    NINGUNA(0, 0);

    private final int dx;
    private final int dy;

    Direccion(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** @return Factor multiplicador de desplazamiento horizontal (-1, 0, 1). */
    public int getDx() { return dx; }

    /** @return Factor multiplicador de desplazamiento vertical (-1, 0, 1). */
    public int getDy() { return dy; }
}
