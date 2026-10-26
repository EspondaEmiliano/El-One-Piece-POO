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

        // Inicializamos el CardLayout y el contenedor
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        // 1. Pantalla de Menú
        PanelInicio panelInicio = new PanelInicio(this);

        // 2. Pantalla de Juego activa (le pasamos 'this')
        this.panelJuego = new PanelJuego(motorEngine, this);

        // 3. Pantalla de Victoria (le pasamos 'this')
        PanelVictoria panelVictoria = new PanelVictoria(this);

        // Agregamos las tres pantallas al CardLayout
        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(this.panelJuego, "JUEGO");
        panelPrincipal.add(panelVictoria, "VICTORIA");

        // Añadimos el contenedor a la ventana
        ventana.add(panelPrincipal);
        ventana.setVisible(true);
    }

    // Cambia de pantalla y gestiona el foco del teclado
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        if ("JUEGO".equalsIgnoreCase(nombre) && panelJuego != null) {
            panelJuego.iniciarJuego();
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        }
    }

    // NUEVO: Método mediador invocado cuando el jugador escapa
    public void finalizarPartidaConVictoria() {
        if (panelJuego != null) {
            panelJuego.pausarJuego(); // 1. Frena el Game Loop
        }
        mostrarPanel("VICTORIA");     // 2. Muestra la pantalla de triunfo
    }
}