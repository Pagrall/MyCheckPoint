package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus;


/**
 * Excepción lanzada cuando no se puede encontrar un recurso solicitado.
 * Corresponde a un estado HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends MyCheckPointException {

    /**
     * Estado HTTP predeterminado para esta excepción (404 Not Found).
     */
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    /**
     * Constructor para ResourceNotFoundException.
     * @param message El mensaje detallado del error, usualmente indicando qué recurso no se encontró.
     */
    public ResourceNotFoundException(String message) {
        super(message, STATUS);
    }

    /**
     * Constructor para ResourceNotFoundException con una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }
}