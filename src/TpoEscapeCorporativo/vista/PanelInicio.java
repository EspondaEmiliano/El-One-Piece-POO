package TpoEscapeCorporativo.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PanelInicio extends JPanel {

    private PanelPrincipal panelPrincipal;

    // Componentes visuales
    private JLabel cartelTitulo;
    private JButton botonPlay;
    private JLabel lblControles;
    private JLabel lblObjetivo;
    private JLabel lblPausa;

    public PanelInicio(PanelPrincipal panelPrincipal){
        this.panelPrincipal = panelPrincipal;
        inicializarPanel();
    }

    private void inicializarPanel() {
        // 1. Mismo fondo carbón oscuro que PanelJuego
        this.setBackground(new Color(25, 25, 25));
        this.setLayout(null);

        // 2. Título Arcade en color dorado
        cartelTitulo = new JLabel("ESCAPE CORPORATIVO", SwingConstants.CENTER);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(18f));
        cartelTitulo.setForeground(new Color(255,215,0)); // Dorado
        add(cartelTitulo);

        // Botón estilo máquina arcade con cursor de mano y borde neón
        botonPlay = new JButton("INICIAR JUEGO");
        botonPlay.setFont(GestorRecursos.getFuenteArcade(12f));
        botonPlay.setBackground(new Color(30,130,60)); // Verde oscuro
        botonPlay.setForeground(Color.WHITE);
        botonPlay.setFocusable(false);
        botonPlay.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2)); // Borde verde neón
        botonPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // VINCULACIÓN: Aquí hacemos que el botón ordene el cambio de panel al hacer clic
        botonPlay.addActionListener(e -> {
            this.panelPrincipal.mostrarPanel("JUEGO");
        });
        add(botonPlay);

        // 4. Carteles de controles e instrucciones
        lblControles = new JLabel("CONTROLES: FLECHAS O W-A-S-D", SwingConstants.CENTER);
        lblControles.setFont(GestorRecursos.getFuenteArcade(8f));
        lblControles.setForeground(Color.CYAN);
        add(lblControles);

        lblObjetivo = new JLabel("ESQUIVA AL JEFE Y LLEGA A LA SALIDA [S]", SwingConstants.CENTER);
        lblObjetivo.setFont(GestorRecursos.getFuenteArcade(8f));
        lblObjetivo.setForeground(Color.LIGHT_GRAY);
        add(lblObjetivo);

        lblPausa = new JLabel("PAUSA: TECLA 'P'", SwingConstants.CENTER);
        lblPausa.setFont(GestorRecursos.getFuenteArcade(8f));
        lblPausa.setForeground(Color.YELLOW);
        add(lblPausa);

        // 5. Posicionamiento proporcional de los elementos
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();

                // Título en la parte superior
                cartelTitulo.setBounds((ancho - 550) / 2, 35, 550, 35);

                // Boton central
                int anchoBoton = 240;
                int altoBoton = 45;
                botonPlay.setBounds((ancho - anchoBoton) / 2, 195, anchoBoton, altoBoton);

                // Bloque de instrucciones inferior
                lblControles.setBounds((ancho - 550) / 2, 280, 550, 20);
                lblObjetivo.setBounds((ancho - 550) / 2, 310, 550, 20);
                lblPausa.setBounds((ancho - 550) / 2, 340, 550, 20);
            }
        });
    }

    /**
     * Dibuja los sprites del empleado y del jefe enfrentados
     * en el espacio libre entre el título y el botón.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int centroX = getWidth() / 2;
        int posY = 100;
        int tamanioSprite = 48; // Escalados ligeramente para el menú

        // Empleado a la izquierda
        Image spriteEmpleado = GestorRecursos.getSpriteEmpleado();
        if (spriteEmpleado != null) {
            g.drawImage(spriteEmpleado, centroX - 110, posY, tamanioSprite, tamanioSprite, this);
        }

        // Versus o flecha en el centro
        g.setFont(GestorRecursos.getFuenteArcade(12f));
        g.setColor(Color.RED);
        g.drawString("VS", centroX - 12, posY + 32);

        // Jefe a la derecha
        Image spriteJefe = GestorRecursos.getSpriteJefe();
        if (spriteJefe != null) {
            g.drawImage(spriteJefe, centroX + 60, posY, tamanioSprite, tamanioSprite, this);
        }
    }
}