package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.modelo.datos.Direccion;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Administra la carga, almacenamiento en caché (Flyweight) y entrega de recursos
 * visuales externos (fuentes tipográficas y sprites de entidades).
 */
public class GestorRecursos {

    private static final String RUTA_FUENTE_ARCADE = "assets/fuentes/PressStart2P.ttf";
    private static final String CARPETA_SPRITES = "assets/sprites/";

    public static final String SPRITE_EMPLEADO_ABAJO = "empleado_abajo.gif";
    public static final String SPRITE_EMPLEADO_ARRIBA = "empleado_arriba.gif";
    public static final String SPRITE_EMPLEADO_IZQ = "empleado_izq.gif";
    public static final String SPRITE_EMPLEADO_DER = "empleado_der.gif";

    public static final String SPRITE_JEFE_DEFAULT = "jefe.gif";
    public static final String SPRITE_JEFE_IZQ = "jefe_izq.gif";
    public static final String SPRITE_JEFE_DER = "jefe_der.gif";
    public static final String SPRITE_JEFE_ATAQUE = "jefe_ataque.gif";

    public static final String SPRITE_PROYECTIL = "proyectil.png";
    public static final String SPRITE_MURO = "muro.png";
    public static final String SPRITE_SALIDA = "salida.png";
    public static final String SPRITE_BUFFO_CAFE = "buffo_cafe.png";
    public static final String SPRITE_BUFFO_AGUINALDO = "buffo_aguinaldo.png";
    public static final String SPRITE_BUFFO_CORAZON = "buffo_corazon.png";

    private static Font fuenteArcadeBase;
    private static final Map<String, Image> cacheImagenes = new HashMap<>();

    private GestorRecursos() {}

    /**
     * Carga y retorna la fuente arcade en el tamaño especificado.
     * Si no se encuentra el archivo, devuelve una fuente Monospaced por defecto.
     *
     * @param tamanio Tamaño en puntos.
     * @return Instancia de {@link Font} lista para aplicar a componentes Swing o {@link Graphics2D}.
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
     * Si el archivo no existe, genera dinámicamente un sprite temporal de respaldo.
     *
     * @param nombreArchivo Nombre del archivo en {@code assets/sprites/} (ej. {@code "muro.png"}).
     * @return Instancia de {@link Image} lista para dibujar con {@link Graphics2D}.
     */
    public static Image getImagen(String nombreArchivo) {
        if (cacheImagenes.containsKey(nombreArchivo)) {
            return cacheImagenes.get(nombreArchivo);
        }
        File archivo = new File(CARPETA_SPRITES + nombreArchivo);
        Image imagen = null;

        if (archivo.exists()) {
            if (nombreArchivo.toLowerCase().endsWith(".gif")) {
                imagen = new ImageIcon(archivo.getAbsolutePath()).getImage();
            } else {
                try {
                    imagen = ImageIO.read(archivo);
                } catch (IOException e) {
                    System.err.println("Error al leer imagen: " + e.getMessage());
                }
            }
        }
        if (imagen == null) {
            imagen = crearPlaceholder(nombreArchivo);
        }
        cacheImagenes.put(nombreArchivo, imagen);
        return imagen;
    }

    /**
     * Obtiene el sprite del empleado orientado según su dirección actual de movimiento.
     *
     * @param direccion Dirección hacia la cual mira el jugador.
     * @return Sprite animado correspondiente a la orientación.
     */
    public static Image getSpriteEmpleado(Direccion direccion) {
        switch (direccion) {
            case ARRIBA:
                return getImagen(SPRITE_EMPLEADO_ARRIBA);
            case IZQUIERDA:
                return getImagen(SPRITE_EMPLEADO_IZQ);
            case DERECHA:
                return getImagen(SPRITE_EMPLEADO_DER);
            case ABAJO:
            default:
                return getImagen(SPRITE_EMPLEADO_ABAJO);
        }
    }

    /**
     * Obtiene el sprite del jefe según su orientación o si está disparando un proyectil.
     *
     * @param direccion    Dirección de desplazamiento horizontal del jefe.
     * @param estaAtacando {@code true} si se encuentra dentro de la animación de ataque.
     * @return Sprite animado del jefe.
     */
    public static Image getSpriteJefe(Direccion direccion, boolean estaAtacando) {
        if (estaAtacando) {
            return getImagen(SPRITE_JEFE_ATAQUE);
        }
        switch (direccion) {
            case IZQUIERDA:
                return getImagen(SPRITE_JEFE_IZQ);
            case DERECHA:
                return getImagen(SPRITE_JEFE_DER);
            default:
                return getImagen(SPRITE_JEFE_DEFAULT);
        }
    }

    /** @return Sprite frontal por defecto del empleado (sobrecarga sin parámetros). */
    public static Image getSpriteEmpleado() {
        return getImagen(SPRITE_EMPLEADO_ABAJO);
    }

    /** @return Sprite frontal por defecto del jefe (sobrecarga sin parámetros). */
    public static Image getSpriteJefe() {
        return getImagen(SPRITE_JEFE_DEFAULT);
    }

    /** @return Sprite del proyectil (pala) arrojado por el jefe. */
    public static Image getSpriteProyectil() {
        return getImagen(SPRITE_PROYECTIL);
    }

    /** @return Sprite de bloque sólido de muro. */
    public static Image getSpriteMuro() {
        return getImagen(SPRITE_MURO);
    }

    /** @return Sprite de la puerta de salida del nivel. */
    public static Image getSpriteSalida() {
        return getImagen(SPRITE_SALIDA);
    }

    /** @return Sprite del potenciador Café. */
    public static Image getSpriteCafe() {
        return getImagen(SPRITE_BUFFO_CAFE);
    }

    /** @return Sprite del potenciador Aguinaldo (escudo). */
    public static Image getSpriteAguinaldo() {
        return getImagen(SPRITE_BUFFO_AGUINALDO);
    }

    /** @return Sprite del potenciador Corazón (+1 vida). */
    public static Image getSpriteCorazon() {
        return getImagen(SPRITE_BUFFO_CORAZON);
    }

    private static BufferedImage crearPlaceholder(String clave) {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (clave.contains("corazon")) {
            int[] xs = {16, 10, 9, 11, 13, 16, 19, 21, 23, 22};
            int[] ys = {22, 16, 13, 10, 10, 13, 10, 10, 13, 16};

            g.setColor(new Color(235, 35, 60));
            g.fillPolygon(xs, ys, xs.length);
            g.setColor(new Color(255, 145, 160));
            g.fillRect(11, 12, 2, 2);
            g.setColor(new Color(255, 220, 225));
            g.setStroke(new BasicStroke(1.0f));
            g.drawPolygon(xs, ys, xs.length);
            g.dispose();
            return img;
        }

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
