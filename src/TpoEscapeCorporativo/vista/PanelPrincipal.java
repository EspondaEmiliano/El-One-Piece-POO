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
        System.out.println("Hola, mundo! (La vista se empezó a inicializar)");

        ventana = new JFrame("Super Escape Maravilloso del Empleado Negligente");
        ventana.setSize(600, 400);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);

        // Inicializamos el CardLayout y el contenedor
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        // Creamos los dos paneles
        PanelInicio panelInicio = new PanelInicio(this);
        this.panelJuego = new PanelJuego(motorEngine);

        // OJO: AQUÍ YA NO LLAMAMOS A iniciarJuego()
        // porque el juego debe esperar a que el usuario presione "Play"

        // Agregamos ambos paneles al contenedor principal
        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(this.panelJuego, "JUEGO");

        // Añadimos el contenedor a la ventana
        ventana.add(panelPrincipal);
        ventana.setVisible(true);
    }

    // Método para cambiar de vista
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);

        // 2. CLAVE: Cuando la vista pasa a ser "JUEGO", AHORA SÍ solicitamos el foco y arrancamos el loop
        if ("JUEGO".equalsIgnoreCase(nombre) && panelJuego != null) {
            panelJuego.iniciarJuego();
            // Aseguramos que Swing termine la transición visual antes de pedir el foco:
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        }
    }
}