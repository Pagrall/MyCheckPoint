package mp.tfg.mycheckpoint.security;

import mp.tfg.mycheckpoint.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections; // Para una lista simple de autoridades
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementación de {@link UserDetails} de Spring Security.
 * Esta clase encapsula la información esencial del usuario necesaria para
 * el framework de seguridad, como nombre de usuario, contraseña, autoridades (roles),
 * y el estado de la cuenta (habilitada, bloqueada, etc.).
 */
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * ID interno del usuario.
     */
    private final Long id;
    /**
     * ID público (UUID) del usuario.
     */
    private final UUID publicId;
    /**
     * Nombre de usuario utilizado por Spring Security (en este caso, el email del usuario).
     */
    private final String username;
    /**
     * Dirección de correo electrónico del usuario.
     */
    private final String email;
    /**
     * Contraseña hasheada del usuario.
     */
    private final String password;
    /**
     * Indica si el correo electrónico del usuario ha sido verificado.
     */
    private final boolean emailVerified;
    /**
     * Colección de autoridades (roles) asignadas al usuario.
     */
    private final Collection<? extends GrantedAuthority> authorities;
    /**
     * Fecha programada para la eliminación de la cuenta, si aplica.
     * Si es nulo, la cuenta no está programada para eliminación.
     * Si la fecha ya pasó, la cuenta se considera deshabilitada.
     */
    private final OffsetDateTime fechaEliminacion;

    /**
     * Constructor para UserDetailsImpl.
     *
     * @param id El ID interno del usuario.
     * @param publicId El ID público (UUID) del usuario.
     * @param usernameForSecurity El nombre de usuario para Spring Security (usualmente el email).
     * @param email La dirección de correo electrónico del usuario.
     * @param password La contraseña hasheada del usuario.
     * @param emailVerified {@code true} si el email del usuario está verificado, {@code false} en caso contrario.
     * @param fechaEliminacion La fecha programada para la eliminación de la cuenta, o {@code null}.
     * @param authorities Las autoridades (roles) concedidas al usuario.
     */
    public UserDetailsImpl(Long id, UUID publicId, String usernameForSecurity, String email, String password,
                           boolean emailVerified,
                           OffsetDateTime fechaEliminacion,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.publicId = publicId;
        this.username = usernameForSecurity;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.authorities = authorities;
        this.fechaEliminacion = fechaEliminacion;
    }

    /**
     * Método factoría para construir una instancia de {@link UserDetailsImpl} a partir de una entidad {@link User}.
     *
     * @param user La entidad {@link User} de la cual se extraerán los detalles.
     * @return Una instancia de {@link UserDetailsImpl} poblada con los datos del usuario.
     */
    public static UserDetailsImpl build(User user) {
        // Por defecto, se asigna el rol "ROLE_USER" a todos los usuarios.
        // Se podría expandir para manejar múltiples roles si la aplicación lo requiere.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new UserDetailsImpl(
                user.getId(),
                user.getPublicId(),
                user.getEmail(), // El email se usa como el "username" para Spring Security
                user.getEmail(),
                user.getContraseña(),
                user.isEmailVerified(),
                user.getFechaEliminacion(),
                authorities
        );
    }

    /**
     * Obtiene el ID interno del usuario.
     * @return El ID interno.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el ID público (UUID) del usuario.
     * @return El ID público.
     */
    public UUID getPublicId() {
        return publicId;
    }

    /**
     * Obtiene la dirección de correo electrónico del usuario.
     * @return El email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Indica si el correo electrónico del usuario ha sido verificado.
     * @return {@code true} si el email está verificado, {@code false} en caso contrario.
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     * En esta implementación, devuelve el email del usuario, que se utiliza como
     * el identificador principal para Spring Security.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     * Indica si la cuenta del usuario no ha expirado.
     * @return {@code true} siempre, ya que la expiración de cuenta no se implementa.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     * Indica si la cuenta del usuario no está bloqueada.
     * @return {@code true} siempre, ya que el bloqueo de cuenta por intentos fallidos no se implementa aquí.
     * Se podría añadir lógica de bloqueo si fuera necesario.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     * Indica si las credenciales del usuario (contraseña) no han expirado.
     * @return {@code true} siempre, ya que la expiración de credenciales no se implementa.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     * Indica si la cuenta del usuario está habilitada.
     * Una cuenta está habilitada si:
     * <ol>
     * <li>Su correo electrónico ha sido verificado.</li>
     * <li>No tiene una fecha de eliminación programada, O si la tiene, esta fecha es futura.</li>
     * </ol>
     * Si el email no está verificado, o si la fecha de eliminación programada ya pasó,
     * la cuenta se considera deshabilitada.
     * @return {@code true} si la cuenta está habilitada, {@code false} en caso contrario.
     */
    @Override
    public boolean isEnabled() {
        if (!this.emailVerified) {
            return false;
        }
        return this.fechaEliminacion == null || this.fechaEliminacion.isAfter(OffsetDateTime.now());
    }

    /**
     * Compara este UserDetailsImpl con otro objeto para determinar si son iguales.
     * La igualdad se basa en el {@code id} del usuario.
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Genera un código hash para este UserDetailsImpl.
     * El hash se basa en el {@code id} del usuario.
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}