package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus;

/**
 * Clase base abstracta para todas las excepciones personalizadas dentro de la aplicación MyCheckPoint.
 * Permite asociar un mensaje de error y un estado HTTP a cada excepción específica.
 */
public abstract class MyCheckPointException extends RuntimeException {

    /**
     * El estado HTTP que se debe devolver cuando esta excepción es lanzada.
     */
    private final HttpStatus httpStatus;

    /**
     * Constructor para MyCheckPointException.
     * @param message El mensaje detallado del error.
     * @param httpStatus El {@link HttpStatus} asociado con esta excepción.
     */
    public MyCheckPointException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Constructor para MyCheckPointException con una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     * @param httpStatus El {@link HttpStatus} asociado con esta excepción.
     */
    public MyCheckPointException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Obtiene el estado HTTP asociado con esta excepción.
     * @return El {@link HttpStatus} correspondiente.
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}