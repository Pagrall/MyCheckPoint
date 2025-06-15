# GameListControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**addGameToMyCustomList**](#addgametomycustomlist) | **POST** /api/v1/users/me/gamelists/{listPublicId}/games | Añadir un juego de la biblioteca del usuario a una de sus listas de juegos|
|[**createMyGameList**](#createmygamelist) | **POST** /api/v1/users/me/gamelists | Crear una nueva lista de juegos para el usuario autenticado|
|[**deleteMyGameList**](#deletemygamelist) | **DELETE** /api/v1/users/me/gamelists/{listPublicId} | Eliminar una lista de juegos del usuario autenticado|
|[**getMyGameLists**](#getmygamelists) | **GET** /api/v1/users/me/gamelists | Obtener todas las listas de juegos del usuario autenticado|
|[**getMySpecificGameList**](#getmyspecificgamelist) | **GET** /api/v1/users/me/gamelists/{listPublicId} | Obtener una lista de juegos específica del usuario autenticado por su ID público|
|[**removeGameFromMyCustomList**](#removegamefrommycustomlist) | **DELETE** /api/v1/users/me/gamelists/{listPublicId}/games/{userGameInternalId} | Eliminar un juego de una lista de juegos personalizada del usuario autenticado|
|[**updateMyGameList**](#updatemygamelist) | **PUT** /api/v1/users/me/gamelists/{listPublicId} | Actualizar una lista de juegos existente del usuario autenticado|
|[**viewAllPublicGameLists**](#viewallpublicgamelists) | **GET** /api/v1/gamelists/public | Obtener todas las listas de juegos públicas|
|[**viewPublicGameList**](#viewpublicgamelist) | **GET** /api/v1/gamelists/{listPublicId}/public | Obtener una lista de juegos pública específica por su ID público|

# **addGameToMyCustomList**
> GameListResponseDTO addGameToMyCustomList(addGameToCustomListRequestDTO)

Permite al usuario autenticado añadir una entrada de juego existente en su biblioteca personal (identificada por su `user_game_id` interno) a una de sus listas de juegos personalizadas (identificada por `listPublicId`). El juego no se añade si ya está presente en la lista. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration,
    AddGameToCustomListRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let listPublicId: string; //ID público (UUID) de la lista de juegos a la que se añadirá el juego. (default to undefined)
let addGameToCustomListRequestDTO: AddGameToCustomListRequestDTO; //

const { status, data } = await apiInstance.addGameToMyCustomList(
    listPublicId,
    addGameToCustomListRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **addGameToCustomListRequestDTO** | **AddGameToCustomListRequestDTO**|  | |
| **listPublicId** | [**string**] | ID público (UUID) de la lista de juegos a la que se añadirá el juego. | defaults to undefined|


### Return type

**GameListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**200** | Juego añadido a la lista exitosamente (o ya estaba presente). Devuelve la lista actualizada. |  -  |
|**400** | Datos de entrada inválidos. El &#x60;user_game_id&#x60; en el cuerpo de la solicitud es nulo o inválido. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. La lista de juegos especificada (&#x60;listPublicId&#x60;) o la entrada de juego de la biblioteca (&#x60;user_game_id&#x60;) no fueron encontradas, o el usuario autenticado no pudo ser verificado. |  -  |
|**403** | Prohibido. El juego que se intenta añadir no pertenece a la biblioteca del usuario autenticado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createMyGameList**
> GameListResponseDTO createMyGameList(gameListRequestDTO)

Permite al usuario autenticado crear una nueva lista de juegos personalizada. Se requiere un nombre para la lista y se puede especificar si es pública o privada. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration,
    GameListRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let gameListRequestDTO: GameListRequestDTO; //

const { status, data } = await apiInstance.createMyGameList(
    gameListRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **gameListRequestDTO** | **GameListRequestDTO**|  | |


### Return type

**GameListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**201** | Lista de juegos creada exitosamente. Devuelve los detalles de la lista recién creada. |  -  |
|**400** | Datos de entrada inválidos. Ocurre si los datos en &#x60;GameListRequestDTO&#x60; no pasan las validaciones (ej. nombre vacío). |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteMyGameList**
> deleteMyGameList()

Permite al usuario autenticado eliminar una de sus listas de juegos existentes, identificada por su ID público (UUID). Esto no elimina los juegos de la biblioteca del usuario, solo la lista en sí. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let listPublicId: string; //ID público (UUID) de la lista de juegos a eliminar. (default to undefined)

const { status, data } = await apiInstance.deleteMyGameList(
    listPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **listPublicId** | [**string**] | ID público (UUID) de la lista de juegos a eliminar. | defaults to undefined|


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
|**404** | No encontrado. La lista de juegos con el ID público especificado no fue encontrada para el usuario actual, o el usuario autenticado no pudo ser verificado. |  -  |
|**204** | Lista de juegos eliminada exitosamente. No hay contenido en la respuesta. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMyGameLists**
> Array<GameListResponseDTO> getMyGameLists()

Recupera una lista de todas las listas de juegos personalizadas creadas por el usuario actualmente autenticado. Las listas se devuelven ordenadas por la fecha de última actualización de forma descendente. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

const { status, data } = await apiInstance.getMyGameLists();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameListResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**200** | Listas de juegos recuperadas exitosamente. La lista puede estar vacía si el usuario no ha creado ninguna. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMySpecificGameList**
> GameListResponseDTO getMySpecificGameList()

Recupera los detalles y los juegos contenidos en una lista de juegos específica, identificada por su ID público (UUID), que pertenezca al usuario actualmente autenticado. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let listPublicId: string; //ID público (UUID) de la lista de juegos a obtener. (default to undefined)

const { status, data } = await apiInstance.getMySpecificGameList(
    listPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **listPublicId** | [**string**] | ID público (UUID) de la lista de juegos a obtener. | defaults to undefined|


### Return type

**GameListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**404** | No encontrado. La lista de juegos con el ID público especificado no fue encontrada para el usuario actual, o el usuario autenticado no pudo ser verificado. |  -  |
|**500** | Error interno del servidor. |  -  |
|**200** | Lista de juegos específica recuperada exitosamente. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **removeGameFromMyCustomList**
> removeGameFromMyCustomList()

Permite al usuario autenticado eliminar un juego específico (identificado por su `userGameInternalId`) de una de sus listas de juegos (identificada por `listPublicId`). Esto no elimina el juego de la biblioteca general del usuario, solo de esta lista en particular. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let listPublicId: string; //ID público (UUID) de la lista de juegos de la cual se eliminará el juego. (default to undefined)
let userGameInternalId: number; //ID interno de la entrada \'UserGame\' (juego en la biblioteca del usuario) a eliminar de la lista. (default to undefined)

const { status, data } = await apiInstance.removeGameFromMyCustomList(
    listPublicId,
    userGameInternalId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **listPublicId** | [**string**] | ID público (UUID) de la lista de juegos de la cual se eliminará el juego. | defaults to undefined|
| **userGameInternalId** | [**number**] | ID interno de la entrada \&#39;UserGame\&#39; (juego en la biblioteca del usuario) a eliminar de la lista. | defaults to undefined|


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
|**204** | Juego eliminado de la lista exitosamente (o no se encontraba en ella). No hay contenido en la respuesta. |  -  |
|**403** | Prohibido. El juego que se intenta eliminar de la lista no pertenece a la biblioteca del usuario autenticado (si esta verificación se realiza antes de intentar la eliminación de la lista). |  -  |
|**500** | Error interno del servidor. |  -  |
|**404** | No encontrado. La lista de juegos (&#x60;listPublicId&#x60;) o la entrada de juego (&#x60;userGameInternalId&#x60;) no fueron encontradas, o el usuario actual no pudo ser verificado. También podría ocurrir si el juego especificado no estaba en la lista para ser eliminado (aunque el servicio actual no lanza error por esto). |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateMyGameList**
> GameListResponseDTO updateMyGameList(gameListRequestDTO)

Permite al usuario autenticado modificar los detalles (nombre, descripción, visibilidad) de una de sus listas de juegos existentes, identificada por su ID público (UUID). Solo los campos proporcionados en el cuerpo de la solicitud serán actualizados. Requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration,
    GameListRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let listPublicId: string; //ID público (UUID) de la lista de juegos a actualizar. (default to undefined)
let gameListRequestDTO: GameListRequestDTO; //

const { status, data } = await apiInstance.updateMyGameList(
    listPublicId,
    gameListRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **gameListRequestDTO** | **GameListRequestDTO**|  | |
| **listPublicId** | [**string**] | ID público (UUID) de la lista de juegos a actualizar. | defaults to undefined|


### Return type

**GameListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Lista de juegos actualizada exitosamente. Devuelve los detalles actualizados de la lista. |  -  |
|**404** | No encontrado. La lista de juegos con el ID público especificado no fue encontrada para el usuario actual, o el usuario autenticado no pudo ser verificado. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Datos de entrada inválidos. Ocurre si los datos en &#x60;GameListRequestDTO&#x60; no pasan las validaciones (ej. nombre en blanco si se modifica, descripción demasiado larga). |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **viewAllPublicGameLists**
> Array<GameListResponseDTO> viewAllPublicGameLists()

Recupera una lista de todas las listas de juegos que han sido marcadas como públicas por sus creadores. Las listas se devuelven ordenadas por la fecha de última actualización de forma descendente. Este endpoint es público y no requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

const { status, data } = await apiInstance.viewAllPublicGameLists();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameListResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**200** | Listas de juegos públicas recuperadas exitosamente. La lista puede estar vacía si no hay ninguna. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **viewPublicGameList**
> GameListResponseDTO viewPublicGameList()

Recupera los detalles y los juegos contenidos en una lista de juegos específica que haya sido marcada como pública, identificada por su ID público (UUID). Este endpoint es público y no requiere autenticación.

### Example

```typescript
import {
    GameListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameListControllerApi(configuration);

let listPublicId: string; //ID público (UUID) de la lista de juegos pública a obtener. (default to undefined)

const { status, data } = await apiInstance.viewPublicGameList(
    listPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **listPublicId** | [**string**] | ID público (UUID) de la lista de juegos pública a obtener. | defaults to undefined|


### Return type

**GameListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**404** | No encontrado. La lista de juegos pública con el ID especificado no fue encontrada o no es pública. |  -  |
|**500** | Error interno del servidor. |  -  |
|**200** | Lista de juegos pública recuperada exitosamente. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

