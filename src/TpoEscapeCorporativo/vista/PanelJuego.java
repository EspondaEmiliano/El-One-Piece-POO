package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;

public class PanelJuego extends JPanel {

    public static final int ANCHO = 600;
    public static final int ALTO = 400;

    private final JuegoEngine motor;
    private Timer timer;

    public PanelJuego(JuegoEngine motor) {
        this.motor = motor;

        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setFocusable(true);

        configurarGameLoop();
    }

    private void configurarGameLoop() {
        this.timer = new Timer(16, e -> {
            motor.actualizar();
            repaint();
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
    }

}