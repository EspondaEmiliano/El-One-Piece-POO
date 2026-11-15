package TpoEscapeCorporativo.excepciones;

/**
 * Excepción unchecked lanzada cuando un proyectil balístico arrojado por el Jefe
 * colisiona e impacta directamente contra el empleado sin escudo activo.
 */
public class ImpactoProyectilException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción con un mensaje descriptivo del impacto.
     *
     * @param message Descripción del evento de impacto.
     */
    public ImpactoProyectilException(String message) {
        super(message);
    }
}
