package TpoEscapeCorporativo.controlador;

import TpoEscapeCorporativo.excepciones.ImpactoProyectilException;
import TpoEscapeCorporativo.excepciones.NivelNoEncontradoException;
import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.EstadoJuego;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.datos.Puntaje;
import TpoEscapeCorporativo.modelo.entidades.*;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;
import TpoEscapeCorporativo.servicios.GestorArchivos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Motor principal del juego: administra el ciclo de actualización de física,
 * la IA exterior del Jefe, la detección de impactos balísticos y zonas de buffos,
 * el control de jornada laboral (tiempo) y las transiciones de estado de partida.
 */
public class JuegoEngine {

    public static final int DURACION_JORNADA_SEGUNDOS = 60;
    public static final int PUNTOS_POR_BUFFO = 50;
    public static final int PUNTOS_BASE_ESCAPE = 500;

    private List<Muro> muros;
    private List<Proyectil> proyectiles;
    private List<ZonaBuffo> zonaBuffos;
    private Empleado jugador;
    private Jefe enemigo;
    private Salida salida;

    private EstadoJuego estadoActual;
    private int numeroNivelActual;
    private int tiempoLimiteSegundos;
    private long tiempoInicioMilisegundos;
    private long tiempoTranscurridoMilisegundos;
    private int puntajeAcumulado;

    private final GestorArchivos gestorArchivos;

    /**
     * Inicializa el motor, prepara las listas de entidades y carga el nivel inicial.
     */
    public JuegoEngine() {
        this.muros = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.zonaBuffos = new ArrayList<>();
        this.gestorArchivos = new GestorArchivos();

        this.numeroNivelActual = 1;
        this.tiempoLimiteSegundos = DURACION_JORNADA_SEGUNDOS;

        reiniciarNivel(this.numeroNivelActual);
    }

    /**
     * Reinicia el estado del juego para un nivel específico, rearmando el mapa,
     * reiniciando cronómetros, listas de proyectiles y puntaje.
     *
     * @param numeroNivel Índice numérico del mapa a cargar.
     */
    public void reiniciarNivel(int numeroNivel) {
        this.numeroNivelActual = numeroNivel;
        this.puntajeAcumulado = 0;
        this.tiempoTranscurridoMilisegundos = 0;
        this.tiempoInicioMilisegundos = System.currentTimeMillis();
        this.proyectiles.clear();
        this.estadoActual = EstadoJuego.JUGANDO;

        cargarNivel(numeroNivel);
    }

    /**
     * Ejecuta un frame de actualización de física, lógica y reglas de negocio.
     * Si el juego no está en {@link EstadoJuego#JUGANDO}, se suspende la actualización.
     */
    public void actualizar() {
        if (this.estadoActual != EstadoJuego.JUGANDO) {
            return;
        }

        // 1. Verificar si se agotó el horario laboral reglamentario
        long tiempoActualMilisegundos = System.currentTimeMillis();
        this.tiempoTranscurridoMilisegundos = tiempoActualMilisegundos - this.tiempoInicioMilisegundos;
        long segundosTranscurridos = this.tiempoTranscurridoMilisegundos / 1000;

        if (segundosTranscurridos >= this.tiempoLimiteSegundos) {
            this.estadoActual = EstadoJuego.DERROTA_TIEMPO;
            return;
        }

        // 2. Mover empleado y descontar tiempo de vida de buffos activos
        this.jugador.mover(this.muros);
        this.jugador.actualizarBuffos(16);

        // 3. Evaluar recolección de potenciadores en el laberinto
        for (ZonaBuffo zona : this.zonaBuffos) {
            if (!zona.isRecolectado() && zona.verificarColision(this.jugador.getPosicion())) {
                Buffo buffoConsumido = zona.consumirBuffo();
                this.jugador.agregarBuffo(buffoConsumido);
                this.puntajeAcumulado += PUNTOS_POR_BUFFO;
            }
        }

        // 4. Evaluar condición de victoria al pisar la salida de escape
        if (this.salida != null && this.salida.esAlcanzada(this.jugador.getPosicion())) {
            this.estadoActual = EstadoJuego.VICTORIA;
            calcularBonificacionVictoria();
            return;
        }

        // 5. Patrulla exterior del Jefe (seguimiento horizontal y disparo balístico)
        this.enemigo.seguirEmpleado(this.jugador.getPosicion());

        if (this.enemigo.puedeAtacar()) {
            this.enemigo.atacar(this.jugador.getPosicion());
            Proyectil nuevoProyectil = this.enemigo.getProyectilGenerado();

            if (nuevoProyectil != null) {
                this.proyectiles.add(nuevoProyectil);
                this.enemigo.registrarAtaque();
                this.enemigo.limpiarProyectilGenerado();
            }
        }

        // 6. Actualizar proyectiles y resolver impactos balísticos
        actualizarProyectiles();
    }

