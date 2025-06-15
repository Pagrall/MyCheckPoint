# ⚙️ Guía de Instalación del Backend (MyCheckPoint - Spring Boot)

Esta guía detalla los pasos necesarios para configurar y ejecutar el backend del proyecto en un entorno de desarrollo local.

### 1. Prerrequisitos

Antes de comenzar, asegúrate de tener instalado el siguiente software en tu sistema:

* **Java Development Kit (JDK):** **Versión 21**. Puedes descargarlo desde [Adoptium (Eclipse Temurin)](https://adoptium.net/) o [Oracle](https://www.oracle.com/java/technologies/downloads/).
* **Apache Maven:** **Versión 3.8 o superior**. Es el gestor de dependencias del proyecto. Puedes descargarlo desde la [página oficial de Maven](https://maven.apache.org/download.cgi).
* **PostgreSQL:** El proyecto está configurado para usar una base de datos PostgreSQL. Asegúrate de tener una instancia de PostgreSQL en ejecución.
* **IDE (Opcional pero recomendado):** [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Visual Studio Code](https://code.visualstudio.com/) con las extensiones de Java, o [Eclipse](https://www.eclipse.org/).

### 2. Configuración Inicial

#### a. Clonar el Repositorio
Abre una terminal y clona el repositorio del proyecto (reemplaza `https://github.com/sindresorhus/del` con la URL real):

```bash
git clone https://github.com/sindresorhus/del
cd MyCheckPoint
```

#### b. Configuración de la Base de Datos PostgreSQL
Necesitarás una base de datos y un usuario para que la aplicación pueda conectarse.

1.  Abre una terminal de `psql` o una herramienta de gestión de bases de datos como DBeaver o pgAdmin.
2.  Ejecuta los siguientes comandos SQL para crear la base de datos y el usuario con los que el proyecto está preconfigurado:

    ```sql
    -- 1. Crea un nuevo usuario (rol). Puedes cambiar 'admin' por una contraseña segura.
    CREATE ROLE mp WITH LOGIN PASSWORD 'admin';

    -- 2. Crea la base de datos que usará la aplicación.
    CREATE DATABASE checkpoint_db;

    -- 3. Asigna al nuevo usuario como propietario de la base de datos.
    ALTER DATABASE checkpoint_db OWNER TO mp;

    -- 4. (Opcional pero recomendado) Conéctate a la nueva base de datos para verificar.
    \c checkpoint_db
    ```
    Si decides usar un nombre de base de datos (`checkpoint_db`), usuario (`mp`) o contraseña (`admin`) diferentes, deberás actualizarlos en el fichero `application.properties`.

#### c. Crear el Directorio de Subidas
La aplicación almacena las fotos de perfil subidas por los usuarios en un directorio local. Desde la raíz de tu proyecto (`MyCheckPoint`), crea la siguiente estructura de carpetas:

```bash
mkdir -p uploads/profile-pictures
```

### 3. Configuración de la Aplicación (`application.properties`)

El fichero principal de configuración es `src/main/resources/application.properties`. Revisa y ajusta las siguientes secciones críticas:

#### a. Credenciales de la API de IGDB
El proyecto depende de la API de IGDB para obtener datos de videojuegos. Necesitas tus propias credenciales.

1.  Ve al [Portal de Desarrolladores de Twitch](https://dev.twitch.tv/login) e inicia sesión o regístrate.
2.  Desde tu panel, ve a "Aplicaciones" y registra una nueva aplicación.
3.  Una vez creada, obtendrás un **Client ID**. También necesitarás generar un **Token de Acceso de Aplicación (App Access Token)**.
4.  Abre el fichero `application.properties` y actualiza estas líneas con tus credenciales:

    ```properties
    # Credenciales de la API de IGDB (reemplazar con las tuyas)
    igdb.api.client-id=TU_CLIENT_ID_DE_TWITCH
    igdb.api.authorization=Bearer TU_APP_ACCESS_TOKEN
    ```

#### b. Configuración de Email (Gmail)
El servicio de correo para verificación y recuperación de contraseña usa una cuenta de Gmail. **Por seguridad, es fundamental que uses tu propia cuenta y una contraseña de aplicación.**

1.  Abre el fichero `application.properties` y cambia `spring.mail.username` por tu dirección de Gmail.
2.  Genera una **Contraseña de Aplicación** para tu cuenta de Google. Sigue las instrucciones oficiales: [Iniciar sesión con contraseñas de aplicación](https://support.google.com/accounts/answer/185833). Google te proporcionará una contraseña de 16 caracteres.
3.  Actualiza el fichero de propiedades con tu usuario y tu nueva contraseña de aplicación:

    ```properties
    # Configuración de Correo (reemplazar con tus datos)
    spring.mail.username=tu-correo@gmail.com
    spring.mail.password=tu-contraseña-de-aplicacion-de-16-caracteres
    ```

#### c. Secreto JWT
El token de autenticación se firma con una clave secreta. La que está en el fichero es solo un ejemplo.

1.  Genera una nueva clave secreta segura codificada en Base64. Puedes usar un generador online o este comando en una terminal (Linux/macOS):
    ```bash
    openssl rand -base64 64
    ```
2.  Copia la clave generada y pégala en `application.properties`:
    ```properties
    # Secreto JWT (reemplazar con tu clave generada)
    app.jwt.secret=TU_CLAVE_SECRETA_EN_BASE64
    ```

### 4. Compilación y Ejecución

Una vez todo está configurado, puedes compilar y ejecutar la aplicación.

1.  **Compilar el Proyecto**
    Desde la raíz del proyecto (`MyCheckPoint`), ejecuta el comando de Maven para descargar las dependencias y compilar el código:
    ```bash
    mvn clean install
    ```

2.  **Ejecutar la Aplicación**
    Puedes ejecutar la aplicación de dos maneras:
    * **Desde tu IDE:** Localiza y ejecuta la clase `MyCheckPointApplication.java`.
    * **Usando Maven (desde la terminal):**
        ```bash
        mvn spring-boot:run
        ```

### 5. Verificación

🚀 Si todo ha ido bien, verás en la consola un mensaje indicando que la aplicación se ha iniciado en el puerto **8080**:

```
... .s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http) with context path ''
... m.t.m.MyCheckPointApplication      : Started MyCheckPointApplication in X.XXX seconds (JVM running for Y.YYY)
```

Para verificar que todo funciona correctamente:

* **Documentación de la API (Swagger):** Abre tu navegador y ve a [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). Deberías ver la interfaz de Swagger con todos los endpoints de la API documentados. Esto confirma que la aplicación se ha iniciado correctamente.
* **Endpoints Públicos:** Puedes usar herramientas como Postman, Insomnia o `curl` para probar los endpoints públicos, como el de registro (`POST /api/v1/usuarios`) o el de login (`POST /api/v1/auth/login`).

&nbsp;

&nbsp;
&nbsp;

&nbsp;
&nbsp;

&nbsp;
&nbsp;

&nbsp;
&nbsp;

&nbsp;

# 🚀 Guía de Instalación del Frontend (Vue.js + Vite)

Esta guía detalla los pasos necesarios para configurar y ejecutar el frontend del proyecto en un entorno de desarrollo local.

### 1. Prerrequisitos

Antes de comenzar, asegúrate de tener instalado el siguiente software en tu sistema:

* **Node.js:** Se recomienda la versión **LTS** (actualmente v20 o superior). Puedes descargarlo desde la [página oficial de Node.js](https://nodejs.org/). Node.js incluye **npm** (Node Package Manager).
* **IDE (Recomendado):** [Visual Studio Code](https://code.visualstudio.com/) con la extensión [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (imprescindible para la mejor experiencia con Vue 3 y Vite).

### 2. Configuración del Entorno

#### a. Clonar el Repositorio
Si ya clonaste el repositorio para el backend, simplemente navega hasta la carpeta raíz del proyecto. Si no, clónalo ahora (reemplaza `https://github.com/sindresorhus/del` con la URL real):

```bash
git clone https://github.com/sindresorhus/del
cd [nombre-del-repositorio]
```

Una vez en la raíz del proyecto, **navega hasta el directorio del frontend** (basado en el nombre de tu proyecto, probablemente se llame `frontend/` o similar).

#### b. Configurar Variables de Entorno
El frontend necesita saber la dirección del backend para comunicarse con la API. Esta configuración se realiza mediante un fichero de entorno.

1.  En la raíz del directorio del frontend, crea un nuevo fichero llamado `.env.local`.
2.  Abre el fichero `.env.local` y añade la siguiente línea. Esta variable apunta al servidor backend que debe estar corriendo en `http://localhost:8080`.

    ```env
    # Fichero: .env.local
    
    # URL base de la API del backend
    VITE_API_BASE_URL=http://localhost:8080
    ```
    > **Nota:** El nombre de la variable (`VITE_API_BASE_URL`) es el estándar para proyectos con Vite. Si en tu código usas un nombre diferente (ej. `VITE_API_URL`), debes ajustarlo aquí.

### 3. Instalación y Ejecución

Una vez configurado el entorno, puedes instalar las dependencias y ejecutar el servidor de desarrollo.

1.  **Instalar Dependencias**
    Desde la raíz del directorio del frontend, ejecuta el siguiente comando para descargar todas las librerías necesarias definidas en `package.json`:
    ```bash
    npm install
    ```

2.  **Ejecutar para Desarrollo**
    Este comando, definido en tu `package.json`, compila la aplicación y levanta un servidor local con recarga en caliente (Hot-Reload).
    ```bash
    npm run dev
    ```

### 4. Verificación

✅ Si todo ha ido bien, la terminal te mostrará un mensaje indicando que el servidor de desarrollo se está ejecutando, junto con la URL para acceder a él. Normalmente será la siguiente:

```
  > Local: http://localhost:5173/
```

Abre la URL **http://localhost:5173** en tu navegador. Deberías ver la interfaz de usuario de la aplicación "MyCheckPoint".

---

### Anexo: Compilación para Producción

Si necesitas generar los ficheros estáticos para desplegar el frontend en un servidor de producción, puedes usar el siguiente comando:

```bash
npm run build
```

Esto creará una carpeta `dist` en el directorio del frontend con todos los ficheros optimizados listos para ser servidos.
