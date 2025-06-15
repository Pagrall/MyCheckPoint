# FriendshipControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**acceptFriendRequest**](#acceptfriendrequest) | **PUT** /api/v1/friends/requests/accept/{requesterUserPublicId} | Aceptar una solicitud de amistad pendiente|
|[**declineOrCancelFriendRequest**](#declineorcancelfriendrequest) | **DELETE** /api/v1/friends/requests/decline/{requesterUserPublicId} | Rechazar o cancelar una solicitud de amistad pendiente|
|[**getMyFriends**](#getmyfriends) | **GET** /api/v1/friends | Obtener la lista de amigos del usuario autenticado|
|[**getPendingRequestsReceived**](#getpendingrequestsreceived) | **GET** /api/v1/friends/requests/received | Obtener las solicitudes de amistad pendientes recibidas por el usuario autenticado|
|[**getPendingRequestsSent**](#getpendingrequestssent) | **GET** /api/v1/friends/requests/sent | Obtener las solicitudes de amistad pendientes enviadas por el usuario autenticado|
|[**removeFriend**](#removefriend) | **DELETE** /api/v1/friends/{friendUserPublicId} | Eliminar un amigo|
|[**sendFriendRequest**](#sendfriendrequest) | **POST** /api/v1/friends/requests/send/{receiverUserPublicId} | Enviar una solicitud de amistad|

# **acceptFriendRequest**
> FriendshipResponseDTO acceptFriendRequest()

Permite al usuario autenticado (que es el receptor de la solicitud) aceptar una solicitud de amistad pendiente de otro usuario. La solicitud debe estar en estado PENDIENTE. Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

let requesterUserPublicId: string; //ID público (UUID) del usuario que envió la solicitud de amistad. (default to undefined)

const { status, data } = await apiInstance.acceptFriendRequest(
    requesterUserPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **requesterUserPublicId** | [**string**] | ID público (UUID) del usuario que envió la solicitud de amistad. | defaults to undefined|


### Return type

**FriendshipResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Solicitud de amistad aceptada exitosamente. Devuelve el estado actualizado de la amistad. |  -  |
|**404** | No encontrado. No se encontró una solicitud de amistad pendiente del usuario especificado, o el usuario solicitante/actual no existe. |  -  |
|**500** | Error interno del servidor. |  -  |
|**403** | Prohibido. El usuario autenticado no es el receptor de la solicitud de amistad pendiente o no tiene permisos para realizar esta acción. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **declineOrCancelFriendRequest**
> declineOrCancelFriendRequest()

Permite al usuario autenticado (que es el receptor de la solicitud) rechazar una solicitud de amistad pendiente. Alternativamente, si el usuario autenticado fue quien envió la solicitud y esta aún está pendiente, puede usar este endpoint para cancelarla (aunque semánticamente esto último podría ser un endpoint diferente, la lógica actual del servicio elimina la solicitud PENDIENTE). La solicitud de amistad es eliminada de la base de datos. Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

let requesterUserPublicId: string; //ID público (UUID) del usuario que originalmente envió la solicitud de amistad que se desea rechazar/cancelar. (default to undefined)

const { status, data } = await apiInstance.declineOrCancelFriendRequest(
    requesterUserPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **requesterUserPublicId** | [**string**] | ID público (UUID) del usuario que originalmente envió la solicitud de amistad que se desea rechazar/cancelar. | defaults to undefined|


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
|**204** | Solicitud de amistad rechazada/cancelada y eliminada exitosamente. No hay contenido en la respuesta. |  -  |
|**403** | Prohibido. El usuario autenticado no es el receptor de la solicitud de amistad pendiente que intenta rechazar (o no tiene permisos para cancelarla si fuera el emisor y el endpoint se usara así). |  -  |
|**404** | No encontrado. No se encontró una solicitud de amistad pendiente del usuario especificado, o el usuario solicitante/actual no existe. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMyFriends**
> Array<FriendshipResponseDTO> getMyFriends()

Recupera una lista de todos los usuarios que son amigos del usuario actualmente autenticado (es decir, aquellas relaciones con estado \'ACCEPTED\'). Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

const { status, data } = await apiInstance.getMyFriends();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<FriendshipResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Lista de amigos recuperada exitosamente. La lista puede estar vacía si el usuario no tiene amigos. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getPendingRequestsReceived**
> Array<FriendshipResponseDTO> getPendingRequestsReceived()

Recupera una lista de todas las solicitudes de amistad que el usuario actualmente autenticado ha recibido y aún están pendientes de acción (aceptar o rechazar). Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

const { status, data } = await apiInstance.getPendingRequestsReceived();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<FriendshipResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**500** | Error interno del servidor. |  -  |
|**200** | Lista de solicitudes pendientes recibidas recuperada exitosamente. La lista puede estar vacía. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getPendingRequestsSent**
> Array<FriendshipResponseDTO> getPendingRequestsSent()

Recupera una lista de todas las solicitudes de amistad que el usuario actualmente autenticado ha enviado y que aún están pendientes de respuesta por parte de los destinatarios. Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

const { status, data } = await apiInstance.getPendingRequestsSent();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<FriendshipResponseDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Lista de solicitudes pendientes enviadas recuperada exitosamente. La lista puede estar vacía. |  -  |
|**500** | Error interno del servidor. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |
|**404** | No encontrado. El usuario autenticado no pudo ser verificado en la base de datos (caso anómalo). |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **removeFriend**
> removeFriend()

Permite al usuario autenticado eliminar una amistad existente con otro usuario, especificado por su ID público. La relación de amistad es eliminada de la base de datos. Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

let friendUserPublicId: string; //ID público (UUID) del amigo que se desea eliminar. (default to undefined)

const { status, data } = await apiInstance.removeFriend(
    friendUserPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **friendUserPublicId** | [**string**] | ID público (UUID) del amigo que se desea eliminar. | defaults to undefined|


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
|**404** | No encontrado. No se encontró una amistad con el usuario especificado, o el amigo a eliminar no existe, o el usuario actual no pudo ser verificado. |  -  |
|**500** | Error interno del servidor. |  -  |
|**204** | Amigo eliminado exitosamente. No hay contenido en la respuesta. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **sendFriendRequest**
> FriendshipResponseDTO sendFriendRequest()

Permite al usuario autenticado enviar una solicitud de amistad a otro usuario especificado por su ID público. Si ya existe una solicitud pendiente del receptor hacia el emisor, la amistad se aceptará automáticamente. Requiere autenticación.

### Example

```typescript
import {
    FriendshipControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new FriendshipControllerApi(configuration);

let receiverUserPublicId: string; //ID público (UUID) del usuario al que se le envía la solicitud de amistad. (default to undefined)

const { status, data } = await apiInstance.sendFriendRequest(
    receiverUserPublicId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **receiverUserPublicId** | [**string**] | ID público (UUID) del usuario al que se le envía la solicitud de amistad. | defaults to undefined|


### Return type

**FriendshipResponseDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**409** | Conflicto. Ya existe una amistad o una solicitud de amistad pendiente con este usuario. |  -  |
|**500** | Error interno del servidor. |  -  |
|**400** | Solicitud incorrecta. El usuario no puede enviarse una solicitud a sí mismo. |  -  |
|**404** | No encontrado. El usuario receptor especificado por &#x60;receiverUserPublicId&#x60; no existe, o el usuario emisor no pudo ser verificado. |  -  |
|**200** | Solicitud de amistad enviada o amistad auto-aceptada exitosamente. Devuelve el estado de la amistad/solicitud. |  -  |
|**401** | No autorizado. El token JWT es inválido, ha expirado o no se proporcionó. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

