# AutenticacinControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**authenticateUser**](#authenticateuser) | **POST** /api/v1/auth/login | Autenticar usuario y obtener token JWT|
|[**confirmUserAccount**](#confirmuseraccount) | **GET** /api/v1/auth/confirm-account | Confirmar la dirección de correo electrónico de un usuario|
|[**forgotPassword**](#forgotpassword) | **POST** /api/v1/auth/forgot-password | Solicitar restablecimiento de contraseña|
|[**resetPassword**](#resetpassword) | **POST** /api/v1/auth/reset-password | Restablecer la contraseña del usuario utilizando un token|

# **authenticateUser**
> JwtResponseDTO authenticateUser(loginRequestDTO)

Permite a un usuario iniciar sesión proporcionando su identificador (email o nombre de usuario) y contraseña. Si las credenciales son válidas y la cuenta está activa, se devuelve un token JWT. Si el usuario tenía una eliminación de cuenta programada y la fecha aún no ha pasado, esta se cancela.

### Example

```typescript
import {
    AutenticacinControllerApi,
    Configuration,
    LoginRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new AutenticacinControllerApi(configuration);

let loginRequestDTO: LoginRequestDTO; //Credenciales del usuario para iniciar sesión.

const { status, data } = await apiInstance.authenticateUser(
    loginRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **loginRequestDTO** | **LoginRequestDTO**| Credenciales del usuario para iniciar sesión. | |


### Return type

**JwtResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Autenticación exitosa. Devuelve el token JWT. |  -  |
|**401** | No autorizado. Credenciales incorrectas o fallo general de autenticación. |  -  |
|**400** | Datos de entrada inválidos. El identificador o la contraseña no cumplen los requisitos de formato o están vacíos. |  -  |
|**404** | No encontrado. El usuario autenticado no se pudo encontrar en la base de datos (error interno anómalo durante la cancelación de eliminación). |  -  |
|**500** | Error interno del servidor. |  -  |
|**403** | Prohibido. La cuenta está deshabilitada (ej. email no verificado) o la cuenta ha sido eliminada porque su fecha de eliminación programada ya pasó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **confirmUserAccount**
> string confirmUserAccount()

Valida un token de verificación enviado al correo electrónico del usuario tras el registro. Si el token es válido y no ha expirado, la cuenta del usuario se marca como verificada. Este endpoint es público y se accede a través del enlace en el correo de verificación.

### Example

```typescript
import {
    AutenticacinControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new AutenticacinControllerApi(configuration);

let token: string; //El token de verificación único enviado al correo electrónico del usuario. (default to undefined)

const { status, data } = await apiInstance.confirmUserAccount(
    token
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **token** | [**string**] | El token de verificación único enviado al correo electrónico del usuario. | defaults to undefined|


### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Correo electrónico verificado exitosamente. |  -  |
|**404** | No encontrado. El token de verificación proporcionado no existe o es inválido. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Solicitud incorrecta. El token es inválido (ej. ya fue usado, ha expirado, o el correo ya estaba verificado). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **forgotPassword**
> object forgotPassword(forgotPasswordDTO)

Inicia el proceso para restablecer la contraseña de un usuario. El usuario proporciona su dirección de correo electrónico. Si el correo está registrado, se enviará un email con un token e instrucciones para restablecer la contraseña. Para no revelar si un email existe en el sistema, este endpoint siempre devuelve una respuesta genérica de éxito, independientemente de si el email fue encontrado o no. Este endpoint es público.

### Example

```typescript
import {
    AutenticacinControllerApi,
    Configuration,
    ForgotPasswordDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new AutenticacinControllerApi(configuration);

let forgotPasswordDTO: ForgotPasswordDTO; //DTO que contiene el correo electrónico del usuario que ha olvidado su contraseña.

const { status, data } = await apiInstance.forgotPassword(
    forgotPasswordDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **forgotPasswordDTO** | **ForgotPasswordDTO**| DTO que contiene el correo electrónico del usuario que ha olvidado su contraseña. | |


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
|**200** | Solicitud procesada. Se enviará un correo si el email está registrado. |  -  |
|**400** | Datos de entrada inválidos. El formato del email proporcionado no es válido o el campo está vacío. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **resetPassword**
> object resetPassword(resetPasswordDTO)

Permite a un usuario establecer una nueva contraseña utilizando el token de restablecimiento que recibió por correo electrónico. El token debe ser válido y no haber expirado. Este endpoint es público.

### Example

```typescript
import {
    AutenticacinControllerApi,
    Configuration,
    ResetPasswordDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new AutenticacinControllerApi(configuration);

let resetPasswordDTO: ResetPasswordDTO; //DTO que contiene el token de restablecimiento y la nueva contraseña deseada.

const { status, data } = await apiInstance.resetPassword(
    resetPasswordDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **resetPasswordDTO** | **ResetPasswordDTO**| DTO que contiene el token de restablecimiento y la nueva contraseña deseada. | |


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
|**200** | Contraseña restablecida exitosamente. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Solicitud incorrecta. Los datos proporcionados en &#x60;ResetPasswordDTO&#x60; no son válidos (ej. token vacío, contraseña nueva no cumple requisitos), el token ya fue usado, ha expirado, o la nueva contraseña es la misma que la actual. |  -  |
|**404** | No encontrado. El token de restablecimiento proporcionado no existe o es inválido. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

