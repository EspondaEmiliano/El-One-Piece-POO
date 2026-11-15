package TpoEscapeCorporativo.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Pantalla de felicitación final al completar la totalidad de los niveles
 * corporativos, ofreciendo acceso directo al Ranking Top 10 o al menú principal.
 */
public class PanelVictoria extends JPanel {

    private final PanelPrincipal panelPrincipal;
    private JLabel cartelVictoria;
    private JLabel subtexto;
    private JButton botonRanking;
    private JButton botonMenu;

    public PanelVictoria(PanelPrincipal panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
        inicializarPanel();
    }

    private void inicializarPanel() {
        setLayout(null);
        setBackground(new Color(20, 30, 20)); // Verde oscuro arcade de victoria

        // Título principal con fuente arcade
        cartelVictoria = new JLabel("¡LOGRASTE ESCAPAR!", SwingConstants.CENTER);
        cartelVictoria.setFont(GestorRecursos.getFuenteArcade(22f));
        cartelVictoria.setForeground(new Color(50, 255, 100)); // Verde brillante
        add(cartelVictoria);

        // Subtítulo descriptivo
        subtexto = new JLabel("HAS COMPLETADO LOS 4 NIVELES Y TERMINADO TU JORNADA", SwingConstants.CENTER);
        subtexto.setFont(GestorRecursos.getFuenteArcade(9f));
        subtexto.setForeground(Color.WHITE);
        add(subtexto);

        // Botón para ver el Ranking Top 10
        botonRanking = new JButton("VER RANKING TOP 10");
        botonRanking.setFont(GestorRecursos.getFuenteArcade(11f));
        botonRanking.setBackground(new Color(20, 80, 140));
        botonRanking.setForeground(Color.WHITE);
        botonRanking.setFocusable(false);
        botonRanking.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 2));
        botonRanking.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonRanking.addActionListener(e -> panelPrincipal.mostrarPanel("RANKING"));
        add(botonRanking);

        // Botón para regresar al menú de inicio
        botonMenu = new JButton("VOLVER AL MENU");
        botonMenu.setFont(GestorRecursos.getFuenteArcade(11f));
        botonMenu.setBackground(new Color(30, 130, 60));
        botonMenu.setForeground(Color.WHITE);
        botonMenu.setFocusable(false);
        botonMenu.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        botonMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonMenu.addActionListener(e -> panelPrincipal.mostrarPanel("INICIO"));
        add(botonMenu);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int alto = getHeight();

                cartelVictoria.setBounds((ancho - 750) / 2, (int) (alto * 0.22), 750, 45);
                subtexto.setBounds((ancho - 800) / 2, (int) (alto * 0.34), 800, 30);

                int anchoBoton = 300;
                int altoBoton = 44;
                botonRanking.setBounds((ancho - anchoBoton) / 2, (int) (alto * 0.50), anchoBoton, altoBoton);
                botonMenu.setBounds((ancho - anchoBoton) / 2, (int) (alto * 0.60), anchoBoton, altoBoton);
            }
        });
    }
}
