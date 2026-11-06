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

    // Componentes de interfaz gráfica
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
        this.setBackground(new Color(25, 25, 25));
        this.setLayout(null);

        // Título Arcade con tipografía retro y color dorado
        cartelTitulo = new JLabel("ESCAPE CORPORATIVO", SwingConstants.CENTER);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(18f));
        cartelTitulo.setForeground(new Color(255, 215, 0));
        add(cartelTitulo);

        // Botón para iniciar el juego desde el primer nivel
        botonPlay = new JButton("INICIAR JUEGO");
        botonPlay.setFont(GestorRecursos.getFuenteArcade(12f));
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
        botonSeleccionarNivel.setFont(GestorRecursos.getFuenteArcade(11f));
        botonSeleccionarNivel.setBackground(new Color(110, 40, 140));
        botonSeleccionarNivel.setForeground(Color.WHITE);
        botonSeleccionarNivel.setFocusable(false);
        botonSeleccionarNivel.setBorder(BorderFactory.createLineBorder(new Color(190, 70, 240), 2));
        botonSeleccionarNivel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonSeleccionarNivel.addActionListener(e -> abrirDialogoSeleccionNivel());
        add(botonSeleccionarNivel);

        // Botón para acceder a la tabla de récords del Top 10
        botonRanking = new JButton("RANKING / TOP 10");
        botonRanking.setFont(GestorRecursos.getFuenteArcade(11f));
        botonRanking.setBackground(new Color(20, 80, 140));
        botonRanking.setForeground(Color.WHITE);
        botonRanking.setFocusable(false);
        botonRanking.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 2));
        botonRanking.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonRanking.addActionListener(e -> {
            this.panelPrincipal.mostrarPanel("RANKING");
        });
        add(botonRanking);

        // Carteles con instrucciones operativas de juego
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

        // Indicador y control deslizante para calibrar el volumen del audio
        lblVolumen = new JLabel("VOLUMEN: 70%", SwingConstants.CENTER);
        lblVolumen.setFont(GestorRecursos.getFuenteArcade(8f));
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

        // Distribución geométrica responsiva de los elementos en pantalla
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int anchoSlider = 200;
                int anchoBoton = 240;
                int altoBoton = 34;

                cartelTitulo.setBounds((ancho - 550) / 2, 30, 550, 35);

                botonPlay.setBounds((ancho - anchoBoton) / 2, 155, anchoBoton, altoBoton);
                botonSeleccionarNivel.setBounds((ancho - anchoBoton) / 2, 197, anchoBoton, altoBoton);
                botonRanking.setBounds((ancho - anchoBoton) / 2, 239, anchoBoton, altoBoton);

                lblControles.setBounds((ancho - 550) / 2, 288, 550, 18);
                lblObjetivo.setBounds((ancho - 550) / 2, 310, 550, 18);
                lblPausa.setBounds((ancho - 550) / 2, 332, 550, 18);

                lblVolumen.setBounds((ancho - 300) / 2, 356, 300, 16);
                sliderVolumen.setBounds((ancho - anchoSlider) / 2, 376, anchoSlider, 20);
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
     * Dibuja los avatares enfrentados del empleado y del jefe en la cabecera del menú.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int centroX = getWidth() / 2;
        int posY = 90;
        int tamanioSprite = 48;

        Image spriteEmpleado = GestorRecursos.getSpriteEmpleado();
        if (spriteEmpleado != null) {
            g.drawImage(spriteEmpleado, centroX - 110, posY, tamanioSprite, tamanioSprite, this);
        }

        g.setFont(GestorRecursos.getFuenteArcade(12f));
        g.setColor(Color.RED);
        g.drawString("VS", centroX - 12, posY + 32);

        Image spriteJefe = GestorRecursos.getSpriteJefe();
        if (spriteJefe != null) {
            g.drawImage(spriteJefe, centroX + 60, posY, tamanioSprite, tamanioSprite, this);
        }
    }
}
