# GameDto

DTO completo para un videojuego, incluyendo todos sus detalles y relaciones.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**cover** | [**CoverDto**](CoverDto.md) |  | [optional] [default to undefined]
**genres** | [**Array&lt;GenreDto&gt;**](GenreDto.md) | Géneros a los que pertenece el juego (ej. RPG, Acción). | [optional] [default to undefined]
**name** | **string** | Nombre completo del juego. | [optional] [default to undefined]
**slug** | **string** | Identificador URL amigable del juego. | [optional] [default to undefined]
**summary** | **string** | Resumen o sinopsis del juego. | [optional] [default to undefined]
**storyline** | **string** | Argumento o historia principal del juego. | [optional] [default to undefined]
**id** | **number** | ID único del juego en IGDB. | [optional] [default to undefined]
**game_modes** | [**Array&lt;GameModeDto&gt;**](GameModeDto.md) | Modos de juego disponibles (ej. Un jugador, Multijugador). | [optional] [default to undefined]
**artworks** | [**Array&lt;ArtworkDto&gt;**](ArtworkDto.md) | Información de los artes del juego. | [optional] [default to undefined]
**first_release_date** | **number** | Fecha del primer lanzamiento del juego (timestamp Unix en segundos). | [optional] [default to undefined]
**first_release_status** | **string** | Estado de lanzamiento del juego. | [optional] [default to undefined]
**franchises** | [**Array&lt;FranchiseDto&gt;**](FranchiseDto.md) | Franquicias a las que pertenece el juego. | [optional] [default to undefined]
**game_engines** | [**Array&lt;GameEngineDto&gt;**](GameEngineDto.md) | Motores de juego utilizados. | [optional] [default to undefined]
**keywords** | [**Array&lt;KeywordDto&gt;**](KeywordDto.md) | Palabras clave asociadas al juego. | [optional] [default to undefined]
**platforms** | [**Array&lt;PlatformDto&gt;**](PlatformDto.md) | Plataformas en las que el juego está disponible. | [optional] [default to undefined]
**screenshots** | [**Array&lt;ScreenshotDto&gt;**](ScreenshotDto.md) | Capturas de pantalla del juego. | [optional] [default to undefined]
**websites** | [**Array&lt;WebsiteDto&gt;**](WebsiteDto.md) | Sitios web relacionados con el juego. | [optional] [default to undefined]
**videos** | [**Array&lt;VideoDto&gt;**](VideoDto.md) | Vídeos relacionados con el juego. | [optional] [default to undefined]
**total_rating** | **number** | Calificación total del juego (promedio de usuarios/críticos). | [optional] [default to undefined]
**total_rating_count** | **number** | Número total de calificaciones recibidas. | [optional] [default to undefined]
**themes** | [**Array&lt;ThemeDto&gt;**](ThemeDto.md) | Temas principales del juego (ej. Fantasía, Ciencia Ficción). | [optional] [default to undefined]
**game_type** | **string** | Tipo de juego (ej. JUEGO_PRINCIPAL, DLC, EXPANSION). | [optional] [default to undefined]
**parent_game** | [**DlcInfoDto**](DlcInfoDto.md) |  | [optional] [default to undefined]
**dlcs** | [**Array&lt;DlcInfoDto&gt;**](DlcInfoDto.md) | Lista de DLCs para este juego. | [optional] [default to undefined]
**expansions** | [**Array&lt;DlcInfoDto&gt;**](DlcInfoDto.md) | Lista de expansiones para este juego. | [optional] [default to undefined]
**bundles** | [**Array&lt;DlcInfoDto&gt;**](DlcInfoDto.md) | Lista de paquetes/bundles que incluyen este juego o de los que este juego forma parte. | [optional] [default to undefined]
**version_parent** | [**DlcInfoDto**](DlcInfoDto.md) |  | [optional] [default to undefined]
**remakes** | [**Array&lt;DlcInfoDto&gt;**](DlcInfoDto.md) | Lista de remakes de este juego. | [optional] [default to undefined]
**remasters** | [**Array&lt;DlcInfoDto&gt;**](DlcInfoDto.md) | Lista de remasters de este juego. | [optional] [default to undefined]
**similar_games** | [**Array&lt;SimilarGameInfoDto&gt;**](SimilarGameInfoDto.md) | Lista de juegos similares a este. | [optional] [default to undefined]
**involved_companies** | [**Array&lt;InvolvedCompanyDto&gt;**](InvolvedCompanyDto.md) | Compañías involucradas en el desarrollo/publicación del juego. | [optional] [default to undefined]
**game_status** | [**GameStatusDto**](GameStatusDto.md) |  | [optional] [default to undefined]

## Example

```typescript
import { GameDto } from '@mycheckpoint/api-client';

const instance: GameDto = {
    cover,
    genres,
    name,
    slug,
    summary,
    storyline,
    id,
    game_modes,
    artworks,
    first_release_date,
    first_release_status,
    franchises,
    game_engines,
    keywords,
    platforms,
    screenshots,
    websites,
    videos,
    total_rating,
    total_rating_count,
    themes,
    game_type,
    parent_game,
    dlcs,
    expansions,
    bundles,
    version_parent,
    remakes,
    remasters,
    similar_games,
    involved_companies,
    game_status,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
