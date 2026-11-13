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
import TpoEscapeCorporativo.modelo.items.Corazon;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;
import TpoEscapeCorporativo.servicios.GestorArchivos;
import TpoEscapeCorporativo.servicios.GestorAudio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Motor principal del juego: administra el ciclo de actualización de física,
 * la IA exterior del Jefe, la detección de impactos balísticos y zonas de buffos,
 * el sistema de 3 corazones para toda la partida, el control de jornada laboral (tiempo)
 * y las transiciones de estado.
 */
public class JuegoEngine {

    public static final int DURACION_JORNADA_SEGUNDOS = 60;
    public static final int PUNTOS_POR_BUFFO = 50;
    public static final int PUNTOS_BASE_ESCAPE = 500;
    public static final int TOTAL_NIVELES = 4;

    private List<Muro> muros;
    private List<Proyectil> proyectiles;
    private List<ZonaBuffo> zonaBuffos;
    private Empleado jugador;
    private Jefe enemigo;
    private Salida salida;

    private EstadoJuego estadoActual;
    private int numeroNivelActual;
    private int nivelMaximoDesbloqueado;
    private boolean selectorDesbloqueado;
    private int tiempoLimiteSegundos;
    private long tiempoInicioMilisegundos;
    private long tiempoTranscurridoMilisegundos;
    private long tiempoAcumuladoNivelesPreviosMs;
    private int puntajeAcumulado;
    private int puntajeBaseNivelActual;
    private int corazonesBaseNivelActual;

    private final GestorArchivos gestorArchivos;

    /**
     * Inicializa el motor, prepara las listas de entidades y carga el nivel inicial con 3 corazones.
     */
    public JuegoEngine() {
        this.muros = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.zonaBuffos = new ArrayList<>();
        this.gestorArchivos = new GestorArchivos();

        this.numeroNivelActual = 1;
        this.nivelMaximoDesbloqueado = 1;
        this.selectorDesbloqueado = false;
        this.tiempoLimiteSegundos = DURACION_JORNADA_SEGUNDOS;
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        this.puntajeBaseNivelActual = 0;
        this.corazonesBaseNivelActual = Empleado.SALUD_DEFAULT;

        reiniciarNivel(this.numeroNivelActual);
    }

    /**
     * Reinicia el estado del juego para un nivel específico, rearmando el mapa,
     * reiniciando el cronómetro del nivel, limpiando proyectiles y restaurando
     * los puntos y corazones con los que el jugador inició dicho nivel.
     *
     * @param numeroNivel Índice numérico del mapa a cargar.
     */
    public void reiniciarNivel(int numeroNivel) {
        this.numeroNivelActual = numeroNivel;
        if (numeroNivel == 1) {
            this.puntajeBaseNivelActual = 0;
            this.tiempoAcumuladoNivelesPreviosMs = 0;
            this.corazonesBaseNivelActual = Empleado.SALUD_DEFAULT;
        }
        this.puntajeAcumulado = this.puntajeBaseNivelActual;
        this.tiempoTranscurridoMilisegundos = 0;
        this.tiempoInicioMilisegundos = System.currentTimeMillis();
        this.proyectiles.clear();
        this.estadoActual = EstadoJuego.JUGANDO;

        cargarNivel(numeroNivel);
        if (this.jugador != null) {
            this.jugador.setSalud(this.corazonesBaseNivelActual);
        }
    }

    /**
     * Inicia una nueva partida desde el menú principal o el selector de niveles,
     * otorgando los 3 corazones reglamentarios y reiniciando contadores.
     *
     * @param numeroNivel Nivel elegido para comenzar a jugar.
     */
    public void iniciarPartidaEnNivel(int numeroNivel) {
        this.puntajeBaseNivelActual = 0;
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        this.corazonesBaseNivelActual = Empleado.SALUD_DEFAULT;
        reiniciarNivel(numeroNivel);
    }

