package TpoEscapeCorporativo.modelo.items;

import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.entidades.Empleado;

/**
 * Representa un potenciador instantáneo de Corazón (Vida extra).
 * Al ser recolectado por el empleado cuando le faltan corazones, restaura
 * 1 corazón (hasta el tope máximo de 3) y suma puntos de bonificación.
 */
public class Corazon implements Buffo {

    private static final int VIDAS_RECUPERADAS_DEFAULT = 1;
    private static final int PUNTOS_BONUS_DEFAULT = 100;

    private final int vidasRecuperadas;
    private final int puntosBonus;
    private boolean yaAplicado;

    /**
     * Constructor por defecto: restaura 1 corazón y otorga 100 puntos bonus.
     */
    public Corazon() {
        this(VIDAS_RECUPERADAS_DEFAULT, PUNTOS_BONUS_DEFAULT);
    }

    /**
     * Constructor sobrecargado configurable.
     *
     * @param vidasRecuperadas Cantidad de corazones que restaura al empleado.
     * @param puntosBonus      Puntos adicionales otorgados al recolectarlo.
     */
    public Corazon(int vidasRecuperadas, int puntosBonus) {
        this.vidasRecuperadas = vidasRecuperadas;
        this.puntosBonus = puntosBonus;
        this.yaAplicado = false;
    }

    @Override
    public void aplicar(Empleado empleado) {
        if (!yaAplicado && empleado != null) {
            int nuevaSalud = Math.min(Empleado.SALUD_DEFAULT, empleado.getSalud() + this.vidasRecuperadas);
            empleado.setSalud(nuevaSalud);
            this.yaAplicado = true;
        }
    }

    @Override
    public void remover(Empleado empleado) {
        // Efecto instantáneo de restauración: no se revierte al expirar
    }

    @Override
    public void actualizar(long deltaTiempo) {
        // No requiere temporizador porque se consume de forma inmediata
    }

    @Override
    public boolean estaExpirado() {
        return yaAplicado;
    }

    @Override
    public String getNombre() {
        return "Corazon (+1 Vida)";
    }

    public int getVidasRecuperadas() {
        return vidasRecuperadas;
    }

    public int getPuntosBonus() {
        return puntosBonus;
    }
}
