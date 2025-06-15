<template>
  <div class="friends-page-view">
    <h1>Amigos y Solicitudes</h1>

    <div class="tabs-navigation">
      <button @click="activeTab = 'friends'" :class="{ 'active-tab': activeTab === 'friends' }">
        Mis Amigos ({{ friends.length }})
      </button>
      <button @click="activeTab = 'received'" :class="{ 'active-tab': activeTab === 'received' }">
        Solicitudes Recibidas ({{ receivedRequests.length }})
      </button>
      <button @click="activeTab = 'sent'" :class="{ 'active-tab': activeTab === 'sent' }">
        Solicitudes Enviadas ({{ sentRequests.length }})
      </button>
    </div>

    <div v-if="isLoading" class="loading-message">Cargando...</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <div class="tab-content" v-if="!isLoading && !errorMessage">
      <div v-if="activeTab === 'friends'">
        <div v-if="friends.length === 0" class="empty-message">
          Aún no tienes amigos. ¡Busca usuarios y envía solicitudes!
        </div>
        <div v-else class="user-list">
          <div v-for="friendship in friends" :key="friendship.friendship_id" class="user-list-item">
            <RouterLink :to="{ name: 'profile', params: { publicId: friendship.user_public_id } }" class="user-info">
              <img :src="getProfilePictureUrl(friendship.profile_picture_url)" alt="Avatar" class="user-avatar-small">
              <span>{{ friendship.username }}</span>
            </RouterLink>
            <div class="user-actions">
              <button @click="handleRemoveFriend(friendship.user_public_id)" class="action-button danger small">Eliminar Amigo</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'received'">
        <div v-if="receivedRequests.length === 0" class="empty-message">
          No tienes solicitudes de amistad pendientes.
        </div>
        <div v-else class="user-list">
          <div v-for="request in receivedRequests" :key="request.friendship_id" class="user-list-item">
            <RouterLink :to="{ name: 'profile', params: { publicId: request.user_public_id } }" class="user-info">
              <img :src="getProfilePictureUrl(request.profile_picture_url)" alt="Avatar" class="user-avatar-small">
              <span>{{ request.username }}</span>
            </RouterLink>
            <div class="user-actions">
              <button @click="handleAcceptRequest(request.user_public_id)" class="action-button primary small">Aceptar</button>
              <button @click="handleDeclineRequest(request.user_public_id)" class="action-button secondary small">Rechazar</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'sent'">
        <div v-if="sentRequests.length === 0" class="empty-message">
          No tienes solicitudes de amistad enviadas pendientes.
        </div>
        <div v-else class="user-list">
          <div v-for="request in sentRequests" :key="request.friendship_id" class="user-list-item">
            <RouterLink :to="{ name: 'profile', params: { publicId: request.user_public_id } }" class="user-info">
              <img :src="getProfilePictureUrl(request.profile_picture_url)" alt="Avatar" class="user-avatar-small">
              <span>{{ request.username }}</span>
            </RouterLink>
            <div class="user-actions">
              <button @click="handleDeclineRequest(request.user_public_id)" class="action-button danger small">Cancelar Solicitud</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES ---
// Importaciones de Vue y Vue Router para reactividad y navegación.
import { ref, onMounted, onUnmounted } from 'vue';
import { RouterLink } from 'vue-router';

// Importaciones de los servicios de API para interactuar con el backend.
import { 
  getMyFriends, 
  getPendingRequestsSent, 
  getPendingRequestsReceived,
  acceptFriendRequest,
  declineOrCancelFriendRequest,
  removeFriend 
} from '@/services/apiInstances';

// Importaciones de configuración y recursos estáticos.
import { BASE_PATH } from '@/api-client/base';
import defaultAvatar from '@/assets/img/default-avatar.svg';


// --- 2. ESTADO DEL COMPONENTE ---
// Ref para rastrear la pestaña activa actualmente ('friends', 'received', 'sent').
const activeTab = ref('friends');

// Refs para almacenar las listas de amigos y solicitudes de amistad obtenidas de la API.
const friends = ref([]);
const sentRequests = ref([]);
const receivedRequests = ref([]);

// Refs para gestionar el estado de la UI durante la carga de datos o en caso de error.
const isLoading = ref(true);
const errorMessage = ref('');


// --- 3. LÓGICA DE POLLING ---
// Variable para mantener la referencia del intervalo de polling.
// Esto es crucial para poder limpiarlo (detenerlo) cuando el componente se destruye.
let pollingInterval = null;


// --- 4. LÓGICA PRINCIPAL: OBTENCIÓN DE DATOS ---
/**
 * @description Obtiene todos los datos relacionados con amigos desde la API.
 * Utiliza Promise.all para ejecutar todas las peticiones en paralelo, mejorando el rendimiento.
 * Gestiona los estados de carga y error para el componente.
 */
