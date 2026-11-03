package TpoEscapeCorporativo.vista;
import TpoEscapeCorporativo.controlador.JuegoEngine;
import javax.swing.*;
import java.awt.*;

public class PanelVictoria extends JPanel {

    private final PanelPrincipal panelPrincipal;

    public PanelVictoria(PanelPrincipal panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
        inicializarPanel();
    }

    private void inicializarPanel() {
        setLayout(null);
        setBackground(new Color(15, 25, 15)); // Verde oscuro de victoria

        // Título principal con fuente arcade
        JLabel cartelVictoria = new JLabel("¡LOGRASTE ESCAPAR!", SwingConstants.CENTER);
        cartelVictoria.setFont(GestorRecursos.getFuenteArcade(22f));
        cartelVictoria.setForeground(new Color(50, 255, 100)); // Verde brillante
        cartelVictoria.setBounds(50, 70, 500, 50);
        add(cartelVictoria);

        // Subtítulo descriptivo
        JLabel subtexto = new JLabel("Has terminado tu jornada laboral con exito", SwingConstants.CENTER);
        subtexto.setFont(new Font("SansSerif", Font.BOLD, 14));
        subtexto.setForeground(Color.WHITE);
        subtexto.setBounds(50, 130, 500, 30);
        add(subtexto);

        // Botón para regresar al menú de inicio
        JButton botonMenu = new JButton("Volver al Menu");
        botonMenu.setBounds(200, 210, 200, 40);
        botonMenu.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonMenu.addActionListener(e -> {
            panelPrincipal.mostrarPanel("INICIO");
        });
        add(botonMenu);
    }
}