package TpoEscapeCorporativo.controlador;

import TpoEscapeCorporativo.excepciones.NivelNoEncontradoException;
import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.entidades.*;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;
import TpoEscapeCorporativo.servicios.GestorArchivos;

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

    // NUEVO: Bandera de victoria
    private boolean juegoGanado = false;

    public JuegoEngine() {
        // Inicializa las listas vacías
        this.muros = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.zonaBuffos = new ArrayList<>();

        // Carga dinámicamente el nivel 1 desde assets/mapas/nivel_1.txt
        cargarNivel(1);
    }

    public void actualizar() {
        // Si ya se ganó, no se procesan más físicas
        if (juegoGanado) return;

        // Este método se invocará en cada ciclo (frame) del Game Loop.
        jugador.mover(muros);

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
            Proyectil proyectil = iterador.next();
            if (proyectil.getEstaActivo()) {
                proyectil.mover();
            } else {
                iterador.remove();
            }
        }

        // NUEVO: Verificar si el jugador alcanzó la salida de escape
        if (salida != null && salida.esAlcanzada(jugador.getPosicion())) {
            this.juegoGanado = true;
        }
    }

    public void procesarEntrada(Direccion nuevaDireccion) {
        jugador.setDireccionActual(nuevaDireccion);
    }

    public void cargarNivel(int numeroNivel) {
        GestorArchivos gestor = new GestorArchivos();
        try {
            char [][] mapa = gestor.cargarMapaNivel(numeroNivel);
            int tamanioCelda = 32;

            muros.clear();
            zonaBuffos.clear();

            for (int fila = 0; fila < mapa.length; fila++) {
                for (int columna = 0; columna < mapa[fila].length; columna++) {
                    int x1 = columna * tamanioCelda + 12; // Margen centrado
                    int y1 = fila * tamanioCelda + 8;    // Margen centrado
                    int x2 = x1 + tamanioCelda;
                    int y2 = y1 + tamanioCelda;
                    Posicion posicion = new Posicion(x1, y1, x2, y2);

                    char c = mapa[fila][columna];
                    switch (c) {
                        case '#': muros.add(new Muro(posicion)); break;
                        case 'E':
                            Posicion posicionEmpleado = new Posicion(x1 + 4, y1 + 4, x1 + 28, y1 + 28);
                            this.jugador = new Empleado(posicionEmpleado, 2);
                            break;
                        case 'J': this.enemigo = new Jefe(posicion, 1, "Jefe", 1500); break;
                        case 'S': this.salida = new Salida(posicion); break;
                        case 'C': zonaBuffos.add(new ZonaBuffo(posicion, new Cafe())); break;
                        case 'A': zonaBuffos.add(new ZonaBuffo(posicion, new Aguinaldo())); break;
                    }
                }
            }
        } catch (NivelNoEncontradoException e){
            System.err.println("Error al cargar nivel: " + e.getMessage());
        }
    }

    public boolean isJuegoGanado() {
        return juegoGanado;
    }

    public Empleado getJugador() {
        return jugador;
    }

    public Jefe getJefe() {
        return enemigo;
    }

    public List<Proyectil> getProyectiles() {
        return new ArrayList<>(proyectiles);
    }

    public List<ZonaBuffo> getZonasBuffo() {
        return new ArrayList<>(zonaBuffos);
    }

    public List<Muro> getMuros() {
        return new ArrayList<>(muros);
    }

    public Salida getSalida() {
        return salida;
    }
}