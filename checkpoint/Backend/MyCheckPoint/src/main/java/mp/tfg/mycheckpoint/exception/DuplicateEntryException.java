package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus;


/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe
 * y viola una restricción de unicidad (ej. email o nombre de usuario duplicado).
 * Corresponde a un estado HTTP 409 Conflict.
 */
public class DuplicateEntryException extends MyCheckPointException {

    /**
     * Estado HTTP predeterminado para esta excepción (409 Conflict).
     */
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    /**
     * Constructor para DuplicateEntryException.
     * @param message El mensaje detallado del error, indicando qué recurso está duplicado.
     */
    public DuplicateEntryException(String message) {
        super(message, STATUS);
    }

    /**
     * Constructor para DuplicateEntryException con una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     */
    public DuplicateEntryException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }
}