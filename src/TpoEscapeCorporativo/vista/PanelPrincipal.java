package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana contenedora principal de la aplicación. Administra la navegación
 * entre pantallas (Menú de Inicio, Juego, Ranking y Victoria) mediante {@link CardLayout}.
 * Configura una resolución fija óptima de 900x680 px sin deformación de aspecto.
 */
public class PanelPrincipal {

    public static final int ANCHO_VENTANA = 900;
    public static final int ALTO_VENTANA = 680;

    private JFrame ventana;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JuegoEngine motorEngine;
    private PanelJuego panelJuego;
    private PanelRanking panelRanking;
    private PanelVictoria panelVictoria;

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

        // Inicializamos el CardLayout y el contenedor con dimensiones fijas 900x680
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));

        // Creamos los paneles con el tamaño dimensionado
        PanelInicio panelInicio = new PanelInicio(this);
        panelInicio.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));

        this.panelJuego = new PanelJuego(motorEngine, this);
        this.panelJuego.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));

        this.panelRanking = new PanelRanking(this);
        this.panelRanking.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));

        this.panelVictoria = new PanelVictoria(this);
        this.panelVictoria.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));

        // Agregamos paneles al contenedor principal
        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(this.panelJuego, "JUEGO");
        panelPrincipal.add(this.panelRanking, "RANKING");
        panelPrincipal.add(this.panelVictoria, "VICTORIA");

        // Añadimos el contenedor a la ventana
        ventana.add(panelPrincipal);

        // pack() ajusta el tamaño exacto de la ventana considerando los bordes y la barra de título del SO
        ventana.pack();
        // Ventana fija: previene estiramientos que deformen los sprites o desfasen los layouts
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        // Inicia la música ambiental en su propio hilo desde la pantalla de bienvenida
        GestorAudio.getInstance().iniciarMusica();
    }

    /**
     * Alterna la vista visible en pantalla según su identificador.
     * Sincroniza la música de fondo y los loops de animación.
     *
     * @param nombre Nombre de la tarjeta ("INICIO", "JUEGO", "RANKING" o "VICTORIA").
     */
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        if ("JUEGO".equalsIgnoreCase(nombre)) {
            panelJuego.iniciarJuego();
        } else if ("RANKING".equalsIgnoreCase(nombre)) {
            panelJuego.pausarJuego();
            panelRanking.cargarYRefrescarDatos();
            GestorAudio.getInstance().iniciarMusica();
        } else {
            panelJuego.pausarJuego();
            GestorAudio.getInstance().iniciarMusica();
        }
    }
}
