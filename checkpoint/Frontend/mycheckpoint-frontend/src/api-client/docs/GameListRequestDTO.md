# GameListRequestDTO

DTO para crear o actualizar una lista de juegos personalizada.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Nombre de la lista de juegos. Debe tener entre 1 y 150 caracteres. | [default to undefined]
**description** | **string** | Descripción opcional para la lista de juegos. Máximo 1000 caracteres. | [optional] [default to undefined]
**is_public** | **boolean** | Indica si la lista de juegos es pública (true) o privada (false). | [default to undefined]

## Example

```typescript
import { GameListRequestDTO } from '@mycheckpoint/api-client';

const instance: GameListRequestDTO = {
    name,
    description,
    is_public,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
