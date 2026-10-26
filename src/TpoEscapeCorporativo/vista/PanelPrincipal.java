package TpoEscapeCorporativo.vista;
import javax.swing.*;
import java.awt.*;

public class PanelPrincipal {

    private JFrame ventana;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    public PanelPrincipal() {
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

        // 1. Creamos nuestro PanelInicio pasándole la referencia (this)
        PanelInicio panelInicio = new PanelInicio(this);

        // 2. Creamos un panel de juego temporal (luego lo reemplazarás por tu clase de juego)
        JPanel panelJuegoTemporal = new JPanel();
        panelJuegoTemporal.setBackground(Color.DARK_GRAY);
        JLabel textoJuego = new JLabel("¡Estás en la pantalla de Juego!");
        textoJuego.setForeground(Color.WHITE);
        panelJuegoTemporal.add(textoJuego);

        // Agregamos ambos paneles al contenedor principal con sus nombres clave
        panelPrincipal.add(panelInicio, "INICIO");
        panelPrincipal.add(panelJuegoTemporal, "JUEGO");

        // Añadimos el contenedor a la ventana
        ventana.add(panelPrincipal);
        ventana.setVisible(true);
    }

    // Método interno para que los propios paneles puedan cambiar de vista fácilmente
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelPrincipal, nombre);
    }
}