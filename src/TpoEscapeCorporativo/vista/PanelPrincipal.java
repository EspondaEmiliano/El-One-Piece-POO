package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.*;
import java.awt.*;

public class PanelPrincipal {

    private JFrame ventana;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JuegoEngine motorEngine;
    private PanelJuego panelJuego;

    public PanelPrincipal() {
        this.motorEngine = new JuegoEngine();
        inicializarVentana();
    }

    private void inicializarVentana() {
        System.out.println("Hola, mundo! (La vista se empezó a inicializar)");

        ventana = new JFrame("Super Escape Maravilloso del Empleado Negligente");
        ventana.setSize(600, 400);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        PanelInicio panelInicio = new PanelInicio(this);
        this.panelJuego = new PanelJuego(motorEngine, this);
        PanelVictoria panelVictoria = new PanelVictoria(this);

        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(this.panelJuego, "JUEGO");
        panelPrincipal.add(panelVictoria, "VICTORIA");

        ventana.add(panelPrincipal);
        ventana.setVisible(true);
    }

    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        if ("JUEGO".equalsIgnoreCase(nombre) && panelJuego != null) {
            panelJuego.iniciarJuego();
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        }
    }

    public void finalizarPartidaConVictoria() {
        if (panelJuego != null) {
            panelJuego.pausarJuego();
        }
        mostrarPanel("VICTORIA");
    }

    public void reiniciarPartida() {
        if (motorEngine != null) {
            motorEngine.reiniciarJuego();
        }
        mostrarPanel("JUEGO");
    }
}