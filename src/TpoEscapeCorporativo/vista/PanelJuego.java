package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.EstadoJuego;
import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Lienzo principal de juego: coordina el Game Loop a 60 FPS ({@link Timer}),
 * captura las entradas de teclado del jugador y gestiona las transiciones de fin de partida,
 * delegando el renderizado 2D completo en {@link RenderizadorJuego}.
 */
public class PanelJuego extends JPanel {

    /** Ancho lógico del tablero de juego en píxeles. */
    public static final int ANCHO = 600;
    /** Alto lógico del área de laberinto en píxeles. */
    public static final int ALTO_JUEGO = 400;
    /** Alto de la barra superior del HUD en píxeles. */
    public static final int ALTO_HUD = 55;
    /** Alto lógico total del lienzo (área de juego + HUD). */
    public static final int ALTO = ALTO_JUEGO + ALTO_HUD;

    private final JuegoEngine motor;
    private final PanelPrincipal panelPrincipal;
    private final RenderizadorJuego renderizador;
    private Timer timer;
    private boolean puntajeRegistrado = false;
    private String aliasJugadorActual = null;
    private final Set<Integer> teclasPresionadas = new LinkedHashSet<>();

    /**
     * Inicializa el panel de juego asociándolo con el motor lógico y el coordinador de vistas.
     *
     * @param motor          Controlador del estado de juego.
     * @param panelPrincipal Contenedor principal para regresar al menú o mostrar la pantalla de victoria.
     */
    public PanelJuego(JuegoEngine motor, PanelPrincipal panelPrincipal) {
        this.motor = motor;
        this.panelPrincipal = panelPrincipal;
        this.renderizador = new RenderizadorJuego();

        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(new Color(25, 25, 25));
        this.setFocusable(true);

        configurarGameLoop();
        configurarControles();
    }

    /**
     * Restablece las banderas de sesión y el estado de teclas antes de comenzar una nueva partida.
     * Visibilidad de paquete (default) para coordinación interna con {@link PanelPrincipal}.
     */
    void prepararNuevaPartida() {
        this.puntajeRegistrado = false;
        this.aliasJugadorActual = null;
        this.teclasPresionadas.clear();
    }

    private void configurarGameLoop() {
        this.timer = new Timer(16, e -> {
            motor.actualizar();
            repaint();

            if (motor.getEstadoActual() != null && motor.getEstadoActual().esFinDePartida()) {
                timer.stop();
                GestorAudio.getInstance().detenerMusica();

                if (motor.getEstadoActual() == EstadoJuego.VICTORIA
                        && !motor.haySiguienteNivel()
                        && !puntajeRegistrado) {
                    puntajeRegistrado = true;
                    solicitarYGuardarPuntajeJugador();
                }
            }
        });
    }

