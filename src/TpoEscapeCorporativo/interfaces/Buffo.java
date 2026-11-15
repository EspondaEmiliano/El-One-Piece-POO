package TpoEscapeCorporativo.interfaces;

import TpoEscapeCorporativo.modelo.entidades.Empleado;

/**
 * Interfaz que define el contrato para todos los potenciadores (buffos) e ítems
 * consumibles del juego que modifican el estado del empleado.
 */
public interface Buffo {

    /**
     * Aplica el efecto del potenciador sobre el empleado (ej. aumentar velocidad, curar vida).
     *
     * @param empleado Instancia del jugador que recibe el efecto.
     */
    void aplicar(Empleado empleado);

    /**
     * Revierte el efecto una vez que expira (ej. restaurar velocidad normal).
     *
     * @param empleado Instancia del jugador sobre el que se remueve el efecto.
     */
    void remover(Empleado empleado);

    /**
     * Actualiza el tiempo restante del buffo en cada ciclo (frame) del Game Loop.
     *
     * @param deltaTiempo Milisegundos trascurridos (por ej. 16 ms a 60 FPS).
     */
    void actualizar(long deltaTiempo);

    /**
     * Indica si el efecto potenciador ha finalizado su duración.
     *
     * @return true si ya expiró y debe ser removido; false si sigue activo.
     */
    boolean estaExpirado();

    /**
     * Retorna el nombre identificatorio del potenciador (útil para la UI y la vista).
     *
     * @return Nombre del buffo (ej. "Cafe", "Aguinaldo").
     */
    String getNombre();

    /**
     * Indica si el potenciador debe reaparecer en el mapa cuando el empleado
     * pierde un corazón y regresa a la posición inicial del nivel.
     *
     * @return {@code true} si el ítem se regenera al reaparecer (ej. Café, Aguinaldo);
     *         {@code false} si es de recolección única por nivel (ej. Corazón).
     */
    boolean esRegenerable();

    /**
     * Crea una nueva instancia limpia del potenciador conservando su configuración inicial.
     *
     * @return Nueva instancia lista para ser colocada en el mapa sin estado residual.
     */
    Buffo crearCopia();
}
