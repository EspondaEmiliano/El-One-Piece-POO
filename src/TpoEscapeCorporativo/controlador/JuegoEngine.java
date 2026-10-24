package TpoEscapeCorporativo.controlador;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.entidades.*;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JuegoEngine {
    private List<Muro> muros;
    private List<Proyectil> proyectiles;
    private List<ZonaBuffo> zonaBuffos;
    private Empleado jugador;
    private Jefe enemigo;
    private Salida salida;

    public JuegoEngine() {
        // Inicialización del hitbox (x1, y1, x2, y2) y la velocidad
        Posicion posicionInicial = new Posicion(10, 10, 42, 42);
        this.jugador = new Empleado(posicionInicial, 5);

        // Inicializa al jefe.
        Posicion posicionJefe = new Posicion(100, 10, 132, 42);
        this.enemigo = new Jefe(posicionJefe, 3, "Jefe", 1500);

        // Crea una lista vacía para almacenar los proyectiles.
        this.proyectiles = new ArrayList<>();

        // Inicializa zonas de buffos e instancias de prueba en el mapa (tamaño 32x32)
        this.zonaBuffos = new ArrayList<>();
        this.zonaBuffos.add(new ZonaBuffo(new Posicion(250,150,282,182),new Cafe()));
        this.zonaBuffos.add(new ZonaBuffo(new Posicion(400,250,432,282),new Aguinaldo()));
    }

    public void actualizar() {
        // Este método se invocará en cada ciclo (frame) del Game Loop.
        jugador.mover();

        // Actualiza el tiempo restante de los buffos del jugador y remueve expirados
        jugador.actualizarBuffos(16);

        // Detecta recolección de potenciadores en el mapa
        for (ZonaBuffo zona : zonaBuffos) {
            if (!zona.isRecolectado() && zona.verificarColision(jugador.getPosicion())) {
                Buffo buffoRecogido = zona.consumirBuffo();
                jugador.agregarBuffo(buffoRecogido);
            }
        }

        // Hace que el jefe siga horizontalmente al empleado.
        enemigo.seguirEmpleado(jugador.getPosicion());

        // Comprueba si el jefe puede atacar.
        if (enemigo.puedeAtacar()) {

            // Crea un proyectil desde la posición actual del jefe.
            enemigo.atacar(jugador.getPosicion());

            // Recupera el proyectil que acaba de crear.
            Proyectil nuevoProyectil = enemigo.getProyectilGenerado();

            // Si se creó correctamente, lo agrega a la lista.
            if (nuevoProyectil != null) {
                proyectiles.add(nuevoProyectil);

                // Registra el momento del ataque.
                enemigo.registrarAtaque();

                // Limpia la referencia temporal del jefe.
                enemigo.limpiarProyectilGenerado();
            }
        }

        // Actualiza todos los proyectiles y elimina los inactivos.
        Iterator<Proyectil> iterador = proyectiles.iterator();

        while (iterador.hasNext()) {

            // Obtiene el siguiente proyectil.
            Proyectil proyectil = iterador.next();

            // Si sigue activo, avanza según su dirección y velocidad.
            if (proyectil.getEstaActivo()) {
                proyectil.mover();
            } else {

                // Elimina los proyectiles que ya no están activos.
                iterador.remove();
            }
        }
    }

    public void procesarEntrada(Direccion nuevaDireccion) {
        // Recibe la orden de movimiento y altera el estado de la entidad
        jugador.setDireccionActual(nuevaDireccion);
    }

    public Empleado getJugador() {
        // Permite a la Vista (VentanaJuego) obtener los datos para el renderizado
        return jugador;
    }


    public Jefe getJefe() {
        // Permite a la vista consultar al jefe.
        return enemigo;
    }


    public List<Proyectil> getProyectiles() {
        // Devuelve una copia de la lista para consultar los proyectiles.
        return new ArrayList<>(proyectiles);
    }

    public List<ZonaBuffo> getZonasBuffo() {
        return new ArrayList<>(zonaBuffos);
    }

}