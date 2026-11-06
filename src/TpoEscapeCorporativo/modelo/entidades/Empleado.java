package TpoEscapeCorporativo.modelo.entidades;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.Direccion;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Representa al personaje principal controlado por el jugador.
 * Hereda el comportamiento de movimiento de EntidadMovil y administra su estado,
 * protección por escudo (Aguinaldo) y modificadores activos (Buffos).
 */
public class Empleado extends EntidadMovil {

    public static final int SALUD_DEFAULT = 100;
    private int salud;
    private boolean tieneEscudo;
    private final List<Buffo> buffosActivos;
    private Direccion ultimaDireccion = Direccion.ABAJO;

    /**
     * Crea un empleado con su posición y velocidad iniciales.
     * Asigna la salud inicial por defecto (100), sin escudo y prepara la lista de buffos.
     *
     * @param posicion  Posición inicial del jugador.
     * @param velocidad Velocidad de movimiento del jugador.
     */
    public Empleado(Posicion posicion, int velocidad) {
        super(posicion, velocidad);
        this.salud = SALUD_DEFAULT;
        this.tieneEscudo = false;
        this.buffosActivos = new ArrayList<>();
    }

    /**
     * Mueve al empleado únicamente si la posición destino tentativa no colisiona
     * con ninguno de los muros presentes en el nivel.
     *
     * @param muros Lista de muros del mapa.
     */
    public void mover(List<Muro> muros) {
        if (direccionActual == Direccion.NINGUNA) {
            return;
        }

        int desplazamientoX = direccionActual.getDx() * velocidad;
        int desplazamientoY = direccionActual.getDy() * velocidad;

        // Hitbox tentativo al que intenta desplazarse
        Posicion posTentativa = new Posicion(
                posicion.getX1() + desplazamientoX,
                posicion.getY1() + desplazamientoY,
                posicion.getX2() + desplazamientoX,
                posicion.getY2() + desplazamientoY
        );

        // Verificar si colisiona contra algún muro del mapa
        boolean hayColision = false;
        if (muros != null) {
            for (Muro muro : muros) {
                if (muro.verificarColision(posTentativa)) {
                    hayColision = true;
                    break;
                }
            }
        }

        // Si no colisiona, se aplican las nuevas coordenadas
        if (!hayColision) {
            posicion.setX1(posTentativa.getX1());
            posicion.setY1(posTentativa.getY1());
            posicion.setX2(posTentativa.getX2());
            posicion.setY2(posTentativa.getY2());
        }
    }

    /**
     * Procesa el impacto de una pala o proyectil sobre el empleado.
     * Si cuenta con un escudo de Aguinaldo activo, lo rompe y el empleado sobrevive.
     * Si no tiene escudo, el impacto es letal (salud = 0).
     *
     * @return {@code true} si el impacto fue absorbido por el escudo (sobrevive);
     *         {@code false} si el impacto resultó fatal (muerte).
     */
    public boolean recibirImpacto() {
        if (this.tieneEscudo) {
            this.tieneEscudo = false;
            // Quiebra y remueve el buffo de Aguinaldo de la lista de activos
            for (Buffo buffo : this.buffosActivos) {
                if (buffo instanceof Aguinaldo) {
                    ((Aguinaldo) buffo).romper();
                }
            }
            this.buffosActivos.removeIf(Buffo::estaExpirado);
            return true; // Golpe absorbido con éxito (sobrevive)
        }

        this.salud = 0;
        return false; // Muerte instantánea
    }

    /**
     * Reduce la salud del empleado o delega al sistema de impacto letal con escudo.
     * Conservado por compatibilidad.
     *
     * @param danio Puntos de daño recibidos.
     */
    public void recibirDanio(int danio) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        recibirImpacto();
    }

    /**
     * Registra un buffo activo en el empleado.
     * Si ya tiene un escudo de Aguinaldo activo y toma otro, reemplaza el anterior
     * evitando duplicaciones en la lista y en el HUD.
     *
     * @param buffo Potenciador a incorporar.
     */
    public void agregarBuffo(Buffo buffo) {
        if (buffo != null) {
            if (buffo instanceof Aguinaldo) {
                this.buffosActivos.removeIf(b -> b instanceof Aguinaldo);
            }
            buffo.aplicar(this);
            this.buffosActivos.add(buffo);
        }
    }

    /**
     * Elimina un buffo de la lista de activos.
     *
     * @param buffo Potenciador a remover.
     */
    public void removerBuffo(Buffo buffo) {
        this.buffosActivos.remove(buffo);
    }

    /**
     * Actualiza el tiempo restante de todos los buffos activos y remueve
     * aquellos que hayan expirado, revirtiendo sus efectos.
     *
     * @param deltaTiempo Milisegundos transcurridos en el frame (ej. 16 ms).
     */
    public void actualizarBuffos(long deltaTiempo) {
        Iterator<Buffo> iterator = this.buffosActivos.iterator();
        while (iterator.hasNext()) {
            Buffo buffo = iterator.next();
            buffo.actualizar(deltaTiempo);

            if (buffo.estaExpirado()) {
                buffo.remover(this);
                iterator.remove();
            }
        }
    }

    /**
     * Indica si el empleado sigue vivo para continuar la partida.
     */
    public boolean estaVivo() {
        return this.salud > 0;
    }

    public Direccion getDireccionVisual() {
        if (direccionActual != Direccion.NINGUNA) {
            ultimaDireccion = direccionActual;
        }
        return ultimaDireccion;
    }

    // --- Getters y Setters ---

    /**
     * Consulta si el empleado cuenta con un escudo protector activo.
     */
    public boolean tieneEscudo() {
        return tieneEscudo;
    }

    /**
     * Modifica el estado del escudo protector.
     */
    public void setTieneEscudo(boolean tieneEscudo) {
        this.tieneEscudo = tieneEscudo;
    }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = Math.max(0, salud);
    }

    /**
     * Devuelve una vista de solo lectura de los buffos para proteger el encapsulamiento.
     */
    public List<Buffo> getBuffosActivos() {
        return Collections.unmodifiableList(buffosActivos);
    }
}
