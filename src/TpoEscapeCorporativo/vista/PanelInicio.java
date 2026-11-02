package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.servicios.GestorAudio;

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
    private JButton botonRanking;
    private JLabel lblVolumen;
    private JSlider sliderVolumen;

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

        // Botón para acceder al Ranking / TPO 10
        botonRanking = new JButton("RANKING / TOP 10");
        botonRanking.setFont(GestorRecursos.getFuenteArcade(11f));
        botonRanking.setBackground(new Color(20,80,140));
        botonRanking.setForeground(Color.WHITE);
        botonRanking.setFocusable(false);
        botonRanking.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 2));
        botonRanking.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botonRanking.addActionListener(e -> {
            this.panelPrincipal.mostrarPanel("RANKING");
        });
        add(botonRanking);

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

        // Etiqueta de volumen arcade
        lblVolumen = new JLabel("VOLUMEN: 70%", SwingConstants.CENTER);
        lblVolumen.setFont(GestorRecursos.getFuenteArcade(8f));
        lblVolumen.setForeground(Color.CYAN);
        add(lblVolumen);

        // Barra de sonido retro (JSlider)
        sliderVolumen = new JSlider(0, 100, 70);
        sliderVolumen.setBackground(new Color(25, 25, 25));
        sliderVolumen.setForeground(Color.CYAN);
        sliderVolumen.setFocusable(false);
        sliderVolumen.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listener: actualiza el volumen en tiempo real cuando el usuario desliza
        sliderVolumen.addChangeListener(e -> {
            int valor = sliderVolumen.getValue();
            lblVolumen.setText("VOLUMEN: " + valor + "%");
            GestorAudio.getInstance().setVolumen(valor / 100.0f);
        });
        add(sliderVolumen);

        // 5. Posicionamiento proporcional de los elementos
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int anchoSlider = 200;

                // Título en la parte superior
                cartelTitulo.setBounds((ancho - 550) / 2, 35, 550, 35);

                int anchoBoton = 240;
                int altoBoton = 36;

                // Botón Play arriba
                botonPlay.setBounds((ancho - anchoBoton) / 2, 175, anchoBoton, altoBoton);

                // Botón Ranking justo debajo
                botonRanking.setBounds((ancho - anchoBoton) / 2, 220, anchoBoton, altoBoton);

                // Bloque de instrucciones inferior
                lblControles.setBounds((ancho - 550) / 2, 275, 550, 20);
                lblObjetivo.setBounds((ancho - 550) / 2, 305, 550, 20);
                lblPausa.setBounds((ancho - 550) / 2, 335, 550, 20);

                lblVolumen.setBounds((ancho - 300) / 2, 360, 300, 18);
                sliderVolumen.setBounds((ancho - anchoSlider) / 2, 385, anchoSlider, 20);
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