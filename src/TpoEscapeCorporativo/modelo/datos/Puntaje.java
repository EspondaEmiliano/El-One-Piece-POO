package TpoEscapeCorporativo.modelo.datos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos que representa el puntaje obtenido por un jugador en una partida.
 * Implementa Serializable para persistencia en disco mediante GestorArchivos
 * e implementa Comparable para definir el orden natural del ranking.
 */
public class Puntaje implements Serializable, Comparable<Puntaje> {

    private static final long serialVersionUID = 1L;

    private String nombreJugador;
    private int puntos;
    private long tiempoSegundos;
    private LocalDateTime fechaRegistro;

    /**
     * Constructor sobrecargado: asigna automáticamente la fecha y hora actual.
     *
     * @param nombreJugador   Nombre o alias del empleado/jugador.
     * @param puntos          Puntos acumulados durante la partida.
     * @param tiempoSegundos  Tiempo total empleado hasta la salida o derrota.
     */
    public Puntaje(String nombreJugador, int puntos, long tiempoSegundos) {
        this(nombreJugador,puntos,tiempoSegundos, LocalDateTime.now());
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
        this.fechaRegistro = (fechaRegistro != null) ? fechaRegistro : LocalDateTime.now() ;
    }

    /**
     * Define el criterio de ordenamiento del ranking:
     * 1. Mayor cantidad de puntos primero (orden descendente).
     * 2. Desempate por menor tiempo empleado (orden ascendente).
     */
    @Override
    public int compareTo(Puntaje nuevoPuntaje){
        if (nuevoPuntaje == null){
            return -1;
        }

        // Criterio 1_ Mayor puntuación primero
        int comparacionPuntos = Integer.compare(nuevoPuntaje.puntos, this.puntos);
        if (comparacionPuntos != 0){
            return comparacionPuntos;
        }

        // Criterio 2: A igualdad de puntos, menor tiempo primero
        return  Long.compare(this.tiempoSegundos, nuevoPuntaje.tiempoSegundos);
    }

    // Getters y Setters

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        if (nombreJugador == null || nombreJugador.trim().isEmpty()){
            this.nombreJugador = "Empleado Anónimo";
        } else {
            this.nombreJugador = nombreJugador.trim();
        }
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public long getTiempoSegundos() {
        return tiempoSegundos;
    }

    public void setTiempoSegundos(long tiempoSegundos) {
        this.tiempoSegundos = Math.max(0,tiempoSegundos);
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Retorna el tiempo en formato mm:ss (ej. "03:23").
     */
    public String getTiempoFormateado() {
        long minutos = this.tiempoSegundos / 60;
        long segundos = this.tiempoSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
                "%-15s | %5d pts | Tiempo: %s | %s",
                nombreJugador, puntos, getTiempoFormateado(), fechaRegistro.format(dtf)
                );
    }
}
