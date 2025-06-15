package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando un usuario autenticado intenta realizar una operación
 * para la cual no tiene los permisos necesarios, o sobre un recurso que no le pertenece.
 * Corresponde a un estado HTTP 403 Forbidden.
 * Distinta de un error de autenticación (401 Unauthorized), que implica que el usuario no está (o no está correctamente) autenticado.
 */
public class UnauthorizedOperationException extends MyCheckPointException {

    /**
     * Estado HTTP predeterminado para esta excepción (403 Forbidden).
     */
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    /**
     * Constructor para UnauthorizedOperationException.
     * @param message El mensaje detallado del error, explicando por qué la operación no está autorizada.
     */
    public UnauthorizedOperationException(String message) {
        super(message, STATUS);
    }

    /**
     * Constructor para UnauthorizedOperationException con una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     */
    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }
}
