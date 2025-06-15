# UserGameLibraryControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**addOrUpdateGameInMyLibrary**](#addorupdategameinmylibrary) | **POST** /api/v1/users/me/library/games/{igdbId} | Añadir o actualizar un juego en la biblioteca del usuario autenticado|
|[**getGameDetails**](#getgamedetails) | **GET** /api/v1/games/{igdbId}/details | Obtener detalles completos de un juego|
|[**getMyGameLibrary**](#getmygamelibrary) | **GET** /api/v1/users/me/library/games | Obtener la biblioteca completa de juegos del usuario autenticado|
|[**getPublicUserLibrary**](#getpublicuserlibrary) | **GET** /api/v1/users/public/{publicId}/library | Obtener la biblioteca de un usuario por su ID público|
|[**getSpecificGameFromMyLibrary**](#getspecificgamefrommylibrary) | **GET** /api/v1/users/me/library/games/{igdbId} | Obtener un juego específico de la biblioteca del usuario autenticado|
|[**removeGameFromMyLibrary**](#removegamefrommylibrary) | **DELETE** /api/v1/users/me/library/games/{igdbId} | Eliminar un juego de la biblioteca del usuario autenticado|

# **addOrUpdateGameInMyLibrary**
> UserGameResponseDTO addOrUpdateGameInMyLibrary(userGameDataDTO)

Permite al usuario autenticado añadir un juego (identificado por su IGDB ID) a su biblioteca personal o actualizar una entrada existente. Si el juego no existe en la base de datos local, se intentará obtener de IGDB. Se proporcionan datos específicos del usuario para este juego (estado, puntuación, plataforma, etc.). Requiere autenticación.

### Example

```typescript
import {
    UserGameLibraryControllerApi,
    Configuration,
    UserGameDataDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UserGameLibraryControllerApi(configuration);

let igdbId: number; //ID de IGDB del juego a añadir o actualizar en la biblioteca. (default to undefined)
let userGameDataDTO: UserGameDataDTO; //

const { status, data } = await apiInstance.addOrUpdateGameInMyLibrary(
    igdbId,
    userGameDataDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **userGameDataDTO** | **UserGameDataDTO**|  | |
| **igdbId** | [**number**] | ID de IGDB del juego a añadir o actualizar en la biblioteca. | defaults to undefined|


### Return type

**UserGameResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**404** | No encontrado. El usuario autenticado no pudo ser verificado, o el juego con el &#x60;igdbId&#x60; proporcionado no se encontró en IGDB. |  -  |
|**500** | Error interno del servidor. Podría ocurrir si hay problemas al contactar IGDB o al guardar los datos. |  -  |
|**200** | Juego añadido o actualizado en la biblioteca exitosamente. Devuelve la entrada de la biblioteca actualizada. |  -  |
|**400** | Datos de entrada inválidos. Ocurre si los datos en &#x60;UserGameDataDTO&#x60; no pasan las validaciones (ej. puntuación fuera de rango). |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getGameDetails**
> GameDetailDTO getGameDetails()

Recupera información detallada sobre un juego específico, identificado por su IGDB ID. Este endpoint es público. Si se proporciona un token JWT de autenticación válido, la respuesta incluirá adicionalmente los datos específicos del usuario para ese juego (si existen en su biblioteca), como su estado, puntuación, etc. Si no se proporciona autenticación o el token es inválido, solo se devolverá la información pública del juego y los comentarios públicos.

### Example

```typescript
import {
    UserGameLibraryControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UserGameLibraryControllerApi(configuration);

let igdbId: number; //ID de IGDB del juego para el cual se solicitan los detalles. (default to undefined)

const { status, data } = await apiInstance.getGameDetails(
    igdbId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **igdbId** | [**number**] | ID de IGDB del juego para el cual se solicitan los detalles. | defaults to undefined|


### Return type

**GameDetailDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. Podría ocurrir si hay problemas al contactar IGDB. |  -  |
|**404** | No encontrado. El juego con el IGDB ID especificado no se encontró o el usuario (si está autenticado) no pudo ser verificado. |  -  |
|**200** | Detalles del juego recuperados exitosamente. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMyGameLibrary**
> Array<UserGameResponseDTO> getMyGameLibrary()

Recupera todas las entradas de juegos que el usuario actualmente autenticado tiene en su biblioteca personal, incluyendo el estado, puntuación, plataforma y otros datos específicos del usuario para cada juego. Requiere autenticación.

### Example

```typescript
import {
    UserGameLibraryControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UserGameLibraryControllerApi(configuration);

const { status, data } = await apiInstance.getMyGameLibrary();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<UserGameResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Biblioteca de juegos recuperada exitosamente. La lista puede estar vacía si el usuario no tiene juegos añadidos. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getPublicUserLibrary**
> Array<UserGameResponseDTO> getPublicUserLibrary()

Recupera la biblioteca de juegos de un usuario específico, sujeto a los permisos de visibilidad del perfil (PÚBLICO o SOLO_AMIGOS). Si el perfil es SOLO_AMIGOS, se requiere autenticación para verificar la amistad. Este endpoint es público.

### Example

```typescript
import {
    UserGameLibraryControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UserGameLibraryControllerApi(configuration);

let publicId: string; //ID público (UUID) del usuario cuya biblioteca se desea obtener. (default to undefined)

const { status, data } = await apiInstance.getPublicUserLibrary(
    publicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **publicId** | [**string**] | ID público (UUID) del usuario cuya biblioteca se desea obtener. | defaults to undefined|


### Return type

**Array<UserGameResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Biblioteca recuperada exitosamente. |  -  |
|**403** | Prohibido. La biblioteca del usuario es privada o solo para amigos y no eres amigo. |  -  |
|**404** | No encontrado. El usuario con el ID público especificado no existe. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getSpecificGameFromMyLibrary**
> UserGameResponseDTO getSpecificGameFromMyLibrary()

Recupera los detalles de un juego específico (identificado por su IGDB ID) tal como existe en la biblioteca personal del usuario autenticado. Esto incluye el estado, puntuación, y otros datos que el usuario haya registrado para ese juego. Requiere autenticación.

### Example

```typescript
import {
    UserGameLibraryControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UserGameLibraryControllerApi(configuration);

let igdbId: number; //ID de IGDB del juego a obtener de la biblioteca del usuario. (default to undefined)

const { status, data } = await apiInstance.getSpecificGameFromMyLibrary(
    igdbId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **igdbId** | [**number**] | ID de IGDB del juego a obtener de la biblioteca del usuario. | defaults to undefined|


### Return type

**UserGameResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Juego específico de la biblioteca recuperado exitosamente. |  -  |
|**404** | No encontrado. El juego con el IGDB ID especificado no se encontró en la biblioteca del usuario, o el usuario autenticado no pudo ser verificado. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **removeGameFromMyLibrary**
> removeGameFromMyLibrary()

Permite al usuario autenticado eliminar un juego específico (identificado por su IGDB ID) de su biblioteca personal. Requiere autenticación.

### Example

```typescript
import {
    UserGameLibraryControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new UserGameLibraryControllerApi(configuration);

let igdbId: number; //ID de IGDB del juego a eliminar de la biblioteca del usuario. (default to undefined)

const { status, data } = await apiInstance.removeGameFromMyLibrary(
    igdbId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **igdbId** | [**number**] | ID de IGDB del juego a eliminar de la biblioteca del usuario. | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**204** | Juego eliminado de la biblioteca exitosamente. No hay contenido en la respuesta. |  -  |
|**404** | No encontrado. El juego con el IGDB ID especificado no se encontró en la biblioteca del usuario para eliminar, o el usuario autenticado no pudo ser verificado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

