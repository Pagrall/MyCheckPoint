package mp.tfg.mycheckpoint.service.impl;

import mp.tfg.mycheckpoint.exception.FileStorageException;
import mp.tfg.mycheckpoint.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Implementación del servicio {@link FileStorageService} para gestionar el almacenamiento
 * de archivos, específicamente para las imágenes de perfil de los usuarios.
 * Se encarga de guardar, eliminar y validar los archivos subidos, asegurando
 * que se cumplan las restricciones de tamaño y tipo de archivo.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    /**
     * Logger para esta clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    /**
     * Ubicación base en el sistema de archivos donde se almacenarán las imágenes de perfil.
     * Esta ruta se normaliza a una ruta absoluta durante la inicialización.
     */
    private final Path profilePictureStorageLocation;

    /**
     * Tamaño máximo permitido para las imágenes de perfil, en bytes.
     * Este valor se calcula a partir de una cadena de configuración (ej. "500KB").
     */
    private final long maxProfilePictureSizeBytes;

    /**
     * Lista de extensiones de archivo permitidas para las imágenes de perfil (ej. ".jpeg", ".png", ".gif").
     * Las extensiones se almacenan en minúsculas para una comparación insensible a mayúsculas.
     */
    private final List<String> allowedProfilePictureExtensions = Arrays.asList(".jpeg", ".jpg", ".png", ".gif");

    /**
     * Lista de tipos MIME permitidos para una validación segura.
     * Estos corresponden a las extensiones permitidas.
     */
    private final List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

    /**
     * Constructor para FileStorageServiceImpl.
     * Inicializa la ubicación de almacenamiento para las fotos de perfil y el tamaño máximo
     * de archivo permitido, leyendo estos valores desde las propiedades de la aplicación.
     * Crea el directorio de almacenamiento si no existe al momento de la inicialización.
     *
     * @param uploadDir La ruta del directorio de subida para las fotos de perfil,
     * inyectada desde la propiedad de la aplicación "file.upload-dir.profile-pictures".
     * @param maxProfilePictureSize El tamaño máximo permitido para las fotos de perfil,
     * expresado como una cadena (ej. "500KB", "2MB"),
     * inyectado desde la propiedad "app.profile-picture.max-size".
     * @throws FileStorageException Si no se puede crear el directorio de almacenamiento
     * configurado (error a nivel de servidor).
     */
    public FileStorageServiceImpl(
            @Value("${file.upload-dir.profile-pictures}") String uploadDir,
            @Value("${app.profile-picture.max-size}") String maxProfilePictureSize) {

        this.profilePictureStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.profilePictureStorageLocation);
            logger.info("Directorio de almacenamiento para fotos de perfil inicializado en: {}", this.profilePictureStorageLocation.toString());
        } catch (Exception ex) {
            logger.error("No se pudo crear el directorio de almacenamiento para fotos de perfil: {}", this.profilePictureStorageLocation.toString(), ex);
            throw new FileStorageException("No se pudo crear el directorio donde se guardarán los archivos subidos.", ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        this.maxProfilePictureSizeBytes = parseSizeToBytes(maxProfilePictureSize);
        logger.info("Límite de tamaño máximo para fotos de perfil configurado a: {} bytes ({}).", this.maxProfilePictureSizeBytes, maxProfilePictureSize);
    }

    /**
     * Convierte una cadena que representa un tamaño de archivo (ej. "500KB", "2MB", "1GB")
     * a su equivalente en bytes.
     * Soporta las unidades KB, MB, GB y B (o sin sufijo para bytes).
     * Si la cadena de tamaño es nula, vacía o tiene un formato inválido,
     * se utiliza un valor por defecto de 1MB y se registra una advertencia o error.
     *
     * @param sizeString La cadena de tamaño a parsear.
     * @return El tamaño equivalente en bytes.
     */
    private long parseSizeToBytes(String sizeString) {
        if (sizeString == null || sizeString.isBlank()) {
            long defaultSizeBytes = 1024L * 1024L; // 1MB por defecto
            logger.warn("La propiedad 'app.profile-picture.max-size' no está configurada o está vacía. Usando un valor por defecto de {} bytes (1MB).", defaultSizeBytes);
            return defaultSizeBytes;
        }
        String upperSize = sizeString.toUpperCase().trim();
        long multiplier = 1L;

        try {
            if (upperSize.endsWith("KB")) {
                multiplier = 1024L;
                upperSize = upperSize.substring(0, upperSize.length() - 2).trim();
            } else if (upperSize.endsWith("MB")) {
                multiplier = 1024L * 1024L;
                upperSize = upperSize.substring(0, upperSize.length() - 2).trim();
            } else if (upperSize.endsWith("GB")) {
                multiplier = 1024L * 1024L * 1024L;
                upperSize = upperSize.substring(0, upperSize.length() - 2).trim();
            } else if (upperSize.endsWith("B")) {
                if (!upperSize.matches("\\d+")) {
                    upperSize = upperSize.substring(0, upperSize.length() - 1).trim();
                }
            }

            if (upperSize.matches("\\d+")) {
                return Long.parseLong(upperSize) * multiplier;
            } else {
                throw new NumberFormatException("Formato numérico inválido en la cadena de tamaño después de procesar unidades.");
            }

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            long fallbackSizeBytes = 1024L * 1024L;
            logger.error("Valor de tamaño de archivo inválido en 'app.profile-picture.max-size': '{}'. Usando valor por defecto de {} bytes (1MB). Error: {}", sizeString, fallbackSizeBytes, e.getMessage());
            return fallbackSizeBytes;
        }
    }

    /**
     * Almacena la foto de perfil de un usuario tras una serie de validaciones de seguridad y formato.
     *
     * <p>El proceso de validación es el siguiente:</p>
     * <ol>
     * <li><b>Validación Básica:</b> Comprueba que el archivo no sea nulo ni esté vacío.</li>
     * <li><b>Tamaño del Archivo:</b> Verifica que el tamaño del archivo no exceda el límite máximo configurado.</li>
     * <li><b>Extensión del Archivo:</b> Realiza una comprobación inicial de que la extensión del archivo (ej. ".png")
     * esté en la lista de extensiones permitidas.</li>
     * <li><b>Validación de Contenido (MIME Type):</b> La validación más importante. Inspecciona los primeros bytes del
     * archivo para determinar su tipo MIME real. Esto previene que un archivo de un tipo no permitido
     * (como un script) sea subido simplemente renombrando su extensión.</li>
     * </ol>
     *
     * <p>Si todas las validaciones son exitosas, el archivo se guarda en el directorio de almacenamiento designado.
     * El nombre del archivo final se construye usando el ID público del usuario para garantizar unicidad y
     * fácil recuperación. Si ya existe una foto de perfil para ese usuario, será reemplazada.</p>
     *
     * @param file El archivo {@link MultipartFile} que representa la imagen de perfil subida por el cliente.
     * @param userPublicId El ID público (normalmente un UUID en formato String) del usuario. Se utiliza para
     * nombrar el archivo guardado (ej. "uuid_del_usuario.png").
     * @return El nombre del archivo generado y almacenado en el sistema.
     * @throws FileStorageException Si ocurre un error durante el proceso de validación o almacenamiento.
     * La excepción contendrá un {@link HttpStatus} apropiado:
     * <ul>
     * <li>{@code BAD_REQUEST}: Si el archivo es nulo, vacío o se detecta un intento de Path Traversal.</li>
     * <li>{@code PAYLOAD_TOO_LARGE}: Si el archivo excede el tamaño máximo permitido.</li>
     * <li>{@code UNSUPPORTED_MEDIA_TYPE}: Si la extensión o el tipo de contenido (MIME type) no son válidos.</li>
     * <li>{@code INTERNAL_SERVER_ERROR}: Si ocurre un error de lectura o escritura en el servidor.</li>
     * </ul>
     */
    @Override
    public String storeProfilePicture(MultipartFile file, String userPublicId) {
        // Validaciones básicas
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("No se puede guardar un archivo vacío o nulo.", HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > this.maxProfilePictureSizeBytes) {
            String configuredMaxSizeReadable = convertBytesToReadableSize(this.maxProfilePictureSizeBytes);
            String actualFileSizeReadable = convertBytesToReadableSize(file.getSize());
            throw new FileStorageException("El archivo excede el tamaño máximo permitido (" +
                    configuredMaxSizeReadable + ").", HttpStatus.PAYLOAD_TOO_LARGE);
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = getFileExtension(originalFilename);

        // Validación de extensión
        if (fileExtension.isBlank() || !this.allowedProfilePictureExtensions.contains(fileExtension)) {
            throw new FileStorageException("Extensión de archivo no permitida. Permitidas: " + String.join(", ", this.allowedProfilePictureExtensions), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        // Validación de contenido
        try (InputStream inputStream = file.getInputStream()) {
            // Se inspecciona el contenido del stream para adivinar el tipo MIME
            InputStream bufferedInputStream = new BufferedInputStream(inputStream);
            String mimeType = URLConnection.guessContentTypeFromStream(bufferedInputStream);

            if (mimeType == null || !this.allowedMimeTypes.contains(mimeType.toLowerCase())) {
                logger.warn("Intento de subida de archivo con tipo de contenido no válido. Archivo: '{}', MIME detectado: '{}'", originalFilename, mimeType);
                throw new FileStorageException("El contenido del archivo no es una imagen válida. Tipos permitidos: " + String.join(", ", this.allowedMimeTypes), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            logger.info("Tipo MIME validado para '{}': {}", originalFilename, mimeType);

        } catch (IOException e) {
            logger.error("Error al leer el archivo para validar su tipo de contenido: {}", originalFilename, e);
            throw new FileStorageException("No se pudo validar el contenido del archivo.", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Almacenamiento seguro del archivo
        String newFileName = userPublicId + fileExtension;
        try {
            Path targetLocation = this.profilePictureStorageLocation.resolve(newFileName).normalize();

            if (!targetLocation.startsWith(this.profilePictureStorageLocation)) {
                throw new FileStorageException("Intento de Path Traversal detectado.", HttpStatus.BAD_REQUEST);
            }

            // Se necesita un nuevo InputStream porque el anterior fue consumido por guessContentTypeFromStream.
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            logger.info("Foto de perfil guardada: {} para el usuario {}", newFileName, userPublicId);
            return newFileName;

        } catch (IOException ex) {
            logger.error("No se pudo guardar el archivo {}. Causa: {}", newFileName, ex.getMessage(), ex);
            throw new FileStorageException("No se pudo guardar el archivo " + newFileName + ". ¡Por favor, inténtalo de nuevo!", ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extrae de forma segura la extensión de un nombre de archivo.
     *
     * <p>Este método de utilidad busca el último punto (".") en el nombre del archivo y
     * devuelve la subcadena desde ese punto hasta el final, convertida a minúsculas.
     * Si no se encuentra un punto o el nombre del archivo es nulo, devuelve una cadena vacía.</p>
     *
     * @param filename El nombre del archivo del cual se extraerá la extensión (ej. "MiFoto.JPG").
     * @return La extensión del archivo en minúsculas, incluyendo el punto (ej. ".jpg"), o una
     * cadena vacía si no tiene extensión o el nombre es nulo.
     */
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf(".");
        if (lastDot >= 0) {
            return filename.substring(lastDot).toLowerCase();
        }
        return "";
    }

    /**
     * Elimina un archivo de foto de perfil previamente almacenado.
     * Si el nombre del archivo es nulo, vacío, o si el archivo no se encuentra
     * o no es un archivo regular en la ubicación esperada, la operación se registra
     * con una advertencia pero no lanza una excepción.
     * También previene intentos de eliminación fuera del directorio de almacenamiento designado.
     *
     * @param fileName El nombre del archivo de la foto de perfil a eliminar (ej. "uuid_del_usuario.png").
     */
    @Override
    public void deleteProfilePicture(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            logger.warn("Intento de eliminar un nombre de archivo de foto de perfil nulo o vacío.");
            return;
        }
        try {
            Path filePath = this.profilePictureStorageLocation.resolve(fileName).normalize();

            if (!filePath.startsWith(this.profilePictureStorageLocation)) {
                logger.warn("Intento de eliminar un archivo fuera del directorio de almacenamiento designado: {}", filePath);
                return;
            }

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                Files.delete(filePath);
                logger.info("Foto de perfil eliminada: {}", fileName);
            } else {
                logger.warn("Se intentó eliminar la foto de perfil, pero no se encontró o no es un archivo regular: {}", fileName);
            }
        } catch (IOException ex) {
            logger.error("No se pudo eliminar la foto de perfil {}: {}", fileName, ex.getMessage());
        }
    }

    /**
     * Convierte un tamaño de archivo en bytes a una representación más legible por humanos
     * utilizando las unidades B, KB, MB, GB, TB.
     *
     * @param sizeBytes El tamaño del archivo en bytes.
     * @return Una cadena formateada que representa el tamaño (ej. "1.50 MB", "500.00 KB").
     * Si {@code sizeBytes} es cero o negativo, devuelve "0 B".
     */
    private String convertBytesToReadableSize(long sizeBytes) {
        if (sizeBytes >= (1024L * 1024L)) {
            return String.format("%.2fMB", (double) sizeBytes / (1024L * 1024L));
        } else if (sizeBytes >= 1024L) {
            return String.format("%.2fKB", (double) sizeBytes / 1024L);
        } else {
            return sizeBytes + "B";
        }
    }
}