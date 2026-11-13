package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.servicios.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla de bienvenida y menú principal del juego.
 * Proporciona acceso al inicio directo de la partida, un selector de niveles
 * con estética arcade (tarjetas interactivas con progresión desbloqueable),
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
        actualizarEstadoSelector();
    }

    private void inicializarPanel() {
        // 1. Mismo fondo carbón oscuro que PanelJuego
        this.setBackground(new Color(25, 25, 25));
        this.setLayout(null);

        // 2. Título Arcade en color dorado
        cartelTitulo = new JLabel("ESCAPE CORPORATIVO", SwingConstants.CENTER);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(22f));
        cartelTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        add(cartelTitulo);

        // Botón estilo máquina arcade para iniciar desde el Nivel 1
        botonPlay = new JButton("INICIAR JUEGO");
        botonPlay.setFont(GestorRecursos.getFuenteArcade(13f));
        botonPlay.setBackground(new Color(30, 130, 60)); // Verde oscuro
        botonPlay.setForeground(Color.WHITE);
        botonPlay.setFocusable(false);
        botonPlay.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2)); // Borde verde neón
        botonPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPlay.addActionListener(e -> {
            this.panelPrincipal.iniciarJuegoEnNivel(1);
        });
        add(botonPlay);

        // Botón para desplegar el selector arcade de niveles
        botonSeleccionarNivel = new JButton("SELECCIONAR NIVEL");
        botonSeleccionarNivel.setFont(GestorRecursos.getFuenteArcade(11f));
        botonSeleccionarNivel.setFocusable(false);
        botonSeleccionarNivel.addActionListener(e -> abrirDialogoSeleccionNivel());
        add(botonSeleccionarNivel);

        // Botón para acceder al Ranking / TOP 10
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

        // 4. Carteles de controles e instrucciones
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

        // Etiqueta de volumen arcade
        lblVolumen = new JLabel("VOLUMEN: 70%", SwingConstants.CENTER);
        lblVolumen.setFont(GestorRecursos.getFuenteArcade(9f));
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

        // 5. Posicionamiento armónico y proporcional de los elementos en 900x680
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = getWidth();
                int alto = getHeight();

                // Título en la parte superior
                cartelTitulo.setBounds((ancho - 700) / 2, (int) (alto * 0.05), 700, 45);

                int anchoBoton = 310;
                int altoBoton = 42;
                int yBotones = (int) (alto * 0.31);

                // Tres botones principales alineados verticalmente
                botonPlay.setBounds((ancho - anchoBoton) / 2, yBotones, anchoBoton, altoBoton);
                botonSeleccionarNivel.setBounds((ancho - anchoBoton) / 2, yBotones + 52, anchoBoton, altoBoton);
                botonRanking.setBounds((ancho - anchoBoton) / 2, yBotones + 104, anchoBoton, altoBoton);

                // Bloque de instrucciones inferior
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
     * Solo se habilita cuando el jugador ha superado al menos un nivel de la partida.
     */
    public void actualizarEstadoSelector() {
        JuegoEngine motor = (this.panelPrincipal != null) ? this.panelPrincipal.getMotorEngine() : null;
        boolean habilitado = (motor != null && motor.isSelectorDesbloqueado());
        int totalNiveles = (motor != null) ? motor.getTotalNiveles() : 4;
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
     * Despliega un cuadro de diálogo modal con estética Arcade que presenta
     * las tarjetas de todos los niveles del juego, permitiendo iniciar directamente
     * cualquiera de los niveles ya desbloqueados y visualizando los que aún están bloqueados.
     */
    private void abrirDialogoSeleccionNivel() {
        JuegoEngine motor = this.panelPrincipal.getMotorEngine();
        if (motor == null || !motor.isSelectorDesbloqueado()) {
            return;
        }

        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(ventanaPadre, "SELECCIONAR NIVEL", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setSize(560, 420);
        dialogo.setLocationRelativeTo(this);

        JPanel contenedor = new JPanel(new BorderLayout(0, 14));
        contenedor.setBackground(new Color(20, 20, 24));
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 70, 245), 3),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera del selector
        JPanel panelCabecera = new JPanel(new GridLayout(2, 1, 0, 6));
        panelCabecera.setOpaque(false);

        JLabel lblTitulo = new JLabel("SELECCION DE NIVEL", SwingConstants.CENTER);
        lblTitulo.setFont(GestorRecursos.getFuenteArcade(15f));
        lblTitulo.setForeground(new Color(255, 215, 0));

        int totalNiveles = motor.getTotalNiveles();
        int maxDesbloqueado = motor.getNivelMaximoDesbloqueado();
        JLabel lblSubtitulo = new JLabel(
                "PROGRESO DESBLOQUEADO: " + maxDesbloqueado + " DE " + totalNiveles + " NIVELES",
                SwingConstants.CENTER
        );
        lblSubtitulo.setFont(GestorRecursos.getFuenteArcade(8f));
        lblSubtitulo.setForeground(Color.CYAN);

        panelCabecera.add(lblTitulo);
        panelCabecera.add(lblSubtitulo);
        contenedor.add(panelCabecera, BorderLayout.NORTH);

        // Grilla central con las tarjetas de los 4 niveles
        JPanel panelNiveles = new JPanel(new GridLayout(totalNiveles, 1, 0, 10));
        panelNiveles.setOpaque(false);

        for (int nivel = 1; nivel <= totalNiveles; nivel++) {
            final int nivelElegido = nivel;
            boolean desbloqueado = (nivel <= maxDesbloqueado);
            String nombreNivel = motor.getNombreNivel(nivel).toUpperCase();
            String etiquetaDificultad = obtenerEtiquetaDificultad(nivel);

            JButton btnNivel = new JButton();
            btnNivel.setLayout(new BorderLayout(10, 0));
            btnNivel.setFocusable(false);
            btnNivel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            desbloqueado ? new Color(0, 200, 255) : new Color(75, 75, 75),
                            2
                    ),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)
            ));

            JLabel lblIzquierda = new JLabel(nombreNivel);
            lblIzquierda.setFont(GestorRecursos.getFuenteArcade(9f));

            JLabel lblDerecha = new JLabel();
            lblDerecha.setFont(GestorRecursos.getFuenteArcade(8f));
            lblDerecha.setHorizontalAlignment(SwingConstants.RIGHT);

            if (desbloqueado) {
                Color bgNormal = new Color(36, 26, 58);
                Color bgHover = new Color(62, 42, 98);
                btnNivel.setBackground(bgNormal);
                btnNivel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                lblIzquierda.setForeground(Color.WHITE);
                lblDerecha.setText(etiquetaDificultad + "  [JUGAR]");
                lblDerecha.setForeground(new Color(80, 255, 130));

                btnNivel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btnNivel.setBackground(bgHover);
                        btnNivel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                                BorderFactory.createEmptyBorder(8, 14, 8, 14)
                        ));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        btnNivel.setBackground(bgNormal);
                        btnNivel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(0, 200, 255), 2),
                                BorderFactory.createEmptyBorder(8, 14, 8, 14)
                        ));
                    }
                });

                btnNivel.addActionListener(e -> {
                    dialogo.dispose();
                    this.panelPrincipal.iniciarJuegoEnNivel(nivelElegido);
                });
            } else {
                btnNivel.setBackground(new Color(32, 32, 34));
                btnNivel.setEnabled(false);

                lblIzquierda.setForeground(new Color(110, 110, 110));
                lblDerecha.setText("BLOQUEADO (SUPERA NIVEL " + (nivel - 1) + ")");
                lblDerecha.setForeground(new Color(150, 90, 90));
            }

            btnNivel.add(lblIzquierda, BorderLayout.WEST);
            btnNivel.add(lblDerecha, BorderLayout.EAST);
            panelNiveles.add(btnNivel);
        }

        contenedor.add(panelNiveles, BorderLayout.CENTER);

        // Pie del diálogo con botón de cierre
        JPanel panelPie = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        panelPie.setOpaque(false);

        JButton btnCerrar = new JButton("VOLVER AL MENU [ESC]");
        btnCerrar.setFont(GestorRecursos.getFuenteArcade(9f));
        btnCerrar.setPreferredSize(new Dimension(240, 36));
        btnCerrar.setBackground(new Color(130, 35, 45));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusable(false);
        btnCerrar.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 90), 2));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dialogo.dispose());

        panelPie.add(btnCerrar);
        contenedor.add(panelPie, BorderLayout.SOUTH);

        // Permitir cerrar con la tecla ESC
        contenedor.registerKeyboardAction(
                e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    /**
     * Retorna una etiqueta concisa de dificultad para presentar en cada tarjeta del selector.
     */
    private String obtenerEtiquetaDificultad(int numeroNivel) {
        switch (numeroNivel) {
            case 1: return "DIF: BAJA";
            case 2: return "DIF: MEDIA";
            case 3: return "DIF: ALTA";
            default: return "DIF: EXTREMA";
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
        int tamanioSprite = 64; // Escalado a 64px para resolución grande 900x680

        // Empleado a la izquierda
        Image spriteEmpleado = GestorRecursos.getSpriteEmpleado();
        if (spriteEmpleado != null) {
            g.drawImage(spriteEmpleado, centroX - 140, posY, tamanioSprite, tamanioSprite, this);
        }

        // Versus en el centro
        g.setFont(GestorRecursos.getFuenteArcade(16f));
        g.setColor(Color.RED);
        g.drawString("VS", centroX - 16, posY + 42);

        // Jefe a la derecha
        Image spriteJefe = GestorRecursos.getSpriteJefe();
        if (spriteJefe != null) {
            g.drawImage(spriteJefe, centroX + 76, posY, tamanioSprite, tamanioSprite, this);
        }
    }
}
