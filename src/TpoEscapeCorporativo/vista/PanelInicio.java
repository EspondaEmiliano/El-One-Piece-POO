package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Pantalla de bienvenida y menú principal del juego.
 * Proporciona acceso al inicio directo de la partida, al selector modal de niveles
 * ({@link DialogoSeleccionNivel}), a la visualización del ranking y al ajuste de volumen.
 */
public class PanelInicio extends JPanel {

    private final PanelPrincipal panelPrincipal;

    private JLabel cartelTitulo;
    private JButton botonPlay;
    private JButton botonSeleccionarNivel;
    private JButton botonRanking;
    private JLabel lblControles;
    private JLabel lblObjetivo;
    private JLabel lblPausa;
    private JLabel lblVolumen;
    private JSlider sliderVolumen;

    /**
     * Construye el menú principal vinculándolo al coordinador de navegación.
     *
     * @param panelPrincipal Contenedor principal que coordina el cambio de vistas.
     */
    public PanelInicio(PanelPrincipal panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
        inicializarPanel();
        actualizarEstadoSelector();
    }

    private void inicializarPanel() {
        this.setBackground(new Color(25, 25, 25));
        this.setLayout(null);

        cartelTitulo = new JLabel("ESCAPE CORPORATIVO", SwingConstants.CENTER);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(22f));
        cartelTitulo.setForeground(new Color(255, 215, 0));
        add(cartelTitulo);

        botonPlay = new JButton("INICIAR JUEGO");
        botonPlay.setFont(GestorRecursos.getFuenteArcade(13f));
        botonPlay.setBackground(new Color(30, 130, 60));
        botonPlay.setForeground(Color.WHITE);
        botonPlay.setFocusable(false);
        botonPlay.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        botonPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPlay.addActionListener(e -> this.panelPrincipal.iniciarJuegoEnNivel(1));
        add(botonPlay);

        botonSeleccionarNivel = new JButton("SELECCIONAR NIVEL");
        botonSeleccionarNivel.setFont(GestorRecursos.getFuenteArcade(11f));
        botonSeleccionarNivel.setFocusable(false);
        botonSeleccionarNivel.addActionListener(e -> DialogoSeleccionNivel.mostrar(this, this.panelPrincipal));
        add(botonSeleccionarNivel);

        botonRanking = new JButton("RANKING / TOP 10");
        botonRanking.setFont(GestorRecursos.getFuenteArcade(12f));
        botonRanking.setBackground(new Color(20, 80, 140));
        botonRanking.setForeground(Color.WHITE);
        botonRanking.setFocusable(false);
        botonRanking.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 2));
        botonRanking.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonRanking.addActionListener(e -> this.panelPrincipal.mostrarPanel("RANKING"));
        add(botonRanking);

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

        configurarDisposicionResponsive();
    }

    private void configurarDisposicionResponsive() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int alto = getHeight();

                cartelTitulo.setBounds((ancho - 700) / 2, (int) (alto * 0.05), 700, 45);

                int anchoBoton = 310;
                int altoBoton = 42;
                int yBotones = (int) (alto * 0.31);

                botonPlay.setBounds((ancho - anchoBoton) / 2, yBotones, anchoBoton, altoBoton);
                botonSeleccionarNivel.setBounds((ancho - anchoBoton) / 2, yBotones + 52, anchoBoton, altoBoton);
                botonRanking.setBounds((ancho - anchoBoton) / 2, yBotones + 104, anchoBoton, altoBoton);

                int yInstrucciones = (int) (alto * 0.57);
                lblControles.setBounds((ancho - 650) / 2, yInstrucciones, 650, 24);
                lblObjetivo.setBounds((ancho - 650) / 2, yInstrucciones + 28, 650, 24);
                lblPausa.setBounds((ancho - 650) / 2, yInstrucciones + 56, 650, 24);

                int anchoSlider = 250;
                int yVolumen = (int) (alto * 0.76);
                lblVolumen.setBounds((ancho - 300) / 2, yVolumen, 300, 20);
                sliderVolumen.setBounds((ancho - anchoSlider) / 2, yVolumen + 25, anchoSlider, 24);
            }
        });
    }

    /**
     * Actualiza el estado visual e interactivo del botón selector de niveles.
     * Visibilidad de paquete (default): invocado por {@link PanelPrincipal} al mostrar el menú.
     * Solo se habilita cuando el jugador ha superado al menos un nivel de la partida.
     */
    void actualizarEstadoSelector() {
        JuegoEngine motor = (this.panelPrincipal != null) ? this.panelPrincipal.getMotorEngine() : null;
        boolean habilitado = (motor != null && motor.isSelectorDesbloqueado());
        int totalNiveles = (motor != null) ? motor.getTotalNiveles() : JuegoEngine.TOTAL_NIVELES;
        int desbloqueados = (motor != null) ? motor.getNivelMaximoDesbloqueado() : 1;

        botonSeleccionarNivel.setEnabled(habilitado);
        if (habilitado) {
            botonSeleccionarNivel.setText("NIVELES (" + desbloqueados + "/" + totalNiveles + ")");
            botonSeleccionarNivel.setBackground(new Color(100, 35, 135));
            botonSeleccionarNivel.setForeground(Color.WHITE);
            botonSeleccionarNivel.setBorder(BorderFactory.createLineBorder(new Color(195, 75, 250), 2));
            botonSeleccionarNivel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonSeleccionarNivel.setToolTipText("Abrir selector de niveles desbloqueados");
        } else {
            botonSeleccionarNivel.setText("NIVELES (BLOQUEADO)");
            botonSeleccionarNivel.setBackground(new Color(42, 42, 42));
            botonSeleccionarNivel.setForeground(new Color(125, 125, 125));
            botonSeleccionarNivel.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 2));
            botonSeleccionarNivel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            botonSeleccionarNivel.setToolTipText("Supera el Nivel 1 para desbloquear el selector de niveles");
        }
    }

    /**
     * Dibuja los sprites del empleado y del jefe enfrentados
     * en el espacio libre entre el título y los botones.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int centroX = getWidth() / 2;
        int posY = (int) (getHeight() * 0.15);
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
