import apiClient from './apiService';
import { Configuration } from '@/api-client/configuration'; //
import {
    AutenticacinControllerApi,
    UsuariosApi,
    GameControllerApi,
    UserGameLibraryControllerApi,
    GameListControllerApi,
    TierListControllerApi,
    FriendshipControllerApi
} from '@/api-client'; 

const config = new Configuration(); 

// Instancia para Autenticación
const authApi = new AutenticacinControllerApi(config, undefined, apiClient); //

// Instancia para Usuarios
const usersApi = new UsuariosApi(config, undefined, apiClient); //

// Instancia para Juegos (IGDB)
const gamesApi = new GameControllerApi(config, undefined, apiClient); //

// Instancia para la Biblioteca de Juegos del Usuario
const userGameLibraryApi = new UserGameLibraryControllerApi(config, undefined, apiClient); //

// Instancia para Listas de Juegos Personalizadas
const gameListsApi = new GameListControllerApi(config, undefined, apiClient); //

// Instancia para Tier Lists
const tierListsApi = new TierListControllerApi(config, undefined, apiClient); //

// Instancia para Amistades
const friendshipApi = new FriendshipControllerApi(config, undefined, apiClient); //


// Autenticación
export const loginUser = (loginRequestDTO) => {
  return authApi.authenticateUser(loginRequestDTO); //
};
export const confirmAccount = (token) => { //
  return authApi.confirmUserAccount(token); //
};
export const requestPasswordReset = (forgotPasswordDTO) => { //
  return authApi.forgotPassword(forgotPasswordDTO); //
};
export const resetPasswordWithToken = (resetPasswordDTO) => { //
  return authApi.resetPassword(resetPasswordDTO); //
};


// Usuarios
export const getCurrentAuthenticatedUser = () => {
  return usersApi.getCurrentAuthenticatedUser(); //
};
export const registerUser = (userCreateDTO) => { //
  return usersApi.registrarUsuario(userCreateDTO); //
};
export const updateUserProfile = (userProfileUpdateDTO) => { //
  return usersApi.updateCurrentUserProfile(userProfileUpdateDTO); //
};
export const uploadUserProfilePicture = (file) => {
  return usersApi.uploadProfilePicture(file); //
};
export const changeUserPassword = (passwordChangeDTO) => { //
  return usersApi.changeMyPassword(passwordChangeDTO); //
};
export const deleteUserAccount = (accountDeleteDTO) => { //
  return usersApi.deleteMyAccount(accountDeleteDTO); //
};
export const getUserByPublicId = (publicId) => { //
  return usersApi.getUsuarioByPublicId(publicId); //
};
export const searchUsers = (username) => {
  return usersApi.searchUsersByUsername(username); //
};

//Amistades
export const getMyFriends = () => {
  return friendshipApi.getMyFriends(); // OperationId: getMyFriends
};

export const getPendingRequestsSent = () => {
  return friendshipApi.getPendingRequestsSent(); // OperationId: getMySentFriendRequests
};

export const getPendingRequestsReceived = () => {
  return friendshipApi.getPendingRequestsReceived(); // OperationId: getMyReceivedFriendRequests
};

export const sendFriendRequest = (receiverPublicId) => {
  return friendshipApi.sendFriendRequest(receiverPublicId); // OperationId: sendFriendRequest
};

export const acceptFriendRequest = (requesterPublicId) => {
  return friendshipApi.acceptFriendRequest(requesterPublicId); // OperationId: acceptFriendRequest
};

export const declineOrCancelFriendRequest = (otherUserPublicId) => {
  return friendshipApi.declineOrCancelFriendRequest(otherUserPublicId); // OperationId: declineFriendRequest
};

export const removeFriend = (friendPublicId) => {
  return friendshipApi.removeFriend(friendPublicId); // OperationId: removeFriend
};


//Bibliotecas / Buscar Juego
export const fetchGameDetailsByIgdbId = (igdbId) => {
  return userGameLibraryApi.getGameDetails(igdbId); 
};

export const getMyUserGameLibrary = () => {
  return userGameLibraryApi.getMyGameLibrary(); 
};

export const getPublicUserLibrary = (publicId) => {
  return userGameLibraryApi.getPublicUserLibrary(publicId); 
}

export const addOrUpdateGameInUserLibrary = (igdbId, userGameDataDTO) => {
  return userGameLibraryApi.addOrUpdateGameInMyLibrary(igdbId, userGameDataDTO); 
};

export const removeGameFromUserLibrary = (igdbId) => { 
    return userGameLibraryApi.removeGameFromMyLibrary(igdbId); 
};

//Listas de Juegos
export const fetchAllPublicGameLists = () => {
  return gameListsApi.viewAllPublicGameLists(); 
};

