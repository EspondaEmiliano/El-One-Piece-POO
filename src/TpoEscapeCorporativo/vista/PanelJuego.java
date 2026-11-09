package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.EstadoJuego;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.entidades.Empleado;
import TpoEscapeCorporativo.modelo.entidades.Jefe;
import TpoEscapeCorporativo.modelo.entidades.Muro;
import TpoEscapeCorporativo.modelo.entidades.Salida;
import TpoEscapeCorporativo.modelo.entidades.Proyectil;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;
import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Lienzo principal de renderizado del juego. Dibuja el laberinto, los proyectiles,
 * las zonas de potenciadores, las entidades vivas, la interfaz superior (HUD)
 * y gestiona las entradas de teclado del usuario.
 */
public class PanelJuego extends JPanel {

    public static final int ANCHO = 600;
    public static final int ALTO_JUEGO = 400;
    public static final int ALTO_HUD = 55;
    public static final int ALTO = ALTO_JUEGO + ALTO_HUD;

    private final JuegoEngine motor;
    private final PanelPrincipal panelPrincipal;
    private Timer timer;
    private boolean puntajeRegistrado = false;

    // Almacena el alias durante la partida para no volver a solicitarlo entre niveles
    private String aliasJugadorActual = null;

    // Almacena las teclas de movimiento que actualmente están presionadas conservando el orden de pulsación
    private final Set<Integer> teclasPresionadas = new LinkedHashSet<>();

    public PanelJuego(JuegoEngine motor, PanelPrincipal panelPrincipal) {
        this.motor = motor;
        this.panelPrincipal = panelPrincipal;

        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(new Color(25, 25, 25));
        this.setFocusable(true);

        configurarGameLoop();
        configurarControles();
    }

    public PanelJuego(JuegoEngine motor) {
        this(motor, null);
    }

