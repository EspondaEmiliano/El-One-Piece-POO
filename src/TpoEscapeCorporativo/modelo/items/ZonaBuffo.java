package TpoEscapeCorporativo.modelo.items;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.interfaces.IColisionable;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.entidades.EntidadEstatica;

/**
 * Representa un ítem o potenciador situado en el mapa de juego.
 * Gestiona su posición en el tablero, la detección de colisiones y su estado de recolección.
 */
public class ZonaBuffo extends EntidadEstatica implements IColisionable {

    private Buffo buffo;
    private boolean recolectado;

    /**
     * Crea una zona de buffo en una posición determinada con un potenciador asociado.
     *
     * @param posicion Coordenadas y límites de la hitbox en el mapa.
     * @param buffo    Potenciador que otorgará al ser recolectado (Cafe, Aguinaldo, Corazon).
     * @throws IllegalArgumentException Si {@code buffo} es nulo.
     */
    public ZonaBuffo(Posicion posicion, Buffo buffo) {
        super(posicion);
        if (buffo == null) {
            throw new IllegalArgumentException("La zona de buffo debe contener un buffo no nulo.");
        }
        this.buffo = buffo;
        this.recolectado = false;
    }

    /**
     * Verifica si otra entidad (como el empleado) colisiona con el área del ítem.
     * Si ya fue recolectado, ignora cualquier colisión.
     *
     * @param otraPosicion Posición de la entidad a verificar.
     * @return {@code true} si colisionan y el ítem está disponible; {@code false} en caso contrario.
     */
    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        if (recolectado) {
            return false;
        }
        return this.posicion.seSuperpone(otraPosicion, true);
    }

    /**
     * Marca el ítem como recolectado para que no vuelva a activarse ni a dibujarse,
     * y entrega la instancia del potenciador.
     *
     * @return El potenciador asociado listo para aplicarse al empleado.
     */
    public Buffo consumirBuffo() {
        this.recolectado = true;
        return this.buffo;
    }

    /**
     * Regenera el ítem en el mapa si ya había sido recolectado y su potenciador
     * admite reaparición (Café o Aguinaldo). Los corazones permanecen consumidos.
     */
    public void regenerarSiCorresponde() {
        if (this.recolectado && this.buffo.esRegenerable()) {
            this.buffo = this.buffo.crearCopia();
            this.recolectado = false;
        }
    }

    /**
     * Indica si el potenciador de esta casilla ya fue recolectado.
     *
     * @return {@code true} si ya fue recogido; {@code false} si sigue disponible en el mapa.
     */
    public boolean isRecolectado() {
        return recolectado;
    }

    /**
     * Retorna el potenciador contenido en esta zona.
     *
     * @return Instancia polimórfica de {@link Buffo}.
     */
    public Buffo getBuffo() {
        return buffo;
    }
}
