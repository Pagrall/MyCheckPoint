# DlcInfoDto

DTO con información resumida de un DLC, expansión o juego relacionado.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**cover** | [**CoverDto**](CoverDto.md) |  | [optional] [default to undefined]
**name** | **string** | Nombre del DLC/expansión/juego relacionado. | [optional] [default to undefined]
**id** | **number** | ID del DLC/expansión/juego relacionado desde IGDB. | [optional] [default to undefined]
**total_rating** | **number** | Calificación total del DLC/expansión/juego relacionado. | [optional] [default to undefined]
**game_type** | **string** | Tipo de juego (ej. DLC, EXPANSION). | [optional] [default to undefined]
**slug** | **string** | Slug del DLC/expansión/juego relacionado. | [optional] [default to undefined]

## Example

```typescript
import { DlcInfoDto } from '@mycheckpoint/api-client';

const instance: DlcInfoDto = {
    cover,
    name,
    id,
    total_rating,
    game_type,
    slug,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
