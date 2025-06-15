package mp.tfg.mycheckpoint.service.impl;

import mp.tfg.mycheckpoint.entity.PasswordResetToken;
import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage; // Correcto para texto plano
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

/**
 * Implementación del servicio {@link EmailService} para el envío de correos electrónicos.
 * Utiliza {@link JavaMailSender} de Spring para la funcionalidad de envío.
 * Configura la URL base de la API y del frontend desde las propiedades de la aplicación
 * para construir enlaces en los correos.
 */
@Service
public class EmailServiceImpls implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpls.class);

    private final JavaMailSender mailSender;

    /**
     * URL base de la API, inyectada desde la propiedad "app.api.base-url".
     * Utilizada para construir enlaces de confirmación de cuenta que apuntan a la API.
     * Valor por defecto: "http://localhost:8080".
     */
    @Value("${app.api.base-url:http://localhost:8080}")
    private String apiBaseUrl;

    /**
     * URL base del frontend, inyectada desde la propiedad "app.frontend.base-url".
     * Utilizada para construir enlaces de restablecimiento de contraseña que dirigen al usuario
     * a la interfaz de usuario del frontend.
     * Valor por defecto: "http://localhost:5173".
     */
    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    /**
     * Dirección de correo electrónico remitente, inyectada desde la propiedad "spring.mail.username".
     * Esta será la dirección "De:" en los correos enviados.
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Constructor para EmailServiceImpls.
     *
     * @param mailSender El {@link JavaMailSender} configurado por Spring para enviar correos.
     */
    @Autowired
    public EmailServiceImpls(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * {@inheritDoc}
     * Construye y envía un correo electrónico de texto plano al usuario con un enlace
     * para verificar su dirección de correo electrónico. El enlace apunta a un endpoint de la API.
     */
    @Override
    public void sendVerificationEmail(User user, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("MyCheckPoint - Confirma tu dirección de correo electrónico");

            String confirmationUrl = apiBaseUrl + "/api/v1/auth/confirm-account?token=" + token;
            String emailBody = String.format(
                    "Hola %s,\n\n" +
                            "Gracias por registrarte en MyCheckPoint. Por favor, haz clic en el siguiente enlace " +
                            "o cópialo en tu navegador para confirmar tu dirección de correo electrónico:\n\n" +
                            "%s\n\n" +
                            "Si no te registraste, por favor ignora este mensaje.\n\n" +
                            "Saludos,\nEl equipo de MyCheckPoint",
                    user.getNombreUsuario(),
                    confirmationUrl
            );

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Correo de verificación enviado a {} desde {}", user.getEmail(), fromEmail);
        } catch (MailException e) {
            logger.error("Error al enviar correo de verificación a {} desde {}: {}", user.getEmail(), fromEmail, e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * Construye y envía un correo electrónico de texto plano al usuario con un enlace
     * para restablecer su contraseña. El enlace apunta a una página del frontend
     * que manejará el proceso de restablecimiento utilizando el token proporcionado.
     * También incluye el token directamente en el cuerpo del correo para copia manual si es necesario.
     */
    @Override
    public void sendPasswordResetEmail(User user, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("MyCheckPoint - Restablecimiento de Contraseña");

            // Construye la URL que apuntará a la página del frontend para resetear la contraseña.
            String frontendResetUrl = frontendBaseUrl + "resetear-password?token=" + token;

            // Dentro de sendPasswordResetEmail
            String emailBody = String.format(
                    "Hola %s,\n\n" +
                            "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en MyCheckPoint.\n" +
                            "Si no solicitaste esto, puedes ignorar este correo de forma segura.\n\n" +
                            "Para restablecer tu contraseña, por favor haz clic en el siguiente enlace o cópialo en tu navegador:\n" +
                            "%s\n\n" +
                            "Saludos,\nEl equipo de MyCheckPoint",
                    user.getNombreUsuario(),
                    frontendResetUrl
            );

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Correo de restablecimiento de contraseña enviado a {} desde {}", user.getEmail(), fromEmail);
        } catch (MailException e) {
            logger.error("Error al enviar correo de restablecimiento de contraseña a {} desde {}: {}", user.getEmail(), fromEmail, e.getMessage(), e);
        }
    }
}
