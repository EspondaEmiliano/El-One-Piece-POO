package TpoEscapeCorporativo.vista;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaJuego {

    public VentanaJuego(){
        inicializarVentana();
        }
    private void inicializarVentana(){
        System.out.println("Hola, mundo! (La vista se empezo a inicializar");
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

        //Adaptarse al tamaño que le de el usuario a la ventana
        ventana.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Adaptar el Título
                int anchoCartel = (int)(ventana.getWidth() * 0.80);
                int posXCartel = (ventana.getWidth() - anchoCartel) / 2;
                int posYCartel = (int)(ventana.getHeight() * 0.05);
                cartelTitulo.setBounds(posXCartel, posYCartel, anchoCartel, 40);
                // Adaptar tamaño
                int tamanioLetra = ventana.getWidth() / 40;
                if (tamanioLetra < 14) tamanioLetra = 14; // Tamaño mínimo
                if (tamanioLetra > 28) tamanioLetra = 28; // Tamaño máximo
                // Fuente
                cartelTitulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, tamanioLetra));


                // Adaptar el Botón (Agregado aquí adentro)
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
