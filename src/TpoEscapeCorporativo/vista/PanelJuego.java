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
import TpoEscapeCorporativo.modelo.items.Corazon;
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
 * las zonas de potenciadores, las entidades vivas, la interfaz superior (HUD) con los 3 corazones
 * y el estado del escudo, y gestiona las entradas de teclado del usuario.
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

    // Almacena el alias durante la partida para no volver a solicitarlo en los siguientes niveles
    private String aliasJugadorActual = null;

    // Almacena las teclas de movimiento que actualmente están presionadas conservando el orden de pulsación.
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

    // Constructor sobrecargado por compatibilidad
    public PanelJuego(JuegoEngine motor) {
        this(motor, null);
    }

    private void configurarGameLoop() {
        this.timer = new Timer(16, e -> {
            motor.actualizar();
            repaint();

            if (motor.getEstadoActual() != null && motor.getEstadoActual().esFinDePartida()) {
                timer.stop();
                GestorAudio.getInstance().detenerMusica();

                // Si ganó el último nivel de la partida y aún no registró su puntaje en el ranking
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
     * Solicita al empleado su nombre mediante un cuadro de diálogo Swing
     * al completar la partida (o al retirarse con puntos acumulados) y persiste
     * el récord consolidado en el Top 10.
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

        // Escalado proporcional basado en el tamaño actual de la ventana
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
                } else if (zona.getBuffo() instanceof Corazon) {
                    sprite = GestorRecursos.getSpriteCorazon();
                }
                if (sprite != null) {
                    g.drawImage(sprite, x, y, ancho, alto, this);
                }
            }
        }
    }

    /**
     * Dibuja las entidades activas del juego: el empleado (con halo de escudo si está activo),
     * el jefe y los proyectiles.
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
     * Dibuja la barra superior del HUD con los 3 corazones de vida, el estado del escudo,
     * el tiempo restante, el puntaje acumulado, la velocidad y el nivel actual.
     */
    private void dibujarHUD(Graphics g) {
        // Fondo de la barra superior del HUD
        g.setColor(new Color(18, 18, 18));
        g.fillRect(0, 0, ANCHO, ALTO_HUD);

        // Línea divisoria entre el HUD y el área de juego
        g.setColor(new Color(55, 55, 55));
        g.drawLine(0, ALTO_HUD - 1, ANCHO, ALTO_HUD - 1);

        Empleado jugador = motor.getJugador();
        if (jugador == null) return;

        g.setFont(GestorRecursos.getFuenteArcade(10f));

        // Fila 1: 3 Corazones (Vidas), Tiempo Restante y Puntaje Acumulado
        g.setColor(Color.WHITE);
        g.drawString("VIDAS:", 15, 22);

        Graphics2D g2d = (Graphics2D) g;
        int corazonesActuales = jugador.getSalud();
        for (int i = 0; i < Empleado.SALUD_DEFAULT; i++) {
            dibujarCorazon(g2d, 82 + (i * 22), 9, i < corazonesActuales);
        }

        int tiempoRestante = motor.getTiempoRestanteSegundos();
        g.setFont(GestorRecursos.getFuenteArcade(11f));
        g.setColor(tiempoRestante <= 10 ? Color.RED : Color.CYAN);
        g.drawString("TIEMPO: " + tiempoRestante + "s", 235, 23);

        g.setColor(new Color(255, 215, 0)); // Dorado
        g.drawString("PTS: " + motor.getPuntajeAcumulado(), 450, 23);

        // Fila 2: Velocidad, Estado del Escudo / Buffos y Nivel actual
        g.setFont(GestorRecursos.getFuenteArcade(9f));
        g.setColor(Color.WHITE);
        g.drawString("VEL: " + jugador.getVelocidad(), 15, 43);

        if (jugador.tieneEscudo()) {
            g.setColor(new Color(50, 255, 100));
            g.drawString("ESCUDO: ACTIVO", 115, 43);
        } else {
            g.setColor(new Color(130, 130, 130));
            g.drawString("ESCUDO: INACTIVO", 115, 43);
        }

        // Mostrar si tiene Café activo además del indicador de escudo
        for (Buffo buffo : jugador.getBuffosActivos()) {
            if (buffo instanceof Cafe) {
                g.setColor(new Color(255, 215, 0));
                g.drawString("+CAFE", 295, 43);
                break;
            }
        }

        g.setFont(GestorRecursos.getFuenteArcade(10f));
        g.setColor(new Color(0, 220, 255));
        g.drawString("NIVEL: " + motor.getNumeroNivelActual() + "/" + motor.getTotalNiveles(), 450, 43);
    }

    /**
     * Dibuja un corazón estilo arcade en las coordenadas especificadas del HUD.
     *
     * @param g2d    Contexto gráfico 2D.
     * @param x      Coordenada horizontal superior izquierda del corazón.
     * @param y      Coordenada vertical superior izquierda del corazón.
     * @param activo {@code true} para corazón lleno (rojo encendido); {@code false} para corazón vacío (gris).
     */
    private void dibujarCorazon(Graphics2D g2d, int x, int y, boolean activo) {
        Graphics2D gCorazon = (Graphics2D) g2d.create();
        gCorazon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int[] xs = {x + 8, x + 1, x,     x + 2, x + 5, x + 8, x + 11, x + 14, x + 16, x + 15};
        int[] ys = {y + 14, y + 7, y + 4, y + 1, y + 1, y + 4, y + 1,  y + 1,  y + 4,  y + 7};

        if (activo) {
            gCorazon.setColor(new Color(235, 35, 60));
            gCorazon.fillPolygon(xs, ys, xs.length);
            gCorazon.setColor(new Color(255, 130, 145));
            gCorazon.fillRect(x + 3, y + 3, 2, 2); // Brillo retro
            gCorazon.setColor(new Color(255, 80, 100));
            gCorazon.drawPolygon(xs, ys, xs.length);
        } else {
            gCorazon.setColor(new Color(45, 45, 45));
            gCorazon.fillPolygon(xs, ys, xs.length);
            gCorazon.setColor(new Color(95, 95, 95));
            gCorazon.drawPolygon(xs, ys, xs.length);
        }

        gCorazon.dispose();
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
            dibujarTextoCentrado(g, esVictoria ? "¡NIVEL " + motor.getNumeroNivelActual() + " COMPLETADO!" : "GAME OVER", ALTO / 2 - 50);

            g.setFont(GestorRecursos.getFuenteArcade(8f));
            g.setColor(Color.WHITE);
            dibujarTextoCentrado(g, estado.getMensajeDescriptivo(), ALTO / 2 - 15);

            g.setFont(GestorRecursos.getFuenteArcade(10f));
            g.setColor(new Color(255, 215, 0));
            dibujarTextoCentrado(g, "Puntaje acumulado: " + motor.getPuntajeAcumulado(), ALTO / 2 + 15);

            // Opciones interactivas para el jugador
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
                    dibujarTextoCentrado(g, "[ENTER] - PANTALLA DE VICTORIA / [M] - MENU", ALTO / 2 + 75);
                }
            } else {
                g.setColor(Color.YELLOW);
                dibujarTextoCentrado(g, "[R] - REINTENTAR / [M] - VOLVER AL MENU", ALTO / 2 + 60);
            }
        }
    }

    // Comprueba si una tecla corresponde a una dirección de movimiento.
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

    // Convierte una tecla del teclado en una dirección de movimiento.
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

    // Actualiza la dirección según las teclas que siguen presionadas.
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

                // Verificamos si la partida terminó.
                if (motor.getEstadoActual() != null &&
                        motor.getEstadoActual().esFinDePartida()) {

                    // ENTER permite avanzar al siguiente nivel si ganamos.
                    if (codigoTecla == KeyEvent.VK_ENTER &&
                            motor.getEstadoActual() == EstadoJuego.VICTORIA) {

                        if (motor.haySiguienteNivel()) {
                            puntajeRegistrado = false;
                            motor.pasarSiguienteNivel();
                            teclasPresionadas.clear();
                            iniciarJuego();
                        } else {
                            // Si terminamos el último nivel, mostramos la pantalla final de Victoria.
                            motor.reiniciarPartidaCompleta();
                            teclasPresionadas.clear();
                            aliasJugadorActual = null;
                            puntajeRegistrado = false;

                            if (panelPrincipal != null) {
                                panelPrincipal.mostrarPanel("VICTORIA");
                            }
                        }
                        return;
                    }

                    // R permite reintentar: si perdió los 3 corazones reinicia la partida o el nivel con sus corazones base.
                    if (codigoTecla == KeyEvent.VK_R) {
                        puntajeRegistrado = false;
                        motor.reiniciarNivel(motor.getNumeroNivelActual());
                        teclasPresionadas.clear();
                        iniciarJuego();
                        return;
                    }

                    // M permite volver al menú principal (guardando récord si había ganado el nivel).
                    if (codigoTecla == KeyEvent.VK_M) {
                        if (motor.getEstadoActual() == EstadoJuego.VICTORIA && !puntajeRegistrado) {
                            puntajeRegistrado = true;
                            String alias = JOptionPane.showInputDialog(
                                    PanelJuego.this,
                                    "¡Nivel completado!\nTu puntaje: " + motor.getPuntajeAcumulado() + "\n\nIngresa tu alias para el Top 10:",
                                    "Registrar Récord",
                                    JOptionPane.PLAIN_MESSAGE
                            );
                            motor.guardarPuntaje(alias);
                        }
                        motor.reiniciarPartidaCompleta();
                        teclasPresionadas.clear();
                        aliasJugadorActual = null;
                        puntajeRegistrado = false;

                        if (panelPrincipal != null) {
                            panelPrincipal.mostrarPanel("INICIO");
                        }
                        return;
                    }
                }

                // Obtenemos la dirección correspondiente a la tecla presionada.
                Direccion direccion = obtenerDireccion(codigoTecla);

                // Si es una tecla de movimiento, actualizamos las teclas presionadas.
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
