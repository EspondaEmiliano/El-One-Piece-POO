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
 * Hereda el comportamiento de movimiento de EntidadMovil y administra sus corazones (vidas),
 * su posición inicial de reaparición, protección por escudo (Aguinaldo) y modificadores activos (Buffos).
 */
public class Empleado extends EntidadMovil {

    /** Cantidad de corazones (vidas) iniciales para toda la partida. */
    public static final int SALUD_DEFAULT = 3;

    private int salud;
    private final Posicion posicionInicial;
    private boolean tieneEscudo;
    private final List<Buffo> buffosActivos;
    private Direccion ultimaDireccion = Direccion.ABAJO;

    /**
     * Crea un empleado con su posición y velocidad iniciales.
     * Asigna los 3 corazones iniciales por defecto, guarda su coordenada de origen,
     * inicia sin escudo y prepara la lista de buffos.
     *
     * @param posicion  Posición inicial del jugador.
     * @param velocidad Velocidad de movimiento del jugador.
     */
    public Empleado(Posicion posicion, int velocidad) {
        super(posicion, velocidad);
        this.salud = SALUD_DEFAULT;
        this.posicionInicial = new Posicion(posicion);
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

        Posicion posTentativa = new Posicion(this.posicion);
        posTentativa.trasladar(desplazamientoX, desplazamientoY);

        boolean hayColision = false;
        if (muros != null) {
            for (Muro muro : muros) {
                if (muro.verificarColision(posTentativa)) {
                    hayColision = true;
                    break;
                }
            }
        }

        if (!hayColision) {
            this.posicion.copiarDesde(posTentativa);
        }
    }

    /**
     * Devuelve al empleado a la casilla de inicio del nivel, detiene su desplazamiento
     * y revierte los buffos temporales activos para reaparecer en estado limpio.
     * Se invoca cuando el jefe lo impacta sin escudo pero aún conserva corazones.
     */
    public void volverAPosicionInicial() {
        limpiarBuffosActivos();
        this.posicion.copiarDesde(this.posicionInicial);
        this.direccionActual = Direccion.NINGUNA;
        this.ultimaDireccion = Direccion.ABAJO;
    }

    /**
     * Revierte los efectos y limpia todos los buffos activos del empleado al volver al inicio.
     */
    private void limpiarBuffosActivos() {
        for (Buffo buffo : this.buffosActivos) {
            buffo.remover(this);
        }
        this.buffosActivos.clear();
        this.tieneEscudo = false;
    }

    /**
     * Procesa el impacto de una pala o proyectil sobre el empleado.
     * Si cuenta con un escudo de Aguinaldo activo, lo rompe y el empleado conserva sus corazones y posición.
     * Si no tiene escudo, pierde 1 corazón (vida).
     *
     * @return {@code true} si el impacto fue absorbido por el escudo (no pierde corazón ni retrocede);
     *         {@code false} si el impacto afectó al empleado descontándole un corazón.
     */
    public boolean recibirImpacto() {
        if (this.tieneEscudo) {
            this.tieneEscudo = false;
            for (Buffo buffo : this.buffosActivos) {
                if (buffo instanceof Aguinaldo) {
                    ((Aguinaldo) buffo).romper();
                }
            }
            this.buffosActivos.removeIf(Buffo::estaExpirado);
            return true;
        }

        this.salud = Math.max(0, this.salud - 1);
        return false;
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
     * Indica si el empleado aún conserva al menos un corazón para continuar la partida.
     *
     * @return {@code true} si {@code salud > 0}; {@code false} si perdió sus 3 corazones.
     */
    public boolean estaVivo() {
        return this.salud > 0;
    }

    /**
     * Retorna la última orientación activa del empleado para seleccionar el sprite correcto
     * aun cuando se encuentre detenido.
     *
     * @return Dirección visual actual del sprite del empleado.
     */
    public Direccion getDireccionVisual() {
        if (direccionActual != Direccion.NINGUNA) {
            ultimaDireccion = direccionActual;
        }
        return ultimaDireccion;
    }

    /**
     * Consulta si el empleado cuenta con un escudo protector activo.
     *
     * @return {@code true} si el escudo está activo; {@code false} en caso contrario.
     */
    public boolean tieneEscudo() {
        return tieneEscudo;
    }

    /**
     * Modifica el estado del escudo protector.
     *
     * @param tieneEscudo Nuevo estado del escudo.
     */
    public void setTieneEscudo(boolean tieneEscudo) {
        this.tieneEscudo = tieneEscudo;
    }

    /**
     * Obtiene la cantidad actual de corazones del empleado.
     *
     * @return Cantidad de corazones restantes entre 0 y {@link #SALUD_DEFAULT}.
     */
    public int getSalud() {
        return salud;
    }

    /**
     * Ajusta la cantidad de corazones acotándola al rango válido {@code [0, SALUD_DEFAULT]}.
     *
     * @param salud Nueva cantidad de corazones.
     */
    public void setSalud(int salud) {
        this.salud = Math.max(0, Math.min(SALUD_DEFAULT, salud));
    }

    /**
     * Devuelve una vista de solo lectura de los buffos activos para proteger el encapsulamiento.
     *
     * @return Lista inmodificable de instancias de {@link Buffo} activas.
     */
    public List<Buffo> getBuffosActivos() {
        return Collections.unmodifiableList(buffosActivos);
    }
}
