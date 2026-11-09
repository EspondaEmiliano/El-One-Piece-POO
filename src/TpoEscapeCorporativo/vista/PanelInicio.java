package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Pantalla de bienvenida y menú principal del juego.
 * Proporciona acceso al inicio directo de la partida, selector de niveles,
 * visualización de mejores puntajes y ajuste de volumen del audio retro.
 */
public class PanelInicio extends JPanel {

    private PanelPrincipal panelPrincipal;

    // Componentes visuales
    private JLabel cartelTitulo;
    private JButton botonPlay;
    private JButton botonSeleccionarNivel;
    private JButton botonRanking;
    private JLabel lblControles;
    private JLabel lblObjetivo;
    private JLabel lblPausa;
    private JLabel lblVolumen;
    private JSlider sliderVolumen;

    public PanelInicio(PanelPrincipal panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
        inicializarPanel();
    }

    private void inicializarPanel() {
        // Fondo carbón oscuro consistente con PanelJuego
        this.setBackground(new Color(25, 25, 25));
        this.setLayout(null);

        // Título Arcade con tipografía retro en color dorado
        cartelTitulo = new JLabel("ESCAPE CORPORATIVO", SwingConstants.CENTER);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(22f));
        cartelTitulo.setForeground(new Color(255, 215, 0));
        add(cartelTitulo);

        // Botón para iniciar el juego desde el primer nivel
        botonPlay = new JButton("INICIAR JUEGO");
        botonPlay.setFont(GestorRecursos.getFuenteArcade(13f));
        botonPlay.setBackground(new Color(30, 130, 60));
        botonPlay.setForeground(Color.WHITE);
        botonPlay.setFocusable(false);
        botonPlay.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        botonPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPlay.addActionListener(e -> {
            this.panelPrincipal.iniciarJuegoEnNivel(1);
        });
        add(botonPlay);

        // Botón para desplegar el selector de niveles disponibles
        botonSeleccionarNivel = new JButton("SELECCIONAR NIVEL");
        botonSeleccionarNivel.setFont(GestorRecursos.getFuenteArcade(12f));
        botonSeleccionarNivel.setBackground(new Color(110, 40, 140));
        botonSeleccionarNivel.setForeground(Color.WHITE);
        botonSeleccionarNivel.setFocusable(false);
        botonSeleccionarNivel.setBorder(BorderFactory.createLineBorder(new Color(190, 70, 240), 2));
        botonSeleccionarNivel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonSeleccionarNivel.addActionListener(e -> abrirDialogoSeleccionNivel());
        add(botonSeleccionarNivel);

