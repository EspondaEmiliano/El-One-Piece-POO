package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.modelo.datos.Direccion;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
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

    // Caché en memoria (Patrón Flyweight para no saturar memoria ni disco)
    private static Font fuenteArcadeBase;
    private static final Map<String, Image> cacheImagenes = new HashMap<>();

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
     * @return Image lista para dibujar con Graphics2D.
     */
    public static Image getImagen(String nombreArchivo) {
        if (cacheImagenes.containsKey(nombreArchivo)) {
            return cacheImagenes.get(nombreArchivo);
        }
        File archivo = new File(CARPETA_SPRITES + nombreArchivo);
        Image imagen = null;

        if (archivo.exists()) {
            if (nombreArchivo.toLowerCase().endsWith(".gif")) {
                // ImageIcon preserva todos los frames animados del GIF
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

    public static Image getSpriteEmpleado(Direccion direccion) {
        switch (direccion) {
            case ARRIBA:    return getImagen(SPRITE_EMPLEADO_ARRIBA);
            case IZQUIERDA: return getImagen(SPRITE_EMPLEADO_IZQ);
            case DERECHA:   return getImagen(SPRITE_EMPLEADO_DER);
            case ABAJO:
            default:        return getImagen(SPRITE_EMPLEADO_ABAJO);
        }
    }

    public static Image getSpriteJefe(Direccion direccion, boolean estaAtacando) {
        if (estaAtacando) {
            return getImagen(SPRITE_JEFE_ATAQUE);
        }
        switch (direccion) {
            case IZQUIERDA: return getImagen(SPRITE_JEFE_IZQ);
            case DERECHA:   return getImagen(SPRITE_JEFE_DER);
            default:        return getImagen(SPRITE_JEFE_DEFAULT);
        }
    }

    // Métodos de acceso rápido por entidad (muy cómodos para la vista)
    public static Image getSpriteEmpleado() {
        return getImagen(SPRITE_EMPLEADO_ABAJO);
    }

    public static Image getSpriteJefe() {
        return getImagen(SPRITE_JEFE_DEFAULT);
    }

    public static Image getSpriteProyectil() {
        return getImagen(SPRITE_PROYECTIL);
    }

    public static Image getSpriteMuro() {
        return getImagen(SPRITE_MURO);
    }

    public static Image getSpriteSalida() {
        return getImagen(SPRITE_SALIDA);
    }

    public static Image getSpriteCafe() {
        return getImagen(SPRITE_BUFFO_CAFE);
    }

    public static Image getSpriteAguinaldo() {
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
