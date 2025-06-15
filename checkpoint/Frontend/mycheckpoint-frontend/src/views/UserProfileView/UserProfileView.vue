<template>
  <div class="user-profile-view">
    <div v-if="isLoadingProfile" class="loading-message">Cargando perfil del usuario...</div>
    <div v-if="profileError" class="error-message">{{ profileError }}</div>

    <div v-if="viewedUser" class="profile-content-wrapper">
      <header class="profile-header section-block">
        <img 
          :src="profilePictureUrl" 
          alt="Foto de perfil" 
          class="profile-avatar"
          @error="onAvatarError"
        />
        <div class="profile-info">
          <h1>{{ viewedUser.nombre_usuario }}</h1>
          <p class="registration-date">Miembro desde: {{ formatReadableDate(viewedUser.fecha_registro) }}</p>
          
          <div v-if="!isOwnProfile" class="friend-actions">
            <button v-if="friendshipStatus === 'NONE'" @click="handleSendFriendRequest" :disabled="isLoadingFriendAction" class="action-button primary">
              {{ isLoadingFriendAction ? 'Enviando...' : 'Añadir Amigo' }}
            </button>

            <button v-if="friendshipStatus === 'SENT'" @click="handleDeclineOrCancelRequest" :disabled="isLoadingFriendAction" class="action-button secondary">
              {{ isLoadingFriendAction ? 'Cancelando...' : 'Cancelar Solicitud' }}
            </button>
            
            <div v-if="friendshipStatus === 'RECEIVED'" class="friend-actions-received">
              <span>{{ viewedUser.nombre_usuario }} te ha enviado una solicitud.</span>
              <button @click="handleAcceptFriendRequest" :disabled="isLoadingFriendAction" class="action-button primary small">Aceptar</button>
              <button @click="handleDeclineOrCancelRequest" :disabled="isLoadingFriendAction" class="action-button danger small">Rechazar</button>
            </div>

            <div v-if="friendshipStatus === 'FRIENDS'" class="friend-actions-friends">
              <span><i class="icon-friends"></i> Amigos</span>
              <button @click="handleRemoveFriend" :disabled="isLoadingFriendAction" class="action-button danger small">Eliminar</button>
            </div>

            <p v-if="friendActionError" class="action-feedback error">{{ friendActionError }}</p>
            <p v-if="friendActionSuccess" class="action-feedback success">{{ friendActionSuccess }}</p>
          </div>
        </div>
        <div v-if="isOwnProfile" class="profile-actions">
          <RouterLink :to="{ name: 'settings' }" class="action-button secondary">
            Ajustes de Mi Perfil
          </RouterLink>
        </div>
      </header>

      <nav class="tabs-navigation profile-tabs">
        <button @click="setActiveTab('library')" :class="{ 'active-tab': activeTab === 'library' }"
                v-if="canViewLibrary">
          Biblioteca
        </button>
        <button @click="setActiveTab('my-gamelists')" :class="{ 'active-tab': activeTab === 'my-gamelists' }"
                v-if="isOwnProfile">
          Mis Listas de Juegos
        </button>
        <button @click="setActiveTab('my-tierlists')" :class="{ 'active-tab': activeTab === 'my-tierlists' }"
                v-if="isOwnProfile">
          Mis Tier Lists
        </button>
      </nav>

      <div class="tab-content">
        <div v-if="activeTab === 'library'">
          <template v-if="isOwnProfile">
            <MyLibraryView />
          </template>
          <template v-else-if="canViewLibrary">
            <div class="section-block">
              <h2>Biblioteca de {{ viewedUser.nombre_usuario }}</h2>
              <div v-if="isLoadingPublicLibrary" class="loading-message">Cargando biblioteca...</div>
              <div v-else-if="publicLibraryError" class="error-message">{{ publicLibraryError }}</div>
              <div v-else-if="publicLibrary.length === 0" class="empty-library-message">
                La biblioteca de este usuario está vacía.
              </div>
              <div class="library-grid" v-else>
                <div v-for="gameEntry in publicLibrary" :key="gameEntry.game_igdb_id" class="library-game-card">
                  <RouterLink :to="{ name: 'game-details', params: { igdbId: gameEntry.game_igdb_id } }">
                    <img 
                      :src="getCoverUrl(gameEntry.game_cover)" 
                      :alt="`Carátula de ${gameEntry.game_name || 'Juego'}`" 
                      class="library-game-cover" 
                      @error="onLibraryCoverError" 
                    />
                    <div class="card-content">
                      <h3 class="game-title">{{ gameEntry.game_name || `Juego ID: ${gameEntry.game_igdb_id}` }}</h3>
                      <div class="user-game-info">
                        <div v-if="gameEntry.status" class="info-item">
                          <strong>Estado:</strong> {{ formatUserGameStatus(gameEntry.status) }}
                        </div>
                        <div v-if="gameEntry.score !== null && gameEntry.score !== undefined" class="info-item">
                          <strong>Puntuación:</strong> {{ gameEntry.score }}/10 
                        </div>
                      </div>
                    </div>
                  </RouterLink>
                </div>
              </div>
            </div>
          </template>
          <template v-else>
              <div class="section-block">
                  <p class="add-to-library-prompt">La biblioteca de este usuario es privada.</p>
              </div>
          </template>
        </div>
        <div v-if="activeTab === 'my-gamelists' && isOwnProfile" class="tab-pane">
          <MyGameListsView />
        </div>
        <div v-if="activeTab === 'my-tierlists' && isOwnProfile" class="tab-pane">
          <MyTierListsView/>
        </div>
      </div>
    </div>
    <div v-if="!isLoadingProfile && !viewedUser && !profileError" class="no-results-message">
      Perfil no encontrado.
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES ---
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { 
  getUserByPublicId, 
  getPublicUserLibrary, 
  getMyFriends, 
  getPendingRequestsSent, 
  getPendingRequestsReceived,
  sendFriendRequest,
  acceptFriendRequest,
  declineOrCancelFriendRequest,
  removeFriend
} from '@/services/apiInstances';
import { BASE_PATH } from '@/api-client/base';
import defaultAvatar from '@/assets/img/default-avatar.svg';
import defaultLibraryCover from '@/assets/img/default-game-cover.svg';

