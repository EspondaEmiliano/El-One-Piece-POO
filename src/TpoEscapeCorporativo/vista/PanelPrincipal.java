package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana contenedora principal de la aplicación. Administra la navegación
 * entre pantallas (Menú de Inicio, Juego y Ranking) mediante {@link CardLayout}.
 */
public class PanelPrincipal {

    private JFrame ventana;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JuegoEngine motorEngine;
    private PanelJuego panelJuego;
    private PanelRanking panelRanking;

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

        // Creamos los paneles
        PanelInicio panelInicio = new PanelInicio(this);
        panelInicio.setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));

        this.panelJuego = new PanelJuego(motorEngine, this);
        this.panelRanking = new PanelRanking(this);

        // Agregamos paneles al contenedor principal
        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(this.panelJuego, "JUEGO");
        panelPrincipal.add(this.panelRanking, "RANKING");

        // Añadimos el contenedor a la ventana
        ventana.add(panelPrincipal);

        // pack() ajusta el tamaño exacto de la ventana considerando los bordes y la barra de título del SO
        ventana.pack();
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        // Inicia la música ambiental en su propio hilo desde la pantalla de bienvenida
        GestorAudio.getInstance().iniciarMusica();
    }

    /**
     * Alterna la vista visible en pantalla según su identificador.
     *
     * @param nombre Clave de la vista ("INICIO", "JUEGO" o "RANKING").
     */
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        // Cuando la vista pasa a ser "JUEGO", solicitamos el foco y arrancamos el loop
        if ("JUEGO".equalsIgnoreCase(nombre) && panelJuego != null) {
            panelJuego.iniciarJuego();
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        } else if ("RANKING".equalsIgnoreCase(nombre) && panelRanking != null) {
            panelRanking.cargarYRefrescarDatos();
        } else if ("INICIO".equalsIgnoreCase(nombre)) {
            // Asegura que la música suene si se regresa al menú principal
            GestorAudio.getInstance().iniciarMusica();
        }
    }
}