    private void solicitarYGuardarPuntajeJugador() {
        SwingUtilities.invokeLater(() -> {
            if (this.aliasJugadorActual == null || this.aliasJugadorActual.trim().isEmpty()) {
                String mensaje = "¡Has logrado escapar de la corporación!\nTu puntaje final: "
                        + motor.getPuntajeAcumulado()
                        + "\n\nIngresa tu alias para el Top 10:";
                String aliasIngresado = JOptionPane.showInputDialog(
                        this,
                        mensaje,
                        "Registrar Récord",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (aliasIngresado != null && !aliasIngresado.trim().isEmpty()) {
                    this.aliasJugadorActual = aliasIngresado.trim();
                } else {
                    this.aliasJugadorActual = "Empleado";
                }
            }

            motor.guardarPuntaje(this.aliasJugadorActual);
            repaint();
        });
    }

    /**
     * Inicia el bucle de juego y la música de fondo.
     * Visibilidad de paquete (default) para coordinación interna con {@link PanelPrincipal}.
     */
    void iniciarJuego() {
        this.requestFocusInWindow();
        if (!timer.isRunning()) {
            timer.start();
            GestorAudio.getInstance().iniciarMusica();
        }
    }

    /**
     * Pausa el bucle de juego y la música de fondo.
     * Visibilidad de paquete (default) para coordinación interna con {@link PanelPrincipal}.
     */
    void pausarJuego() {
        if (timer.isRunning()) {
            timer.stop();
            GestorAudio.getInstance().pausarMusica();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.renderizador.renderizar(g, getWidth(), getHeight(), this.motor, this);
    }

    private Direccion obtenerDireccion(int tecla) {
        switch (tecla) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                return Direccion.ARRIBA;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                return Direccion.ABAJO;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                return Direccion.IZQUIERDA;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                return Direccion.DERECHA;
            default:
                return null;
        }
    }

    private void actualizarDireccion() {
        Direccion direccion = Direccion.NINGUNA;
        for (Integer tecla : teclasPresionadas) {
            Direccion nuevaDireccion = obtenerDireccion(tecla);
            if (nuevaDireccion != null) {
                direccion = nuevaDireccion;
            }
        }
        motor.procesarEntrada(direccion);
    }

    private void configurarControles() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigoTecla = e.getKeyCode();

                if (motor.getEstadoActual() != null && motor.getEstadoActual().esFinDePartida()) {
                    if (procesarTeclaFinDePartida(codigoTecla)) {
                        return;
                    }
                }

                Direccion direccion = obtenerDireccion(codigoTecla);
                if (direccion != null) {
                    if (!teclasPresionadas.contains(codigoTecla)) {
                        teclasPresionadas.add(codigoTecla);
                    }
                    actualizarDireccion();
                } else if (codigoTecla == KeyEvent.VK_P || codigoTecla == KeyEvent.VK_ESCAPE) {
                    alternarPausaDesdeTeclado();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (teclasPresionadas.remove(e.getKeyCode())) {
                    actualizarDireccion();
                }
            }
        });
    }

    private boolean procesarTeclaFinDePartida(int codigoTecla) {
        if (codigoTecla == KeyEvent.VK_ENTER && motor.getEstadoActual() == EstadoJuego.VICTORIA) {
            if (motor.haySiguienteNivel()) {
                puntajeRegistrado = false;
                motor.pasarSiguienteNivel();
                teclasPresionadas.clear();
                iniciarJuego();
            } else {
                motor.reiniciarPartidaCompleta();
                prepararNuevaPartida();
                if (panelPrincipal != null) {
                    panelPrincipal.mostrarPanel("VICTORIA");
                }
            }
            return true;
        }

        if (codigoTecla == KeyEvent.VK_R) {
            if (motor.getEstadoActual() == EstadoJuego.DERROTA_VIDA) {
                aliasJugadorActual = null;
            }
            puntajeRegistrado = false;
            motor.reintentarTrasDerrota();
            teclasPresionadas.clear();
            iniciarJuego();
            return true;
        }

        if (codigoTecla == KeyEvent.VK_M) {
            if (motor.getEstadoActual() == EstadoJuego.VICTORIA && !puntajeRegistrado) {
                puntajeRegistrado = true;
                String alias = JOptionPane.showInputDialog(
                        PanelJuego.this,
                        "¡Nivel completado!\nTu puntaje: " + motor.getPuntajeAcumulado() + "\n\nIngresa tu alias para el Top 10:",
                        "Registrar Récord",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (alias != null) {
                    motor.guardarPuntaje(alias);
                }
            }
            motor.reiniciarPartidaCompleta();
            prepararNuevaPartida();
            if (panelPrincipal != null) {
                panelPrincipal.mostrarPanel("INICIO");
            }
            return true;
        }

        return false;
    }

    private void alternarPausaDesdeTeclado() {
        motor.alternarPausa();
        if (motor.getEstadoActual() == EstadoJuego.PAUSADO) {
            GestorAudio.getInstance().pausarMusica();
            teclasPresionadas.clear();
            motor.procesarEntrada(Direccion.NINGUNA);
        } else if (motor.getEstadoActual() == EstadoJuego.JUGANDO) {
            GestorAudio.getInstance().iniciarMusica();
        }
        repaint();
    }
}