// Se importan los componentes hijos que se usarán en las pestañas del perfil propio.
import MyLibraryView from '../MyLibraryView/MyLibraryView.vue';
import MyGameListsView from '../MyGameListsView/MyGameListsView.vue';
import MyTierListsView from '../MyTierListsView/MyTierListsView.vue';


// --- 2. CONFIGURACIÓN INICIAL ---
const route = useRoute();
const authStore = useAuthStore();


// --- 3. ESTADO DEL COMPONENTE ---

// -- Estado del Perfil y Pestañas --
const viewedUser = ref(null); // Almacena los datos del perfil que se está visitando.
const isLoadingProfile = ref(true); // Controla el estado de carga principal del perfil.
const profileError = ref(''); // Almacena mensajes de error relacionados con la carga del perfil.
const activeTab = ref('library'); // Controla la pestaña activa actualmente.

// -- Estado de la Biblioteca Pública --
const publicLibrary = ref([]); // Almacena la biblioteca de juegos de otro usuario si es visible.
const isLoadingPublicLibrary = ref(false); // Loader para la carga de la biblioteca pública.
const publicLibraryError = ref(''); // Errores específicos de la carga de la biblioteca pública.

// -- Estado de Amistad --
// Almacenan los datos de amistad del usuario logueado para determinar la relación con el perfil visitado.
const friendsList = ref([]);
const sentRequests = ref([]);
const receivedRequests = ref([]);
// Controlan el estado de los botones de acción de amistad.
const isLoadingFriendAction = ref(false);
const friendActionError = ref('');
const friendActionSuccess = ref('');


// --- 4. PROPIEDADES COMPUTADAS ---
/**
 * @description Determina si el perfil que se está viendo es el del propio usuario logueado.
 * Es crucial para mostrar la UI correcta (ej. botón de "Ajustes" vs "Añadir Amigo").
 */
const isOwnProfile = computed(() => {
  return authStore.isAuthenticated && viewedUser.value && authStore.currentUser?.public_id === viewedUser.value.public_id;
});

/**
 * @description Comprueba si el usuario del perfil visitado está en la lista de amigos.
 */
const areFriends = computed(() => {
  if (!viewedUser.value || !friendsList.value) return false;
  return friendsList.value.some(friend => friend.user_public_id === viewedUser.value.public_id);
});

