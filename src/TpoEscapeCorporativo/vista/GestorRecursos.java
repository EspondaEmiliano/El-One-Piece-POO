package TpoEscapeCorporativo.vista;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Administra la carga, almacenamiento en caché y entrega de recursos
 * visuales externos (fuentes tipográficas y sprites de entidades).
 */
public class GestorRecursos {

    // Rutas base
    private static final String RUTA_FUENTE_ARCADE = "assets/fuentes/PressStart2P.ttf";
    private static final String CARPETA_SPRITES = "assets/sprites/";

    // Nombres de archivos estándar
    public static final String SPRITE_EMPLEADO = "empleado_abajo.gif";
    public static final String SPRITE_JEFE = "jefe.gif";
    public static final String SPRITE_PROYECTIL = "proyectil.png";
    public static final String SPRITE_MURO = "muro.png";
    public static final String SPRITE_SALIDA = "salida.png";
    public static final String SPRITE_BUFFO_CAFE = "buffo_cafe.png";
    public static final String SPRITE_BUFFO_AGUINALDO = "buffo_aguinaldo.png";

    // Caché en memoria (Patrón Flyweight para no saturar memoria ni disco)
    private static Font fuenteArcadeBase;
    private static final Map<String, BufferedImage> cacheImagenes = new HashMap<>();

    // Constructor privado: clase utilitaria pura
    private GestorRecursos() {}

    /**
     * Carga y retorna la fuente arcade en el tamaño especificado.
     * Si no se encuentra el archivo, devuelve una fuente Monospaced por defecto.
     *
     * @param tamanio Tamaño en puntos.
     * @return Instancia de Font lista para aplicar a componentes Swing o Graphics2D.
     */
    public static Font getFuenteArcade(float tamanio) {
        if (fuenteArcadeBase == null) {
            try {
                File archivoFuente = new File(RUTA_FUENTE_ARCADE);
                if (archivoFuente.exists()) {
                    fuenteArcadeBase = Font.createFont(Font.TRUETYPE_FONT, archivoFuente);
                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    ge.registerFont(fuenteArcadeBase);
                } else {
                    System.err.println("Advertencia: No se encontró la fuente en " + RUTA_FUENTE_ARCADE);
                    return new Font("Monospaced", Font.BOLD, (int) tamanio);
                }
            } catch (FontFormatException | IOException e) {
                System.err.println("Error al cargar la fuente arcade: " + e.getMessage());
                return new Font("Monospaced", Font.BOLD, (int) tamanio);
            }
        }
        return fuenteArcadeBase.deriveFont(tamanio);
    }

    /**
     * Obtiene una imagen de la caché o la lee del disco si es la primera vez que se solicita.
     * Si el archivo no existe, genera dinámicamente un sprite temporal de respaldo (placeholder).
     *
     * @param nombreArchivo Nombre del archivo en assets/sprites/ (ej: "muro.png").
     * @return BufferedImage lista para dibujar con Graphics2D.
     */
    public static BufferedImage getImagen(String nombreArchivo) {
        if (cacheImagenes.containsKey(nombreArchivo)) {
            return cacheImagenes.get(nombreArchivo);
        }

        File archivo = new File(CARPETA_SPRITES + nombreArchivo);
        BufferedImage imagen = null;

        if (archivo.exists()) {
            try {
                imagen = ImageIO.read(archivo);
            } catch (IOException e) {
                System.err.println("Error al leer la imagen " + nombreArchivo + ": " + e.getMessage());
            }
        } else {
            System.err.println("Aviso: Sprite no encontrado (" + archivo.getPath() + "). Usando placeholder.");
        }

        // Si la imagen no pudo cargarse, creamos un placeholder de color de 32x32 para que el juego siga andando
        if (imagen == null) {
            imagen = crearPlaceholder(nombreArchivo);
        }

        cacheImagenes.put(nombreArchivo, imagen);
        return imagen;
    }

    // Métodos de acceso rápido por entidad (muy cómodos para la vista)
    public static BufferedImage getSpriteEmpleado() {
        return getImagen(SPRITE_EMPLEADO);
    }

    public static BufferedImage getSpriteJefe() {
        return getImagen(SPRITE_JEFE);
    }

    public static BufferedImage getSpriteProyectil() {
        return getImagen(SPRITE_PROYECTIL);
    }

    public static BufferedImage getSpriteMuro() {
        return getImagen(SPRITE_MURO);
    }

    public static BufferedImage getSpriteSalida() {
        return getImagen(SPRITE_SALIDA);
    }

    public static BufferedImage getSpriteCafe() {
        return getImagen(SPRITE_BUFFO_CAFE);
    }

    public static BufferedImage getSpriteAguinaldo() {
        return getImagen(SPRITE_BUFFO_AGUINALDO);
    }

    /**
     * Genera un bloque de color temporal de 32x32 píxeles si la imagen PNG aún no existe.
     * Esto evita que el juego se rompa antes de que agreguen los assets finales.
     */
    private static BufferedImage crearPlaceholder(String clave) {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Color distintivo según la entidad
        if (clave.contains("empleado")) g.setColor(Color.BLUE);
        else if (clave.contains("jefe")) g.setColor(Color.RED);
        else if (clave.contains("muro")) g.setColor(Color.DARK_GRAY);
        else if (clave.contains("salida")) g.setColor(Color.GREEN);
        else if (clave.contains("proyectil")) g.setColor(Color.ORANGE);
        else g.setColor(Color.MAGENTA);

        g.fillRect(0, 0, 32, 32);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 31, 31);
        g.dispose();

        return img;
    }
}
