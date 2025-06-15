package mp.tfg.mycheckpoint.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración de Spring MVC para la aplicación.
 * Esta clase implementa {@link WebMvcConfigurer} para personalizar la configuración
 * de Spring MVC, como la gestión de recursos estáticos, específicamente para
 * servir las imágenes de perfil de los usuarios.
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Logger para esta clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    /**
     * Patrón de URL bajo el cual las imágenes de perfil estarán accesibles.
     * Este valor se inyecta desde la propiedad de la aplicación "file.resource-handler.profile-pictures".
     * Ejemplo: "/profile-pictures/**"
     */
    @Value("${file.resource-handler.profile-pictures}")
    private String profilePicturesResourceHandler; // ej: /profile-pictures/**

    /**
     * Ubicación en el sistema de archivos donde se almacenan las imágenes de perfil.
     * Este valor se inyecta desde la propiedad de la aplicación "file.resource-locations.profile-pictures".
     * Ejemplo: "file:./uploads/profile-pictures/"
     */
    @Value("${file.resource-locations.profile-pictures}")
    private String profilePicturesResourceLocations; // ej: file:./uploads/profile-pictures/

    /**
     * Registra los manejadores de recursos estáticos en Spring MVC.
     * Este método se invoca durante la configuración de Spring MVC para permitir
     * la personalización de cómo se sirven los recursos estáticos.
     * Aquí se configura el manejador para las imágenes de perfil.
     *
     * @param registry El registro donde se añaden los manejadores de recursos.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir fotos de perfil
        exposeDirectory(profilePicturesResourceHandler, profilePicturesResourceLocations, registry);
        // Puedes añadir más handlers para otros tipos de archivos estáticos si es necesario
    }

    /**
     * Método de utilidad para configurar un manejador de recursos que expone un directorio
     * del sistema de archivos a través de un patrón de URL HTTP.
     *
     * Si el {@code pathPattern} o {@code fileSystemPath} son nulos,
     * se registrará una advertencia y no se configurará el manejador.
     *
     * @param pathPattern El patrón de URL HTTP para el cual se servirán los recursos (ej. "/images/**").
     * @param fileSystemPath La ruta en el sistema de archivos donde residen los recursos
     * (ej. "file:./uploads/images/"). El prefijo "file:" es opcional aquí,
     * ya que se maneja internamente.
     * @param registry El {@link ResourceHandlerRegistry} al que se añadirá el nuevo manejador.
     */
    private void exposeDirectory(String pathPattern, String fileSystemPath, ResourceHandlerRegistry registry) {
        if (pathPattern == null || fileSystemPath == null) {
            logger.warn("La configuración para el resource handler (pathPattern o fileSystemPath) es nula. No se configurará.");
            return;
        }

        Path uploadDir = Paths.get(fileSystemPath.replace("file:", ""));
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        logger.info("Configurando resource handler: {} -> {}", pathPattern, "file:" + uploadPath + "/");

        registry.addResourceHandler(pathPattern)
                .addResourceLocations("file:" + uploadPath + "/");
    }
}