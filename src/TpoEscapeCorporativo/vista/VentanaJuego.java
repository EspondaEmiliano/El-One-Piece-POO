package TpoEscapeCorporativo.vista;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import TpoEscapeCorporativo.vista.GestorRecursos;

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

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
}
}
