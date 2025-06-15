package mp.tfg.mycheckpoint.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz para el servicio de almacenamiento de archivos.
 * Define operaciones para guardar y eliminar archivos, con un enfoque actual
 * en las imágenes de perfil de los usuarios.
 */
public interface FileStorageService {

    /**
     * Guarda el archivo de imagen de perfil proporcionado.
     * El nombre del archivo guardado se basa en el {@code userPublicId} para asegurar unicidad
     * y facilitar la recuperación y gestión. Se realizan validaciones de tamaño y tipo de archivo.
     *
     * @param file El archivo {@link MultipartFile} subido por el usuario.
     * @param userPublicId El UUID público del usuario, usado para nombrar el archivo.
     * @return El nombre del archivo generado y almacenado (o ruta relativa) que se
     * guardará en la base de datos para el perfil del usuario.
     * @throws mp.tfg.mycheckpoint.exception.FileStorageException Si ocurre un error durante el guardado,
     * como un formato de archivo no permitido, tamaño excedido, o error de I/O.
     */
    String storeProfilePicture(MultipartFile file, String userPublicId);

    /**
     * Elimina una foto de perfil existente del sistema de archivos.
     * Si el archivo no existe o el nombre es inválido, la operación
     * podría no tener efecto o registrar una advertencia, sin lanzar una excepción crítica.
     *
     * @param fileName El nombre del archivo de la foto de perfil a eliminar.
     */
    void deleteProfilePicture(String fileName);

}