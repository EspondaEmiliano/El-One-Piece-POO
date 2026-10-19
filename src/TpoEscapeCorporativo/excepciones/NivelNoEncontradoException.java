package TpoEscapeCorporativo.excepciones;

/**
 * Excepción checked que se lanza cuando un archivo de mapa de nivel
 * no existe o se encuentra corrupto/ilegible.
 */
public class NivelNoEncontradoException extends Exception {

    public NivelNoEncontradoException(String message) {
        super(message);
    }

    public NivelNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
