package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.entidades.Empleado;
import TpoEscapeCorporativo.modelo.entidades.Jefe;
import TpoEscapeCorporativo.modelo.entidades.Muro;
import TpoEscapeCorporativo.modelo.entidades.Salida;
import TpoEscapeCorporativo.modelo.entidades.Proyectil;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class PanelJuego extends JPanel {

    public static final int ANCHO = 600;
    public static final int ALTO = 400;

    private final JuegoEngine motor;
    private final PanelPrincipal panelPrincipal; // NUEVO: Enlace con el navegador
    private Timer timer;

    // Constructor principal
    public PanelJuego(JuegoEngine motor, PanelPrincipal panelPrincipal) {
        this.motor = motor;
        this.panelPrincipal = panelPrincipal;

        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(new Color(25, 25, 25));
        this.setFocusable(true);

        configurarGameLoop();
        configurarControles();
    }

    // Constructor auxiliar por compatibilidad
    public PanelJuego(JuegoEngine motor) {
        this(motor, null);
    }

    private void configurarGameLoop() {
        this.timer = new Timer(16, e -> {
            motor.actualizar();
            repaint();

            // NUEVO: Notifica al navegador si el motor declara victoria
            if (motor.isJuegoGanado() && panelPrincipal != null) {
                panelPrincipal.finalizarPartidaConVictoria();
            }
        });
    }

    public void iniciarJuego() {
        this.requestFocusInWindow();
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void pausarJuego() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Dibujar el mapa (muros y salida de escape)
        dibujarMapa(g);

        // 2. Dibujar ítems / zonas de buffos
        dibujarBuffos(g);

        // 3. Dibujar entidades (jugador, jefe, proyectiles)
        dibujarEntidades(g);

        // 4. Dibujar interfaz / HUD (salud y buffos activos)
        dibujarHUD(g);
    }

    private void dibujarBuffos(Graphics g) {
        for (ZonaBuffo zona : motor.getZonasBuffo()) {
            if (!zona.isRecolectado()) {
                Posicion posicion = zona.getPosicion();
                int x = posicion.getX1();
                int y = posicion.getY1();
                int ancho = posicion.getX2() - x;
                int alto = posicion.getY2() - y;

                BufferedImage sprite = null;
                if (zona.getBuffo() instanceof Cafe ) {
                    sprite = GestorRecursos.getSpriteCafe();
                } else if (zona.getBuffo() instanceof Aguinaldo) {
                    sprite = GestorRecursos.getSpriteAguinaldo();
                }
                if (sprite != null) {
                    g.drawImage(sprite, x, y, ancho, alto, null);
                }
            }
        }
    }

    private void dibujarEntidades(Graphics g) {
        Empleado jugador = motor.getJugador();
        if (jugador != null) {
            Posicion posicionJugador = jugador.getPosicion();
            BufferedImage spriteJugador = GestorRecursos.getSpriteEmpleado();
            g.drawImage(spriteJugador, posicionJugador.getX1(), posicionJugador.getY1(),
                    posicionJugador.getX2() - posicionJugador.getX1(), posicionJugador.getY2() - posicionJugador.getY1(), null);
        }

        Jefe jefe = motor.getJefe();
        if (jefe != null) {
            Posicion posicionJefe = jefe.getPosicion();
            BufferedImage spriteJefe = GestorRecursos.getSpriteJefe();
            g.drawImage(spriteJefe, posicionJefe.getX1(), posicionJefe.getY1(),
                    posicionJefe.getX2() - posicionJefe.getX1(), posicionJefe.getY2() - posicionJefe.getY1(), null);
        }

        for (Proyectil proyectil : motor.getProyectiles()) {
            if (proyectil.getEstaActivo()) {
                Posicion posicionProyectil = proyectil.getPosicion();
                BufferedImage spriteProyectil = GestorRecursos.getSpriteProyectil();
                g.drawImage(spriteProyectil, posicionProyectil.getX1(), posicionProyectil.getY1(),
                        posicionProyectil.getX2() - posicionProyectil.getX1(), posicionProyectil.getY2() - posicionProyectil.getY1(), null);
            }
        }
    }

    private void dibujarHUD(Graphics g) {
        Empleado jugador = motor.getJugador();
        if (jugador == null) return;

        g.setFont(GestorRecursos.getFuenteArcade(11f));

        g.setColor(jugador.getSalud() > 30 ? Color.GREEN : Color.RED);
        g.drawString("HP: " + jugador.getSalud() + "/100", 15, 25);

        g.setColor(Color.WHITE);
        g.drawString("VEL: " + jugador.getVelocidad(), 15, 45);

        int offsetY = 65;
        for (Buffo buffo : jugador.getBuffosActivos()) {
            g.setColor(new Color(255, 215, 0)); // Dorado
            g.drawString("BUFF ACTIVO: " + buffo.getNombre(), 15, offsetY);
            offsetY += 18;
        }
    }

    private void configurarControles() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        motor.procesarEntrada(Direccion.ARRIBA);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        motor.procesarEntrada(Direccion.ABAJO);
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        motor.procesarEntrada(Direccion.IZQUIERDA);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        motor.procesarEntrada(Direccion.DERECHA);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                motor.procesarEntrada(Direccion.NINGUNA);
            }
        });
    }

    private void dibujarMapa(Graphics g) {
        Salida salida = motor.getSalida();
        if (salida != null) {
            Posicion posS = salida.getPosicion();
            BufferedImage spriteSalida = GestorRecursos.getSpriteSalida();
            g.drawImage(spriteSalida, posS.getX1(), posS.getY1(),
                    posS.getX2() - posS.getX1(), posS.getY2() - posS.getY1(), null);
        }

        BufferedImage spriteMuro = GestorRecursos.getSpriteMuro();
        for (Muro muro : motor.getMuros()) {
            Posicion posM = muro.getPosicion();
            g.drawImage(spriteMuro, posM.getX1(), posM.getY1(),
                    posM.getX2() - posM.getX1(), posM.getY2() - posM.getY1(), null);
        }
    }
}