/**
 * @description Determina el estado de amistad entre el usuario logueado y el perfil visitado.
 * Devuelve un estado ('FRIENDS', 'SENT', 'RECEIVED', 'NONE') que el template usa
 * para renderizar los botones de acción de amistad correspondientes.
 */
const friendshipStatus = computed(() => {
  if (!viewedUser.value || !authStore.isAuthenticated || isOwnProfile.value) return null;
  if (areFriends.value) return 'FRIENDS';
  if (sentRequests.value.some(req => req.user_public_id === viewedUser.value.public_id)) return 'SENT';
  if (receivedRequests.value.some(req => req.user_public_id === viewedUser.value.public_id)) return 'RECEIVED';
  return 'NONE';
});

/**
 * @description Comprueba si la biblioteca del perfil visitado puede ser vista por el usuario actual.
 * Depende de la configuración de visibilidad del perfil y de si son amigos.
 */
const canViewLibrary = computed(() => {
    if (!viewedUser.value) return false;
    if (isOwnProfile.value) return true;
    if (viewedUser.value.visibilidad_perfil === 'PUBLICO') return true;
    return viewedUser.value.visibilidad_perfil === 'SOLO_AMIGOS' && areFriends.value;
});

/**
 * @description Construye la URL de la foto de perfil.
 * Incluye una lógica de "cache busting" (romper la caché) para el perfil propio,
 * añadiendo un timestamp que fuerza al navegador a recargar la imagen si ha cambiado.
 */
const profilePictureUrl = computed(() => {
  const fotoPerfilValue = viewedUser.value?.foto_perfil;
  if (fotoPerfilValue) {
    if (fotoPerfilValue.startsWith('http')) {
      return fotoPerfilValue;
    }
    const baseApiUrl = BASE_PATH.endsWith('/') ? BASE_PATH.slice(0, -1) : BASE_PATH;
    const relativeImagePath = fotoPerfilValue.startsWith('/') ? fotoPerfilValue.substring(1) : fotoPerfilValue;
    const url = `${baseApiUrl}/profile-pictures/${relativeImagePath}`;
    // Se añade el 'cache buster' solo si es el perfil del usuario actual, usando un trigger del store.
    return isOwnProfile.value ? `${url}?t=${authStore.imageUpdateTrigger}` : url;
  }
  return defaultAvatar;
});


// --- 5. CICLO DE VIDA Y WATCHERS ---
onMounted(() => {
  fetchUserProfile(route.params.publicId);
  fetchAllFriendshipData(); // Carga los datos de amistad del usuario logueado.
});

// Observa si el ID del perfil en la URL cambia para recargar los datos del nuevo perfil.
watch(() => route.params.publicId, (newId) => {
  if (newId) {
    activeTab.value = 'library'; // Resetea la pestaña a la de la biblioteca.
    fetchUserProfile(newId);
    // No es necesario recargar 'fetchAllFriendshipData' porque es independiente del perfil visitado.
  }
});


// --- 6. MÉTODOS DE DATOS ---
/**
 * @description Carga la información principal del perfil del usuario visitado.
 */
const fetchUserProfile = async (publicId) => {
  if (!publicId) {
    profileError.value = "No se ha especificado un ID de perfil.";
    isLoadingProfile.value = false;
    return;
  }
  isLoadingProfile.value = true;
  profileError.value = '';
  viewedUser.value = null;
  publicLibrary.value = [];

  try {
    const response = await getUserByPublicId(publicId);
    viewedUser.value = response.data;
    
    // Si podemos ver la biblioteca y no es nuestro perfil, la cargamos.
    if (canViewLibrary.value && !isOwnProfile.value) {
      fetchPublicLibrary(publicId);
    }
  } catch (err) {
    console.error("Error fetching user profile:", err);
    if (err.response && err.response.status === 404) {
      profileError.value = "Perfil de usuario no encontrado.";
    } else {
      profileError.value = "Error al cargar el perfil del usuario.";
    }
    viewedUser.value = null;
  } finally {
    isLoadingProfile.value = false;
  }
};

/**
 * @description Carga la biblioteca de juegos de un perfil público o de un amigo.
 */
