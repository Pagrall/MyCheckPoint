# TierListControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**addItemToTierListSection**](#additemtotierlistsection) | **POST** /api/v1/tierlists/{tierListPublicId}/sections/{sectionInternalId}/items | Añadir o mover un ítem (juego) a una sección específica de una Tier List de perfil|
|[**addItemToUnclassifiedSection**](#additemtounclassifiedsection) | **POST** /api/v1/tierlists/{tierListPublicId}/items/unclassified | Añadir o mover un ítem (juego) a la sección \&#39;Sin Clasificar\&#39; de una Tier List de perfil|
|[**addSectionToTierList**](#addsectiontotierlist) | **POST** /api/v1/tierlists/{tierListPublicId}/sections | Añadir una nueva sección (tier) a una Tier List existente|
|[**createProfileTierList**](#createprofiletierlist) | **POST** /api/v1/users/me/tierlists | Crear una nueva Tier List de perfil para el usuario autenticado|
|[**deleteTierList**](#deletetierlist) | **DELETE** /api/v1/tierlists/{tierListPublicId} | Eliminar una Tier List existente|
|[**getAllProfileTierListsForCurrentUser**](#getallprofiletierlistsforcurrentuser) | **GET** /api/v1/users/me/tierlists | Obtener todas las Tier Lists de perfil del usuario autenticado|
|[**getAllPublicTierLists**](#getallpublictierlists) | **GET** /api/v1/tierlists/public | Obtener todas las Tier Lists públicas|
|[**getOrCreateTierListForGameList**](#getorcreatetierlistforgamelist) | **GET** /api/v1/gamelists/{gameListPublicId}/tierlist | Obtener o crear la Tier List asociada a una GameList específica|
|[**getTierListByPublicId**](#gettierlistbypublicid) | **GET** /api/v1/tierlists/{tierListPublicId} | Obtener una Tier List específica por su ID público|
|[**moveItemInTierList**](#moveitemintierlist) | **PUT** /api/v1/tierlists/{tierListPublicId}/items/{tierListItemInternalId}/move | Mover un ítem (juego) dentro de una Tier List|
|[**removeItemFromTierList**](#removeitemfromtierlist) | **DELETE** /api/v1/tierlists/{tierListPublicId}/items/{tierListItemInternalId} | Eliminar un ítem (juego) de una Tier List de perfil|
|[**removeSectionFromTierList**](#removesectionfromtierlist) | **DELETE** /api/v1/tierlists/{tierListPublicId}/sections/{sectionInternalId} | Eliminar una sección (tier) de una Tier List|
|[**updateTierListMetadata**](#updatetierlistmetadata) | **PUT** /api/v1/tierlists/{tierListPublicId} | Actualizar los metadatos de una Tier List existente|
|[**updateTierSection**](#updatetiersection) | **PUT** /api/v1/{tierListPublicId}/sections/{sectionInternalId} | Actualizar una sección (tier) específica de una Tier List|

# **addItemToTierListSection**
> TierListResponseDTO addItemToTierListSection(tierListItemAddRequestDTO)

Permite al propietario autenticado añadir un juego de su biblioteca (UserGame) a una sección específica de una Tier List de tipo \'PROFILE_GLOBAL\'. Si el juego ya está en otra sección de esta Tier List, se moverá a la nueva sección y posición. No se puede usar este endpoint para Tier Lists de tipo \'FROM_GAMELIST\' ni para añadir a la sección \'Juegos por Clasificar\'. Se puede especificar el orden del ítem dentro de la sección. Requiere autenticación y ser propietario.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierListItemAddRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List a la que se añadirá el ítem. (default to undefined)
let sectionInternalId: number; //ID interno (Long) de la sección (tier) destino dentro de la Tier List. (default to undefined)
let tierListItemAddRequestDTO: TierListItemAddRequestDTO; //

const { status, data } = await apiInstance.addItemToTierListSection(
    tierListPublicId,
    sectionInternalId,
    tierListItemAddRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListItemAddRequestDTO** | **TierListItemAddRequestDTO**|  | |
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List a la que se añadirá el ítem. | defaults to undefined|
| **sectionInternalId** | [**number**] | ID interno (Long) de la sección (tier) destino dentro de la Tier List. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Ítem añadido o movido a la sección exitosamente. Devuelve la Tier List completa y actualizada. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Solicitud incorrecta. El &#x60;user_game_id&#x60; es nulo, la Tier List es de tipo \&#39;FROM_GAMELIST\&#39;, o se intenta añadir a la sección \&#39;Sin Clasificar\&#39; usando este endpoint. |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List o el UserGame a añadir no le pertenece. |  -  |
|**404** | No encontrado. La Tier List, la sección especificada o el UserGame a añadir no fueron encontrados, o el usuario no pudo ser verificado. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **addItemToUnclassifiedSection**
> TierListResponseDTO addItemToUnclassifiedSection(tierListItemAddRequestDTO)

Permite al propietario autenticado añadir un juego de su biblioteca (UserGame) directamente a la sección \'Juegos por Clasificar\' de una Tier List de tipo \'PROFILE_GLOBAL\'. Si el juego ya está en otra sección de esta Tier List, se moverá a la sección \'Juegos por Clasificar\'. No se puede usar este endpoint para Tier Lists de tipo \'FROM_GAMELIST\'. Se puede especificar el orden del ítem dentro de la sección. Requiere autenticación y ser propietario.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierListItemAddRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List a la que se añadirá el ítem en la sección \'Sin Clasificar\'. (default to undefined)
let tierListItemAddRequestDTO: TierListItemAddRequestDTO; //

const { status, data } = await apiInstance.addItemToUnclassifiedSection(
    tierListPublicId,
    tierListItemAddRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListItemAddRequestDTO** | **TierListItemAddRequestDTO**|  | |
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List a la que se añadirá el ítem en la sección \&#39;Sin Clasificar\&#39;. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**404** | No encontrado. La Tier List o el UserGame a añadir no fueron encontrados, o el usuario no pudo ser verificado. |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List o el UserGame a añadir no le pertenece. |  -  |
|**400** | Solicitud incorrecta. El &#x60;user_game_id&#x60; es nulo, o la Tier List es de tipo \&#39;FROM_GAMELIST\&#39;. |  -  |
|**500** | Error interno del servidor (ej. la sección \&#39;Sin Clasificar\&#39; no se encontró). |  -  |
|**200** | Ítem añadido o movido a la sección \&#39;Sin Clasificar\&#39; exitosamente. Devuelve la Tier List completa y actualizada. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **addSectionToTierList**
> TierListResponseDTO addSectionToTierList(tierSectionRequestDTO)

Permite al propietario autenticado de una Tier List añadir una nueva sección personalizada. Existe un límite en la cantidad de secciones personalizables que se pueden añadir. Requiere autenticación y ser el propietario de la Tier List.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierSectionRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List a la que se añadirá la nueva sección. (default to undefined)
let tierSectionRequestDTO: TierSectionRequestDTO; //

const { status, data } = await apiInstance.addSectionToTierList(
    tierListPublicId,
    tierSectionRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierSectionRequestDTO** | **TierSectionRequestDTO**|  | |
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List a la que se añadirá la nueva sección. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Sección añadida exitosamente. Devuelve la Tier List completa y actualizada con la nueva sección. |  -  |
|**500** | Error interno del servidor. |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List a la que intenta añadir una sección. |  -  |
|**400** | Datos de entrada inválidos (ej. nombre de sección vacío o demasiado largo) o se ha alcanzado el límite máximo de secciones personalizables. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. La Tier List con el ID público especificado no fue encontrada para el usuario actual, o el usuario autenticado no pudo ser verificado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createProfileTierList**
> TierListResponseDTO createProfileTierList(tierListCreateRequestDTO)

Permite al usuario autenticado crear una nueva Tier List de tipo \'PROFILE_GLOBAL\'. Se requiere un nombre para la lista y opcionalmente una descripción y si es pública. Se crearán secciones por defecto (S, A, B, C, D y \'Juegos por Clasificar\'). Requiere autenticación.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierListCreateRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListCreateRequestDTO: TierListCreateRequestDTO; //

const { status, data } = await apiInstance.createProfileTierList(
    tierListCreateRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListCreateRequestDTO** | **TierListCreateRequestDTO**|  | |


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Tier List de perfil creada exitosamente. Devuelve los detalles de la lista recién creada, incluyendo las secciones por defecto. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**400** | Datos de entrada inválidos. Ocurre si los datos en &#x60;TierListCreateRequestDTO&#x60; no pasan las validaciones (ej. nombre vacío o demasiado largo). |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteTierList**
> deleteTierList()

Permite al propietario autenticado de una Tier List eliminarla permanentemente. Esto también eliminará todas las secciones y los ítems contenidos en ella. Requiere autenticación y ser el propietario de la Tier List.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List a eliminar. (default to undefined)

const { status, data } = await apiInstance.deleteTierList(
    tierListPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List a eliminar. | defaults to undefined|


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
|**204** | Tier List eliminada exitosamente. No hay contenido en la respuesta. |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List que intenta eliminar. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. La Tier List con el ID público especificado no fue encontrada para el usuario actual, o el usuario autenticado no pudo ser verificado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAllProfileTierListsForCurrentUser**
> Array<TierListResponseDTO> getAllProfileTierListsForCurrentUser()

Recupera una lista de todas las Tier Lists de tipo \'PROFILE_GLOBAL\' creadas por el usuario actualmente autenticado. Cada Tier List incluye sus secciones y los ítems clasificados. Requiere autenticación.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

const { status, data } = await apiInstance.getAllProfileTierListsForCurrentUser();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<TierListResponseDTO>**

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
|**200** | Tier Lists de perfil recuperadas exitosamente. La lista puede estar vacía si el usuario no ha creado ninguna. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAllPublicTierLists**
> Array<TierListResponseDTO> getAllPublicTierLists()

Recupera una lista de todas las Tier Lists que han sido marcadas como públicas por sus creadores. Cada Tier List incluye sus secciones y los ítems clasificados. Las listas se devuelven ordenadas por la fecha de última actualización. Este endpoint es público y no requiere autenticación.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

const { status, data } = await apiInstance.getAllPublicTierLists();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<TierListResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**200** | Lista de Tier Lists públicas recuperada exitosamente. La lista puede estar vacía si no hay ninguna. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getOrCreateTierListForGameList**
> TierListResponseDTO getOrCreateTierListForGameList()

Recupera la Tier List de tipo \'FROM_GAMELIST\' asociada a la GameList especificada por su ID público. Si no existe una Tier List para esa GameList, se crea una nueva automáticamente con secciones por defecto y se sincroniza con los juegos de la GameList (añadiéndolos a la sección \'Sin Clasificar\'). Este endpoint es público si la GameList y la TierList resultante son públicas. Si la GameList es privada, se requiere autenticación y ser el propietario para acceder o crear la TierList asociada. Si se proporciona un token JWT válido, la respuesta puede incluir información adicional si el usuario es el propietario.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let gameListPublicId: string; //ID público (UUID) de la GameList para la cual se obtendrá o creará la Tier List. (default to undefined)

const { status, data } = await apiInstance.getOrCreateTierListForGameList(
    gameListPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **gameListPublicId** | [**string**] | ID público (UUID) de la GameList para la cual se obtendrá o creará la Tier List. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Tier List recuperada o creada y sincronizada exitosamente. |  -  |
|**404** | No encontrado. La GameList con el ID público especificado no existe, o el usuario (si está autenticado) no pudo ser verificado. |  -  |
|**403** | Prohibido. El usuario autenticado no tiene permiso para acceder a la GameList especificada (si es privada y no es el propietario) o a la TierList resultante. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. Se proporcionó un token JWT inválido o expirado al intentar acceder a recursos que lo requerían (ej. GameList privada). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTierListByPublicId**
> TierListResponseDTO getTierListByPublicId()

Recupera los detalles completos de una Tier List (incluyendo secciones e ítems) utilizando su ID público (UUID). Si la Tier List es pública, cualquiera puede acceder a ella. Si la Tier List es privada, solo el propietario autenticado puede acceder. La autenticación (JWT) es opcional; si se proporciona un token válido y la lista es privada, se verificará la propiedad.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List a obtener. (default to undefined)

const { status, data } = await apiInstance.getTierListByPublicId(
    tierListPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List a obtener. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**403** | Prohibido. La Tier List es privada y el usuario (autenticado o anónimo) no tiene permiso para accederla. |  -  |
|**200** | Tier List recuperada exitosamente. |  -  |
|**401** | No autorizado. Se proporcionó un token JWT inválido o expirado al intentar acceder a una Tier List privada. |  -  |
|**404** | No encontrado. La Tier List con el ID público especificado no existe, o el usuario (si está autenticado) no pudo ser verificado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **moveItemInTierList**
> TierListResponseDTO moveItemInTierList(tierListItemMoveRequestDTO)

Permite al propietario autenticado mover un ítem existente (identificado por `tierListItemInternalId`) a una nueva sección (`target_section_internal_id`) y/o a una nueva posición (`new_order`) dentro de esa sección en una Tier List específica. Para Tier Lists de tipo \'FROM_GAMELIST\', se verifica que el juego del ítem aún pertenezca a la GameList origen. Requiere autenticación y ser propietario de la Tier List.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierListItemMoveRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List que contiene el ítem a mover. (default to undefined)
let tierListItemInternalId: number; //ID interno (Long) del TierListItem a mover. (default to undefined)
let tierListItemMoveRequestDTO: TierListItemMoveRequestDTO; //

const { status, data } = await apiInstance.moveItemInTierList(
    tierListPublicId,
    tierListItemInternalId,
    tierListItemMoveRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListItemMoveRequestDTO** | **TierListItemMoveRequestDTO**|  | |
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List que contiene el ítem a mover. | defaults to undefined|
| **tierListItemInternalId** | [**number**] | ID interno (Long) del TierListItem a mover. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List. |  -  |
|**500** | Error interno del servidor. |  -  |
|**200** | Ítem movido exitosamente. Devuelve la Tier List completa y actualizada. |  -  |
|**400** | Solicitud incorrecta. Los datos en &#x60;TierListItemMoveRequestDTO&#x60; son inválidos (ej. IDs nulos), o la operación es inválida para el tipo de Tier List (ej. juego ya no en GameList origen). |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. La Tier List, el ítem a mover, o la sección destino no fueron encontrados, o el usuario no pudo ser verificado. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **removeItemFromTierList**
> TierListResponseDTO removeItemFromTierList()

Permite al propietario autenticado eliminar un ítem específico (identificado por `tierListItemInternalId`) de una de sus Tier Lists de tipo \'PROFILE_GLOBAL\'. Esto no elimina el juego de la biblioteca general del usuario, solo de esta Tier List. No se puede usar este endpoint para Tier Lists de tipo \'FROM_GAMELIST\'. Requiere autenticación y ser propietario.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List de la cual se eliminará el ítem. (default to undefined)
let tierListItemInternalId: number; //ID interno (Long) del TierListItem a eliminar de la Tier List. (default to undefined)

const { status, data } = await apiInstance.removeItemFromTierList(
    tierListPublicId,
    tierListItemInternalId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List de la cual se eliminará el ítem. | defaults to undefined|
| **tierListItemInternalId** | [**number**] | ID interno (Long) del TierListItem a eliminar de la Tier List. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**404** | No encontrado. La Tier List, o el ítem específico dentro de ella, no fueron encontrados para el usuario actual, o el usuario no pudo ser verificado. |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List. |  -  |
|**200** | Ítem eliminado exitosamente de la Tier List. Devuelve la Tier List completa y actualizada. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Solicitud incorrecta. No se pueden eliminar ítems de una Tier List de tipo \&#39;FROM_GAMELIST\&#39; a través de este endpoint. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **removeSectionFromTierList**
> TierListResponseDTO removeSectionFromTierList()

Permite al propietario autenticado de una Tier List eliminar una de sus secciones personalizadas. La sección por defecto \'Juegos por Clasificar\' no puede ser eliminada. Debe quedar al menos una sección personalizable tras la eliminación. Si la sección eliminada contenía ítems (juegos), estos serán movidos a la sección \'Juegos por Clasificar\'. Requiere autenticación y ser el propietario de la Tier List.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List de la cual se eliminará la sección. (default to undefined)
let sectionInternalId: number; //ID interno (Long) de la sección (tier) a eliminar. (default to undefined)

const { status, data } = await apiInstance.removeSectionFromTierList(
    tierListPublicId,
    sectionInternalId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List de la cual se eliminará la sección. | defaults to undefined|
| **sectionInternalId** | [**number**] | ID interno (Long) de la sección (tier) a eliminar. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor (ej. la sección \&#39;Sin Clasificar\&#39; no se encontró al intentar mover ítems). |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List. |  -  |
|**400** | Solicitud incorrecta. No se puede eliminar la sección por defecto \&#39;Juegos por Clasificar\&#39; o se intenta eliminar la última sección personalizable. |  -  |
|**404** | No encontrado. La Tier List o la sección especificada no fueron encontradas para el usuario actual, o el usuario no pudo ser verificado. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**200** | Sección eliminada exitosamente (e ítems reubicados si aplicable). Devuelve la Tier List completa y actualizada. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateTierListMetadata**
> TierListResponseDTO updateTierListMetadata(tierListUpdateRequestDTO)

Permite al propietario autenticado de una Tier List modificar sus metadatos como el nombre, la descripción y el estado de visibilidad (pública/privada). Solo los campos proporcionados en el cuerpo de la solicitud serán actualizados. Requiere autenticación y ser el propietario de la Tier List.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierListUpdateRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List a actualizar. (default to undefined)
let tierListUpdateRequestDTO: TierListUpdateRequestDTO; //

const { status, data } = await apiInstance.updateTierListMetadata(
    tierListPublicId,
    tierListUpdateRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierListUpdateRequestDTO** | **TierListUpdateRequestDTO**|  | |
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List a actualizar. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Metadatos de la Tier List actualizados exitosamente. Devuelve la Tier List completa y actualizada. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Datos de entrada inválidos. Ocurre si los datos en &#x60;TierListUpdateRequestDTO&#x60; no pasan las validaciones (ej. nombre demasiado largo). |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. La Tier List con el ID público especificado no fue encontrada para el usuario actual, o el usuario autenticado no pudo ser verificado. |  -  |
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List que intenta modificar. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateTierSection**
> TierListResponseDTO updateTierSection(tierSectionRequestDTO)

Permite al propietario autenticado de una Tier List cambiar las propiedades de una de sus secciones, como el nombre y el color. Requiere autenticación y ser el propietario de la Tier List.

### Example

```typescript
import {
    TierListControllerApi,
    Configuration,
    TierSectionRequestDTO
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new TierListControllerApi(configuration);

let tierListPublicId: string; //ID público (UUID) de la Tier List que contiene la sección a actualizar. (default to undefined)
let sectionInternalId: number; //ID interno (Long) de la sección (tier) cuyo nombre se va a actualizar. (default to undefined)
let tierSectionRequestDTO: TierSectionRequestDTO; //

const { status, data } = await apiInstance.updateTierSection(
    tierListPublicId,
    sectionInternalId,
    tierSectionRequestDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **tierSectionRequestDTO** | **TierSectionRequestDTO**|  | |
| **tierListPublicId** | [**string**] | ID público (UUID) de la Tier List que contiene la sección a actualizar. | defaults to undefined|
| **sectionInternalId** | [**number**] | ID interno (Long) de la sección (tier) cuyo nombre se va a actualizar. | defaults to undefined|


### Return type

**TierListResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**403** | Prohibido. El usuario autenticado no es el propietario de la Tier List. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Datos de entrada inválidos. El DTO de la sección no cumple con las validaciones (ej. nombre vacío, formato de color inválido). |  -  |
|**404** | No encontrado. La Tier List con el ID público especificado o la sección con el ID interno no fueron encontradas. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**200** | Sección actualizada exitosamente. Devuelve la Tier List completa y actualizada. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