    /**
     * Desplaza los proyectiles arrojados y procesa colisiones con muros interiores
     * y con el empleado mediante {@link ImpactoProyectilException}.
     */
    private void actualizarProyectiles() {
        Iterator<Proyectil> iteradorProyectiles = this.proyectiles.iterator();

        while (iteradorProyectiles.hasNext()) {
            Proyectil proyectilActual = iteradorProyectiles.next();

            if (proyectilActual.getEstaActivo()) {
                proyectilActual.mover();

                // Colisión balística contra el empleado
                if (proyectilActual.verificarColision(this.jugador.getPosicion())) {
                    proyectilActual.impacto();

                    try {
                        this.jugador.recibirDanio(20);

                        // Si la salud llega a cero, se pasa a estado de derrota
                        if (this.jugador.getSalud() <= 0) {
                            this.estadoActual = EstadoJuego.DERROTA_VIDA;
                        }

                        throw new ImpactoProyectilException("Proyectil de trabajo impactó al empleado.");
                    } catch (ImpactoProyectilException excepcionImpacto) {
                        System.out.println("[ALERTA] " + excepcionImpacto.getMessage()
                                + " Salud restante: " + this.jugador.getSalud());
                    }
                }

                // Colisión con muros interiores del laberinto (Y >= 72)
                if (proyectilActual.getEstaActivo()) {
                    for (Muro muro : this.muros) {
                        if (muro.getPosicion().getY1() >= 72 && muro.verificarColision(proyectilActual.getPosicion())) {
                            proyectilActual.impacto();
                            break;
                        }
                    }
                }

                // Salida de la pantalla por el límite inferior
                if (proyectilActual.getPosicion().getY1() > 400) {
                    proyectilActual.impacto();
                }
            }

            // Remoción de proyectiles inactivos
            if (!proyectilActual.getEstaActivo()) {
                iteradorProyectiles.remove();
            }
        }
    }

    /**
     * Añade bonificación de puntos por tiempo sobrante y salud remanente al escapar.
     */
    private void calcularBonificacionVictoria() {
        int segundosRestantes = Math.max(0, getTiempoRestanteSegundos());
        int bonificacionTiempo = segundosRestantes * 10;
        int bonificacionSalud = this.jugador.getSalud() * 5;

        this.puntajeAcumulado += PUNTOS_BASE_ESCAPE + bonificacionTiempo + bonificacionSalud;
    }

    /**
     * Guarda el puntaje final del jugador en el ranking binario mediante GestorArchivos.
     *
     * @param aliasJugador Nombre o identificador del empleado para el Top 10.
     * @return {@code true} si el puntaje se serializó con éxito; {@code false} en caso contrario.
     */
    public boolean guardarPuntaje(String aliasJugador) {
        String nombreFinal = (aliasJugador != null && !aliasJugador.trim().isEmpty())
                ? aliasJugador.trim()
                : "Empleado";

        long tiempoTotalEmpleadoSegundos = this.tiempoTranscurridoMilisegundos / 1000;
        Puntaje registro = new Puntaje(nombreFinal, this.puntajeAcumulado, tiempoTotalEmpleadoSegundos);

        try {
            this.gestorArchivos.guardarPuntaje(registro);
            return true;
        } catch (Exception excepcionGuardado) {
            System.err.println("Error al persistir puntaje: " + excepcionGuardado.getMessage());
            return false;
        }
    }

