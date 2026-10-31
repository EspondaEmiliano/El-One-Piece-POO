package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.*;
import java.awt.*;

public class PanelPrincipal {

    private JFrame ventana;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JuegoEngine motorEngine;

    // 1. Guardamos la referencia de PanelJuego como atributo
    private PanelJuego panelJuego;

    public PanelPrincipal() {
        this.motorEngine = new JuegoEngine();
        inicializarVentana();
    }

    private void inicializarVentana() {

        ventana = new JFrame("Super Escape Maravilloso del Empleado Negligente");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicializamos el CardLayout y el contenedor
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        // Creamos los dos paneles
        PanelInicio panelInicio = new PanelInicio(this);
        panelInicio.setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));

        this.panelJuego = new PanelJuego(motorEngine);

        // Agregamos ambos paneles al contenedor principal
        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(this.panelJuego, "JUEGO");

        // Añadimos el contenedor a la ventana
        ventana.add(panelPrincipal);

        // pack() ajusta el tamaño exacto de la ventana considerando los bordes y la barra de título del SO
        ventana.pack();
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    // Método para cambiar de vista
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        // Cuando la vista pasa a ser "JUEGO", solicitamos el foco y arrancamos el loop
        if ("JUEGO".equalsIgnoreCase(nombre) && panelJuego != null) {
            panelJuego.iniciarJuego();
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        }
    }
}
