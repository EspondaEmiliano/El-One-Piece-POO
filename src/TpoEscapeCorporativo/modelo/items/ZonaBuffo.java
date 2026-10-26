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

    private final Buffo buffo;
    private boolean recolectado;

    /**
     * Crea una zona de buffo en una posición determinada con un potenciador asociado.
     *
     * @param posicion  Coordenadas y límites del hitbox en el mapa.
     * @param buffo     Potenciador que otorgará al ser recolectado (Cafe, Aguinaldo, etc.).
     */
    public ZonaBuffo(Posicion posicion, Buffo buffo) {
        super(posicion);
        if(buffo == null) {
            throw new IllegalArgumentException("La zona de buffo debe contener un buffo no nulo.");
        }
        this.buffo = buffo;
        this.recolectado = false;
    }

    /**
     * Verifica si otra entidad (como el empleado) colisiona con el área del ítem.
     * Si ya fue recolectado, ignora cualquier colisión.
     *
     * @param otraPosicion  Posición de la entidad a verificar.
     * @return true si colisionan y el ítem está disponible; false en caso contrario.
     */
    @Override
    public Boolean verificarColision(Posicion otraPosicion) {
        if (recolectado || otraPosicion == null) {
            return false;
        }
        return posicion.getX1() <= otraPosicion.getX2()
                && posicion.getX2() >= otraPosicion.getX1()
                && posicion.getY1() <= otraPosicion.getY2()
                && posicion.getY2() >= otraPosicion.getY1();
    }

    /**
     * Marca el ítem como recolectado para que no vuelva a activarse ni a dibujarse,
     * y entrega la instancia del potenciador.
     *
     * @return El potenciador asociado listo para aplicarse al empleado
     */
    public Buffo consumirBuffo() {
        this.recolectado = true;
        return this.buffo;
    }

    // --- Getters ---

    public boolean isRecolectado() {
        return recolectado;
    }

    public Buffo getBuffo() {
        return buffo;
    }

    public Posicion getPosicion() {
        return posicion;
    }
}
