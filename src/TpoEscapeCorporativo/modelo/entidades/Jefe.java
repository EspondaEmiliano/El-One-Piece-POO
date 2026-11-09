package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.Posicion;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa al antagonista del juego que patrulla el pasillo superior,
 * sigue horizontalmente al empleado y dispara proyectiles periódicamente.
 */
public class Jefe extends EntidadMovil {

    /** Dimensiones y velocidad configuradas para el proyectil (pala). */
    public static final int ANCHO_PALA = 16;
    public static final int ALTO_PALA = 16;
    public static final int VELOCIDAD_PALA = 3;

    /** Separación entre centros que deja un espacio libre de 36 px entre palas (superior a un muro de 32 px). */
    public static final int SEPARACION_PALAS = 52;

    /** Límites horizontales del área interior jugable, excluyendo los muros laterales del mapa. */
    private static final int LIMITE_INTERIOR_IZQ = 44;
    private static final int LIMITE_INTERIOR_DER = 556;

    /** Duración en milisegundos de la animación de disparo. */
    public static final int DURACION_ANIMACION_ATAQUE_MS = 400;

    private String nombre;
    private int intervalAtaque;
    private long ultimoAtaque;
    private boolean ataqueTriple;
    private final List<Proyectil> proyectilesGenerados;

    /**
     * Inicializa al jefe con ataque simple por defecto y sus parámetros de patrulla.
     *
     * @param posicion Hitbox inicial en el pasillo superior.
     * @param velocidad Velocidad de seguimiento horizontal.
     * @param nombre Nombre descriptivo del jefe.
     * @param intervalAtaque Cadencia de disparo en milisegundos.
     */
    public Jefe(Posicion posicion, int velocidad, String nombre, int intervalAtaque) {
        this(posicion, velocidad, nombre, intervalAtaque, false);
    }

    /**
     * Inicializa al jefe con su configuración de patrulla, cadencia y modalidad de disparo.
     *
     * @param posicion Hitbox inicial en el pasillo superior.
     * @param velocidad Velocidad de seguimiento horizontal.
     * @param nombre Nombre descriptivo del jefe.
     * @param intervalAtaque Cadencia de disparo en milisegundos.
     * @param ataqueTriple {@code true} para disparar tres palas paralelas; {@code false} para disparo simple.
     */
    public Jefe(Posicion posicion, int velocidad, String nombre, int intervalAtaque, boolean ataqueTriple) {
        super(posicion, velocidad);
        this.nombre = nombre;
        this.intervalAtaque = intervalAtaque;
        this.ataqueTriple = ataqueTriple;
        this.proyectilesGenerados = new ArrayList<>();
        this.ultimoAtaque = System.currentTimeMillis();
    }

    /**
     * Evalúa si transcurrió el intervalo requerido para realizar un nuevo disparo.
     *
     * @return {@code true} si puede disparar; {@code false} si está en recarga.
     */
    public boolean puedeAtacar() {
        long tiempoActual = System.currentTimeMillis();
        return (tiempoActual - ultimoAtaque) >= intervalAtaque;
    }

    /**
     * Registra el instante en el que se ejecuta el ataque para controlar el tiempo de recarga.
     */
    public void registrarAtaque() {
        this.ultimoAtaque = System.currentTimeMillis();
    }

    /**
     * Desplaza horizontalmente al jefe para alinearse con la posición del empleado.
     *
     * @param posicionEmpleado Hitbox actual del jugador a perseguir.
     */
    public void seguirEmpleado(Posicion posicionEmpleado) {
        int centroJefe = (posicion.getX1() + posicion.getX2()) / 2;
        int centroEmpleado = (posicionEmpleado.getX1() + posicionEmpleado.getX2()) / 2;
        int diferenciaX = centroEmpleado - centroJefe;

        // Si la distancia horizontal es mayor a 3 px, se desplaza hacia el empleado
        if (Math.abs(diferenciaX) > 3) {
            if (diferenciaX > 0) {
                direccionActual = Direccion.DERECHA;
            } else {
                direccionActual = Direccion.IZQUIERDA;
            }

            // Evita que el jefe salga de los límites horizontales del mapa
            if (posicion.getX1() <= 12 && direccionActual == Direccion.IZQUIERDA) {
                direccionActual = Direccion.NINGUNA;
            } else if (posicion.getX2() >= 588 && direccionActual == Direccion.DERECHA) {
                direccionActual = Direccion.NINGUNA;
            } else {
                mover();
            }
        } else {
            // Si ya está alineado con el empleado, se detiene para disparar
            direccionActual = Direccion.NINGUNA;
        }
    }

    /**
     * Genera la ráfaga de proyectiles descendentes en la base del jefe según su modalidad de ataque.
     * En ataque triple añade dos palas laterales separadas siempre que no invadan los bordes.
     *
     * @param posicionObjetivo Coordenadas del objetivo al que se apunta.
     */
    public void atacar(Posicion posicionObjetivo) {
        this.proyectilesGenerados.clear();
        int centroX = (posicion.getX1() + posicion.getX2()) / 2;

        agregarPalaSiEsValida(centroX, false);

        if (this.ataqueTriple) {
            agregarPalaSiEsValida(centroX - SEPARACION_PALAS, true);
            agregarPalaSiEsValida(centroX + SEPARACION_PALAS, true);
        }
    }

    /**
     * Crea un proyectil vertical hacia abajo centrado en la coordenada indicada y lo agrega
     * a la ráfaga pendiente, omitiéndolo si supera los límites interiores cuando corresponde validar bordes.
     *
     * @param centroX Coordenada horizontal central del proyectil.
     * @param validarBorde {@code true} para descartar palas fuera de los muros laterales del mapa.
     */
    private void agregarPalaSiEsValida(int centroX, boolean validarBorde) {
        int radioX = ANCHO_PALA / 2;
        int x1 = centroX - radioX;
        int x2 = centroX + radioX;

        if (validarBorde && (x1 < LIMITE_INTERIOR_IZQ || x2 > LIMITE_INTERIOR_DER)) {
            return;
        }

        Posicion posicionProyectil = new Posicion(
                x1,
                posicion.getY2(),
                x2,
                posicion.getY2() + ALTO_PALA
        );

        Proyectil pala = new Proyectil(posicionProyectil, VELOCIDAD_PALA);
        pala.setDireccionActual(Direccion.ABAJO);
        this.proyectilesGenerados.add(pala);
    }

    /**
     * Consulta si el jefe se encuentra dentro de la ventana temporal de animación de ataque.
     *
     * @return {@code true} si está atacando; {@code false} si está en reposo o patrulla.
     */
    public boolean estaAtacando() {
        return (System.currentTimeMillis() - ultimoAtaque) < DURACION_ANIMACION_ATAQUE_MS;
    }

    /**
     * Devuelve la lista de proyectiles generados por el último ataque del jefe.
     *
     * @return Copia de la lista de proyectiles pendientes de incorporar al motor.
     */
    public List<Proyectil> getProyectilesGenerados() {
        return new ArrayList<>(proyectilesGenerados);
    }

    /**
     * Vacía la lista de proyectiles generados luego de ser transferidos al motor.
     */
    public void limpiarProyectilGenerado() {
        proyectilesGenerados.clear();
    }

    /**
     * Indica si el jefe está configurado para disparar tres palas en paralelo.
     *
     * @return {@code true} si el ataque es triple; {@code false} si es disparo simple.
     */
    public boolean isAtaqueTriple() {
        return ataqueTriple;
    }
}
