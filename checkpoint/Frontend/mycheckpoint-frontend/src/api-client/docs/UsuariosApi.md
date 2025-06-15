# UsuariosApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**changeMyPassword**](#changemypassword) | **PUT** /api/v1/usuarios/me/password | Cambiar la contraseña del usuario autenticado actualmente|
|[**deleteMyAccount**](#deletemyaccount) | **DELETE** /api/v1/usuarios/me | Programar la eliminación de la cuenta del usuario autenticado|
|[**getCurrentAuthenticatedUser**](#getcurrentauthenticateduser) | **GET** /api/v1/usuarios/me | Obtener los datos del usuario autenticado actualmente|
|[**getUsuarioByPublicId**](#getusuariobypublicid) | **GET** /api/v1/usuarios/public/{publicId} | Obtener un usuario por su ID público|
|[**getUsuarioSummaryByPublicId**](#getusuariosummarybypublicid) | **GET** /api/v1/usuarios/public/summary/{publicId} | Obtener un resumen de usuario por su ID público|
|[**registrarUsuario**](#registrarusuario) | **POST** /api/v1/usuarios | Registrar un nuevo usuario|
|[**searchUsersByUsername**](#searchusersbyusername) | **GET** /api/v1/usuarios/search | Buscar usuarios por nombre de usuario|
|[**updateCurrentUserProfile**](#updatecurrentuserprofile) | **PUT** /api/v1/usuarios/me | Actualizar el perfil del usuario autenticado actualmente|
|[**uploadProfilePicture**](#uploadprofilepicture) | **POST** /api/v1/usuarios/me/profile-picture | Subir o actualizar la foto de perfil del usuario autenticado|

# **changeMyPassword**
> object changeMyPassword(passwordChangeDTO)

Permite al usuario autenticado cambiar su contraseña actual por una nueva. Se requiere la contraseña actual para la verificación. Requiere un token JWT válido.

### Example

```typescript
import {
    UsuariosApi,
    Configuration,
    PasswordChangeDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let passwordChangeDTO: PasswordChangeDTO; //DTO con la contraseña actual y la nueva contraseña.

const { status, data } = await apiInstance.changeMyPassword(
    passwordChangeDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **passwordChangeDTO** | **PasswordChangeDTO**| DTO con la contraseña actual y la nueva contraseña. | |


### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**400** | Datos de entrada inválidos o solicitud incorrecta (ej. nueva contraseña igual a la actual). |  -  |
|**200** | Contraseña actualizada correctamente. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. La contraseña actual proporcionada es incorrecta, o el token JWT es inválido/expirado. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser encontrado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteMyAccount**
> object deleteMyAccount(accountDeleteDTO)

Permite al usuario autenticado solicitar la eliminación de su cuenta. Se requiere la contraseña actual para confirmación. La cuenta se marcará para eliminación y se borrará permanentemente después de un período de gracia. Tras esta operación, la sesión actual del usuario se invalidará. Requiere un token JWT válido.

### Example

```typescript
import {
    UsuariosApi,
    Configuration,
    AccountDeleteDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let accountDeleteDTO: AccountDeleteDTO; //DTO que contiene la contraseña actual del usuario para confirmar la eliminación de la cuenta.

const { status, data } = await apiInstance.deleteMyAccount(
    accountDeleteDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **accountDeleteDTO** | **AccountDeleteDTO**| DTO que contiene la contraseña actual del usuario para confirmar la eliminación de la cuenta. | |


### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. La contraseña actual proporcionada es incorrecta, o el token JWT es inválido/expirado. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser encontrado en la base de datos (caso anómalo). |  -  |
|**200** | Solicitud de eliminación de cuenta procesada. La cuenta ha sido programada para eliminación y la sesión actual invalidada. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCurrentAuthenticatedUser**
> UserDTO getCurrentAuthenticatedUser()

Recupera los detalles del perfil y preferencias del usuario que ha iniciado sesión. Requiere un token JWT válido en la cabecera de autorización.

### Example

```typescript
import {
    UsuariosApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

const { status, data } = await apiInstance.getCurrentAuthenticatedUser();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**UserDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Datos del usuario autenticado devueltos exitosamente. |  -  |
|**404** | No encontrado. El usuario autenticado (identificado por el token) no pudo ser encontrado en la base de datos. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó, o el contexto de seguridad no contiene un principal válido. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUsuarioByPublicId**
> UserDTO getUsuarioByPublicId()

Recupera los detalles de un usuario específico utilizando su ID público (UUID). Este endpoint es público y no requiere autenticación.

### Example

```typescript
import {
    UsuariosApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let publicId: string; //ID público (UUID) del usuario a obtener. (default to undefined)

const { status, data } = await apiInstance.getUsuarioByPublicId(
    publicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **publicId** | [**string**] | ID público (UUID) del usuario a obtener. | defaults to undefined|


### Return type

**UserDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**404** | No encontrado. No existe ningún usuario con el ID público proporcionado. |  -  |
|**500** | Error interno del servidor. |  -  |
|**200** | Usuario encontrado y devuelto exitosamente. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUsuarioSummaryByPublicId**
> UserSearchResultDTO getUsuarioSummaryByPublicId()

Recupera un resumen de información pública de un usuario específico utilizando su ID público (UUID). Este endpoint es público y no requiere autenticación.

### Example

```typescript
import {
    UsuariosApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let publicId: string; //ID público (UUID) del usuario a obtener. (default to undefined)

const { status, data } = await apiInstance.getUsuarioSummaryByPublicId(
    publicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **publicId** | [**string**] | ID público (UUID) del usuario a obtener. | defaults to undefined|


### Return type

**UserSearchResultDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Resumen de usuario encontrado y devuelto exitosamente. |  -  |
|**404** | No encontrado. No existe ningún usuario con el ID público proporcionado. |  -  |
|**500** | Error interno del servidor. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **registrarUsuario**
> UserDTO registrarUsuario(userCreateDTO)

Crea una nueva cuenta de usuario en el sistema. Tras el registro exitoso, se enviará un correo electrónico de verificación a la dirección proporcionada para activar la cuenta.

### Example

```typescript
import {
    UsuariosApi,
    Configuration,
    UserCreateDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let userCreateDTO: UserCreateDTO; //Datos del nuevo usuario a registrar. Todos los campos son obligatorios.

const { status, data } = await apiInstance.registrarUsuario(
    userCreateDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **userCreateDTO** | **UserCreateDTO**| Datos del nuevo usuario a registrar. Todos los campos son obligatorios. | |


### Return type

**UserDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**400** | Datos de entrada inválidos. Ocurre si los datos proporcionados en &#x60;UserCreateDTO&#x60; no pasan las validaciones (ej. email no válido, contraseña corta). |  -  |
|**409** | Conflicto. El email o el nombre de usuario proporcionado ya se encuentra registrado en el sistema. |  -  |
|**500** | Error interno del servidor. Ocurrió un problema inesperado durante el proceso de registro. |  -  |
|**201** | Usuario creado exitosamente. Devuelve los datos del usuario recién creado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **searchUsersByUsername**
> Array<UserSearchResultDTO> searchUsersByUsername()

Permite a un usuario autenticado buscar otros usuarios en el sistema por su nombre de usuario. La búsqueda es parcial (contiene) e ignora mayúsculas/minúsculas. El propio usuario que realiza la búsqueda será excluido de los resultados. Se requiere un término de búsqueda de al menos 2 caracteres. Requiere autenticación.

### Example

```typescript
import {
    UsuariosApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let username: string; //Término de búsqueda para el nombre de usuario. Debe tener al menos 2 caracteres. (default to undefined)

const { status, data } = await apiInstance.searchUsersByUsername(
    username
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **username** | [**string**] | Término de búsqueda para el nombre de usuario. Debe tener al menos 2 caracteres. | defaults to undefined|


### Return type

**Array<UserSearchResultDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**400** | Solicitud incorrecta. El parámetro \&#39;username\&#39; es obligatorio y debe tener al menos 2 caracteres. |  -  |
|**404** | No encontrado. No se encontraron usuarios con el nombre de usuario proporcionado |  -  |
|**500** | Error interno del servidor. |  -  |
|**200** | Búsqueda exitosa. Devuelve una lista de usuarios que coinciden con el criterio. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateCurrentUserProfile**
> UserDTO updateCurrentUserProfile(userProfileUpdateDTO)

Permite al usuario autenticado modificar los detalles de su perfil, como el nombre de usuario, tema, foto de perfil, preferencias de notificación y visibilidad del perfil. Requiere un token JWT válido.

### Example

```typescript
import {
    UsuariosApi,
    Configuration,
    UserProfileUpdateDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let userProfileUpdateDTO: UserProfileUpdateDTO; //Datos del perfil del usuario a actualizar. Solo se actualizarán los campos proporcionados.

const { status, data } = await apiInstance.updateCurrentUserProfile(
    userProfileUpdateDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **userProfileUpdateDTO** | **UserProfileUpdateDTO**| Datos del perfil del usuario a actualizar. Solo se actualizarán los campos proporcionados. | |


### Return type

**UserDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Perfil de usuario actualizado exitosamente. Devuelve los datos actualizados del usuario. |  -  |
|**404** | No encontrado. El usuario autenticado (identificado por el token) no pudo ser encontrado en la base de datos para la actualización. |  -  |
|**409** | Conflicto. El nuevo nombre de usuario elegido ya está en uso por otro usuario. |  -  |
|**400** | Datos de entrada inválidos. Ocurre si los datos proporcionados en &#x60;UserProfileUpdateDTO&#x60; no pasan las validaciones (ej. nombre de usuario demasiado corto/largo). |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **uploadProfilePicture**
> UserDTO uploadProfilePicture()

Permite al usuario autenticado subir un nuevo archivo de imagen para su foto de perfil. El archivo debe ser de un formato permitido (JPEG, PNG, GIF) y no exceder el tamaño máximo configurado. Si ya existe una foto de perfil, será reemplazada. Requiere autenticación.

### Example

```typescript
import {
    UsuariosApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UsuariosApi(configuration);

let file: File; //El archivo de imagen a subir como foto de perfil. (default to undefined)

const { status, data } = await apiInstance.uploadProfilePicture(
    file
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **file** | [**File**] | El archivo de imagen a subir como foto de perfil. | defaults to undefined|


### Return type

**UserDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Foto de perfil subida y perfil actualizado exitosamente. Devuelve los datos actualizados del usuario. |  -  |
|**400** | Solicitud incorrecta. El archivo proporcionado está vacío, tiene un formato no permitido, o hay un problema con el nombre del archivo. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser encontrado en la base de datos (caso anómalo). |  -  |
|**413** | Payload Too Large. El archivo excede el tamaño máximo permitido para fotos de perfil o el límite general de subida. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**500** | Error interno del servidor. No se pudo crear el directorio de almacenamiento, guardar el archivo, o ocurrió otro error inesperado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

