# TierListResponseDTO

DTO que representa una Tier List, incluyendo sus secciones y los ítems (juegos) clasificados en ellas.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**public_id** | **string** | ID público único de la Tier List. | [optional] [readonly] [default to undefined]
**name** | **string** | Nombre de la Tier List. | [optional] [default to undefined]
**description** | **string** | Descripción detallada de la Tier List. | [optional] [default to undefined]
**type** | **string** | Tipo de Tier List (ej. general de perfil o basada en una GameList). | [optional] [readonly] [default to undefined]
**source_game_list_public_id** | **string** | ID público de la GameList origen, si esta Tier List se generó a partir de una. Nulo para Tier Lists de perfil global. | [optional] [readonly] [default to undefined]
**owner_username** | **string** | Nombre de usuario del propietario de la Tier List. | [optional] [readonly] [default to undefined]
**is_public** | **boolean** | Indica si la Tier List es pública (true) o privada (false). | [optional] [default to undefined]
**sections** | [**Array&lt;TierSectionResponseDTO&gt;**](TierSectionResponseDTO.md) | Lista de secciones (tiers) definidas por el usuario, ordenadas. No incluye la sección \&#39;Sin Clasificar\&#39;. | [optional] [default to undefined]
**unclassified_section** | [**TierSectionResponseDTO**](TierSectionResponseDTO.md) |  | [optional] [default to undefined]
**created_at** | **string** | Fecha y hora de creación de la Tier List (formato ISO 8601). | [optional] [readonly] [default to undefined]
**updated_at** | **string** | Fecha y hora de la última actualización de la Tier List (formato ISO 8601). | [optional] [readonly] [default to undefined]

## Example

```typescript
import { TierListResponseDTO } from '@mycheckpoint/api-client';

const instance: TierListResponseDTO = {
    public_id,
    name,
    description,
    type,
    source_game_list_public_id,
    owner_username,
    is_public,
    sections,
    unclassified_section,
    created_at,
    updated_at,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