    /**
     * Restablece las banderas de sesión y el estado de teclas antes de comenzar una nueva partida.
     */
    public void prepararNuevaPartida() {
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

                // Registra automáticamente el récord al completar el último nivel
                if (motor.getEstadoActual() == EstadoJuego.VICTORIA
                        && !motor.haySiguienteNivel()
                        && !puntajeRegistrado) {
                    puntajeRegistrado = true;
                    solicitarYGuardarPuntajeJugador();
                }
            }
        });
    }

    /**
     * Solicita al empleado su alias mediante un cuadro de diálogo Swing
     * al completar la partida y persiste el récord consolidado en el Top 10.
     */
    private void solicitarYGuardarPuntajeJugador() {
        SwingUtilities.invokeLater(() -> {
            if (this.aliasJugadorActual == null || this.aliasJugadorActual.trim().isEmpty()) {
                String mensaje = "¡Has logrado escapar de la corporación!\nTu puntaje final: " + motor.getPuntajeAcumulado() + "\n\nIngresa tu alias para el Top 10:";
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

            // Persiste el récord utilizando el alias memorizado de la sesión
            motor.guardarPuntaje(this.aliasJugadorActual);
            repaint();
        });
    }

    public void iniciarJuego() {
        this.requestFocusInWindow();
        if (!timer.isRunning()) {
            timer.start();
            GestorAudio.getInstance().iniciarMusica();
        }
    }

    public void pausarJuego() {
        if (timer.isRunning()) {
            timer.stop();
            GestorAudio.getInstance().pausarMusica();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Escalado proporcional al tamaño actual del panel
        double escalaX = (double) getWidth() / ANCHO;
        double escalaY = (double) getHeight() / ALTO;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.scale(escalaX, escalaY);

        dibujarHUD(g2d);

        Graphics2D gJuego = (Graphics2D) g2d.create();
        gJuego.translate(0, ALTO_HUD);

        dibujarMapa(gJuego);
        dibujarBuffos(gJuego);
        dibujarEntidades(gJuego);

        gJuego.dispose();

        dibujarOverlayEstado(g2d);

        g2d.dispose();
    }

    /**
     * Dibuja los potenciadores presentes en el mapa que aún no hayan sido recolectados.
     */
    private void dibujarBuffos(Graphics g) {
        for (ZonaBuffo zona : motor.getZonasBuffo()) {
            if (!zona.isRecolectado()) {
                Posicion posicion = zona.getPosicion();
                int x = posicion.getX1();
                int y = posicion.getY1();
                int ancho = posicion.getX2() - x;
                int alto = posicion.getY2() - y;

                Image sprite = null;
                if (zona.getBuffo() instanceof Cafe) {
                    sprite = GestorRecursos.getSpriteCafe();
                } else if (zona.getBuffo() instanceof Aguinaldo) {
                    sprite = GestorRecursos.getSpriteAguinaldo();
                }
                if (sprite != null) {
                    g.drawImage(sprite, x, y, ancho, alto, this);
                }
            }
        }
    }

    /**
     * Dibuja las entidades activas del juego: el empleado, el jefe y los proyectiles.
     */
    private void dibujarEntidades(Graphics g) {
        // Renderizado del empleado y de su escudo protector si se encuentra activo
        Empleado jugador = motor.getJugador();
        if (jugador != null) {
            Posicion pos = jugador.getPosicion();
            int x = pos.getX1();
            int y = pos.getY1();
            int ancho = pos.getX2() - x;
            int alto = pos.getY2() - y;

            if (jugador.tieneEscudo()) {
                Graphics2D gHalo = (Graphics2D) g.create();
                gHalo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gHalo.setColor(new Color(0, 220, 255, 60));
                gHalo.fillOval(x - 4, y - 4, ancho + 8, alto + 8);
                gHalo.setColor(new Color(0, 255, 255, 190));
                gHalo.setStroke(new BasicStroke(1.5f));
                gHalo.drawOval(x - 4, y - 4, ancho + 8, alto + 8);
                gHalo.dispose();
            }

            Image spriteJugador = GestorRecursos.getSpriteEmpleado(jugador.getDireccionVisual());
            g.drawImage(spriteJugador, x, y, ancho, alto, this);
        }

        // Renderizado del jefe
        Jefe jefe = motor.getJefe();
        if (jefe != null) {
            Posicion pos = jefe.getPosicion();
            Image spriteJefe = GestorRecursos.getSpriteJefe(jefe.getDireccionActual(), jefe.estaAtacando());
            g.drawImage(spriteJefe, pos.getX1(), pos.getY1(),
                    pos.getX2() - pos.getX1(), pos.getY2() - pos.getY1(), this);
        }

        // Renderizado de las palas proyectadas
        for (Proyectil proyectil : motor.getProyectiles()) {
            if (proyectil.getEstaActivo()) {
                Posicion posicionProyectil = proyectil.getPosicion();
                Image spriteProyectil = GestorRecursos.getSpriteProyectil();
                g.drawImage(spriteProyectil, posicionProyectil.getX1(), posicionProyectil.getY1(),
                        posicionProyectil.getX2() - posicionProyectil.getX1(), posicionProyectil.getY2() - posicionProyectil.getY1(), this);
            }
        }
    }

    /**
     * Dibuja la barra superior del HUD con el estado del escudo, tiempo restante, puntaje, velocidad y nivel.
     */
    private void dibujarHUD(Graphics g) {
        g.setColor(new Color(18, 18, 18));
        g.fillRect(0, 0, ANCHO, ALTO_HUD);

        g.setColor(new Color(55, 55, 55));
        g.drawLine(0, ALTO_HUD - 1, ANCHO, ALTO_HUD - 1);

        Empleado jugador = motor.getJugador();
        if (jugador == null) return;

        g.setFont(GestorRecursos.getFuenteArcade(11f));

        // Fila 1: Indicador de Escudo, Tiempo Restante y Puntaje Acumulado
        if (jugador.tieneEscudo()) {
            g.setColor(new Color(50, 255, 100));
            g.drawString("ESCUDO: ACTIVO", 15, 23);
        } else {
            g.setColor(new Color(130, 130, 130));
            g.drawString("ESCUDO: INACTIVO", 15, 23);
        }

        int tiempoRestante = motor.getTiempoRestanteSegundos();
        g.setColor(tiempoRestante <= 10 ? Color.RED : Color.CYAN);
        g.drawString("TIEMPO: " + tiempoRestante + "s", 235, 23);

        g.setColor(new Color(255, 215, 0));
        g.drawString("PTS: " + motor.getPuntajeAcumulado(), 450, 23);

        // Fila 2: Velocidad de desplazamiento, Buff consumible activo y Nivel actual
        g.setColor(Color.WHITE);
        g.drawString("VEL: " + jugador.getVelocidad(), 15, 43);

        if (!jugador.getBuffosActivos().isEmpty()) {
            g.setFont(GestorRecursos.getFuenteArcade(9f));
            g.setColor(new Color(255, 215, 0));
            g.drawString("BUFF: " + jugador.getBuffosActivos().get(0).getNombre(), 215, 43);
        }

        g.setFont(GestorRecursos.getFuenteArcade(10f));
        g.setColor(new Color(0, 220, 255));
        if (motor.esNivelFinalActual()) {
            g.setColor(new Color(255, 215, 0));
            g.drawString("NIVEL: FINAL", 450, 43);
        } else {
            g.drawString("NIVEL: " + motor.getNumeroNivelActual() + "/" + motor.getTotalNiveles(), 450, 43);
        }
    }

    /**
     * Dibuja carteles informativos superpuestos según el estado actual de la partida.
     */
    private void dibujarOverlayEstado(Graphics g) {
        EstadoJuego estado = motor.getEstadoActual();
        if (estado == null || estado == EstadoJuego.JUGANDO) {
            return;
        }

        if (estado == EstadoJuego.PAUSADO) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, ANCHO, ALTO);

            g.setFont(GestorRecursos.getFuenteArcade(18f));
            g.setColor(Color.YELLOW);
            dibujarTextoCentrado(g, "JUEGO EN PAUSA", ALTO / 2 - 15);

            g.setFont(GestorRecursos.getFuenteArcade(9f));
            g.setColor(Color.WHITE);
            dibujarTextoCentrado(g, "Presiona 'P' para continuar", ALTO / 2 + 15);
        } else if (estado.esFinDePartida()) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, ANCHO, ALTO);

            boolean esVictoria = (estado == EstadoJuego.VICTORIA);
            g.setFont(GestorRecursos.getFuenteArcade(18f));
            g.setColor(esVictoria ? Color.GREEN : Color.RED);
            String tituloEstado;
            if (!esVictoria) {
                tituloEstado = "GAME OVER";
            } else if (motor.esNivelFinalActual()) {
                tituloEstado = "¡NIVEL FINAL COMPLETADO!";
                g.setFont(GestorRecursos.getFuenteArcade(16f));
            } else {
                tituloEstado = "¡NIVEL " + motor.getNumeroNivelActual() + " COMPLETADO!";
            }
            dibujarTextoCentrado(g, tituloEstado, ALTO / 2 - 50);

            g.setFont(GestorRecursos.getFuenteArcade(8f));
            g.setColor(Color.WHITE);
            dibujarTextoCentrado(g, estado.getMensajeDescriptivo(), ALTO / 2 - 15);

            g.setFont(GestorRecursos.getFuenteArcade(10f));
            g.setColor(new Color(255, 215, 0));
            dibujarTextoCentrado(g, "Puntaje acumulado: " + motor.getPuntajeAcumulado(), ALTO / 2 + 15);

            // Opciones interactivas según el resultado y niveles restantes
            g.setFont(GestorRecursos.getFuenteArcade(9f));
            if (esVictoria) {
                if (motor.haySiguienteNivel()) {
                    g.setColor(Color.CYAN);
                    dibujarTextoCentrado(g, "[ENTER] - SIGUIENTE NIVEL", ALTO / 2 + 50);
                    g.setColor(Color.LIGHT_GRAY);
                    dibujarTextoCentrado(g, "[M] - GUARDAR Y VOLVER AL MENU", ALTO / 2 + 75);
                } else {
                    g.setColor(new Color(255, 215, 0));
                    dibujarTextoCentrado(g, "¡FELICITACIONES! HAS COMPLETADO TODOS LOS NIVELES", ALTO / 2 + 50);
                    g.setColor(Color.CYAN);
                    dibujarTextoCentrado(g, "[ENTER] - CONTINUAR / [M] - VOLVER AL MENU", ALTO / 2 + 75);
                }
            } else {
                g.setColor(Color.YELLOW);
                dibujarTextoCentrado(g, "[R] - REINTENTAR / [M] - VOLVER AL MENU", ALTO / 2 + 60);
            }
        }
    }

    private boolean esTeclaMovimiento(int codigoTecla) {
        return codigoTecla == KeyEvent.VK_UP ||
                codigoTecla == KeyEvent.VK_W ||
                codigoTecla == KeyEvent.VK_DOWN ||
                codigoTecla == KeyEvent.VK_S ||
                codigoTecla == KeyEvent.VK_LEFT ||
                codigoTecla == KeyEvent.VK_A ||
                codigoTecla == KeyEvent.VK_RIGHT ||
                codigoTecla == KeyEvent.VK_D;
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

    private void dibujarTextoCentrado(Graphics g, String texto, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (ANCHO - fm.stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
    }

    /**
     * Configura el teclado para mover al empleado con las flechas o con W, A, S, D, y pausar con P o ESC.
     */
    private void configurarControles() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigoTecla = e.getKeyCode();

                if (motor.getEstadoActual() != null && motor.getEstadoActual().esFinDePartida()) {
                    // ENTER avanza al siguiente nivel o abre la pantalla de Victoria al completar el último
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
                        return;
                    }

                    // R reinicia el nivel actual
                    if (codigoTecla == KeyEvent.VK_R) {
                        puntajeRegistrado = false;
                        motor.reiniciarNivel(motor.getNumeroNivelActual());
                        teclasPresionadas.clear();
                        iniciarJuego();
                        return;
                    }

                    // M vuelve al menú principal, ofreciendo guardar el récord si superó un nivel
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
                        return;
                    }
                }

                Direccion direccion = obtenerDireccion(codigoTecla);
                if (direccion != null) {
                    if (!teclasPresionadas.contains(codigoTecla)) {
                        teclasPresionadas.add(codigoTecla);
                    }
                    actualizarDireccion();
                } else {
                    switch (codigoTecla) {
                        case KeyEvent.VK_P:
                        case KeyEvent.VK_ESCAPE:
                            motor.alternarPausa();
                            if (motor.getEstadoActual() == EstadoJuego.PAUSADO) {
                                GestorAudio.getInstance().pausarMusica();
                                teclasPresionadas.clear();
                                motor.procesarEntrada(Direccion.NINGUNA);
                            } else if (motor.getEstadoActual() == EstadoJuego.JUGANDO) {
                                GestorAudio.getInstance().iniciarMusica();
                            }
                            repaint();
                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int codigoTecla = e.getKeyCode();
                boolean teclaEliminada = teclasPresionadas.remove(codigoTecla);
                if (teclaEliminada) {
                    actualizarDireccion();
                }
            }
        });
    }

    /**
     * Dibuja los muros y la puerta de salida cargados desde el archivo de mapa.
     */
    private void dibujarMapa(Graphics g) {
        Salida salida = motor.getSalida();
        if (salida != null) {
            Posicion posS = salida.getPosicion();
            Image spriteSalida = GestorRecursos.getSpriteSalida();
            g.drawImage(spriteSalida, posS.getX1(), posS.getY1(),
                    posS.getX2() - posS.getX1(), posS.getY2() - posS.getY1(), this);
        }

        Image spriteMuro = GestorRecursos.getSpriteMuro();
        for (Muro muro : motor.getMuros()) {
            Posicion posM = muro.getPosicion();
            g.drawImage(spriteMuro, posM.getX1(), posM.getY1(),
                    posM.getX2() - posM.getX1(), posM.getY2() - posM.getY1(), this);
        }
    }
}
