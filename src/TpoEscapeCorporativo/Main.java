package  TpoEscapeCorporativo;

import TpoEscapeCorporativo.vista.PanelInicio;
import TpoEscapeCorporativo.vista.PanelPrincipal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        ///PanelInicio ventanita = new PanelInicio(); ///Primer version

        SwingUtilities.invokeLater(() -> {
            PanelPrincipal panel= new PanelPrincipal();
        });
    }
}
