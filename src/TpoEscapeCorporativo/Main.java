package TpoEscapeCorporativo;

import TpoEscapeCorporativo.vista.PanelPrincipal;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal (Entry Point) que inicializa la interfaz de usuario
 * del juego en el hilo de eventos de Swing (Event Dispatch Thread).
 */
public class Main {

    /**
     * Método principal que arranca la aplicación.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PanelPrincipal();
        });
    }
}
