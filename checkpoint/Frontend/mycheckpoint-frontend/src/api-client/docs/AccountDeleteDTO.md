# AccountDeleteDTO

DTO utilizado para confirmar la solicitud de eliminación de cuenta. Requiere la contraseña actual del usuario.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**contrasea_actual** | **string** | La contraseña actual del usuario. Es necesaria para verificar la identidad antes de programar la eliminación de la cuenta. | [default to undefined]

## Example

```typescript
import { AccountDeleteDTO } from '@mycheckpoint/api-client';

const instance: AccountDeleteDTO = {
    contrasea_actual,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
