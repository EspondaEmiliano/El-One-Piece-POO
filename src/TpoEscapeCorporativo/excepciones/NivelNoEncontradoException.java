package TpoEscapeCorporativo.excepciones;

/**
 * Excepción checked que se lanza cuando un archivo de mapa de nivel
 * no existe o se encuentra corrupto/ilegible.
 */
public class NivelNoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción indicando el detalle del nivel faltante o inválido.
     *
     * @param message Descripción del error de carga de nivel.
     */
    public NivelNoEncontradoException(String message) {
        super(message);
    }

    /**
     * Construye la excepción encadenando la causa original de E/S.
     *
     * @param message Descripción del error de carga de nivel.
     * @param cause   Excepción subyacente que originó el fallo.
     */
    public NivelNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
