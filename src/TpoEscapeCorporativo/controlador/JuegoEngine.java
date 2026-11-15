package TpoEscapeCorporativo.controlador;

import TpoEscapeCorporativo.excepciones.ImpactoProyectilException;
import TpoEscapeCorporativo.excepciones.NivelNoEncontradoException;
import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.EstadoJuego;
import TpoEscapeCorporativo.modelo.datos.Puntaje;
import TpoEscapeCorporativo.modelo.entidades.*;
import TpoEscapeCorporativo.modelo.items.Corazon;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;
import TpoEscapeCorporativo.servicios.GestorArchivos;
import TpoEscapeCorporativo.servicios.GestorAudio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Motor principal del juego (Controlador): administra el ciclo de actualización de física,
 * la coordinación de la IA del Jefe, la detección de impactos balísticos y zonas de buffos,
 * el sistema de 3 corazones para toda la partida, el control de jornada laboral (tiempo)
 * y las transiciones de estado. Delega la carga/construcción de mapas a {@link CargadorNivel}.
 */
public class JuegoEngine {

    /** Duración reglamentaria de la jornada laboral por cada nivel en segundos. */
    public static final int DURACION_JORNADA_SEGUNDOS = 60;
    /** Puntos base otorgados al alcanzar la puerta de salida en un nivel. */
    public static final int PUNTOS_BASE_ESCAPE = 500;
    /** Cantidad total de niveles esperada por defecto. */
    public static final int TOTAL_NIVELES = 5;

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
    private final int tiempoLimiteSegundos;
    private long tiempoInicioMilisegundos;
    private long tiempoTranscurridoMilisegundos;
    private long tiempoAcumuladoNivelesPreviosMs;
    private int puntajeAcumulado;
    private int puntajeBaseNivelActual;
    private int corazonesBaseNivelActual;

    private final GestorArchivos gestorArchivos;
    private final CargadorNivel cargadorNivel;
    private final int totalNiveles;

