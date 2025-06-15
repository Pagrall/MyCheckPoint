package mp.tfg.mycheckpoint.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI (Swagger) para la documentación de la API.
 * Define la información general de la API, esquemas de seguridad JWT,
 * y esquemas globales para respuestas de error comunes.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define y personaliza el bean {@link OpenAPI} que Springdoc utiliza para generar la documentación.
     *
     * @param appName El nombre de la aplicación, inyectado desde las propiedades.
     * @param appVersion La versión de la API, inyectada desde las propiedades.
     * @param appDescription La descripción de la API, inyectada desde las propiedades.
     * @return Una instancia configurada de {@link OpenAPI}.
     */
    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name:MyCheckPoint API}") String appName,
                                 @Value("${app.api.version:v1}") String appVersion,
                                 @Value("${app.api.description:API para la aplicación MyCheckPoint}") String appDescription) {
        final String securitySchemeName = "bearerAuth"; // Nombre del esquema de seguridad para JWT

        return new OpenAPI()
                .info(new Info().title(appName)
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("http://swagger.io/terms/") // URL de términos de servicio (ejemplo)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))) // Licencia (ejemplo)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Aplica el esquema de seguridad globalmente
                .components(
                        new Components()
                                // Define el esquema de seguridad para autenticación Bearer JWT
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                                // Define esquemas globales para respuestas de error reutilizables
                                .addSchemas("ErrorResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("error", new StringSchema().description("Mensaje detallado del error.").example("El recurso solicitado no fue encontrado."))
                                        .description("Respuesta de error genérica para errores 4xx y 5xx.")
                                )
                                .addSchemas("ValidationErrorResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("errors", new ArraySchema().items(new StringSchema()).description("Lista de mensajes de error de validación.").example("[\"El nombre de usuario es obligatorio\", \"El email debe ser válido\"]"))
                                        .description("Respuesta específica para errores de validación de campos (HTTP 400).")
                                )
                                .addSchemas("DuplicatedResourceResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("message", new StringSchema().description("Mensaje de error indicando que el recurso ya existe.").example("El usuario ya existe."))
                                        .description("Respuesta específica para errores de recursos duplicados (HTTP 409).")
                                )
                                .addSchemas("UnauthorizedResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("message", new StringSchema().description("Mensaje de error indicando que la autenticación falló.").example("No autorizado. Token inválido o expirado. O contraseña incorrecta"))
                                        .description("Respuesta específica para errores de autorización (HTTP 401).")
                                )
                                .addSchemas("ValidationPasswordErrorResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("errors", new ArraySchema().items(new StringSchema()).description("Lista de mensajes de error de validación.").example("[\"La contraseña no debe ser igual a la anterior\", \"La nueva contraseña debe tener entre 8 y 100 caracteres\"]"))
                                        .description("Respuesta específica para errores de validación de campos relacionados con contraseñas (HTTP 400).")
                                )
                                .addSchemas("RequiredErrorResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("errors", new ArraySchema().items(new StringSchema()).description("Mensaje de error genérico para campos obligatorios.").example("El recurso es obligatorio."))
                                        .description("Respuesta específica para errores de validación de campos obligatorios (HTTP 400).")
                                )
                                .addSchemas("TooLargeResponse", new ObjectSchema()
                                        .type("object")
                                        .addProperty("errors", new ArraySchema().items(new StringSchema()).description("Mensaje de error para archivos que exceden el tamaño permitido.").example("El recurso es demasiado grande. El tamaño máximo permitido es de 500KB."))
                                        .description("Respuesta específica para errores de tamaño de payload excedido (HTTP 413).")
                                )
                );
    }
}