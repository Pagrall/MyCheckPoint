package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando se proporciona un token (ej. de verificación, de reseteo de contraseña)
 * que es inválido, ha expirado, ya ha sido utilizado o no se encuentra.
 * Generalmente corresponde a un estado HTTP 400 Bad Request.
 */
public class InvalidTokenException extends MyCheckPointException {

    /**
     * Estado HTTP predeterminado para esta excepción (400 Bad Request).
     * Podría ser 404 Not Found si el token específicamente no se encuentra.
     */
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    /**
     * Constructor para InvalidTokenException.
     * @param message El mensaje detallado del error, indicando por qué el token es inválido.
     */
    public InvalidTokenException(String message) {
        super(message, STATUS);
    }

    /**
     * Constructor para InvalidTokenException con una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }
}
