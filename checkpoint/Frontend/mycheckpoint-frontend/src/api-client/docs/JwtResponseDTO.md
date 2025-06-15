# JwtResponseDTO

DTO para la respuesta de inicio de sesión exitoso. Contiene el token de acceso JWT.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**token_acceso** | **string** | Token de acceso JWT generado para el usuario autenticado. | [optional] [default to undefined]
**tipo_token** | **string** | Tipo de token, generalmente \&#39;Bearer\&#39;. | [optional] [default to undefined]

## Example

```typescript
import { JwtResponseDTO } from '@mycheckpoint/api-client';

const instance: JwtResponseDTO = {
    token_acceso,
    tipo_token,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