    /**
     * Ejecuta un frame de actualización de física, lógica y reglas de negocio.
     * Si el juego no está en {@link EstadoJuego#JUGANDO}, se suspende la actualización.
     */
    public void actualizar() {
        if (this.estadoActual != EstadoJuego.JUGANDO) {
            return;
        }

        // 1. Verificar si se agotó el horario laboral reglamentario (Derrota por tiempo)
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
                // Si es un Corazón y el jugador ya tiene los 3 corazones llenos, no lo consume aún
                if (zona.getBuffo() instanceof Corazon && this.jugador.getSalud() >= Empleado.SALUD_DEFAULT) {
                    continue;
                }

                Buffo buffoConsumido = zona.consumirBuffo();
                this.jugador.agregarBuffo(buffoConsumido);

                if (buffoConsumido instanceof Aguinaldo) {
                    Aguinaldo aguinaldo = (Aguinaldo) buffoConsumido;
                    this.puntajeAcumulado += aguinaldo.getPuntosBonus();
                } else if (buffoConsumido instanceof Corazon) {
                    Corazon corazon = (Corazon) buffoConsumido;
                    this.puntajeAcumulado += corazon.getPuntosBonus();
                } else {
                    this.puntajeAcumulado += PUNTOS_POR_BUFFO;
                }
            }
        }

        // 4. Evaluar condición de victoria al pisar la salida de escape
        if (this.salida != null && this.salida.esAlcanzada(this.jugador.getPosicion())) {
            this.estadoActual = EstadoJuego.VICTORIA;
            calcularBonificacionVictoria();
            desbloquearProgresoNivel(this.numeroNivelActual);
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
     * Registra la superación de un nivel, habilitando el selector en el menú principal
     * y desbloqueando el nivel superado junto con el siguiente nivel disponible.
     *
     * @param nivelSuperado Número del nivel que acaba de completarse.
     */
    private void desbloquearProgresoNivel(int nivelSuperado) {
        this.selectorDesbloqueado = true;
        int siguienteDesbloqueable = Math.min(nivelSuperado + 1, getTotalNiveles());
        if (siguienteDesbloqueable > this.nivelMaximoDesbloqueado) {
            this.nivelMaximoDesbloqueado = siguienteDesbloqueable;
        }
    }

    /**
     * Desplaza los proyectiles arrojados balísticamente por el jefe
     * y procesa su impacto contra el empleado mediante {@link ImpactoProyectilException}.
     *
     * - Si el empleado tiene escudo (Aguinaldo), absorbe el impacto sin perder corazón ni retroceder.
     * - Si no tiene escudo, pierde 1 corazón:
     *   - Si aún le quedan corazones (> 0), vuelve a su casilla inicial y se limpian las palas en vuelo,
     *     manteniendo el reloj corriendo y los buffos del mapa.
     *   - Si pierde su 3.er corazón (0 corazones), la partida termina en {@link EstadoJuego#DERROTA_VIDA}.
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

                        boolean golpeAbsorbido = this.jugador.recibirImpacto();

                        if (golpeAbsorbido) {
                            System.out.println("[ESCUDO] ¡Pala absorbida por el Aguinaldo! El empleado continúa sin perder corazones.");
                        } else if (this.jugador.estaVivo()) {
                            // Aún le quedan corazones: vuelve a la casilla inicial y limpia palas en el aire
                            this.jugador.volverAPosicionInicial();
                            this.proyectiles.clear();
                            if (this.enemigo != null) {
                                this.enemigo.registrarAtaque();
                            }
                            throw new ImpactoProyectilException(
                                    "El jefe devolvió al empleado al inicio. Corazones restantes: " + this.jugador.getSalud()
                            );
                        } else {
                            // Perdió los 3 corazones: Game Over definitivo
                            this.estadoActual = EstadoJuego.DERROTA_VIDA;
                            throw new ImpactoProyectilException(
                                    "Impacto fatal de pala: el empleado perdió sus 3 corazones."
                            );
                        }
                    } catch (ImpactoProyectilException excepcionImpacto) {
                        System.out.println("[ALERTA] " + excepcionImpacto.getMessage());
                        return; // Corta la iteración tras limpiar proyectiles o finalizar la partida
                    }
                }

                // Salida de la pantalla por el límite inferior
                if (proyectilActual.getPosicion().getY1() > 400) {
                    proyectilActual.impacto();
                }
            }

            // Purga de proyectiles desactivados
            if (!proyectilActual.getEstaActivo()) {
                iteradorProyectiles.remove();
            }
        }
    }

    /**
     * Añade bonificación de puntos por tiempo sobrante, corazones conservados y escudo activo al escapar.
     */
    private void calcularBonificacionVictoria() {
        int segundosRestantes = Math.max(0, getTiempoRestanteSegundos());
        int bonificacionTiempo = segundosRestantes * 10;
        int bonificacionCorazones = this.jugador.getSalud() * 100;
        int bonificacionEscudo = this.jugador.tieneEscudo() ? 250 : 0;

        this.puntajeAcumulado += PUNTOS_BASE_ESCAPE + bonificacionTiempo + bonificacionCorazones + bonificacionEscudo;
    }

    /**
     * Persiste el récord obtenido por el jugador en la tabla Top 10 del ranking.
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
     * Soporta '#' (Muro), 'E' (Empleado), 'J' (Jefe), 'S' (Salida),
     * 'C' (Café), 'A' (Aguinaldo/Escudo) y 'V' (Corazón/+1 Vida).
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
                            // Cadencia de disparo incremental según la dificultad del nivel
                            int cadenciaJefe;
                            switch (numeroNivel) {
                                case 1: cadenciaJefe = 2200; break; // 2.2s (Nivel 1: Primer Paso - Baja)
                                case 2: cadenciaJefe = 1850; break; // 1.85s (Nivel 2: Laberinto Corporativo - Media)
                                case 3: cadenciaJefe = 1550; break; // 1.55s (Nivel 3: Ruta Dual - Alta)
                                default: cadenciaJefe = 1250; break; // 1.25s (Nivel 4: El Búnker Central - Extrema)
                            }
                            this.enemigo = new Jefe(posicion, 1, "Jefe", cadenciaJefe);
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
                        case 'V':
                        case 'H':
                            this.zonaBuffos.add(new ZonaBuffo(posicion, new Corazon()));
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
     * Obtiene la cantidad total de niveles disponibles para jugar.
     *
     * @return Total de niveles encontrados por el gestor de archivos.
     */
    public int getTotalNiveles() {
        return this.gestorArchivos.contarNivelesDisponibles();
    }

    /**
     * Obtiene el título descriptivo de un nivel específico.
     */
    public String getNombreNivel(int numeroNivel) {
        return this.gestorArchivos.getNombreNivel(numeroNivel);
    }

    /**
     * Obtiene las etiquetas descriptivas de los niveles que el jugador ya ha desbloqueado.
     *
     * @return Arreglo de títulos formateados de cada nivel desbloqueado.
     */
    public String[] getOpcionesNivelesDesbloqueados() {
        return this.gestorArchivos.getNombresNivelesDisponibles(this.nivelMaximoDesbloqueado);
    }

    /**
     * Indica si el selector de niveles se encuentra habilitado (requiere haber superado al menos un nivel).
     */
    public boolean isSelectorDesbloqueado() {
        return selectorDesbloqueado;
    }

    /**
     * Retorna el nivel más alto desbloqueado hasta el momento por el jugador.
     */
    public int getNivelMaximoDesbloqueado() {
        return nivelMaximoDesbloqueado;
    }

    /**
     * Avanza al siguiente nivel del juego, conservando el puntaje acumulado, el tiempo total
     * y los corazones que le quedan al jugador, pero restableciendo el cronómetro del nivel,
     * proyectiles y cargando el nuevo mapa.
     */
    public void pasarSiguienteNivel() {
        if (haySiguienteNivel()) {
            // Conservamos los corazones restantes para el siguiente nivel
            if (this.jugador != null && this.jugador.getSalud() > 0) {
                this.corazonesBaseNivelActual = this.jugador.getSalud();
            }

            this.numeroNivelActual++;
            this.puntajeBaseNivelActual = this.puntajeAcumulado;
            this.tiempoAcumuladoNivelesPreviosMs += this.tiempoTranscurridoMilisegundos;
            this.tiempoTranscurridoMilisegundos = 0;
            this.tiempoInicioMilisegundos = System.currentTimeMillis();
            this.proyectiles.clear();
            this.estadoActual = EstadoJuego.JUGANDO;

            cargarNivel(this.numeroNivelActual);
            if (this.jugador != null) {
                this.jugador.setSalud(this.corazonesBaseNivelActual);
            }
        }
    }

    /**
     * Reinicia la partida completa desde el nivel 1 restableciendo el puntaje a cero y los 3 corazones.
     */
    public void reiniciarPartidaCompleta() {
        this.puntajeBaseNivelActual = 0;
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        this.corazonesBaseNivelActual = Empleado.SALUD_DEFAULT;
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