const fetchData = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    // Usamos Promise.all para ejecutar todas las peticiones de forma concurrente.
    // Es más eficiente que esperarlas una por una (await secuencial).
    const [friendsRes, sentRes, receivedRes] = await Promise.all([
      getMyFriends(),
      getPendingRequestsSent(),
      getPendingRequestsReceived()
    ]);
    
    // Asignamos los datos de las respuestas a nuestras variables reactivas de estado.
    friends.value = friendsRes.data;
    sentRequests.value = sentRes.data;
    receivedRequests.value = receivedRes.data;

  } catch (error) {
    console.error("Error cargando los datos de amistad:", error);
    errorMessage.value = "No se pudo cargar la información de amigos. Inténtalo de nuevo más tarde.";
  } finally {
    // Este bloque se ejecuta siempre, tanto si el 'try' tuvo éxito como si falló.
    // Nos aseguramos de que el estado de carga siempre se desactive.
    isLoading.value = false;
  }
};


// --- 5. CICLO DE VIDA DEL COMPONENTE ---
onMounted(() => {
  // 1. Obtiene los datos iniciales tan pronto como el componente se monta en el DOM.
  fetchData(); 

  // 2. Inicia un mecanismo de polling (sondeo) para refrescar los datos automáticamente.
  // Esto mantiene las listas actualizadas (p. ej. si otro usuario acepta una solicitud)
  // sin necesidad de que el usuario refresque la página manualmente.
  pollingInterval = setInterval(fetchData, 5000); // Refresca cada 5 segundos
});

onUnmounted(() => {
  // Es fundamental limpiar/detener el intervalo cuando el componente se destruye (el usuario navega a otra página).
  // Si no se hace, el intervalo seguiría ejecutándose en segundo plano, causando
  // peticiones innecesarias a la API y posibles fugas de memoria en la aplicación.
  if (pollingInterval) {
    clearInterval(pollingInterval);
  }
});


// --- 6. MANEJADORES DE ACCIONES ---
/**
 * @description Función genérica para manejar las acciones del usuario (aceptar, rechazar, eliminar).
 * Ejecuta una acción asíncrona y, si tiene éxito, vuelve a cargar todos los datos para actualizar la vista.
 * Este enfoque evita la duplicación de código en los manejadores de eventos específicos.
 * @param {Function} action - La función asíncrona (la llamada a la API) que se debe ejecutar.
 */
const handleAction = async (action) => {
  try {
    // Ejecuta la acción específica que se ha pasado como argumento.
    await action();
    // Tras una acción exitosa, se vuelven a pedir todos los datos para reflejar los cambios.
    fetchData();
  } catch (error) {
    console.error("Error realizando la acción de amistad:", error);
    // Muestra un mensaje de error legible para el usuario.
    errorMessage.value = error.response?.data?.error || "Ocurrió un error al realizar la acción.";
  }
};

// Los siguientes manejadores utilizan la función genérica 'handleAction' para ejecutar su lógica.
const handleAcceptRequest = (userId) => handleAction(() => acceptFriendRequest(userId));
const handleDeclineRequest = (userId) => handleAction(() => declineOrCancelFriendRequest(userId));

const handleRemoveFriend = (userId) => {
  // Se añade un paso de confirmación antes de una acción destructiva como eliminar un amigo.
  if (window.confirm("¿Estás seguro de que quieres eliminar a este amigo?")) {
    handleAction(() => removeFriend(userId));
  }
};


// --- 7. FUNCIONES DE UTILIDAD ---
/**
 * @description Construye la URL absoluta para la imagen de perfil de un usuario.
 * Contempla los casos en que la URL ya es absoluta, es una ruta relativa, o no existe.
 * @param {string | null} fotoPerfil - La ruta de la imagen de perfil que viene de la API.
 * @returns {string} La URL completa y utilizable para el `src` de una imagen, o un avatar por defecto.
 */
const getProfilePictureUrl = (fotoPerfil) => {
  if (fotoPerfil) {
    // Si ya es una URL completa (comienza con http), la devuelve directamente.
    if (fotoPerfil.startsWith('http')) return fotoPerfil;

    // Si es una ruta relativa, construye la URL completa.
    const baseApiUrl = BASE_PATH.endsWith('/') ? BASE_PATH.slice(0, -1) : BASE_PATH;
    const relativeImagePath = fotoPerfil.startsWith('/') ? fotoPerfil.substring(1) : fotoPerfil;
    return `${baseApiUrl}/profile-pictures/${relativeImagePath}`;
  }
  // Si no se proporciona 'fotoPerfil', devuelve el avatar por defecto importado.
  return defaultAvatar;
};
</script>

<style src="./FriendsPageView.css" scoped> </style>