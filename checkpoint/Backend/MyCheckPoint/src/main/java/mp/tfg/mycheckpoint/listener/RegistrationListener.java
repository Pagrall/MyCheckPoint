package mp.tfg.mycheckpoint.listener;

import mp.tfg.mycheckpoint.event.OnRegistrationCompleteEvent;
import mp.tfg.mycheckpoint.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * Listener de eventos de la aplicación que se activa cuando se completa el registro de un nuevo usuario.
 * Su responsabilidad principal es iniciar el proceso de envío del correo electrónico de verificación
 * al usuario recién registrado.
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final EmailService emailService;

    /**
     * Constructor para {@code RegistrationListener}.
     * Inyecta el servicio de correo electrónico necesario para enviar notificaciones.
     *
     * @param emailService El servicio para el envío de correos electrónicos.
     */
    @Autowired
    public RegistrationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Maneja el evento {@link OnRegistrationCompleteEvent} que se publica
     * cuando un usuario completa su registro.
     * Este método invoca la lógica para enviar el correo de verificación.
     *
     * @param event El evento {@link OnRegistrationCompleteEvent} que contiene
     * los detalles del usuario registrado y el token de verificación.
     */
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    /**
     * Método privado que delega al {@link EmailService} el envío del correo
     * electrónico de verificación al usuario.
     *
     * @param event El evento {@link OnRegistrationCompleteEvent} que contiene
     * el usuario y el token necesarios para el correo de verificación.
     */
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        emailService.sendVerificationEmail(event.getUser(), event.getToken());
    }
}