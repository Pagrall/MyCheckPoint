# UserSearchResultDTO

DTO que representa un resultado de búsqueda de usuario, mostrando información pública básica.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**public_id** | **string** | ID público del usuario encontrado. | [optional] [default to undefined]
**nombre_usuario** | **string** | Nombre de usuario del usuario encontrado. | [optional] [default to undefined]
**foto_perfil** | **string** | URL de la foto de perfil del usuario encontrado. Puede ser nulo. | [optional] [default to undefined]

## Example

```typescript
import { UserSearchResultDTO } from '@mycheckpoint/api-client';

const instance: UserSearchResultDTO = {
    public_id,
    nombre_usuario,
    foto_perfil,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
