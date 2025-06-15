package mp.tfg.mycheckpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad que representa un token de verificación de correo electrónico.
 * Se genera cuando un usuario se registra y se envía por correo para confirmar su dirección.
 */
@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    /**
     * Duración de la validez del token en minutos (ej. 24 horas = 1440 minutos).
     */
    private static final int EXPIRATION_MINUTES = 60 * 24;

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
     * El usuario al que pertenece este token de verificación.
     * La relación es uno a uno, y el usuario se carga de forma temprana (EAGER).
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFICATION_TOKEN_USER"))
    private User user;

    /**
     * Fecha y hora en que el token expirará.
     */
    @Column(nullable = false)
    private OffsetDateTime expiryDate;

    /**
     * Indica si el token ya ha sido utilizado para verificar una cuenta.
     * Por defecto es {@code false}.
     */
    @Column(nullable = false)
    private boolean used = false;

    /**
     * Constructor para crear un nuevo token de verificación para un usuario específico.
     * Genera un token UUID aleatorio y calcula la fecha de expiración.
     * @param user El {@link User} para el cual se genera el token.
     */
    public VerificationToken(User user) {
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