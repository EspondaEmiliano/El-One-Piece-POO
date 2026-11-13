package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.Posicion;

/**
 * Representa al antagonista del juego que patrulla el pasillo superior,
 * sigue horizontalmente al empleado y dispara proyectiles periódicamente.
 */
public class Jefe extends EntidadMovil {

    /** Dimensiones y velocidad calibradas para el proyectil (pala). */
    public static final int ANCHO_PALA = 16;
    public static final int ALTO_PALA = 16;
    public static final int VELOCIDAD_PALA = 3;

    /** Duración en milisegundos de la animación de disparo. */
    public static final int DURACION_ANIMACION_ATAQUE_MS = 400;

    private String nombre;
    private int intervalAtaque;
    private long ultimoAtaque;
    private Proyectil proyectilGenerado;

    /**
     * Inicializa al jefe con sus parámetros de patrulla y cadencia de fuego.
     * Al instanciarse, inicia el cronómetro del último ataque para respetar el tiempo de recarga
     * antes de disparar su primera pala.
     *
     * @param posicion Hitbox inicial en el pasillo superior.
     * @param velocidad Velocidad de seguimiento horizontal.
     * @param nombre Nombre descriptivo del jefe.
     * @param intervalAtaque Cadencia de disparo en milisegundos.
     */
    public Jefe(Posicion posicion, int velocidad, String nombre, int intervalAtaque) {
        super(posicion, velocidad);
        this.nombre = nombre;
        this.intervalAtaque = intervalAtaque;
        // Reinicia la cadencia al spawnear o reiniciar el nivel
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
     * Registra el timestamp del instante en el que se ejecuta el ataque.
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

            // Evita que el jefe se salga de los límites de la ventana (ancho 600)
            if (posicion.getX1() <= 12 && direccionActual == Direccion.IZQUIERDA) {
                direccionActual = Direccion.NINGUNA;
            } else if (posicion.getX2() >= 588 && direccionActual == Direccion.DERECHA) {
                direccionActual = Direccion.NINGUNA;
            } else {
                mover();
            }
        } else {
            // Si ya está alineado con el empleado, se frena para disparar
            direccionActual = Direccion.NINGUNA;
        }
    }

    /**
     * Instancia un proyectil calibrado (16x16 px a velocidad 3) en la base del jefe con dirección descendente.
     *
     * @param posicionObjetivo Coordenadas del objetivo al que se apunta.
     */
    public void atacar(Posicion posicionObjetivo) {
        int centroX = (posicion.getX1() + posicion.getX2()) / 2;
        int radioX = ANCHO_PALA / 2;

        Posicion posicionProyectil = new Posicion(
                centroX - radioX,
                posicion.getY2(),
                centroX + radioX,
                posicion.getY2() + ALTO_PALA
        );

        proyectilGenerado = new Proyectil(posicionProyectil, VELOCIDAD_PALA);
        proyectilGenerado.setDireccionActual(Direccion.ABAJO);
    }

    /**
     * Consulta si el jefe se encuentra dentro de la ventana temporal de animación de ataque.
     *
     * @return {@code true} si está atacando; {@code false} si está en reposo/patrulla.
     */
    public boolean estaAtacando() {
        return (System.currentTimeMillis() - ultimoAtaque) < DURACION_ANIMACION_ATAQUE_MS;
    }

    /** @return El último proyectil instanciado listo para ser incorporado al juego. */
    public Proyectil getProyectilGenerado() {
        return proyectilGenerado;
    }

    /** Limpia la referencia del proyectil una vez añadido a la lista del motor. */
    public void limpiarProyectilGenerado() {
        proyectilGenerado = null;
    }
}
