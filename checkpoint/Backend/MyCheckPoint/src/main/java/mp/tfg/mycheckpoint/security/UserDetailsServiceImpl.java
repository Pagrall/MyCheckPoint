package mp.tfg.mycheckpoint.security;

import mp.tfg.mycheckpoint.entity.User;
import mp.tfg.mycheckpoint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link UserDetailsService} de Spring Security.
 * Se encarga de cargar los detalles específicos del usuario ({@link UserDetails})
 * a partir de un identificador (nombre de usuario o email) durante el proceso de autenticación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor para UserDetailsServiceImpl.
     *
     * @param userRepository El repositorio para acceder a los datos de los usuarios.
     */
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su nombre de usuario o dirección de correo electrónico.
     * Este método es invocado por Spring Security durante el proceso de autenticación.
     * Intenta buscar al usuario primero por email y, si no lo encuentra, por nombre de usuario.
     *
     * @param usernameOrEmail El identificador del usuario (puede ser su email o nombre de usuario).
     * @return Un objeto {@link UserDetails} que representa al usuario encontrado.
     * @throws UsernameNotFoundException Si no se encuentra ningún usuario con el identificador proporcionado.
     */
    @Override
    @Transactional(readOnly = true) // Indica que es una operación de solo lectura para optimización.
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Intenta buscar por email primero, luego por nombre de usuario.
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseGet(() -> userRepository.findByNombreUsuario(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con identificador: " + usernameOrEmail)));

        // Construye y devuelve el objeto UserDetails a partir de la entidad User.
        return UserDetailsImpl.build(user);
    }
}