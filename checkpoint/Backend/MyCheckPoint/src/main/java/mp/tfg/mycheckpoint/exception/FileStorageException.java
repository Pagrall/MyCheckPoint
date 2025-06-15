package mp.tfg.mycheckpoint.exception;

import org.springframework.http.HttpStatus; // Necesario para HttpStatus

/**
 * Excepción personalizada para errores relacionados con el almacenamiento de archivos.
 * Puede ser lanzada por diversas razones, como problemas de permisos,
 * tamaño de archivo excedido, formato no permitido, o errores de I/O.
 * El estado HTTP puede variar dependiendo de la naturaleza específica del error.
 */
public class FileStorageException extends MyCheckPointException {

    /**
     * Constructor que utiliza un estado HTTP predeterminado (BAD_REQUEST)
     * si no se especifica uno más concreto.
     * @param message El mensaje detallado del error de almacenamiento.
     */
    public FileStorageException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Constructor que permite especificar un HttpStatus particular para el error de almacenamiento.
     * @param message El mensaje detallado del error.
     * @param httpStatus El {@link HttpStatus} específico para este error (ej. PAYLOAD_TOO_LARGE).
     */
    public FileStorageException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    /**
     * Constructor que utiliza un estado HTTP predeterminado (BAD_REQUEST) y acepta una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }

    /**
     * Constructor que permite especificar un HttpStatus y una causa raíz.
     * @param message El mensaje detallado del error.
     * @param cause La causa raíz de la excepción.
     * @param httpStatus El {@link HttpStatus} específico para este error.
     */
    public FileStorageException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause, httpStatus);
    }
}