export const getMyGameLists = () => {
  return gameListsApi.getMyGameLists(); 
};

export const createMyGameList = (gameListRequestDTO) => {
  return gameListsApi.createMyGameList(gameListRequestDTO); 
};

export const deleteMyGameList = (listPublicId) => {
  return gameListsApi.deleteMyGameList(listPublicId); //
};

export const getMySpecificGameListDetails = (listPublicId) => {
  return gameListsApi.getMySpecificGameList(listPublicId); 
};

export const viewPublicGameListDetails = (listPublicId) => {
  return gameListsApi.viewPublicGameList(listPublicId); 
};

export const updateMyUserGameList = (listPublicId, gameListRequestDTO) => {
  return gameListsApi.updateMyGameList(listPublicId, gameListRequestDTO); 
};

export const addGameToMyGameList = (listPublicId, addGameToCustomListRequestDTO) => {
  return gameListsApi.addGameToMyCustomList(listPublicId, addGameToCustomListRequestDTO); 
};

export const removeGameFromMyGameList = (listPublicId, userGameInternalId) => {
  return gameListsApi.removeGameFromMyCustomList(listPublicId, userGameInternalId); 
};

export const getOrCreateTierListFromGameList = (gameListPublicId) => {
  return tierListsApi.getOrCreateTierListForGameList(gameListPublicId);
};

//Tier Lists
export const fetchAllPublicTierLists = () => {
  return tierListsApi.getAllPublicTierLists(); 
};

export const getMyProfileTierLists = () => {
  return tierListsApi.getAllProfileTierListsForCurrentUser(); 
};

export const getTierListDetailsByPublicId = (tierListPublicId) => {
  return tierListsApi.getTierListByPublicId(tierListPublicId); 
};

export const createMyProfileTierList = (tierListCreateRequestDTO) => { 
  return tierListsApi.createProfileTierList(tierListCreateRequestDTO); 
};

export const updateMyTierListMetadata = (tierListPublicId, tierListUpdateRequestDTO) => {
  return tierListsApi.updateTierListMetadata(tierListPublicId, tierListUpdateRequestDTO); 
};

export const deleteMyTierList = (tierListPublicId) => {
  return tierListsApi.deleteTierList(tierListPublicId); //
};

export const addSectionToMyTierList = (tierListPublicId, tierSectionRequestDTO) => {
  return tierListsApi.addSectionToTierList(tierListPublicId, tierSectionRequestDTO); //
};

export const updateMyTierSection = (tierListPublicId, sectionInternalId, tierSectionRequestDTO) => {
  return tierListsApi.updateTierSection(tierListPublicId, sectionInternalId, tierSectionRequestDTO); //
};

export const removeSectionFromMyTierList = (tierListPublicId, sectionInternalId) => {
  return tierListsApi.removeSectionFromTierList(tierListPublicId, sectionInternalId); //
};

export const addItemToMyUnclassifiedSection = (tierListPublicId, tierListItemAddRequestDTO) => {
  return tierListsApi.addItemToUnclassifiedSection(tierListPublicId, tierListItemAddRequestDTO); //
};

export const removeItemFromMyTierList = (tierListPublicId, tierListItemInternalId) => {
  return tierListsApi.removeItemFromTierList(tierListPublicId, tierListItemInternalId); //
};

export const moveItemInMyTierList = (tierListPublicId, tierListItemInternalId, tierListItemMoveRequestDTO) => {
  return tierListsApi.moveItemInTierList(tierListPublicId, tierListItemInternalId, tierListItemMoveRequestDTO); //
};

//IGDB
export const findRecentlyReleasedGames = () => {
  return gamesApi.findRecentlyReleasedGames();
};

export const findMostHypedGames = () => {
  return gamesApi.findMostHypedGames();
};

export const findHighlyAnticipatedGames = () => {
  return gamesApi.findHighlyAnticipatedGames();
};

export const findUpcomingReleases = () => {
  return gamesApi.findUpcomingReleases();
};

export const buscarJuegosEnIgdb = (query) => {
  return gamesApi.buscarJuegosEnIgdb(query);
};

export const filtrarJuegosEnIgdb = (fechaInicio, fechaFin, idGenero, idTema, idModoJuego, limite) => {
  return gamesApi.filtrarJuegosEnIgdb(fechaInicio, fechaFin, idGenero, idTema, idModoJuego, limite);
};

export const findAllGameModes = () => {
  return gamesApi.findAllGameModes();
};

export const findAllGenres = () => {
  return gamesApi.findAllGenres();
};

export const findAllThemes = () => {
  return gamesApi.findAllThemes();
};

