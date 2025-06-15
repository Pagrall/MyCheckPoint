package mp.tfg.mycheckpoint;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * Clase principal de la aplicación MyCheckPoint.
 * Esta clase sirve como punto de entrada para la aplicación Spring Boot.
 * Habilita la autoconfiguración de Spring Boot, el escaneo de componentes
 * y la configuración de la aplicación. También activa la funcionalidad de
 * tareas programadas y establece la zona horaria por defecto a UTC al iniciar.
 */
@SpringBootApplication
@EnableScheduling
public class MyCheckPointApplication {

    /**
     * Método ejecutado después de la construcción de la instancia de la aplicación.
     * Establece la zona horaria por defecto de la JVM a "UTC". Esto asegura
     * la consistencia en el manejo de fechas y horas a través de la aplicación,
     * independientemente de la zona horaria del servidor donde se ejecute.
     */
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos pasados al iniciar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(MyCheckPointApplication.class, args);
    }

}
