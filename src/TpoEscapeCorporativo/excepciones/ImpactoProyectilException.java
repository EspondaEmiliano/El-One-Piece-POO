package TpoEscapeCorporativo.excepciones;

/**
 * Excepción lanzada cuando un proyectil balístico arrojado por el Jefe
 * colisiona e impacta directamente contra el empleado.
 */
public class ImpactoProyectilException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo del impacto.
     *
     * @param message Descripción del evento de impacto.
     */
    public ImpactoProyectilException(String message) {
        super(message);
    }
}