const fetchPublicLibrary = async (publicId) => {
  isLoadingPublicLibrary.value = true;
  publicLibraryError.value = '';
  try {
    const response = await getPublicUserLibrary(publicId);
    publicLibrary.value = response.data;
  } catch (error) {
    console.error("Error fetching public library:", error);
    if (error.response && (error.response.status === 403 || error.response.status === 401)) {
        publicLibraryError.value = "La biblioteca de este usuario es privada o solo para amigos.";
    } else {
        publicLibraryError.value = "No se pudo cargar la biblioteca de este usuario.";
    }
  } finally {
    isLoadingPublicLibrary.value = false;
  }
};

/**
 * @description Carga todos los datos de amistad (amigos, solicitudes enviadas y recibidas) del usuario logueado.
 * Usa Promise.all para ejecutar todas las peticiones en paralelo y mejorar la eficiencia.
 */
const fetchAllFriendshipData = async () => {
  if (!authStore.isAuthenticated) return;
  friendsList.value = [];
  sentRequests.value = [];
  receivedRequests.value = [];

  try {
    const [friends, sent, received] = await Promise.all([
      getMyFriends(),
      getPendingRequestsSent(),
      getPendingRequestsReceived()
    ]);
    friendsList.value = friends.data;
    sentRequests.value = sent.data;
    receivedRequests.value = received.data;
  } catch (error) {
    console.error("Error cargando datos de amistad:", error);
    friendActionError.value = "No se pudo cargar la información de amigos.";
  }
};


// --- 7. MANEJADORES DE ACCIONES DE AMISTAD ---
/**
 * @description Función genérica para manejar cualquier acción de amistad.
 * Centraliza la gestión de estados de carga/error y la recarga de datos.
 */
const handleFriendAction = async (action) => {
  isLoadingFriendAction.value = true;
  friendActionError.value = '';
  friendActionSuccess.value = '';
  try {
    await action();
    // Tras una acción exitosa, se recargan todos los datos de amistad para que el
    // estado de los botones se actualice correctamente.
    await fetchAllFriendshipData();
  } catch(error) {
    console.error("Error en la acción de amistad:", error);
    friendActionError.value = error.response?.data?.error || 'Ocurrió un error.';
  } finally {
    isLoadingFriendAction.value = false;
  }
};

const handleSendFriendRequest = () => handleFriendAction(() => sendFriendRequest(viewedUser.value.public_id));
const handleAcceptFriendRequest = () => handleFriendAction(() => acceptFriendRequest(viewedUser.value.public_id));
const handleDeclineOrCancelRequest = () => handleFriendAction(() => declineOrCancelFriendRequest(viewedUser.value.public_id));
const handleRemoveFriend = () => {
    if (window.confirm(`¿Estás seguro de que quieres eliminar a ${viewedUser.value.nombre_usuario} de tus amigos?`)) {
        handleFriendAction(() => removeFriend(viewedUser.value.public_id));
    }
};


// --- 8. MÉTODOS DE UI Y UTILIDADES ---
const setActiveTab = (tabName) => {
  activeTab.value = tabName;
};

const onAvatarError = (event) => {
  event.target.src = defaultAvatar;
};

const getCoverUrl = (coverData) => { 
  if (coverData && typeof coverData.url === 'string' && coverData.url.trim() !== '') { 
    let imageUrl = coverData.url;
    if (imageUrl.startsWith('//')) { imageUrl = `https:${imageUrl}`; }
    if (imageUrl.includes('/t_thumb/')) {
      imageUrl = imageUrl.replace('/t_thumb/', '/t_cover_big/');
    } else if (imageUrl.includes('/t_cover_small/')) {
      imageUrl = imageUrl.replace('/t_cover_small/', '/t_cover_big/'); 
    }
    return imageUrl;
  }
  return defaultLibraryCover; 
};

const onLibraryCoverError = (event) => {
  event.target.src = defaultLibraryCover;
};

const formatReadableDate = (isoDateString) => {
  if (!isoDateString) return '';
  return new Date(isoDateString).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
};

const formatUserGameStatus = (status) => { 
  if (!status) return 'No especificado';
  const statusMap = {
    'COMPLETED': 'Completado',
    'PLAYING': 'Jugando',
    // ...etc
  };
  return statusMap[status] || status; 
};
</script>

<style src="./UserProfileView.css" scoped>

</style>