package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Diálogo modal con estética arcade que muestra las tarjetas de niveles del juego,
 * permitiendo iniciar directamente cualquiera de los pisos desbloqueados.
 * Visibilidad de paquete (default) para uso exclusivo de {@link PanelInicio}.
 */
class DialogoSeleccionNivel {

    private DialogoSeleccionNivel() {}

    /**
     * Despliega el selector modal centrado respecto al componente padre indicado.
     *
     * @param padre          Componente Swing desde el que se abre el diálogo.
     * @param panelPrincipal Coordinador principal para iniciar la partida en el nivel elegido.
     */
    static void mostrar(Component padre, PanelPrincipal panelPrincipal) {
        if (panelPrincipal == null) {
            return;
        }
        JuegoEngine motor = panelPrincipal.getMotorEngine();
        if (motor == null || !motor.isSelectorDesbloqueado()) {
            return;
        }

        Window ventanaPadre = SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(ventanaPadre, "SELECCIONAR NIVEL", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setSize(580, 480);
        dialogo.setLocationRelativeTo(padre);

        JPanel contenedor = crearContenedorPrincipal(dialogo, panelPrincipal, motor);

        contenedor.registerKeyboardAction(
                e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private static JPanel crearContenedorPrincipal(JDialog dialogo, PanelPrincipal panelPrincipal, JuegoEngine motor) {
        JPanel contenedor = new JPanel(new BorderLayout(0, 14));
        contenedor.setBackground(new Color(20, 20, 24));
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 70, 245), 3),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        int totalNiveles = motor.getTotalNiveles();
        int maxDesbloqueado = motor.getNivelMaximoDesbloqueado();

        contenedor.add(crearCabecera(totalNiveles, maxDesbloqueado), BorderLayout.NORTH);
        contenedor.add(crearGrillaNiveles(dialogo, panelPrincipal, motor, totalNiveles, maxDesbloqueado), BorderLayout.CENTER);
        contenedor.add(crearPie(dialogo), BorderLayout.SOUTH);

        return contenedor;
    }

    private static JPanel crearCabecera(int totalNiveles, int maxDesbloqueado) {
        JPanel panelCabecera = new JPanel(new GridLayout(2, 1, 0, 6));
        panelCabecera.setOpaque(false);

        JLabel lblTitulo = new JLabel("SELECCION DE NIVEL", SwingConstants.CENTER);
        lblTitulo.setFont(GestorRecursos.getFuenteArcade(15f));
        lblTitulo.setForeground(new Color(255, 215, 0));

        JLabel lblSubtitulo = new JLabel(
                "PROGRESO DESBLOQUEADO: " + maxDesbloqueado + " DE " + totalNiveles + " NIVELES",
                SwingConstants.CENTER
        );
        lblSubtitulo.setFont(GestorRecursos.getFuenteArcade(8f));
        lblSubtitulo.setForeground(Color.CYAN);

        panelCabecera.add(lblTitulo);
        panelCabecera.add(lblSubtitulo);
        return panelCabecera;
    }

    private static JPanel crearGrillaNiveles(
            JDialog dialogo,
            PanelPrincipal panelPrincipal,
            JuegoEngine motor,
            int totalNiveles,
            int maxDesbloqueado
    ) {
        JPanel panelNiveles = new JPanel(new GridLayout(totalNiveles, 1, 0, 8));
        panelNiveles.setOpaque(false);

        for (int nivel = 1; nivel <= totalNiveles; nivel++) {
            panelNiveles.add(crearTarjetaNivel(dialogo, panelPrincipal, motor, nivel, nivel <= maxDesbloqueado));
        }

        return panelNiveles;
    }

    private static JButton crearTarjetaNivel(
            JDialog dialogo,
            PanelPrincipal panelPrincipal,
            JuegoEngine motor,
            int nivel,
            boolean desbloqueado
    ) {
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

        JLabel lblIzquierda = new JLabel(motor.getNombreNivel(nivel).toUpperCase());
        lblIzquierda.setFont(GestorRecursos.getFuenteArcade(9f));

        JLabel lblDerecha = new JLabel();
        lblDerecha.setFont(GestorRecursos.getFuenteArcade(8f));
        lblDerecha.setHorizontalAlignment(SwingConstants.RIGHT);

        if (desbloqueado) {
            configurarTarjetaDesbloqueada(dialogo, panelPrincipal, motor, nivel, btnNivel, lblIzquierda, lblDerecha);
        } else {
            btnNivel.setBackground(new Color(32, 32, 34));
            btnNivel.setEnabled(false);
            lblIzquierda.setForeground(new Color(110, 110, 110));
            lblDerecha.setText("BLOQUEADO (SUPERA NIVEL " + (nivel - 1) + ")");
            lblDerecha.setForeground(new Color(150, 90, 90));
        }

        btnNivel.add(lblIzquierda, BorderLayout.WEST);
        btnNivel.add(lblDerecha, BorderLayout.EAST);
        return btnNivel;
    }

    private static void configurarTarjetaDesbloqueada(
            JDialog dialogo,
            PanelPrincipal panelPrincipal,
            JuegoEngine motor,
            int nivel,
            JButton btnNivel,
            JLabel lblIzquierda,
            JLabel lblDerecha
    ) {
        Color bgNormal = new Color(36, 26, 58);
        Color bgHover = new Color(62, 42, 98);

        btnNivel.setBackground(bgNormal);
        btnNivel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblIzquierda.setForeground(Color.WHITE);
        lblDerecha.setText(motor.getEtiquetaDificultadNivel(nivel) + "  [JUGAR]");
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
            panelPrincipal.iniciarJuegoEnNivel(nivel);
        });
    }

    private static JPanel crearPie(JDialog dialogo) {
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
        return panelPie;
    }
}
