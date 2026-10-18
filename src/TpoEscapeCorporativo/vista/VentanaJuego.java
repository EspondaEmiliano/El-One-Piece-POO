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
        JFrame ventana = new JFrame("Super Escape Maravilloso del Empleado Negligente");
        ventana.setSize(600,400);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(null);

        JLabel cartelTitulo = new JLabel("Super Escape Maravilloso del Empleado Negligente", SwingConstants.CENTER);
        cartelTitulo.setBounds(150, 20, 300, 40);
        ventana.add(cartelTitulo);

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
}
}
