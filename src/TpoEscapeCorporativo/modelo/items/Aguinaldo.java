package TpoEscapeCorporativo.modelo.items;

import  TpoEscapeCorporativo.interfaces.Buffo;
import  TpoEscapeCorporativo.modelo.entidades.Empleado;

/**
 * Representa un potenciador instantáneo de Aguinaldo.
 * Recupera salud al empleado (hasta su máximo de 100) y otorga puntos de bonificación.
 */
public class Aguinaldo implements  Buffo {

    private static final int SALUD_RECUPERADA_DEFAULT = 25; // +25 HP
    private static final int PUNTOS_BONUS_DEFAULT = 500;    // +500 puntos

    private final int saludRecuperada;
    private final int  puntosBonus;
    private boolean yaAplicado;

    /**
     * Constructor por defecto: recupera 25 de salud y otorga 500 puntos bonus.
     */
    public Aguinaldo() {
        this(SALUD_RECUPERADA_DEFAULT, PUNTOS_BONUS_DEFAULT);
    }

    /**
     * Constructor configurable.
     *
     * @param saludRecuperada   Puntos de vida que restaura al empleado.
     * @param puntosBonus       Puntos que suma a la puntuación del juego.
     */
    public Aguinaldo(int saludRecuperada, int puntosBonus) {
        this.saludRecuperada = saludRecuperada;
        this.puntosBonus = puntosBonus;
        this.yaAplicado = false;
    }

    @Override
    public void aplicar(Empleado empleado) {
        if (!yaAplicado && empleado != null) {
            // Recupera vida hasta el límite máximo de salud (100)
            int saludNueva = Math.min(Empleado.SALUD_DEFAULT, empleado.getSalud() + saludRecuperada);
            empleado.setSalud(saludNueva);
            this.yaAplicado = true;
        }
    }

    @Override
    public void remover(Empleado empleado) {
        // Al ser un efecto de curación/consumible instantáneo, no requiere revertirse al expirar
    }

    @Override
    public void actualizar(long deltaTiempo) {
        // No requiere conteo de tiempo porque se aplica de forma inmediata
    }

    @Override
    public boolean estaExpirado() {
        // Una vez aplicado se considera finalizado para liberarlo de la lista
        return yaAplicado;
    }

    @Override
    public String getNombre() {
        return "Aguinaldo";
    }

    public int getSaludRecuperada() {
        return saludRecuperada;
    }

    public int getPuntosBonus() {
        return puntosBonus;
    }
}
