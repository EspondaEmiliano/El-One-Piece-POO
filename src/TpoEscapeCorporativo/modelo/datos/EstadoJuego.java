package TpoEscapeCorporativo.modelo.datos;

/**
 * Representa los diferentes estados en los que puede encontrarse una partida
 * dentro del ciclo de vida del motor de juego.
 */
public enum EstadoJuego {

    /**
     * La partida está activa y procesando física, colisiones y entradas de teclado.
     */
    JUGANDO("Partida en curso"),

    /**
     * El empleado ha pausado temporalmente el juego.
     */
    PAUSADO("Juego en pausa"),

    /**
     * El empleado alcanzó la salida antes de finalizar el tiempo.
     */
    VICTORIA("¡Felicitaciones! Has escapado de la oficina con éxito."),

    /**
     * El empleado perdió toda su salud debido al impacto de proyectiles.
     */
    DERROTA_VIDA("Game Over: Tu salud llegó a cero por exceso de tareas."),

    /**
     * El tiempo reglamentario de la jornada laboral expiró antes de alcanzar la salida.
     */
    DERROTA_TIEMPO("Game Over: El horario laboral terminó. Quedaste atrapado.");

    private final String mensajeDescriptivo;

    /**
     * Constructor del enumerador.
     *
     * @param mensajeDescriptivo Mensaje asociado al estado actual.
     */
    EstadoJuego(String mensajeDescriptivo) {
        this.mensajeDescriptivo = mensajeDescriptivo;
    }

    /**
     * Obtiene el mensaje descriptivo del estado para mostrar en la interfaz o logs.
     *
     * @return Cadena de texto con la descripción o resultado de la partida.
     */
    public String getMensajeDescriptivo() {
        return mensajeDescriptivo;
    }

    /**
     * Determina si el estado actual representa una conclusión definitiva de la partida
     * (sea por victoria o cualquiera de las variantes de derrota).
     *
     * @return {@code true} si la partida finalizó; {@code false} si sigue en curso o pausada.
     */
    public boolean esFinDePartida() {
        return this == VICTORIA || this == DERROTA_VIDA || this == DERROTA_TIEMPO;
    }
}
