package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;

import javax.swing.*;

public class VentanaJuego {

    public VentanaJuego(){
        inicializarVentana();
    }

    private void inicializarVentana(){
        System.out.println("Hola, mundo! (La vista se empezo a inicializar)");
        JFrame ventana = new JFrame("Escape Corporativo");
        ventana.setSize(600,400);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(null);

        JLabel cartelTitulo = new JLabel("Escape Corporativo", SwingConstants.CENTER);
        cartelTitulo.setBounds(50, 20, 500, 40);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(20f));

        ventana.add(cartelTitulo);

        JButton botonPlay = new JButton("Play");
        ventana.add(botonPlay);

        // Acción al hacer clic en Play:
        botonPlay.addActionListener(e -> {
            // 1. Instanciar el motor y la vista del juego
            JuegoEngine motor = new JuegoEngine();
            PanelJuego panelJuego = new PanelJuego(motor);

            // 2. Reemplazar la pantalla de inicio por el panel de juego
            ventana.setContentPane(panelJuego);
            ventana.revalidate();
            ventana.repaint();

            // 3. Arrancar el Game Loop y darle foco al panel para recibir teclas
            panelJuego.iniciarJuego();
        });

        // Adaptarse al tamaño que le de el usuario a la ventana
        ventana.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Adaptar el Título
                int anchoCartel = (int)(ventana.getWidth() * 0.80);
                int posXCartel = (ventana.getWidth() - anchoCartel) / 2;
                int posYCartel = (int)(ventana.getHeight() * 0.05);
                cartelTitulo.setBounds(posXCartel, posYCartel, anchoCartel, 40);

                // Adaptar tamaño letra
                int tamanioLetra = ventana.getWidth() / 40;
                if (tamanioLetra < 14) tamanioLetra = 14; // Tamaño mínimo
                if (tamanioLetra > 28) tamanioLetra = 28; // Tamaño máximo

                // Mantener fuente arcade adaptada dinámicamente
                cartelTitulo.setFont(GestorRecursos.getFuenteArcade((float) tamanioLetra));

                // Adaptar el Botón
                int anchoBoton = (int)(ventana.getWidth() * 0.30);
                int posXBoton = (ventana.getWidth() - anchoBoton) / 2;
                int posYBoton = (int)(ventana.getHeight() * 0.25);
                botonPlay.setBounds(posXBoton, posYBoton, anchoBoton, 35);
            }
        });

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
