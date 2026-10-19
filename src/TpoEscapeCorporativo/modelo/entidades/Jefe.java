package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.Posicion;

public class Jefe extends EntidadMovil{
    private String nombre;
    private int intervalAtaque;
    private long ultimoAtaque; // Momento en el que se realizó el último ataque

    public Jefe(Posicion posicion, int velocidad, String nombre, int intervalAtaque) {
        super(posicion, velocidad);
        this.nombre = nombre;
        this.intervalAtaque = intervalAtaque;
        this.ultimoAtaque = 0;
    }

    public boolean puedeAtacar() {
        // Determina si el jefe ya puede realizar otro ataque

        long tiempoActual = System.currentTimeMillis(); // Obtiene el tiempo actual en milisegundos

        return tiempoActual - ultimoAtaque >= intervalAtaque; // Comprueba si transcurrió el intervalo establecido.
    }

    public void registrarAtaque() {
        // Registra el momento en el que se realiza un ataque
        ultimoAtaque = System.currentTimeMillis(); // Guarda el instante del ataque para calcular el próximo intervalo
    }

    public void seguirEmpleado(Posicion posicionEmpleado) {
        // Mueve horizontalmente al jefe para seguir al empleado

        // Compara el centro horizontal del jefe con el del empleado
        int centroJefe = (posicion.getX1() + posicion.getX2()) / 2;
        int centroEmpleado = (posicionEmpleado.getX1() + posicionEmpleado.getX2()) / 2;

        // Si el empleado está a la derecha, el jefe se mueve hacia la derecha
        if (centroJefe < centroEmpleado) {
            direccionActual = Direccion.DERECHA;
        }

        // Si el empleado está a la izquierda, el jefe se mueve hacia la izquierda
        else if (centroJefe > centroEmpleado) {
            direccionActual = Direccion.IZQUIERDA;
        }

        // Si ambos están alineados horizontalmente, el jefe deja de desplazarse.
        else {
            direccionActual = Direccion.NINGUNA;
        }

        mover();
    }

    public void atacar(Posicion posicionObjetivo) {
        // Implementar después de ver la lógica de Proyectil
    }


}