    /**
     * Inicializa el motor, prepara las estructuras del nivel y comienza en el Nivel 1 con 3 corazones.
     */
    public JuegoEngine() {
        this.muros = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.zonaBuffos = new ArrayList<>();
        this.gestorArchivos = new GestorArchivos();
        this.cargadorNivel = new CargadorNivel(this.gestorArchivos);
        this.totalNiveles = this.gestorArchivos.contarNivelesDisponibles();

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
     * Ejecuta un ciclo de actualización de física, IA, colisiones y reglas de negocio.
     * Si el juego no está en {@link EstadoJuego#JUGANDO}, suspende la actualización.
     */
    public void actualizar() {
        if (this.estadoActual != EstadoJuego.JUGANDO) {
            return;
        }

        if (evaluarTiempoAgotado()) {
            return;
        }

        this.jugador.mover(this.muros);
        this.jugador.actualizarBuffos(16);

        evaluarRecoleccionBuffos();

        if (this.salida != null && this.salida.esAlcanzada(this.jugador.getPosicion())) {
            this.estadoActual = EstadoJuego.VICTORIA;
            calcularBonificacionVictoria();
            desbloquearProgresoNivel(this.numeroNivelActual);
            return;
        }

        actualizarJefe();
        actualizarProyectiles();
    }

    /**
     * Actualiza el cronómetro del nivel y verifica si finalizó el horario laboral.
     */
    private boolean evaluarTiempoAgotado() {
        long tiempoActualMilisegundos = System.currentTimeMillis();
        this.tiempoTranscurridoMilisegundos = tiempoActualMilisegundos - this.tiempoInicioMilisegundos;
        long segundosTranscurridos = this.tiempoTranscurridoMilisegundos / 1000;

        if (segundosTranscurridos >= this.tiempoLimiteSegundos) {
            this.estadoActual = EstadoJuego.DERROTA_TIEMPO;
            return true;
        }
        return false;
    }

    /**
     * Evalúa la colisión del empleado con las zonas de buffo y aplica el efecto
     * y los puntos bonus de forma polimórfica vía {@link Buffo}.
     */
    private void evaluarRecoleccionBuffos() {
        for (ZonaBuffo zona : this.zonaBuffos) {
            if (!zona.isRecolectado() && zona.verificarColision(this.jugador.getPosicion())) {
                if (zona.getBuffo() instanceof Corazon && this.jugador.getSalud() >= Empleado.SALUD_DEFAULT) {
                    continue;
                }

                Buffo buffoConsumido = zona.consumirBuffo();
                this.jugador.agregarBuffo(buffoConsumido);
                this.puntajeAcumulado += buffoConsumido.getPuntosBonus();
            }
        }
    }

    /**
     * Coordina el seguimiento horizontal y la cadencia de disparo del Jefe.
     */
    private void actualizarJefe() {
        this.enemigo.seguirEmpleado(this.jugador.getPosicion());

        if (this.enemigo.puedeAtacar()) {
            this.enemigo.atacar();
            List<Proyectil> nuevosProyectiles = this.enemigo.getProyectilesGenerados();

            if (!nuevosProyectiles.isEmpty()) {
                this.proyectiles.addAll(nuevosProyectiles);
                this.enemigo.registrarAtaque();
                this.enemigo.limpiarProyectilGenerado();
            }
        }
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
     */
    private void actualizarProyectiles() {
        Iterator<Proyectil> iteradorProyectiles = this.proyectiles.iterator();

        while (iteradorProyectiles.hasNext()) {
            Proyectil proyectilActual = iteradorProyectiles.next();

            if (proyectilActual.getEstaActivo()) {
                proyectilActual.mover();

                if (proyectilActual.verificarColision(this.jugador.getPosicion())) {
                    proyectilActual.impacto();

                    try {
                        GestorAudio.reproducirImpactoPala();
                        boolean golpeAbsorbido = this.jugador.recibirImpacto();

                        if (golpeAbsorbido) {
                            System.out.println("[ESCUDO] ¡Pala absorbida por el Aguinaldo! El empleado continúa sin perder corazones.");
                        } else if (this.jugador.estaVivo()) {
                            this.jugador.volverAPosicionInicial();
                            if (this.enemigo != null) {
                                this.enemigo.volverAPosicionInicial();
                            }
                            this.proyectiles.clear();
                            regenerarBuffosMapa();
                            throw new ImpactoProyectilException(
                                    "El jefe devolvió al empleado al inicio. Corazones restantes: " + this.jugador.getSalud()
                            );
                        } else {
                            this.estadoActual = EstadoJuego.DERROTA_VIDA;
                            throw new ImpactoProyectilException(
                                    "Impacto fatal de pala: el empleado perdió sus 3 corazones."
                            );
                        }
                    } catch (ImpactoProyectilException excepcionImpacto) {
                        System.out.println("[ALERTA] " + excepcionImpacto.getMessage());
                        return;
                    }
                }

                if (proyectilActual.getPosicion().getY1() > CargadorNivel.LIMITE_INFERIOR_ARENA_Y) {
                    proyectilActual.impacto();
                }
            }

            if (!proyectilActual.getEstaActivo()) {
                iteradorProyectiles.remove();
            }
        }
    }

    /**
     * Regenera en el mapa únicamente los potenciadores de Café y Aguinaldo ya recolectados
     * cuando el empleado vuelve al inicio tras perder un corazón. Los corazones no reaparecen.
     */
    private void regenerarBuffosMapa() {
        for (ZonaBuffo zona : this.zonaBuffos) {
            zona.regenerarSiCorresponde();
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
     * Alterna entre los estados {@link EstadoJuego#JUGANDO} y {@link EstadoJuego#PAUSADO}.
     */
    public void alternarPausa() {
        if (this.estadoActual == EstadoJuego.JUGANDO) {
            this.estadoActual = EstadoJuego.PAUSADO;
        } else if (this.estadoActual == EstadoJuego.PAUSADO) {
            this.estadoActual = EstadoJuego.JUGANDO;
        }
    }

    /**
     * Carga el nivel indicado delegando la lectura y construcción de entidades en {@link CargadorNivel}.
     *
     * @param numeroNivel Identificador del nivel a cargar.
     */
    public void cargarNivel(int numeroNivel) {
        try {
            CargadorNivel.DatosNivel datos = this.cargadorNivel.cargar(numeroNivel, this.totalNiveles);
            this.muros = datos.getMuros();
            this.zonaBuffos = datos.getZonasBuffo();
            this.jugador = datos.getJugador();
            this.enemigo = datos.getEnemigo();
            this.salida = datos.getSalida();
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
        return this.numeroNivelActual == getTotalNiveles();
    }

    /**
     * Obtiene la cantidad total de niveles disponibles para jugar.
     *
     * @return Total de niveles detectados en disco al inicializar el motor.
     */
    public int getTotalNiveles() {
        return this.totalNiveles;
    }

    /**
     * Obtiene el título descriptivo de un nivel específico.
     *
     * @param numeroNivel Identificador numérico del nivel.
     * @return Denominación temática del nivel.
     */
    public String getNombreNivel(int numeroNivel) {
        return this.gestorArchivos.getNombreNivel(numeroNivel);
    }

    /**
     * Obtiene la etiqueta descriptiva de dificultad asociada a un nivel.
     *
     * @param numeroNivel Identificador numérico del nivel.
     * @return Etiqueta de dificultad del nivel.
     */
    public String getEtiquetaDificultadNivel(int numeroNivel) {
        return this.cargadorNivel.getEtiquetaDificultad(numeroNivel);
    }

    /**
     * Indica si el selector de niveles se encuentra habilitado (requiere haber superado al menos un nivel).
     *
     * @return {@code true} si se desbloqueó el acceso al selector; {@code false} en caso contrario.
     */
    public boolean isSelectorDesbloqueado() {
        return selectorDesbloqueado;
    }

    /**
     * Retorna el nivel más alto desbloqueado hasta el momento por el jugador.
     *
     * @return Número del nivel máximo habilitado.
     */
    public int getNivelMaximoDesbloqueado() {
        return nivelMaximoDesbloqueado;
    }

    /**
     * Avanza al siguiente nivel del juego, conservando el puntaje acumulado, el tiempo total
     * y los corazones restantes, restableciendo el cronómetro del nivel y cargando el nuevo mapa.
     */
    public void pasarSiguienteNivel() {
        if (haySiguienteNivel()) {
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
     * Reinicia la partida completa desde el Nivel 1 restableciendo el puntaje a cero y los 3 corazones.
     */
    public void reiniciarPartidaCompleta() {
        this.puntajeBaseNivelActual = 0;
        this.tiempoAcumuladoNivelesPreviosMs = 0;
        this.corazonesBaseNivelActual = Empleado.SALUD_DEFAULT;
        reiniciarNivel(1);
    }

    /**
     * Gestiona el reintento tras finalizar una partida:
     * si el empleado perdió sus 3 corazones ({@link EstadoJuego#DERROTA_VIDA}),
     * reinicia la partida completa desde el Nivel 1 conservando el progreso del selector;
     * en caso contrario (p. ej. {@link EstadoJuego#DERROTA_TIEMPO}), reintenta el nivel actual.
     */
    public void reintentarTrasDerrota() {
        if (this.estadoActual == EstadoJuego.DERROTA_VIDA) {
            reiniciarPartidaCompleta();
        } else {
            reiniciarNivel(this.numeroNivelActual);
        }
    }

    /** @return Estado actual del ciclo de vida de la partida. */
    public EstadoJuego getEstadoActual() { return estadoActual; }

    /** @return Puntaje total acumulado por el jugador en la partida actual. */
    public int getPuntajeAcumulado() { return puntajeAcumulado; }

    /** @return Segundos restantes de la jornada laboral en el nivel actual. */
    public int getTiempoRestanteSegundos() {
        long segundosTranscurridos = this.tiempoTranscurridoMilisegundos / 1000;
        return (int) Math.max(0, this.tiempoLimiteSegundos - segundosTranscurridos);
    }

    /** @return Instancia actual del empleado controlado por el jugador. */
    public Empleado getJugador() { return jugador; }

    /** @return Instancia actual del jefe antagonista. */
    public Jefe getJefe() { return enemigo; }

    /** @return Copia defensiva de los proyectiles presentes en el nivel. */
    public List<Proyectil> getProyectiles() { return new ArrayList<>(proyectiles); }

    /** @return Copia defensiva de las zonas de buffo del nivel. */
    public List<ZonaBuffo> getZonasBuffo() { return new ArrayList<>(zonaBuffos); }

    /** @return Copia defensiva de los muros del laberinto. */
    public List<Muro> getMuros() { return new ArrayList<>(muros); }

    /** @return Puerta de salida del nivel actual. */
    public Salida getSalida() { return salida; }

    /** @return Número del nivel que se está jugando actualmente. */
    public int getNumeroNivelActual() { return numeroNivelActual; }
}
