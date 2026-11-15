package TpoEscapeCorporativo.modelo.datos;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Criterio de ordenamiento personalizado para la clasificación de puntajes.
 * Prioriza a los empleados que logran escapar en el menor tiempo posible (modo Speedrun),
 * utilizando la mayor cantidad de puntos como criterio de desempate.
 *
 * Implementa {@code Comparator<Puntaje>} para satisfacer los contenidos de la Unidad 4.
 */
public class ComparadorPorTiempo implements Comparator<Puntaje>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Compara dos puntajes priorizando el menor tiempo acumulado y desempatando por mayor puntuación.
     *
     * @param p1 Primer registro de puntaje a comparar.
     * @param p2 Segundo registro de puntaje a comparar.
     * @return Valor negativo si {@code p1} precede a {@code p2}, positivo si va después, o 0 si empatan.
     */
    @Override
    public int compare(Puntaje p1, Puntaje p2) {
        if (p1 == null && p2 == null) return 0;
        if (p1 == null) return 1;
        if (p2 == null) return -1;

        int comparacionTiempo = Long.compare(p1.getTiempoSegundos(), p2.getTiempoSegundos());
        if (comparacionTiempo != 0) return comparacionTiempo;

        return Integer.compare(p2.getPuntos(), p1.getPuntos());
    }
}
