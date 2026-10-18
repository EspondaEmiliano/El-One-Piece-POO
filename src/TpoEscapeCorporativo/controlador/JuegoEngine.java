package TpoEscapeCorporativo.controlador;

import TpoEscapeCorporativo.modelo.entidades.Empleado;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.datos.Direccion;

public class JuegoEngine {
    private Empleado jugador;

    public JuegoEngine() {
        // Inicialización del hitbox (x1, y1, x2, y2) y la velocidad
        Posicion posicionInicial = new Posicion(10, 10, 42, 42);
        this.jugador = new Empleado(posicionInicial, 5);
    }

    public void actualizar() {
        // Este método se invocará en cada ciclo (frame) del Game Loop
        jugador.mover();
    }

    public void procesarEntrada(Direccion nuevaDireccion) {
        // Recibe la orden de movimiento y altera el estado de la entidad
        jugador.setDireccionActual(nuevaDireccion);
    }

    public Empleado getJugador() {
        // Permite a la Vista (VentanaJuego) obtener los datos para el renderizado
        return jugador;
    }
}