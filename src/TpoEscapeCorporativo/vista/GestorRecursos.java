package TpoEscapeCorporativo.vista;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

/**
 * Administra la carga de recursos visuales externos (fuentes, imágenes, etc.).
 */
public class GestorRecursos {

    private static final String RUTA_FUENTE_ARCADE = "assets/fuentes/PressStart2P.ttf";
    private static Font fuenteArcadeBase;

    /**
     * Carga y registra la fuente arcade desde el disco.
     * Si no la encuentra o falla, retorna una fuente estándar de respaldo (fallback).
     *
     * @param tamanio Tamaño de la fuente en puntos.
     * @return Objeto Font listo para usar.
     */
    public static Font getFuenteArcade(float tamanio) {
        if (fuenteArcadeBase == null) {
            try {
                File archivoFuente = new File(RUTA_FUENTE_ARCADE);
                if (archivoFuente.exists()) {
                    // Carga la fuente TrueType
                    fuenteArcadeBase = Font.createFont(Font.TRUETYPE_FONT, archivoFuente);
                    // Opcional: la registra en el entorno gráfico del sistema
                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    ge.registerFont(fuenteArcadeBase);
                } else {
                    System.err.println("Advertencia: No se encontró la fuente en " + RUTA_FUENTE_ARCADE);
                    return new Font("Monospaced", Font.BOLD, (int) tamanio);
                }
            } catch (FontFormatException | IOException e) {
                System.err.println("Error al cargar la fuente arcade: " + e.getMessage());
                return new Font("Monospaced", Font.BOLD, (int) tamanio); // Fallback seguro
            }
        }
        // Se deriva la fuente base con el tamaño deseado
        return fuenteArcadeBase.deriveFont(tamanio);
    }
}
