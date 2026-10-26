package TpoEscapeCorporativo.vista;

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
        setBackground(new Color(15, 25, 15)); // Fondo verde oscuro

        // Título de victoria
        JLabel cartelVictoria = new JLabel("¡LOGRASTE ESCAPAR!", SwingConstants.CENTER);
        cartelVictoria.setFont(GestorRecursos.getFuenteArcade(22f));
        cartelVictoria.setForeground(new Color(50, 255, 100));
        cartelVictoria.setBounds(50, 60, 500, 50);
        add(cartelVictoria);

        // Subtítulo
        JLabel subtexto = new JLabel("Has terminado tu jornada laboral con exito", SwingConstants.CENTER);
        subtexto.setFont(new Font("SansSerif", Font.BOLD, 14));
        subtexto.setForeground(Color.WHITE);
        subtexto.setBounds(50, 120, 500, 30);
        add(subtexto);

        // Botón 1: Volver a Jugar (Reinicia la partida)
        JButton botonReintentar = new JButton("Volver a Jugar");
        botonReintentar.setBounds(200, 185, 200, 40);
        botonReintentar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonReintentar.addActionListener(e -> {
            panelPrincipal.reiniciarPartida();
        });
        add(botonReintentar);

        // Botón 2: Volver al Menú Inicial
        JButton botonMenu = new JButton("Volver al Menu");
        botonMenu.setBounds(200, 240, 200, 40);
        botonMenu.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonMenu.addActionListener(e -> {
            panelPrincipal.mostrarPanel("INICIO");
        });
        add(botonMenu);
    }
}