package mp.tfg.mycheckpoint.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty; // Importar
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.TemaEnum;
import mp.tfg.mycheckpoint.dto.enums.VisibilidadEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO que representa la información pública y de preferencias de un usuario.
 * Se devuelve tras un registro exitoso o al solicitar datos de un usuario.
 */
@Schema(description = "DTO que representa la información pública y de preferencias de un usuario. Se devuelve tras un registro o al solicitar datos de usuario.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * Identificador público único del usuario, generado automáticamente.
     * Este campo es de solo lectura.
     */
    @Schema(description = "Identificador público único del usuario, generado automáticamente.",
            example = "123e4567-e89b-12d3-a456-426614174000", format="uuid", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("public_id")
    private UUID publicId;

    /**
     * Nombre de usuario elegido por el usuario.
     */
    @Schema(description = "Nombre de usuario elegido por el usuario.", example = "nuevoUsuario123")
    @JsonProperty("nombre_usuario")
    private String nombreUsuario;

    /**
     * Dirección de correo electrónico del usuario.
     */
    @Schema(description = "Dirección de correo electrónico del usuario.", example = "usuario@example.com", format="email")
    @JsonProperty("email")
    private String email;

    /**
     * Fecha y hora en que el usuario se registró en el sistema.
     * Formato ISO 8601. Este campo es de solo lectura.
     */
    @Schema(description = "Fecha y hora en que el usuario se registró en el sistema (formato ISO 8601).",
            example = "2024-05-25T10:15:30.123Z", format="date-time", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("fecha_registro")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaRegistro;

    /**
     * Tema de la interfaz preferido por el usuario.
     */
    @Schema(description = "Tema de la interfaz preferido por el usuario.", example = "CLARO",
            allowableValues = {"CLARO", "OSCURO"})
    @JsonProperty("tema")
    private TemaEnum tema;

    /**
     * URL relativa o absoluta de la foto de perfil del usuario.
     * Puede ser nulo si no se ha subido ninguna.
     */
    @Schema(description = "URL relativa o absoluta de la foto de perfil del usuario. Puede ser nulo si no se ha subido ninguna.",
            example = "/profile-pictures/123e4567-e89b-12d3-a456-426614174000.jpg", nullable = true)
    @JsonProperty("foto_perfil")
    private String fotoPerfil;

    /**
     * Indica si el usuario desea recibir notificaciones.
     * Por defecto es true.
     */
    @Schema(description = "Indica si el usuario desea recibir notificaciones. Por defecto es true.", example = "true")
    @JsonProperty("notificaciones")
    private Boolean notificaciones;

    /**
     * Nivel de visibilidad del perfil del usuario.
     * Por defecto es PUBLICO.
     */
    @Schema(description = "Nivel de visibilidad del perfil del usuario. Por defecto es PUBLICO.", example = "PUBLICO",
            allowableValues = {"PUBLICO", "PRIVADO", "SOLO_AMIGOS"})
    @JsonProperty("visibilidad_perfil")
    private VisibilidadEnum visibilidadPerfil;
}