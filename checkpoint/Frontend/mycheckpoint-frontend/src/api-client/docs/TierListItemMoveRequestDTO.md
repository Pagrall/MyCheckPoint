# TierListItemMoveRequestDTO

DTO para especificar el movimiento de un ítem (juego) a una nueva sección y/o posición dentro de una Tier List.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**target_section_internal_id** | **number** | ID interno de la sección (tier) destino a la que se moverá el ítem. Es obligatorio. | [default to undefined]
**new_order** | **number** | Nueva posición (orden basado en cero) para el ítem dentro de la sección destino. Es obligatorio. | [default to undefined]

## Example

```typescript
import { TierListItemMoveRequestDTO } from '@mycheckpoint/api-client';

const instance: TierListItemMoveRequestDTO = {
    target_section_internal_id,
    new_order,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
