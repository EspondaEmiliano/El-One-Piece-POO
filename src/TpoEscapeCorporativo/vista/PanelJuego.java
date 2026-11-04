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

public class PanelJuego extends JPanel {

    public static final int ANCHO = 600;
    public static final int ALTO_JUEGO = 400;
    public static final int ALTO_HUD = 55;
    public static final int ALTO = ALTO_JUEGO + ALTO_HUD;

    private final JuegoEngine motor;
    private final PanelPrincipal panelPrincipal;
    private Timer timer;
    private boolean puntajeRegistrado = false;
    // Almacena las teclas de movimiento que actualmente están presionadas.
    // LinkedHashSet conserva el orden en que se agregaron las teclas.
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

                // Si ganó la partida y aún no registró su puntaje en el ranking
                if (motor.getEstadoActual() == EstadoJuego.VICTORIA && !puntajeRegistrado) {
                    puntajeRegistrado = true;
                    solicitarYGuardarPuntajeJugador();
                }
            }
        });
    }

    /**
     * Solicita al empleado su nombre mediante un cuadro de diálogo Swing
     * y persiste su récord en el archivo binario del Top 10
     */
    private void solicitarYGuardarPuntajeJugador() {
        SwingUtilities.invokeLater(() -> {
            String mensaje = "Has logrado escapar!\nTu puntaje: " + motor.getPuntajeAcumulado() + "\n\nIngresa tu alias para el top 10:";
            String aliasIngresado = JOptionPane.showInputDialog(
                    this,
                    mensaje,
                    "Registrar Récord",
                    JOptionPane.PLAIN_MESSAGE
            );

            // Si cancela la ventana o deja en blanco, motor asignará "Empleado"
            if (aliasIngresado != null) {
                motor.guardarPuntaje(aliasIngresado);
                repaint();
            }
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

        // 1. Calculamos la proporción basada en el tamaño actual de la ventana
        double escalaX = (double) getWidth() / ANCHO;
        double escalaY = (double) getHeight() / ALTO;

        Graphics2D g2d = (Graphics2D) g.create();

        // 2. Aplicamos la proporción a todo para que se estire/encoja solo
        g2d.scale(escalaX, escalaY);

        // 3. Dibujamos todo usando este lápiz escalado (g2d)
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
                if (zona.getBuffo() instanceof Cafe ) {
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
     * Dibuja las entidades activas del juego.
     */
    private void dibujarEntidades(Graphics g) {
        // Dibujar jugador
        Empleado jugador = motor.getJugador();
        if (jugador != null) {
            Posicion pos = jugador.getPosicion();
            Image spriteJugador = GestorRecursos.getSpriteEmpleado(jugador.getDireccionVisual());
            g.drawImage(spriteJugador, pos.getX1(), pos.getY1(),
                    pos.getX2() - pos.getX1(), pos.getY2() - pos.getY1(), this);
        }

        // Dibujar jefe
        Jefe jefe = motor.getJefe();
        if (jefe != null) {
            Posicion pos = jefe.getPosicion();
            Image spriteJefe = GestorRecursos.getSpriteJefe(jefe.getDireccionActual(), jefe.estaAtacando());
            g.drawImage(spriteJefe, pos.getX1(), pos.getY1(),
                    pos.getX2() - pos.getX1(), pos.getY2() - pos.getY1(), this);
        }

        // Dibujar proyectiles
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
     * Dibuja la barra superior con los indicadores: salud, velocidad, tiempo restante y puntaje.
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

        g.setFont(GestorRecursos.getFuenteArcade(11f));

        // Fila 1: Salud, Tiempo y Puntaje
        g.setColor(jugador.getSalud() > 30 ? Color.GREEN : Color.RED);
        g.drawString("HP: " + jugador.getSalud() + "/100", 15, 23);

        int tiempoRestante = motor.getTiempoRestanteSegundos();
        g.setColor(tiempoRestante <= 10 ? Color.RED : Color.CYAN);
        g.drawString("TIEMPO: " + tiempoRestante + "s", 235, 23);

        g.setColor(new Color(255, 215, 0)); // Dorado
        g.drawString("PTS: " + motor.getPuntajeAcumulado(), 450, 23);

        // Fila 2: Velocidad y Buff activo
        g.setColor(Color.WHITE);
        g.drawString("VEL: " + jugador.getVelocidad(), 15, 43);

        if (!jugador.getBuffosActivos().isEmpty()) {
            g.setFont(GestorRecursos.getFuenteArcade(9f));
            g.setColor(new Color(255, 215, 0));
            g.drawString("BUFF: " + jugador.getBuffosActivos().get(0).getNombre(), 235, 43);
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
            dibujarTextoCentrado(g, esVictoria ? "¡NIVEL " + motor.getNumeroNivelActual() + " COMPLETADO!" : "GAME OVER", ALTO / 2 - 50);

            g.setFont(GestorRecursos.getFuenteArcade(8f));
            g.setColor(Color.WHITE);
            dibujarTextoCentrado(g, estado.getMensajeDescriptivo(), ALTO / 2 - 15);

            g.setFont(GestorRecursos.getFuenteArcade(10f));
            g.setColor(new Color(255, 215, 0));
            dibujarTextoCentrado(g, "Puntaje acumulado: " + motor.getPuntajeAcumulado(), ALTO / 2 + 15);

            // Opciones interactivas para el jugador
            g.setFont(GestorRecursos.getFuenteArcade(9f));
            if (esVictoria && motor.haySiguienteNivel()) {
                g.setColor(Color.CYAN);
                dibujarTextoCentrado(g, "[ENTER] - SIGUIENTE NIVEL", ALTO / 2 + 50);
                g.setColor(Color.LIGHT_GRAY);
                dibujarTextoCentrado(g, "[M] - VOLVER AL MENU PRINCIPAL", ALTO / 2 + 75);
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

    // Actualiza la dirección del empleado según las teclas que siguen presionadas.
    private void actualizarDireccion() {

        // Por defecto, el personaje permanece detenido.
        Direccion direccion = Direccion.NINGUNA;

        // Recorremos las teclas en el orden en que fueron presionadas.
        for (Integer tecla : teclasPresionadas) {

            // La última tecla de movimiento recorrida determina la dirección.
            switch (tecla) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    direccion = Direccion.ARRIBA;
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    direccion = Direccion.ABAJO;
                    break;

                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    direccion = Direccion.IZQUIERDA;
                    break;

                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    direccion = Direccion.DERECHA;
                    break;
            }
        }

        // Enviamos la dirección resultante al motor del juego.
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

                // Acciones de fin de partida (victoria o derrota)
                if (motor.getEstadoActual() != null && motor.getEstadoActual().esFinDePartida()) {
                    if (codigoTecla == KeyEvent.VK_ENTER && motor.getEstadoActual() == EstadoJuego.VICTORIA) {
                        if (motor.haySiguienteNivel()) {
                            puntajeRegistrado = false;
                            motor.pasarSiguienteNivel();
                            iniciarJuego();
                        } else {
                            // Si completo el último nivel, ENTER lo devuelve al menú principal
                            motor.reiniciarPartidaCompleta();
                            if (panelPrincipal != null) {
                                panelPrincipal.mostrarPanel("INICIO");
                            }
                        }
                    }
                    // Reintentar el nivel actual con R
                    else if (codigoTecla == KeyEvent.VK_R) {
                        puntajeRegistrado = false;
                        motor.reiniciarNivel(motor.getNumeroNivelActual());
                        iniciarJuego();
                    }
                    // Volver al menú principal con M
                    else if (codigoTecla == KeyEvent.VK_M) {
                        motor.reiniciarPartidaCompleta();
                        if (panelPrincipal != null) {
                            panelPrincipal.mostrarPanel("INICIO");
                        }
                    }
                }

                // Gestión de movimiento y pausa.
                if (esTeclaMovimiento(codigoTecla)) {

                    // Eliminamos la tecla si ya estaba registrada.
                    // Esto permite actualizar su prioridad si vuelve a presionarse.
                    teclasPresionadas.remove(codigoTecla);

                    // Registramos la tecla como la última presionada.
                    teclasPresionadas.add(codigoTecla);

                    // Actualizamos la dirección del empleado.
                    actualizarDireccion();

                } else {

                    // Gestión de las teclas especiales.
                    switch (codigoTecla) {
                        case KeyEvent.VK_P:
                        case KeyEvent.VK_ESCAPE:
                            motor.alternarPausa();
                            repaint();
                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                int codigoTecla = e.getKeyCode();

                // Eliminamos de la colección la tecla que se acaba de soltar.
                teclasPresionadas.remove(codigoTecla);

                // Recalculamos la dirección según las teclas que siguen presionadas.
                actualizarDireccion();
            }
        });
    }

    /**
     * Dibuja los muros y la puerta de salida cargados desde el archivo de mapa.
     */
    private void dibujarMapa(Graphics g) {
        // Dibujar la salida
        Salida salida = motor.getSalida();
        if (salida != null) {
            Posicion posS = salida.getPosicion();
            Image spriteSalida = GestorRecursos.getSpriteSalida();
            g.drawImage(spriteSalida, posS.getX1(), posS.getY1(),
                    posS.getX2() - posS.getX1(), posS.getY2() - posS.getY1(), this);
        }

        // Dibujar los muros
        Image spriteMuro = GestorRecursos.getSpriteMuro();
        for (Muro muro : motor.getMuros()) {
            Posicion posM = muro.getPosicion();
            g.drawImage(spriteMuro, posM.getX1(), posM.getY1(),
                    posM.getX2() - posM.getX1(), posM.getY2() - posM.getY1(), this);
        }
    }
}
