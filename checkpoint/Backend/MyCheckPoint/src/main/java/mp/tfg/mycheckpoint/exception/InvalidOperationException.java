package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando se intenta realizar una operación que no es válida
 * bajo las circunstancias actuales o para el recurso especificado.
 * Por ejemplo, intentar eliminar una entidad protegida o realizar una acción
 * en un estado incorrecto del objeto.
 * Corresponde a un estado HTTP 400 Bad Request.
 */
public class InvalidOperationException extends MyCheckPointException {

    /**
     * Estado HTTP predeterminado para esta excepción (400 Bad Request).
     */
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    /**
     * Constructor para InvalidOperationException.
     * @param message El mensaje detallado del error, explicando por qué la operación es inválida.
     */
    public InvalidOperationException(String message) {
        super(message, STATUS);
    }

    /**
     * Constructor para InvalidOperationException con una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }
}