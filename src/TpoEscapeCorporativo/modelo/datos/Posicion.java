package TpoEscapeCorporativo.modelo.datos;

/**
 * Representa la hitbox rectangular o área de ocupación de una entidad
 * delimitada por dos puntos: esquina superior izquierda (x1, y1) y
 * esquina inferior derecha (x2, y2).
 */
public class Posicion {
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    /**
     * Crea un nuevo delimitador rectangular.
     *
     * @param x1 Coordenada horizontal izquierda.
     * @param y1 Coordenada vertical superior.
     * @param x2 Coordenada horizontal derecha.
     * @param y2 Coordenada vertical inferior.
     */
    public Posicion(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /** @return Coordenada horizontal izquierda (x1). */
    public int getX1() { return x1; }
    /** @param x1 Nueva coordenada horizontal izquierda. */
    public void setX1(int x1) { this.x1 = x1; }

    /** @return Coordenada vertical superior (y1). */
    public int getY1() { return y1; }
    /** @param y1 Nueva coordenada vertical superior. */
    public void setY1(int y1) { this.y1 = y1; }

    /** @return Coordenada horizontal derecha (x2). */
    public int getX2() { return x2; }
    /** @param x2 Nueva coordenada horizontal derecha. */
    public void setX2(int x2) { this.x2 = x2; }

    /** @return Coordenada vertical inferior (y2). */
    public int getY2() { return y2; }
    /** @param y2 Nueva coordenada vertical inferior. */
    public void setY2(int y2) { this.y2 = y2; }
}
