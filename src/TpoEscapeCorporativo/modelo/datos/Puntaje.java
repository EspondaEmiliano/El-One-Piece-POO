package TpoEscapeCorporativo.modelo.datos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos que representa el puntaje obtenido por un jugador en una partida.
 * Implementa {@link Serializable} para persistencia en disco mediante GestorArchivos
 * e implementa {@link Comparable} para definir el orden natural del ranking.
 */
public class Puntaje implements Serializable, Comparable<Puntaje> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String nombreJugador;
    private int puntos;
    private long tiempoSegundos;
    private LocalDateTime fechaRegistro;

    /**
     * Constructor sobrecargado: asigna automáticamente la fecha y hora actual.
     *
     * @param nombreJugador  Nombre o alias del empleado/jugador.
     * @param puntos         Puntos acumulados durante la partida.
     * @param tiempoSegundos Tiempo total empleado hasta la salida o derrota.
     */
    public Puntaje(String nombreJugador, int puntos, long tiempoSegundos) {
        this(nombreJugador, puntos, tiempoSegundos, LocalDateTime.now());
    }

    /**
     * Constructor principal completo.
     *
     * @param nombreJugador  Nombre o alias del empleado/jugador.
     * @param puntos         Puntos acumulados durante la partida.
     * @param tiempoSegundos Tiempo total del empleado hasta la salida o derrota.
     * @param fechaRegistro  Fecha y hora del registro de la partida.
     */
    public Puntaje(String nombreJugador, int puntos, long tiempoSegundos, LocalDateTime fechaRegistro) {
        setNombreJugador(nombreJugador);
        setPuntos(puntos);
        setTiempoSegundos(tiempoSegundos);
        this.fechaRegistro = (fechaRegistro != null) ? fechaRegistro : LocalDateTime.now();
    }

    /**
     * Define el criterio de ordenamiento natural del ranking:
     * 1. Mayor cantidad de puntos primero (orden descendente).
     * 2. Desempate por menor tiempo empleado (orden ascendente).
     *
     * @param nuevoPuntaje Otro registro de puntaje a comparar.
     * @return Valor negativo si {@code this} debe ir antes, positivo si va después, o 0 si empatan.
     */
    @Override
    public int compareTo(Puntaje nuevoPuntaje) {
        if (nuevoPuntaje == null) {
            return -1;
        }

        int comparacionPuntos = Integer.compare(nuevoPuntaje.puntos, this.puntos);
        if (comparacionPuntos != 0) {
            return comparacionPuntos;
        }

        return Long.compare(this.tiempoSegundos, nuevoPuntaje.tiempoSegundos);
    }

    /** @return Nombre o alias sanitizado del jugador. */
    public String getNombreJugador() {
        return nombreJugador;
    }

    /**
     * Asigna el alias del jugador, usando "Empleado Anónimo" si es nulo o vacío.
     *
     * @param nombreJugador Alias ingresado por el usuario.
     */
    public void setNombreJugador(String nombreJugador) {
        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            this.nombreJugador = "Empleado Anónimo";
        } else {
            this.nombreJugador = nombreJugador.trim();
        }
    }

    /** @return Puntos acumulados en la partida. */
    public int getPuntos() {
        return puntos;
    }

    /** @param puntos Nuevo puntaje acumulado. */
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    /** @return Tiempo total acumulado expresado en segundos. */
    public long getTiempoSegundos() {
        return tiempoSegundos;
    }

    /**
     * Asigna el tiempo total en segundos garantizando valores no negativos.
     *
     * @param tiempoSegundos Segundos totales de partida.
     */
    public void setTiempoSegundos(long tiempoSegundos) {
        this.tiempoSegundos = Math.max(0, tiempoSegundos);
    }

    /** @return Fecha y hora en la que se registró el puntaje. */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Retorna el tiempo en formato {@code mm:ss} (ej. "03:23").
     *
     * @return Cadena formateada de minutos y segundos.
     */
    public String getTiempoFormateado() {
        long minutos = this.tiempoSegundos / 60;
        long segundos = this.tiempoSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    /**
     * Retorna la fecha de registro formateada como {@code dd/MM/yyyy HH:mm}.
     *
     * @return Fecha formateada o "Fecha desc." si es nula.
     */
    public String getFechaFormateada() {
        return (this.fechaRegistro != null) ? this.fechaRegistro.format(FORMATO_FECHA) : "Fecha desc.";
    }

    @Override
    public String toString() {
        return String.format(
                "%-15s | %5d pts | Tiempo: %s | %s",
                nombreJugador, puntos, getTiempoFormateado(), getFechaFormateada()
        );
    }
}