    /**
     * Modifica la dirección de desplazamiento del empleado.
     *
     * @param nuevaDireccion Orientación recibida desde los controles de la vista.
     */
    public void procesarEntrada(Direccion nuevaDireccion) {
        if (this.jugador != null && this.estadoActual == EstadoJuego.JUGANDO) {
            this.jugador.setDireccionActual(nuevaDireccion);
        }
    }

    /**
     * Pausa o reanuda la partida.
     */
    public void alternarPausa() {
        if (this.estadoActual == EstadoJuego.JUGANDO) {
            this.estadoActual = EstadoJuego.PAUSADO;
        } else if (this.estadoActual == EstadoJuego.PAUSADO) {
            this.estadoActual = EstadoJuego.JUGANDO;
        }
    }

    /**
     * Carga y parsea la matriz de nivel desde el archivo de texto en disco.
     *
     * @param numeroNivel Identificador del nivel a cargar.
     */
    public void cargarNivel(int numeroNivel) {
        try {
            char[][] mapa = this.gestorArchivos.cargarMapaNivel(numeroNivel);
            int tamanioCelda = 32;

            this.muros.clear();
            this.zonaBuffos.clear();

            for (int fila = 0; fila < mapa.length; fila++) {
                for (int columna = 0; columna < mapa[fila].length; columna++) {
                    int x1 = columna * tamanioCelda + 12;
                    int y1 = fila * tamanioCelda + 8;
                    int x2 = x1 + tamanioCelda;
                    int y2 = y1 + tamanioCelda;
                    Posicion posicion = new Posicion(x1, y1, x2, y2);

                    char caracterCelda = mapa[fila][columna];
                    switch (caracterCelda) {
                        case '#': this.muros.add(new Muro(posicion)); break;
                        case 'E':
                            Posicion posicionEmpleado = new Posicion(x1 + 4, y1 + 4, x1 + 28, y1 + 28);
                            this.jugador = new Empleado(posicionEmpleado, 2);
                            break;
                        case 'J':
                            this.enemigo = new Jefe(posicion, 1, "Jefe", 1500);
                            break;
                        case 'S':
                            this.salida = new Salida(posicion);
                            break;
                        case 'C':
                            this.zonaBuffos.add(new ZonaBuffo(posicion, new Cafe()));
                            break;
                        case 'A':
                            this.zonaBuffos.add(new ZonaBuffo(posicion, new Aguinaldo()));
                            break;
                    }
                }
            }
        } catch (NivelNoEncontradoException excepcionNivel) {
            System.err.println("Error al cargar nivel: " + excepcionNivel.getMessage());
        }
    }

    // --- Métodos de consulta y Getters ---

    public EstadoJuego getEstadoActual() { return estadoActual; }

    public int getPuntajeAcumulado() { return puntajeAcumulado; }

    public int getTiempoLimiteSegundos() { return tiempoLimiteSegundos; }

    public int getTiempoRestanteSegundos() {
        long segundosTranscurridos = this.tiempoTranscurridoMilisegundos / 1000;
        return (int) Math.max(0, this.tiempoLimiteSegundos - segundosTranscurridos);
    }

    public Empleado getJugador() { return jugador; }

    public Jefe getJefe() { return enemigo; }

    public List<Proyectil> getProyectiles() { return new ArrayList<>(proyectiles); }

    public List<ZonaBuffo> getZonasBuffo() { return new ArrayList<>(zonaBuffos); }

    public List<Muro> getMuros() { return new ArrayList<>(muros); }

    public Salida getSalida() { return salida; }
}