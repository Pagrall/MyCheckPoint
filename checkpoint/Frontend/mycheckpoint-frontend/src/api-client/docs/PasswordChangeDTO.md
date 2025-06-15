# PasswordChangeDTO

DTO para solicitar un cambio de contraseña. Requiere la contraseña actual del usuario y la nueva contraseña deseada.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**contrasea_actual** | **string** | La contraseña actual del usuario para verificación. | [default to undefined]
**nueva_contrasea** | **string** | La nueva contraseña deseada para la cuenta. Debe tener entre 8 y 100 caracteres. | [default to undefined]

## Example

```typescript
import { PasswordChangeDTO } from '@mycheckpoint/api-client';

const instance: PasswordChangeDTO = {
    contrasea_actual,
    nueva_contrasea,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
