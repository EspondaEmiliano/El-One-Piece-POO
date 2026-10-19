package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class PanelJuego extends JPanel {

    public static final int ANCHO = 600;
    public static final int ALTO = 400;

    private final JuegoEngine motor;

    public PanelJuego(JuegoEngine motor) {
        this.motor = motor;

        this.setPreferredSize(new Dimension(ANCHO, ALTO));

        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}