        // Botón para acceder a la tabla de récords del Top 10
        botonRanking = new JButton("RANKING / TOP 10");
        botonRanking.setFont(GestorRecursos.getFuenteArcade(12f));
        botonRanking.setBackground(new Color(20, 80, 140));
        botonRanking.setForeground(Color.WHITE);
        botonRanking.setFocusable(false);
        botonRanking.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 2));
        botonRanking.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonRanking.addActionListener(e -> {
            this.panelPrincipal.mostrarPanel("RANKING");
        });
        add(botonRanking);

        // Carteles de controles e instrucciones operativas
        lblControles = new JLabel("CONTROLES: FLECHAS O W-A-S-D", SwingConstants.CENTER);
        lblControles.setFont(GestorRecursos.getFuenteArcade(9f));
        lblControles.setForeground(Color.CYAN);
        add(lblControles);

        lblObjetivo = new JLabel("ESQUIVA AL JEFE Y LLEGA A LA SALIDA [S]", SwingConstants.CENTER);
        lblObjetivo.setFont(GestorRecursos.getFuenteArcade(9f));
        lblObjetivo.setForeground(Color.LIGHT_GRAY);
        add(lblObjetivo);

        lblPausa = new JLabel("PAUSA: TECLA 'P' O 'ESC'", SwingConstants.CENTER);
        lblPausa.setFont(GestorRecursos.getFuenteArcade(9f));
        lblPausa.setForeground(Color.YELLOW);
        add(lblPausa);

        // Indicador y control deslizante para calibrar el volumen del audio
        lblVolumen = new JLabel("VOLUMEN: 70%", SwingConstants.CENTER);
        lblVolumen.setFont(GestorRecursos.getFuenteArcade(9f));
        lblVolumen.setForeground(Color.CYAN);
        add(lblVolumen);

        sliderVolumen = new JSlider(0, 100, 70);
        sliderVolumen.setBackground(new Color(25, 25, 25));
        sliderVolumen.setForeground(Color.CYAN);
        sliderVolumen.setFocusable(false);
        sliderVolumen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sliderVolumen.addChangeListener(e -> {
            int valor = sliderVolumen.getValue();
            lblVolumen.setText("VOLUMEN: " + valor + "%");
            GestorAudio.getInstance().setVolumen(valor / 100.0f);
        });
        add(sliderVolumen);

        // Distribución proporcional de los elementos en resolución fija 900x680
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int alto = getHeight();

                cartelTitulo.setBounds((ancho - 700) / 2, (int) (alto * 0.05), 700, 45);

                int anchoBoton = 280;
                int altoBoton = 44;
                int yBotones = (int) (alto * 0.30);

                botonPlay.setBounds((ancho - anchoBoton) / 2, yBotones, anchoBoton, altoBoton);
                botonSeleccionarNivel.setBounds((ancho - anchoBoton) / 2, yBotones + 54, anchoBoton, altoBoton);
                botonRanking.setBounds((ancho - anchoBoton) / 2, yBotones + 108, anchoBoton, altoBoton);

                int yInstrucciones = (int) (alto * 0.58);
                lblControles.setBounds((ancho - 650) / 2, yInstrucciones, 650, 24);
                lblObjetivo.setBounds((ancho - 650) / 2, yInstrucciones + 30, 650, 24);
                lblPausa.setBounds((ancho - 650) / 2, yInstrucciones + 60, 650, 24);

                int anchoSlider = 250;
                int yVolumen = (int) (alto * 0.77);
                lblVolumen.setBounds((ancho - 300) / 2, yVolumen, 300, 20);
                sliderVolumen.setBounds((ancho - anchoSlider) / 2, yVolumen + 25, anchoSlider, 24);
            }
        });
    }

    /**
     * Despliega una ventana emergente para elegir el nivel de inicio entre los mapas existentes.
     */
    private void abrirDialogoSeleccionNivel() {
        String[] opcionesNiveles = this.panelPrincipal.getMotorEngine().getOpcionesNiveles();
        if (opcionesNiveles == null || opcionesNiveles.length == 0) {
            return;
        }

        JComboBox<String> comboNiveles = new JComboBox<>(opcionesNiveles);
        comboNiveles.setFont(GestorRecursos.getFuenteArcade(9f));
        comboNiveles.setBackground(new Color(30, 30, 30));
        comboNiveles.setForeground(Color.WHITE);

        JPanel panelDialogo = new JPanel(new GridLayout(2, 1, 5, 8));
        panelDialogo.setBackground(new Color(25, 25, 25));

        JLabel lblInstruccion = new JLabel("SELECCIONA EL NIVEL DE PARTIDA:", SwingConstants.CENTER);
        lblInstruccion.setFont(GestorRecursos.getFuenteArcade(8f));
        lblInstruccion.setForeground(Color.CYAN);

        panelDialogo.add(lblInstruccion);
        panelDialogo.add(comboNiveles);

        int resultado = JOptionPane.showConfirmDialog(
                this,
                panelDialogo,
                "SELECCION DE NIVEL",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            int indiceSeleccionado = comboNiveles.getSelectedIndex();
            int nivelElegido = indiceSeleccionado + 1;
            this.panelPrincipal.iniciarJuegoEnNivel(nivelElegido);
        }
    }

    /**
     * Dibuja los avatares enfrentados del empleado y del jefe en el espacio entre título y botones.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int centroX = getWidth() / 2;
        int posY = (int) (getHeight() * 0.16);
        int tamanioSprite = 64;

        Image spriteEmpleado = GestorRecursos.getSpriteEmpleado();
        if (spriteEmpleado != null) {
            g.drawImage(spriteEmpleado, centroX - 140, posY, tamanioSprite, tamanioSprite, this);
        }

        g.setFont(GestorRecursos.getFuenteArcade(16f));
        g.setColor(Color.RED);
        g.drawString("VS", centroX - 16, posY + 42);

        Image spriteJefe = GestorRecursos.getSpriteJefe();
        if (spriteJefe != null) {
            g.drawImage(spriteJefe, centroX + 76, posY, tamanioSprite, tamanioSprite, this);
        }
    }
}
