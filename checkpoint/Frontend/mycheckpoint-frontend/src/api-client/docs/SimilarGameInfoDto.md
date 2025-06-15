# SimilarGameInfoDto

DTO con información resumida de un juego similar.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**cover** | [**CoverDto**](CoverDto.md) |  | [optional] [default to undefined]
**name** | **string** | Nombre del juego similar. | [optional] [default to undefined]
**slug** | **string** | Slug del juego similar. | [optional] [default to undefined]
**summary** | **string** | Resumen del juego similar. | [optional] [default to undefined]
**id** | **number** | ID del juego similar desde IGDB. | [optional] [default to undefined]
**total_rating** | **number** | Calificación total del juego similar. | [optional] [default to undefined]

## Example

```typescript
import { SimilarGameInfoDto } from '@mycheckpoint/api-client';

const instance: SimilarGameInfoDto = {
    cover,
    name,
    slug,
    summary,
    id,
    total_rating,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
