# InvolvedCompanyDto

DTO para la relación de una compañía con un juego y su rol.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**company** | [**CompanyInfoDto**](CompanyInfoDto.md) |  | [optional] [default to undefined]
**developer** | **boolean** | True si la compañía es desarrolladora. | [optional] [default to undefined]
**porting** | **boolean** | True si la compañía realizó la portabilidad del juego. | [optional] [default to undefined]
**publisher** | **boolean** | True si la compañía es editora. | [optional] [default to undefined]
**supporting** | **boolean** | True si la compañía dio soporte al desarrollo. | [optional] [default to undefined]
**id** | **number** | ID de la relación de involucramiento desde IGDB. | [optional] [default to undefined]

## Example

```typescript
import { InvolvedCompanyDto } from '@mycheckpoint/api-client';

const instance: InvolvedCompanyDto = {
    company,
    developer,
    porting,
    publisher,
    supporting,
    id,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
