## @mycheckpoint/api-client@v1

This generator creates TypeScript/JavaScript client that utilizes [axios](https://github.com/axios/axios). The generated Node module can be used in the following environments:

Environment
* Node.js
* Webpack
* Browserify

Language level
* ES5 - you must have a Promises/A+ library installed
* ES6

Module system
* CommonJS
* ES6 module system

It can be used in both TypeScript and JavaScript. In TypeScript, the definition will be automatically resolved via `package.json`. ([Reference](https://www.typescriptlang.org/docs/handbook/declaration-files/consumption.html))

### Building

To build and compile the typescript sources to javascript use:
```
npm install
npm run build
```

### Publishing

First build the package then run `npm publish`

### Consuming

navigate to the folder of your consuming project and run one of the following commands.

_published:_

```
npm install @mycheckpoint/api-client@v1 --save
```

_unPublished (not recommended):_

```
npm install PATH_TO_GENERATED_PACKAGE --save
```

### Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AutenticacinControllerApi* | [**authenticateUser**](docs/AutenticacinControllerApi.md#authenticateuser) | **POST** /api/v1/auth/login | Autenticar usuario y obtener token JWT
*AutenticacinControllerApi* | [**confirmUserAccount**](docs/AutenticacinControllerApi.md#confirmuseraccount) | **GET** /api/v1/auth/confirm-account | Confirmar la dirección de correo electrónico de un usuario
*AutenticacinControllerApi* | [**forgotPassword**](docs/AutenticacinControllerApi.md#forgotpassword) | **POST** /api/v1/auth/forgot-password | Solicitar restablecimiento de contraseña
*AutenticacinControllerApi* | [**resetPassword**](docs/AutenticacinControllerApi.md#resetpassword) | **POST** /api/v1/auth/reset-password | Restablecer la contraseña del usuario utilizando un token
*FriendshipControllerApi* | [**acceptFriendRequest**](docs/FriendshipControllerApi.md#acceptfriendrequest) | **PUT** /api/v1/friends/requests/accept/{requesterUserPublicId} | Aceptar una solicitud de amistad pendiente
*FriendshipControllerApi* | [**declineOrCancelFriendRequest**](docs/FriendshipControllerApi.md#declineorcancelfriendrequest) | **DELETE** /api/v1/friends/requests/decline/{requesterUserPublicId} | Rechazar o cancelar una solicitud de amistad pendiente
*FriendshipControllerApi* | [**getMyFriends**](docs/FriendshipControllerApi.md#getmyfriends) | **GET** /api/v1/friends | Obtener la lista de amigos del usuario autenticado
*FriendshipControllerApi* | [**getPendingRequestsReceived**](docs/FriendshipControllerApi.md#getpendingrequestsreceived) | **GET** /api/v1/friends/requests/received | Obtener las solicitudes de amistad pendientes recibidas por el usuario autenticado
*FriendshipControllerApi* | [**getPendingRequestsSent**](docs/FriendshipControllerApi.md#getpendingrequestssent) | **GET** /api/v1/friends/requests/sent | Obtener las solicitudes de amistad pendientes enviadas por el usuario autenticado
*FriendshipControllerApi* | [**removeFriend**](docs/FriendshipControllerApi.md#removefriend) | **DELETE** /api/v1/friends/{friendUserPublicId} | Eliminar un amigo
*FriendshipControllerApi* | [**sendFriendRequest**](docs/FriendshipControllerApi.md#sendfriendrequest) | **POST** /api/v1/friends/requests/send/{receiverUserPublicId} | Enviar una solicitud de amistad
*GameControllerApi* | [**buscarJuegosEnIgdb**](docs/GameControllerApi.md#buscarjuegosenigdb) | **GET** /api/juegos/igdb/buscar | Buscar juegos en IGDB por nombre
*GameControllerApi* | [**filtrarJuegosEnIgdb**](docs/GameControllerApi.md#filtrarjuegosenigdb) | **GET** /api/juegos/igdb/filtrar | Filtrar juegos en IGDB por múltiples criterios
*GameControllerApi* | [**findAllGameModes**](docs/GameControllerApi.md#findallgamemodes) | **GET** /api/juegos/igdb/game-modes | Obtener todos los modos de juego de IGDB
*GameControllerApi* | [**findAllGenres**](docs/GameControllerApi.md#findallgenres) | **GET** /api/juegos/igdb/genres | Obtener todos los géneros de IGDB
*GameControllerApi* | [**findAllThemes**](docs/GameControllerApi.md#findallthemes) | **GET** /api/juegos/igdb/themes | Obtener todos los temas de IGDB
*GameControllerApi* | [**findHighlyAnticipatedGames**](docs/GameControllerApi.md#findhighlyanticipatedgames) | **GET** /api/juegos/igdb/highly-anticipated | Obtener los próximos lanzamientos más esperados desde IGDB
*GameControllerApi* | [**findMostHypedGames**](docs/GameControllerApi.md#findmosthypedgames) | **GET** /api/juegos/igdb/most-hyped | Obtener los juegos más populares (hyped) desde IGDB
*GameControllerApi* | [**findRecentlyReleasedGames**](docs/GameControllerApi.md#findrecentlyreleasedgames) | **GET** /api/juegos/igdb/recently-released | Obtener juegos lanzados recientemente desde IGDB
*GameControllerApi* | [**findUpcomingReleases**](docs/GameControllerApi.md#findupcomingreleases) | **GET** /api/juegos/igdb/upcoming-releases | Obtener los próximos lanzamientos desde IGDB
*GameListControllerApi* | [**addGameToMyCustomList**](docs/GameListControllerApi.md#addgametomycustomlist) | **POST** /api/v1/users/me/gamelists/{listPublicId}/games | Añadir un juego de la biblioteca del usuario a una de sus listas de juegos
*GameListControllerApi* | [**createMyGameList**](docs/GameListControllerApi.md#createmygamelist) | **POST** /api/v1/users/me/gamelists | Crear una nueva lista de juegos para el usuario autenticado
*GameListControllerApi* | [**deleteMyGameList**](docs/GameListControllerApi.md#deletemygamelist) | **DELETE** /api/v1/users/me/gamelists/{listPublicId} | Eliminar una lista de juegos del usuario autenticado
*GameListControllerApi* | [**getMyGameLists**](docs/GameListControllerApi.md#getmygamelists) | **GET** /api/v1/users/me/gamelists | Obtener todas las listas de juegos del usuario autenticado
*GameListControllerApi* | [**getMySpecificGameList**](docs/GameListControllerApi.md#getmyspecificgamelist) | **GET** /api/v1/users/me/gamelists/{listPublicId} | Obtener una lista de juegos específica del usuario autenticado por su ID público
*GameListControllerApi* | [**removeGameFromMyCustomList**](docs/GameListControllerApi.md#removegamefrommycustomlist) | **DELETE** /api/v1/users/me/gamelists/{listPublicId}/games/{userGameInternalId} | Eliminar un juego de una lista de juegos personalizada del usuario autenticado
*GameListControllerApi* | [**updateMyGameList**](docs/GameListControllerApi.md#updatemygamelist) | **PUT** /api/v1/users/me/gamelists/{listPublicId} | Actualizar una lista de juegos existente del usuario autenticado
*GameListControllerApi* | [**viewAllPublicGameLists**](docs/GameListControllerApi.md#viewallpublicgamelists) | **GET** /api/v1/gamelists/public | Obtener todas las listas de juegos públicas
*GameListControllerApi* | [**viewPublicGameList**](docs/GameListControllerApi.md#viewpublicgamelist) | **GET** /api/v1/gamelists/{listPublicId}/public | Obtener una lista de juegos pública específica por su ID público
*TierListControllerApi* | [**addItemToTierListSection**](docs/TierListControllerApi.md#additemtotierlistsection) | **POST** /api/v1/tierlists/{tierListPublicId}/sections/{sectionInternalId}/items | Añadir o mover un ítem (juego) a una sección específica de una Tier List de perfil
*TierListControllerApi* | [**addItemToUnclassifiedSection**](docs/TierListControllerApi.md#additemtounclassifiedsection) | **POST** /api/v1/tierlists/{tierListPublicId}/items/unclassified | Añadir o mover un ítem (juego) a la sección \&#39;Sin Clasificar\&#39; de una Tier List de perfil
*TierListControllerApi* | [**addSectionToTierList**](docs/TierListControllerApi.md#addsectiontotierlist) | **POST** /api/v1/tierlists/{tierListPublicId}/sections | Añadir una nueva sección (tier) a una Tier List existente
*TierListControllerApi* | [**createProfileTierList**](docs/TierListControllerApi.md#createprofiletierlist) | **POST** /api/v1/users/me/tierlists | Crear una nueva Tier List de perfil para el usuario autenticado
*TierListControllerApi* | [**deleteTierList**](docs/TierListControllerApi.md#deletetierlist) | **DELETE** /api/v1/tierlists/{tierListPublicId} | Eliminar una Tier List existente
*TierListControllerApi* | [**getAllProfileTierListsForCurrentUser**](docs/TierListControllerApi.md#getallprofiletierlistsforcurrentuser) | **GET** /api/v1/users/me/tierlists | Obtener todas las Tier Lists de perfil del usuario autenticado
*TierListControllerApi* | [**getAllPublicTierLists**](docs/TierListControllerApi.md#getallpublictierlists) | **GET** /api/v1/tierlists/public | Obtener todas las Tier Lists públicas
*TierListControllerApi* | [**getOrCreateTierListForGameList**](docs/TierListControllerApi.md#getorcreatetierlistforgamelist) | **GET** /api/v1/gamelists/{gameListPublicId}/tierlist | Obtener o crear la Tier List asociada a una GameList específica
*TierListControllerApi* | [**getTierListByPublicId**](docs/TierListControllerApi.md#gettierlistbypublicid) | **GET** /api/v1/tierlists/{tierListPublicId} | Obtener una Tier List específica por su ID público
*TierListControllerApi* | [**moveItemInTierList**](docs/TierListControllerApi.md#moveitemintierlist) | **PUT** /api/v1/tierlists/{tierListPublicId}/items/{tierListItemInternalId}/move | Mover un ítem (juego) dentro de una Tier List
*TierListControllerApi* | [**removeItemFromTierList**](docs/TierListControllerApi.md#removeitemfromtierlist) | **DELETE** /api/v1/tierlists/{tierListPublicId}/items/{tierListItemInternalId} | Eliminar un ítem (juego) de una Tier List de perfil
*TierListControllerApi* | [**removeSectionFromTierList**](docs/TierListControllerApi.md#removesectionfromtierlist) | **DELETE** /api/v1/tierlists/{tierListPublicId}/sections/{sectionInternalId} | Eliminar una sección (tier) de una Tier List
*TierListControllerApi* | [**updateTierListMetadata**](docs/TierListControllerApi.md#updatetierlistmetadata) | **PUT** /api/v1/tierlists/{tierListPublicId} | Actualizar los metadatos de una Tier List existente
*TierListControllerApi* | [**updateTierSection**](docs/TierListControllerApi.md#updatetiersection) | **PUT** /api/v1/{tierListPublicId}/sections/{sectionInternalId} | Actualizar una sección (tier) específica de una Tier List
*UserGameLibraryControllerApi* | [**addOrUpdateGameInMyLibrary**](docs/UserGameLibraryControllerApi.md#addorupdategameinmylibrary) | **POST** /api/v1/users/me/library/games/{igdbId} | Añadir o actualizar un juego en la biblioteca del usuario autenticado
*UserGameLibraryControllerApi* | [**getGameDetails**](docs/UserGameLibraryControllerApi.md#getgamedetails) | **GET** /api/v1/games/{igdbId}/details | Obtener detalles completos de un juego
*UserGameLibraryControllerApi* | [**getMyGameLibrary**](docs/UserGameLibraryControllerApi.md#getmygamelibrary) | **GET** /api/v1/users/me/library/games | Obtener la biblioteca completa de juegos del usuario autenticado
*UserGameLibraryControllerApi* | [**getPublicUserLibrary**](docs/UserGameLibraryControllerApi.md#getpublicuserlibrary) | **GET** /api/v1/users/public/{publicId}/library | Obtener la biblioteca de un usuario por su ID público
*UserGameLibraryControllerApi* | [**getSpecificGameFromMyLibrary**](docs/UserGameLibraryControllerApi.md#getspecificgamefrommylibrary) | **GET** /api/v1/users/me/library/games/{igdbId} | Obtener un juego específico de la biblioteca del usuario autenticado
*UserGameLibraryControllerApi* | [**removeGameFromMyLibrary**](docs/UserGameLibraryControllerApi.md#removegamefrommylibrary) | **DELETE** /api/v1/users/me/library/games/{igdbId} | Eliminar un juego de la biblioteca del usuario autenticado
*UsuariosApi* | [**changeMyPassword**](docs/UsuariosApi.md#changemypassword) | **PUT** /api/v1/usuarios/me/password | Cambiar la contraseña del usuario autenticado actualmente
*UsuariosApi* | [**deleteMyAccount**](docs/UsuariosApi.md#deletemyaccount) | **DELETE** /api/v1/usuarios/me | Programar la eliminación de la cuenta del usuario autenticado
*UsuariosApi* | [**getCurrentAuthenticatedUser**](docs/UsuariosApi.md#getcurrentauthenticateduser) | **GET** /api/v1/usuarios/me | Obtener los datos del usuario autenticado actualmente
*UsuariosApi* | [**getUsuarioByPublicId**](docs/UsuariosApi.md#getusuariobypublicid) | **GET** /api/v1/usuarios/public/{publicId} | Obtener un usuario por su ID público
*UsuariosApi* | [**getUsuarioSummaryByPublicId**](docs/UsuariosApi.md#getusuariosummarybypublicid) | **GET** /api/v1/usuarios/public/summary/{publicId} | Obtener un resumen de usuario por su ID público
*UsuariosApi* | [**registrarUsuario**](docs/UsuariosApi.md#registrarusuario) | **POST** /api/v1/usuarios | Registrar un nuevo usuario
*UsuariosApi* | [**searchUsersByUsername**](docs/UsuariosApi.md#searchusersbyusername) | **GET** /api/v1/usuarios/search | Buscar usuarios por nombre de usuario
*UsuariosApi* | [**updateCurrentUserProfile**](docs/UsuariosApi.md#updatecurrentuserprofile) | **PUT** /api/v1/usuarios/me | Actualizar el perfil del usuario autenticado actualmente
*UsuariosApi* | [**uploadProfilePicture**](docs/UsuariosApi.md#uploadprofilepicture) | **POST** /api/v1/usuarios/me/profile-picture | Subir o actualizar la foto de perfil del usuario autenticado


### Documentation For Models

 - [AccountDeleteDTO](docs/AccountDeleteDTO.md)
 - [AddGameToCustomListRequestDTO](docs/AddGameToCustomListRequestDTO.md)
 - [ArtworkDto](docs/ArtworkDto.md)
 - [CompanyInfoDto](docs/CompanyInfoDto.md)
 - [CoverDto](docs/CoverDto.md)
 - [DlcInfoDto](docs/DlcInfoDto.md)
 - [DuplicatedResourceResponse](docs/DuplicatedResourceResponse.md)
 - [ErrorResponse](docs/ErrorResponse.md)
 - [ForgotPasswordDTO](docs/ForgotPasswordDTO.md)
 - [FranchiseDto](docs/FranchiseDto.md)
 - [FriendshipResponseDTO](docs/FriendshipResponseDTO.md)
 - [GameDetailDTO](docs/GameDetailDTO.md)
 - [GameDto](docs/GameDto.md)
 - [GameEngineDto](docs/GameEngineDto.md)
 - [GameListRequestDTO](docs/GameListRequestDTO.md)
 - [GameListResponseDTO](docs/GameListResponseDTO.md)
 - [GameModeDto](docs/GameModeDto.md)
 - [GameStatusDto](docs/GameStatusDto.md)
 - [GenreDto](docs/GenreDto.md)
 - [InvolvedCompanyDto](docs/InvolvedCompanyDto.md)
 - [JwtResponseDTO](docs/JwtResponseDTO.md)
 - [KeywordDto](docs/KeywordDto.md)
 - [LoginRequestDTO](docs/LoginRequestDTO.md)
 - [PasswordChangeDTO](docs/PasswordChangeDTO.md)
 - [PlatformDto](docs/PlatformDto.md)
 - [PlatformLogoDto](docs/PlatformLogoDto.md)
 - [PublicGameCommentDTO](docs/PublicGameCommentDTO.md)
 - [RequiredErrorResponse](docs/RequiredErrorResponse.md)
 - [ResetPasswordDTO](docs/ResetPasswordDTO.md)
 - [ScreenshotDto](docs/ScreenshotDto.md)
 - [SimilarGameInfoDto](docs/SimilarGameInfoDto.md)
 - [ThemeDto](docs/ThemeDto.md)
 - [TierListCreateRequestDTO](docs/TierListCreateRequestDTO.md)
 - [TierListItemAddRequestDTO](docs/TierListItemAddRequestDTO.md)
 - [TierListItemGameInfoDTO](docs/TierListItemGameInfoDTO.md)
 - [TierListItemMoveRequestDTO](docs/TierListItemMoveRequestDTO.md)
 - [TierListResponseDTO](docs/TierListResponseDTO.md)
 - [TierListUpdateRequestDTO](docs/TierListUpdateRequestDTO.md)
 - [TierSectionRequestDTO](docs/TierSectionRequestDTO.md)
 - [TierSectionResponseDTO](docs/TierSectionResponseDTO.md)
 - [TooLargeResponse](docs/TooLargeResponse.md)
 - [UnauthorizedResponse](docs/UnauthorizedResponse.md)
 - [UserCreateDTO](docs/UserCreateDTO.md)
 - [UserDTO](docs/UserDTO.md)
 - [UserGameDataDTO](docs/UserGameDataDTO.md)
 - [UserGameResponseDTO](docs/UserGameResponseDTO.md)
 - [UserProfileUpdateDTO](docs/UserProfileUpdateDTO.md)
 - [UserSearchResultDTO](docs/UserSearchResultDTO.md)
 - [ValidationErrorResponse](docs/ValidationErrorResponse.md)
 - [ValidationPasswordErrorResponse](docs/ValidationPasswordErrorResponse.md)
 - [VideoDto](docs/VideoDto.md)
 - [WebsiteDto](docs/WebsiteDto.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="bearerAuth"></a>
### bearerAuth

- **Type**: Bearer authentication (JWT)

