package mp.tfg.mycheckpoint.event;

import lombok.Getter;
import mp.tfg.mycheckpoint.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se publica cuando se completa el proceso de registro de un nuevo usuario.
 * Este evento transporta la entidad del usuario recién registrado y el token de verificación
 * generado para él, permitiendo a los listeners (como {@link mp.tfg.mycheckpoint.listener.RegistrationListener})
 * realizar acciones posteriores, como enviar un correo electrónico de verificación.
 */
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    /**
     * El usuario que acaba de completar el registro.
     */
    private final User user;
    /**
     * El token de verificación generado para el usuario, que se utilizará
     * para confirmar su dirección de correo electrónico.
     */
    private final String token;

    /**
     * Crea una nueva instancia de {@code OnRegistrationCompleteEvent}.
     *
     * @param user El objeto {@link User} que representa al usuario recién registrado.
     * Este se considera la fuente del evento.
     * @param token El token de verificación de correo electrónico generado para este usuario.
     */
    public OnRegistrationCompleteEvent(User user, String token) {
        super(user);
        this.user = user;
        this.token = token;
    }
}
