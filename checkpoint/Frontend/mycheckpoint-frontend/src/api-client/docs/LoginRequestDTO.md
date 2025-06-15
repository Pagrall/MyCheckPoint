# LoginRequestDTO

DTO para la solicitud de inicio de sesión. Requiere un identificador (email o nombre de usuario) y una contraseña.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**identificador** | **string** | Identificador del usuario, puede ser su email o su nombre de usuario. | [default to undefined]
**contrasea** | **string** | Contraseña del usuario. | [default to undefined]

## Example

```typescript
import { LoginRequestDTO } from '@mycheckpoint/api-client';

const instance: LoginRequestDTO = {
    identificador,
    contrasea,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
