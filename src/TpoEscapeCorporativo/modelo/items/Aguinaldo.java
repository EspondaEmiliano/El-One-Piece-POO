package TpoEscapeCorporativo.modelo.items;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.entidades.Empleado;

/**
 * Representa el potenciador de Aguinaldo.
 * Actúa como un escudo protector que permite al empleado absorber un (1)
 * impacto de proyectil sin perder corazones, además de otorgar puntos de bonificación.
 */
public class Aguinaldo implements Buffo {

    private static final int PUNTOS_BONUS_DEFAULT = 500;

    private final int puntosBonus;
    private boolean roto;

    /**
     * Constructor por defecto: otorga 500 puntos bonus y provee un escudo protector.
     */
    public Aguinaldo() {
        this(PUNTOS_BONUS_DEFAULT);
    }

    /**
     * Constructor configurable.
     *
     * @param puntosBonus Puntos que suma a la puntuación del juego al recolectarlo.
     */
    public Aguinaldo(int puntosBonus) {
        this.puntosBonus = puntosBonus;
        this.roto = false;
    }

    @Override
    public void aplicar(Empleado empleado) {
        if (empleado != null) {
            empleado.setTieneEscudo(true);
            this.roto = false;
        }
    }

    @Override
    public void remover(Empleado empleado) {
        if (empleado != null) {
            empleado.setTieneEscudo(false);
        }
    }

    @Override
    public void actualizar(long deltaTiempo) {
        // El escudo no expira por tiempo; permanece activo hasta que una pala impacta al empleado.
    }

    @Override
    public boolean estaExpirado() {
        return roto;
    }

    /**
     * Quiebra el escudo al absorber el impacto de un proyectil.
     */
    public void romper() {
        this.roto = true;
    }

    @Override
    public String getNombre() {
        return "Escudo (Aguinaldo)";
    }

    @Override
    public int getPuntosBonus() {
        return puntosBonus;
    }

    @Override
    public boolean esRegenerable() {
        return true;
    }

    @Override
    public Buffo crearCopia() {
        return new Aguinaldo(this.puntosBonus);
    }
}
