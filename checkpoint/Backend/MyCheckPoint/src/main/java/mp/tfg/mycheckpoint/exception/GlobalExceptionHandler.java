package mp.tfg.mycheckpoint.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura excepciones específicas y genéricas, formateando respuestas HTTP adecuadas.
 * Utiliza {@link ControllerAdvice} para aplicar este manejo a todos los controladores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja todas las excepciones que extienden de {@link MyCheckPointException}.
     * Extrae el mensaje y el estado HTTP de la excepción para construir la respuesta.
     * @param ex La instancia de {@link MyCheckPointException} capturada.
     * @return Un {@link ResponseEntity} con el mensaje de error y el estado HTTP correspondiente.
     */
    @ExceptionHandler(MyCheckPointException.class)
    public ResponseEntity<Map<String, String>> handleMyCheckPointException(MyCheckPointException ex) {
        logger.warn("MyCheckPointException capturada: {} - HTTP Status: {} - Mensaje: {}",
                ex.getClass().getSimpleName(), ex.getHttpStatus(), ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Maneja errores de validación de argumentos de método (ej. anotaciones de validación en DTOs).
     * Agrupa todos los mensajes de error de validación en una lista.
     * @param ex La instancia de {@link MethodArgumentNotValidException} capturada.
     * @return Un {@link ResponseEntity} con una lista de errores y estado HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        logger.warn("Error de validación: {}", errors);
        return new ResponseEntity<>(getErrorsMap(errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * Método helper para formatear la respuesta de errores de validación.
     * @param errors Lista de mensajes de error.
     * @return Un mapa que contiene la lista de errores bajo la clave "errors".
     */
    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

    /**
     * Maneja {@link BadCredentialsException} de Spring Security, típicamente lanzada durante
     * un intento de login fallido debido a credenciales incorrectas.
     * @param ex La instancia de {@link BadCredentialsException} capturada.
     * @return Un {@link ResponseEntity} con un mensaje genérico y estado HTTP 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Error de credenciales: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Credenciales inválidas.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja {@link AccessDeniedException} de Spring Security, lanzada cuando un usuario
     * autenticado intenta acceder a un recurso para el cual no tiene los permisos necesarios.
     * @param ex La instancia de {@link AccessDeniedException} capturada.
     * @return Un {@link ResponseEntity} con un mensaje de acceso denegado y estado HTTP 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "No tienes permiso para realizar esta acción.");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


    /**
     * Maneja {@link MaxUploadSizeExceededException}, lanzada cuando un archivo subido
     * excede el tamaño máximo permitido configurado en Spring.
     * @param ex La instancia de {@link MaxUploadSizeExceededException} capturada.
     * @return Un {@link ResponseEntity} con un mensaje de error y estado HTTP 413 Payload Too Large.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        logger.warn("Intento de subir un archivo que excede el límite máximo global: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        // Este mensaje podría ser más específico si se conoce el límite que se excedió (el global de Spring vs. el de la app)
        errorResponse.put("error", "El archivo es demasiado grande. El tamaño máximo de subida está configurado en el servidor.");
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Manejador genérico para cualquier otra excepción no controlada explícitamente.
     * Este debe ser el último manejador en la clase.
     * Loguea la excepción completa y devuelve un error genérico al cliente.
     * @param ex La instancia de {@link Exception} capturada.
     * @return Un {@link ResponseEntity} con un mensaje de error interno y estado HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("Excepción no controlada: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Ocurrió un error interno inesperado en el servidor.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}