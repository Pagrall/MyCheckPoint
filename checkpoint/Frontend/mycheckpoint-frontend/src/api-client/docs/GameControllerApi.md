# GameControllerApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**buscarJuegosEnIgdb**](#buscarjuegosenigdb) | **GET** /api/juegos/igdb/buscar | Buscar juegos en IGDB por nombre|
|[**filtrarJuegosEnIgdb**](#filtrarjuegosenigdb) | **GET** /api/juegos/igdb/filtrar | Filtrar juegos en IGDB por múltiples criterios|
|[**findAllGameModes**](#findallgamemodes) | **GET** /api/juegos/igdb/game-modes | Obtener todos los modos de juego de IGDB|
|[**findAllGenres**](#findallgenres) | **GET** /api/juegos/igdb/genres | Obtener todos los géneros de IGDB|
|[**findAllThemes**](#findallthemes) | **GET** /api/juegos/igdb/themes | Obtener todos los temas de IGDB|
|[**findHighlyAnticipatedGames**](#findhighlyanticipatedgames) | **GET** /api/juegos/igdb/highly-anticipated | Obtener los próximos lanzamientos más esperados desde IGDB|
|[**findMostHypedGames**](#findmosthypedgames) | **GET** /api/juegos/igdb/most-hyped | Obtener los juegos más populares (hyped) desde IGDB|
|[**findRecentlyReleasedGames**](#findrecentlyreleasedgames) | **GET** /api/juegos/igdb/recently-released | Obtener juegos lanzados recientemente desde IGDB|
|[**findUpcomingReleases**](#findupcomingreleases) | **GET** /api/juegos/igdb/upcoming-releases | Obtener los próximos lanzamientos desde IGDB|

# **buscarJuegosEnIgdb**
> Array<GameDto> buscarJuegosEnIgdb()

Realiza una búsqueda de juegos en la base de datos de IGDB utilizando un término de búsqueda para el nombre. Devuelve un flujo (o lista) de juegos que coinciden, con un conjunto limitado de campos (nombre, calificación, carátula, fecha de lanzamiento, tipo, resumen, ID). Este endpoint es público.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

let nombre: string; //Término de búsqueda para el nombre del juego. (default to undefined)

const { status, data } = await apiInstance.buscarJuegosEnIgdb(
    nombre
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **nombre** | [**string**] | Término de búsqueda para el nombre del juego. | defaults to undefined|


### Return type

**Array<GameDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. Devuelve una lista de juegos encontrados (puede estar vacía). |  -  |
|**400** | Solicitud incorrecta. El parámetro \&#39;nombre\&#39; es obligatorio. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **filtrarJuegosEnIgdb**
> Array<GameDto> filtrarJuegosEnIgdb()

Permite buscar juegos en IGDB aplicando filtros opcionales como rango de fechas de lanzamiento, ID de género, ID de tema, y ID de modo de juego. Devuelve un flujo (o lista) de juegos que coinciden, con un conjunto limitado de campos. Este endpoint es público.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

let fechaInicio: number; //Fecha de inicio del rango de lanzamiento (timestamp Unix en segundos). Opcional. (optional) (default to undefined)
let fechaFin: number; //Fecha de fin del rango de lanzamiento (timestamp Unix en segundos). Opcional. (optional) (default to undefined)
let idGenero: number; //ID del género según IGDB para filtrar. Opcional. (optional) (default to undefined)
let idTema: number; //ID del tema según IGDB para filtrar. Opcional. (optional) (default to undefined)
let idModoJuego: number; //ID del modo de juego según IGDB para filtrar. Opcional. (optional) (default to undefined)
let limite: number; //Número máximo de resultados a devolver. Opcional. El valor por defecto se gestiona en el servidor (ej. 20), máximo 500. (optional) (default to undefined)

const { status, data } = await apiInstance.filtrarJuegosEnIgdb(
    fechaInicio,
    fechaFin,
    idGenero,
    idTema,
    idModoJuego,
    limite
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **fechaInicio** | [**number**] | Fecha de inicio del rango de lanzamiento (timestamp Unix en segundos). Opcional. | (optional) defaults to undefined|
| **fechaFin** | [**number**] | Fecha de fin del rango de lanzamiento (timestamp Unix en segundos). Opcional. | (optional) defaults to undefined|
| **idGenero** | [**number**] | ID del género según IGDB para filtrar. Opcional. | (optional) defaults to undefined|
| **idTema** | [**number**] | ID del tema según IGDB para filtrar. Opcional. | (optional) defaults to undefined|
| **idModoJuego** | [**number**] | ID del modo de juego según IGDB para filtrar. Opcional. | (optional) defaults to undefined|
| **limite** | [**number**] | Número máximo de resultados a devolver. Opcional. El valor por defecto se gestiona en el servidor (ej. 20), máximo 500. | (optional) defaults to undefined|


### Return type

**Array<GameDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**400** | Solicitud incorrecta. Ocurre si alguno de los parámetros numéricos no puede ser parseado correctamente (ej. texto en lugar de número). |  -  |
|**200** | Búsqueda por filtros exitosa. Devuelve una lista de juegos encontrados (puede estar vacía). |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findAllGameModes**
> Array<GameModeDto> findAllGameModes()

Recupera una lista de todos los modos de juego disponibles en IGDB.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findAllGameModes();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameModeDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findAllGenres**
> Array<GenreDto> findAllGenres()

Recupera una lista de todos los géneros de juegos disponibles en IGDB.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findAllGenres();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GenreDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findAllThemes**
> Array<ThemeDto> findAllThemes()

Recupera una lista de todos los temas de juegos disponibles en IGDB.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findAllThemes();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<ThemeDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findHighlyAnticipatedGames**
> Array<GameDto> findHighlyAnticipatedGames()

Recupera una lista de los 10 próximos lanzamientos más esperados, filtrados por su \'hype\'.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findHighlyAnticipatedGames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findMostHypedGames**
> Array<GameDto> findMostHypedGames()

Recupera una lista de los 10 juegos más populares basados en su \'hype\' y un número significativo de calificaciones.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findMostHypedGames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findRecentlyReleasedGames**
> Array<GameDto> findRecentlyReleasedGames()

Recupera una lista de los 10 juegos lanzados en los últimos 30 días, ordenados por fecha de lanzamiento descendente.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findRecentlyReleasedGames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findUpcomingReleases**
> Array<GameDto> findUpcomingReleases()

Recupera una lista de los 10 próximos lanzamientos, ordenados por fecha de lanzamiento ascendente.

### Example

```typescript
import {
    GameControllerApi,
    Configuration
} from '@mycheckpoint/api-client';

const configuration = new Configuration();
const apiInstance = new GameControllerApi(configuration);

const { status, data } = await apiInstance.findUpcomingReleases();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<GameDto>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Búsqueda exitosa. |  -  |
|**500** | Error interno del servidor o error al comunicarse con la API de IGDB. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

