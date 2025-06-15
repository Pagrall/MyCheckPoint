# TierSectionResponseDTO

DTO para una sección (tier) de una Tier List, incluyendo sus ítems.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**internal_id** | **number** | ID interno de la sección. | [optional] [default to undefined]
**name** | **string** | Nombre de la sección. | [optional] [default to undefined]
**color** | **string** | Color de la sección en formato hexadecimal. | [optional] [default to undefined]
**order** | **number** | Orden de la sección dentro de la Tier List. | [optional] [default to undefined]
**is_default_unclassified** | **boolean** | Indica si es la sección por defecto para ítems sin clasificar. | [optional] [default to undefined]
**items** | [**Array&lt;TierListItemGameInfoDTO&gt;**](TierListItemGameInfoDTO.md) | Lista de ítems (juegos) en esta sección. | [optional] [default to undefined]

## Example

```typescript
import { TierSectionResponseDTO } from '@mycheckpoint/api-client';

const instance: TierSectionResponseDTO = {
    internal_id,
    name,
    color,
    order,
    is_default_unclassified,
    items,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
