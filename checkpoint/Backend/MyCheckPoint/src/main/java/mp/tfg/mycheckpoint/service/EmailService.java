package mp.tfg.mycheckpoint.service;

import mp.tfg.mycheckpoint.entity.User;

/**
 * Interfaz para el servicio de envío de correos electrónicos.
 * Define los métodos para enviar diferentes tipos de correos, como
 * verificación de cuenta y restablecimiento de contraseña.
 */
public interface EmailService {

    /**
     * Envía un correo electrónico de verificación al usuario especificado.
     * Este correo contendrá un enlace con un token único para que el usuario
     * pueda confirmar su dirección de correo electrónico.
     *
     * @param user El {@link User} al que se enviará el correo de verificación.
     * @param token El token de verificación único generado para este usuario.
     */
    void sendVerificationEmail(User user, String token);

    /**
     * Envía un correo electrónico al usuario con instrucciones y un token
     * para restablecer su contraseña.
     *
     * @param user El {@link User} que ha solicitado el restablecimiento de contraseña.
     * @param token El token único generado para el proceso de restablecimiento.
     */
    void sendPasswordResetEmail(User user, String token);

}