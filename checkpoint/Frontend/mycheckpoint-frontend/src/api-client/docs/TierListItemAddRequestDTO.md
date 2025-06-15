# TierListItemAddRequestDTO

DTO para añadir un ítem (juego de la biblioteca del usuario) a una sección de una Tier List.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_game_id** | **number** | ID interno de la entrada \&#39;UserGame\&#39; (juego en la biblioteca del usuario) que se desea añadir o mover. Es obligatorio. | [default to undefined]
**order** | **number** | Posición (orden basado en cero) deseada para el ítem dentro de la sección destino. Si es nulo o está fuera de rango, el ítem se añadirá al final de la sección. Opcional. | [optional] [default to undefined]

## Example

```typescript
import { TierListItemAddRequestDTO } from '@mycheckpoint/api-client';

const instance: TierListItemAddRequestDTO = {
    user_game_id,
    order,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
