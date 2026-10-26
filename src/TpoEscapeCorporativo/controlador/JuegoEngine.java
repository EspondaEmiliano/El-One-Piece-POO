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

    private boolean juegoGanado = false;

    public JuegoEngine() {
        this.muros = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.zonaBuffos = new ArrayList<>();

        cargarNivel(1);
    }

    public void actualizar() {
        if (juegoGanado) return;

        jugador.mover(muros);
        jugador.actualizarBuffos(16);

        for (ZonaBuffo zona : zonaBuffos) {
            if (!zona.isRecolectado() && zona.verificarColision(jugador.getPosicion())) {
                Buffo buffoRecogido = zona.consumirBuffo();
                jugador.agregarBuffo(buffoRecogido);
            }
        }

        enemigo.seguirEmpleado(jugador.getPosicion());

        if (enemigo.puedeAtacar()) {
            enemigo.atacar(jugador.getPosicion());
            Proyectil nuevoProyectil = enemigo.getProyectilGenerado();

            if (nuevoProyectil != null) {
                proyectiles.add(nuevoProyectil);
                enemigo.registrarAtaque();
                enemigo.limpiarProyectilGenerado();
            }
        }

        Iterator<Proyectil> iterador = proyectiles.iterator();
        while (iterador.hasNext()) {
            Proyectil proyectil = iterador.next();
            if (proyectil.getEstaActivo()) {
                proyectil.mover();
            } else {
                iterador.remove();
            }
        }

        if (salida != null && salida.esAlcanzada(jugador.getPosicion())) {
            this.juegoGanado = true;
        }
    }

    // NUEVO: Método para reiniciar la partida
    public void reiniciarJuego() {
        this.juegoGanado = false; // 1. Apaga la victoria
        cargarNivel(1);           // 2. Coloca al empleado al inicio y recarga ítems y muros
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
                    int x1 = columna * tamanioCelda + 12;
                    int y1 = fila * tamanioCelda + 8;
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