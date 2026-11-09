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
import TpoEscapeCorporativo.servicios.GestorAudio;

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
    public static final int TOTAL_NIVELES = 5;

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
    private long tiempoAcumuladoNivelesPreviosMs;
    private int puntajeAcumulado;
    private int puntajeBaseNivelActual;

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
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        this.puntajeBaseNivelActual = 0;

        reiniciarNivel(this.numeroNivelActual);
    }

    /**
     * Inicia una nueva partida desde el nivel indicado, restableciendo
     * el puntaje base y el tiempo acumulado de niveles previos.
     *
     * @param numeroNivel Índice numérico del mapa inicial elegido.
     */
    public void iniciarPartidaEnNivel(int numeroNivel) {
        this.puntajeBaseNivelActual = 0;
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        reiniciarNivel(numeroNivel);
    }

    /**
     * Reinicia el estado del juego para un nivel específico:
     * - Restablece el cronómetro al límite completo (60s).
     * - Restaura todos los buffos del mapa.
     * - Reubica al jefe y al empleado en sus coordenadas originales.
     * - Restablece el puntaje al acumulado hasta el inicio de dicho nivel.
     * - Limpia los proyectiles activos y mantiene el estado en {@link EstadoJuego#JUGANDO}.
     *
     * @param numeroNivel Índice numérico del mapa a cargar.
     */
    public void reiniciarNivel(int numeroNivel) {
        this.numeroNivelActual = numeroNivel;
        if (numeroNivel == 1) {
            this.puntajeBaseNivelActual = 0;
            this.tiempoAcumuladoNivelesPreviosMs = 0;
        }
        this.puntajeAcumulado = this.puntajeBaseNivelActual;
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

                if (buffoConsumido instanceof Aguinaldo) {
                    Aguinaldo aguinaldo = (Aguinaldo) buffoConsumido;
                    this.puntajeAcumulado += aguinaldo.getPuntosBonus();
                } else {
                    this.puntajeAcumulado += PUNTOS_POR_BUFFO;
                }
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
            List<Proyectil> nuevosProyectiles = this.enemigo.getProyectilesGenerados();

            if (!nuevosProyectiles.isEmpty()) {
                this.proyectiles.addAll(nuevosProyectiles);
                this.enemigo.registrarAtaque();
                this.enemigo.limpiarProyectilGenerado();
            }
        }

        // 6. Actualizar proyectiles y resolver impactos balísticos
        actualizarProyectiles();
    }

    /**
     * Desplaza los proyectiles arrojados balísticamente por el jefe
     * y procesa su impacto contra el empleado mediante {@link ImpactoProyectilException}.
     *
     * Si el empleado cuenta con escudo activo (Aguinaldo), absorbe el impacto y continúa la partida.
     * Si no cuenta con protección, el impacto reinicia inmediatamente el nivel actual sin transiciones.
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
                        GestorAudio.reproducirImpactoPala();

                        // Evaluar si el escudo absorbe el impacto o si resulta letal
                        boolean golpeAbsorbido = this.jugador.recibirImpacto();

                        if (golpeAbsorbido) {
                            System.out.println("[ESCUDO] ¡Pala absorbida por el Aguinaldo! El empleado continúa.");
                        } else {
                            System.out.println("[IMPACTO LETAL] El empleado fue alcanzado por una pala. Reiniciando nivel " + this.numeroNivelActual + "...");
                            reiniciarNivel(this.numeroNivelActual);
                            throw new ImpactoProyectilException("Impacto fatal de pala en nivel " + this.numeroNivelActual + ". Nivel reiniciado.");
                        }
                    } catch (ImpactoProyectilException excepcionImpacto) {
                        System.out.println("[ALERTA] " + excepcionImpacto.getMessage());
                        return; // Corta la iteración del ciclo para no operar sobre la lista reiniciada
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
     * Añade bonificación de puntos por tiempo sobrante y conservación de escudo al escapar.
     */
    private void calcularBonificacionVictoria() {
        int segundosRestantes = Math.max(0, getTiempoRestanteSegundos());
        int bonificacionTiempo = segundosRestantes * 10;
        int bonificacionEscudo = this.jugador.tieneEscudo() ? 250 : 0;

        this.puntajeAcumulado += PUNTOS_BASE_ESCAPE + bonificacionTiempo + bonificacionEscudo;
    }

    /**
     * Persiste el récord obtenido por el jugador en la tabla Top 10 del ranking,
     * consolidando el tiempo total acumulado a lo largo de la partida.
     *
     * @param aliasJugador Nombre o identificador del empleado para el Top 10.
     * @return {@code true} si el puntaje se serializó con éxito; {@code false} en caso contrario.
     */
    public boolean guardarPuntaje(String aliasJugador) {
        String nombreFinal = (aliasJugador != null && !aliasJugador.trim().isEmpty())
                ? aliasJugador.trim()
                : "Empleado";

        long tiempoTotalEmpleadoSegundos = (this.tiempoAcumuladoNivelesPreviosMs + this.tiempoTranscurridoMilisegundos) / 1000;
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
                        case '#':
                            this.muros.add(new Muro(posicion));
                            break;
                        case 'E':
                            Posicion posicionEmpleado = new Posicion(x1 + 4, y1 + 4, x1 + 28, y1 + 28);
                            this.jugador = new Empleado(posicionEmpleado, 2);
                            break;
                        case 'J':
                            // Configura velocidad, cadencia y modalidad de ataque según la dificultad del nivel
                            int velocidadJefe = 1;
                            int cadenciaJefe;
                            boolean ataqueTriple = false;
                            if (this.gestorArchivos.esNivelFinal(numeroNivel)) {
                                velocidadJefe = 1;
                                cadenciaJefe = 2000;
                                ataqueTriple = true;
                            } else {
                                switch (numeroNivel) {
                                    case 1: cadenciaJefe = 2000; break;
                                    case 2: cadenciaJefe = 1700; break;
                                    case 3: cadenciaJefe = 1400; break;
                                    default: cadenciaJefe = 1100; break;
                                }
                            }
                            this.enemigo = new Jefe(posicion, velocidadJefe, "Jefe", cadenciaJefe, ataqueTriple);
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

    /**
     * Evalúa si existe un nivel siguiente disponible en el juego.
     *
     * @return {@code true} si aún no se alcanzó el nivel final; {@code false} en caso contrario.
     */
    public boolean haySiguienteNivel() {
        return this.numeroNivelActual < getTotalNiveles();
    }

    /**
     * Indica si el nivel que se está jugando actualmente corresponde al nivel final.
     *
     * @return {@code true} si el jugador se encuentra en el nivel final; {@code false} en caso contrario.
     */
    public boolean esNivelFinalActual() {
        return this.gestorArchivos.esNivelFinal(this.numeroNivelActual);
    }

    /**
     * Obtiene la cantidad total de niveles disponibles para jugar.
     *
     * @return Total de niveles encontrados por el gestor de archivos.
     */
    public int getTotalNiveles() {
        return this.gestorArchivos.contarNivelesDisponibles();
    }

    /**
     * Obtiene las etiquetas descriptivas de todos los niveles para su presentación en el menú.
     *
     * @return Arreglo de títulos formateados de cada nivel.
     */
    public String[] getOpcionesNiveles() {
        return this.gestorArchivos.getNombresNivelesDisponibles();
    }

    /**
     * Avanza al siguiente nivel del juego, conservando el puntaje acumulado y el tiempo total
     * pero restableciendo el tiempo reglamentario del nivel, proyectiles y cargando el nuevo mapa.
     */
    public void pasarSiguienteNivel() {
        if (haySiguienteNivel()) {
            this.numeroNivelActual++;
            this.puntajeBaseNivelActual = this.puntajeAcumulado;
            this.tiempoAcumuladoNivelesPreviosMs += this.tiempoTranscurridoMilisegundos;
            this.tiempoTranscurridoMilisegundos = 0;
            this.tiempoInicioMilisegundos = System.currentTimeMillis();
            this.proyectiles.clear();
            this.estadoActual = EstadoJuego.JUGANDO;

            cargarNivel(this.numeroNivelActual);
        }
    }

    /**
     * Reinicia la partida completa desde el nivel 1 restableciendo el puntaje y tiempo acumulado a cero.
     */
    public void reiniciarPartidaCompleta() {
        this.puntajeBaseNivelActual = 0;
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        reiniciarNivel(1);
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

    public int getNumeroNivelActual() { return numeroNivelActual; }
}
