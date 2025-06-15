# FriendshipResponseDTO

DTO que representa el estado de una amistad o solicitud de amistad.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**friendship_id** | **number** | ID interno de la relación de amistad/solicitud. | [optional] [default to undefined]
**user_public_id** | **string** | ID público del otro usuario en la relación (amigo o solicitante/receptor). | [optional] [default to undefined]
**username** | **string** | Nombre de usuario del otro usuario en la relación. | [optional] [default to undefined]
**profile_picture_url** | **string** | URL de la foto de perfil del otro usuario. Puede ser nulo. | [optional] [default to undefined]
**status** | **string** | Estado actual de la amistad o solicitud. | [optional] [default to undefined]
**is_initiated_by_current_user** | **boolean** | Indica si el usuario autenticado fue quien inició originalmente la solicitud de amistad. True si el usuario actual es el \&#39;requester\&#39;, False si es el \&#39;receiver\&#39;. | [optional] [default to undefined]
**created_at** | **string** | Fecha y hora de creación de la solicitud o de cuando se estableció la amistad. | [optional] [default to undefined]
**updated_at** | **string** | Fecha y hora de la última actualización del estado (ej. aceptación). | [optional] [default to undefined]

## Example

```typescript
import { FriendshipResponseDTO } from '@mycheckpoint/api-client';

const instance: FriendshipResponseDTO = {
    friendship_id,
    user_public_id,
    username,
    profile_picture_url,
    status,
    is_initiated_by_current_user,
    created_at,
    updated_at,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
