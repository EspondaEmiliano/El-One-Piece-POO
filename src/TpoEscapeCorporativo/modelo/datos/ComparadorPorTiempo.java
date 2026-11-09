package TpoEscapeCorporativo.modelo.datos;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Criterio de ordenamiento personalizado para la clasificación de puntajes.
 * Prioriza a los empleados que logran escapar en el menor tiempo posible (modo Speedrun),
 * utilizando la mayor cantidad de puntos como criterio de desempate.
 *
 * Implementa Comparator<Puntaje> para satisfacer los contenidos de la unidad 4.
 */
public class ComparadorPorTiempo implements Comparator<Puntaje>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Puntaje p1, Puntaje p2) {
        if (p1 == null && p2 == null) return 0;
        if (p1 == null) return 1;
        if (p2 == null) return -1;

        // Criterio 1: Menor tiempo primero (orden ascendente)
        int comparacionTiempo = Long.compare(p1.getTiempoSegundos(), p2.getTiempoSegundos());
        if (comparacionTiempo != 0) return comparacionTiempo;

        // Criterio 2 (Desempate): Mayor puntuación primero (orden descendente)
        return Integer.compare(p2.getPuntos(), p1.getPuntos());
    }
}
