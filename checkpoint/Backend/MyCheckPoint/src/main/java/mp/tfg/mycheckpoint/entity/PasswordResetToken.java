package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad que representa un token para el restablecimiento de contraseña.
 * Se genera cuando un usuario solicita restablecer su contraseña y se envía por correo.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    /**
     * Duración de la validez del token en minutos (ej. 1 hora = 60 minutos).
     */
    public static final int EXPIRATION_MINUTES = 60;

    /**
     * Identificador interno único del token (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El valor del token, una cadena UUID única.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * El usuario al que pertenece este token de restablecimiento de contraseña.
     * La relación es uno a uno, y el usuario se carga de forma temprana (EAGER).
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_PASSWORD_RESET_TOKEN_USER"))
    private User user;

    /**
     * Fecha y hora en que el token expirará.
     */
    @Column(nullable = false)
    private OffsetDateTime expiryDate;

    /**
     * Indica si el token ya ha sido utilizado para restablecer una contraseña.
     * Por defecto es {@code false}.
     */
    @Column(nullable = false)
    private boolean used = false;

    /**
     * Constructor para crear un nuevo token de restablecimiento de contraseña para un usuario específico.
     * Genera un token UUID aleatorio y calcula la fecha de expiración.
     * @param user El {@link User} para el cual se genera el token.
     */
    public PasswordResetToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = OffsetDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }

    /**
     * Comprueba si el token ha expirado comparando la fecha de expiración con la actual.
     * @return {@code true} si el token ha expirado, {@code false} en caso contrario.
     */
    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(this.expiryDate);
    }
}
