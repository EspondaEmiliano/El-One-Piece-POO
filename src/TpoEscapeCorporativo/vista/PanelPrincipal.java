package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana contenedora principal de la aplicación. Administra la navegación
 * entre pantallas (Menú de Inicio y Partida de Juego) mediante {@link CardLayout}.
 */
public class PanelPrincipal {

    private JFrame ventana;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JuegoEngine motorEngine;
    private PanelJuego panelJuego;

    /**
     * Instancia el motor de juego e inicializa la ventana del juego.
     */
    public PanelPrincipal() {
        this.motorEngine = new JuegoEngine();
        inicializarVentana();
    }

    private void inicializarVentana() {

        ventana = new JFrame("ESCAPE CORPORATIVO");
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

    /**
     * Alterna la vista visible en pantalla según su identificador.
     *
     * @param nombre Clave de la vista ("INICIO" o "JUEGO").
     */
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        // Cuando la vista pasa a ser "JUEGO", solicitamos el foco y arrancamos el loop
        if ("JUEGO".equalsIgnoreCase(nombre) && panelJuego != null) {
            panelJuego.iniciarJuego();
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        }
    }
}
