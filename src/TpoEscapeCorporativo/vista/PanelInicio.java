package TpoEscapeCorporativo.vista;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PanelInicio extends JPanel {

    private PanelPrincipal panelPrincipal;

    public PanelInicio(PanelPrincipal panelPrincipal){
        this.panelPrincipal = panelPrincipal;
        inicializarPanel();
    }

    private void inicializarPanel(){
        System.out.println("Hola, mundo! (El panel de inicio se empezó a inicializar)");

        // Mantenemos el layout en null para tus setBounds y proporciones
        setLayout(null);

        JLabel cartelTitulo = new JLabel("Super Escape Maravilloso del Empleado Negligente", SwingConstants.CENTER);
        cartelTitulo.setFont(GestorRecursos.getFuenteArcade(20f));
        add(cartelTitulo);

        JButton botonPlay = new JButton("Play");

        // VINCULACIÓN: Aquí hacemos que el botón ordene el cambio de panel al hacer clic
        botonPlay.addActionListener(e -> {
            this.panelPrincipal.mostrarPanel("JUEGO");
        });

        add(botonPlay);

        // Adaptarse al tamaño que le dé el usuario al panel principal/ventana
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Adaptar el Título usando getWidth() y getHeight() del panel
                int anchoCartel = (int)(getWidth() * 0.80);
                int posXCartel = (getWidth() - anchoCartel) / 2;
                int posYCartel = (int)(getHeight() * 0.05);
                cartelTitulo.setBounds(posXCartel, posYCartel, anchoCartel, 40);

                // Adaptar tamaño letra
                int tamanioLetra = getWidth() / 80;
                if (tamanioLetra < 14) tamanioLetra = 7; // Tamaño mínimo
                if (tamanioLetra > 28) tamanioLetra = 14; // Tamaño máximo
                cartelTitulo.setFont(GestorRecursos.getFuenteArcade((float) tamanioLetra));

                // Adaptar el Botón
                int anchoBoton = (int)(getWidth() * 0.30);
                int posXBoton = (getWidth() - anchoBoton) / 2;
                int posYBoton = (int)(getHeight() * 0.25);
                botonPlay.setBounds(posXBoton, posYBoton, anchoBoton, 35);
            }
        });
    }
}