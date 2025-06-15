package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mp.tfg.mycheckpoint.dto.enums.TemaEnum;
import mp.tfg.mycheckpoint.dto.enums.VisibilidadEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;


import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad que representa a un usuario en el sistema.
 * Contiene información de autenticación, perfil, preferencias y metadatos.
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Identificador interno único del usuario (clave primaria).
     * Autogenerado por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador público único del usuario (UUID).
     * Utilizado para referenciar al usuario en APIs públicas sin exponer el ID interno.
     * Es generado automáticamente y no puede ser actualizado.
     */
    @Column(name = "public_id", unique = true, nullable = false, updatable = false, columnDefinition = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID publicId;

    /**
     * Nombre de usuario único elegido por el usuario.
     * Longitud máxima de 100 caracteres.
     */
    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 100)
    private String nombreUsuario;

    /**
     * Dirección de correo electrónico única del usuario.
     * Utilizada para la autenticación y comunicación.
     * Longitud máxima de 255 caracteres.
     */
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    /**
     * Contraseña del usuario, almacenada de forma segura (hasheada).
     * Longitud máxima de 255 caracteres (para el hash).
     */
    @Column(name = "contraseña", nullable = false, length = 255)
    private String contraseña;

    /**
     * Indica si la dirección de correo electrónico del usuario ha sido verificada.
     * Por defecto es {@code false}.
     */
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    /**
     * Fecha y hora en que el usuario se registró en el sistema.
     * Generada automáticamente y no puede ser actualizada.
     */
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime fechaRegistro;

    /**
     * Tema de la interfaz preferido por el usuario.
     * Por defecto es {@link TemaEnum#CLARO}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tema", nullable = false, length = 50)
    private TemaEnum tema = TemaEnum.OSCURO;

    /**
     * URL o identificador de la foto de perfil del usuario.
     * Puede ser nulo si no se ha establecido una foto de perfil. Almacenado como TEXT.
     */
    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    /**
     * Indica si el usuario desea recibir notificaciones.
     * Por defecto es {@code true}.
     */
    @Column(name = "notificaciones", nullable = false)
    private Boolean notificaciones = true;

    /**
     * Nivel de visibilidad del perfil del usuario.
     * Por defecto es {@link VisibilidadEnum#PUBLICO}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibilidad_perfil", nullable = false, length = 50)
    private VisibilidadEnum visibilidadPerfil = VisibilidadEnum.PUBLICO;

    /**
     * Fecha y hora de creación del registro del usuario en la base de datos.
     * Generada automáticamente y no puede ser actualizada.
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación del registro del usuario.
     * Actualizada automáticamente en cada modificación.
     */
    @Column(name = "fecha_modificacion", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime fechaModificacion;

    /**
     * Fecha y hora en que se programó la eliminación de la cuenta del usuario.
     * Si es nulo, la cuenta no está programada para eliminación.
     * Utilizado para el proceso de borrado suave (soft delete) con período de gracia.
     */
    @Column(name = "fecha_eliminacion")
    private OffsetDateTime fechaEliminacion;
}