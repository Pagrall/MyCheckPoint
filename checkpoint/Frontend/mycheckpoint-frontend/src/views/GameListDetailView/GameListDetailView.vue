<template>
  <div class="gamelist-detail-view">
    <div v-if="isLoading && !gameListDetails" class="loading-message">Cargando detalles de la lista...</div>
    <div v-else-if="errorMessageApi" class="error-message">{{ errorMessageApi }}</div>

    <div v-if="gameListDetails" class="gamelist-content">
      <section class="gamelist-header-section section-block">
        <div class="header-content">
          <div class="header-info">
            <h1>{{ gameListDetails.name }}</h1>
            <p v-if="gameListDetails.description" class="list-description-detail">{{ gameListDetails.description }}</p>
            <p v-else class="list-description-detail-empty"><em>Esta lista no tiene descripción.</em></p>
            <div class="list-meta-detail">
              <span :class="['status-chip', gameListDetails.is_public ? 'public' : 'private']">
                {{ gameListDetails.is_public ? 'Pública' : 'Privada' }}
              </span>
              <span class="meta-separator">|</span>
              <span>Creada por: {{ gameListDetails.owner_username }}</span>
              <span v-if="gameListDetails.updated_at" class="meta-separator">|</span>
              <span v-if="gameListDetails.updated_at">
                Última actualización: {{ formatReadableDate(gameListDetails.updated_at) }}
              </span>
            </div>
          </div>
          <div v-if="isOwner" class="header-actions-gamelist">
            <button @click="openEditMetadataModal" class="action-button secondary">
              Editar Detalles de Lista
            </button>
            <button @click="handleGetOrCreateTierListFromGameList" class="action-button primary"
              :disabled="isLoadingTierListCreation || isLoadingMetadataUpdate || isLoadingActionOnGame">
              {{ isLoadingTierListCreation ? 'Procesando Tier List...' : 'Crear/Ver Tier List de esta Lista' }}
            </button>
            <button @click="handleDeleteList" :disabled="isLoadingMetadataUpdate || isLoadingActionOnGame"
              class="action-button danger">
              Eliminar Lista
            </button>
          </div>
        </div>
      </section>

      <div v-if="showEditMetadataModal && isOwner" class="modal-overlay" @click.self="closeEditMetadataModal">
        <div class="modal-panel">
          <form @submit.prevent="handleUpdateListMetadata" class="edit-list-form-modal">
  <div class="modal-header">
    <h3>Editar Detalles de la Lista</h3>
    <button type="button" @click="closeEditMetadataModal" class="modal-close-button"
      aria-label="Cerrar">&times;</button>
  </div>
  <div class="modal-body">
    <div class="form-group">
      <label for="editListName">Nombre de la Lista:</label>
      <input type="text" id="editListName" v-model="editListForm.name" required maxlength="30">
      <small class="char-counter">{{ 30 - editListForm.name.length }} caracteres restantes</small>
    </div>
    <div class="form-group">
      <label for="editListDescription">Descripción (opcional):</label>
      <textarea id="editListDescription" v-model="editListForm.description" rows="4" maxlength="200"></textarea>
      <small class="char-counter">{{ 200 - (editListForm.description || '').length }} caracteres restantes</small>
    </div>
    <div class="form-group checkbox-group">
      <input type="checkbox" id="editListIsPublic" v-model="editListForm.is_public">
      <label for="editListIsPublic">¿Hacer esta lista pública?</label>
    </div>
  </div>
  <div class="modal-footer">
    <div v-if="editMetadataMessage" :class="editMetadataError ? 'error-message' : 'success-message'"
      class="action-message modal-action-message">
      {{ editMetadataMessage }}
    </div>
    <button type="button" @click="closeEditMetadataModal" class="action-button secondary"
      :disabled="isLoadingMetadataUpdate">
      Cancelar
    </button>
    <button type="submit" :disabled="isLoadingMetadataUpdate || !editListForm.name"
      class="action-button primary">
      {{ isLoadingMetadataUpdate ? 'Guardando...' : 'Guardar Cambios' }}
    </button>
  </div>
</form>
        </div>
      </div>

      <section class="games-in-list-section section-block">
        <div class="section-header-actions">
          <h2>Juegos en "{{ gameListDetails.name }}" ({{ gameListDetails.game_count || (gameListDetails.games_in_list ?
            gameListDetails.games_in_list.length : 0) }})</h2>
          <button v-if="isOwner" @click="openAddGamesModal" class="action-button primary"
            :disabled="isLoadingActionOnGame">Añadir Juegos</button>
        </div>

        <div v-if="isLoadingActionOnGame && !showAddGamesModal && isOwner" class="loading-message small-loader">
          Actualizando lista...</div>
        <div v-else-if="!gameListDetails.games_in_list || gameListDetails.games_in_list.length === 0"
          class="empty-message list-empty">
          Esta lista aún no tiene juegos.
        </div>
        <div class="library-grid" v-else>
            <div v-for="gameEntry in gameListDetails.games_in_list" :key="gameEntry.internal_id"
  class="library-game-card">

  <RouterLink :to="{ name: 'game-details', params: { igdbId: gameEntry.game_igdb_id } }"
    class="game-card-link-content">
    
    <div class="card-cover-container">
      <img :src="getCoverUrl(gameEntry.game_cover, 'cover_big')"
        :alt="`Carátula de ${gameEntry.game_name || 'Juego'}`" class="library-game-cover"
        @error="onListGameCoverError" />

      <div v-if="gameEntry.personal_platform" class="platform-logo-on-cover">
        <img :src="getPlatformLogoUrl(gameEntry.personal_platform)" :alt="gameEntry.personal_platform" />
      </div>
    </div>

    <div class="card-content">
      <h3 class="game-title">{{ gameEntry.game_name || `Juego ID: ${gameEntry.game_igdb_id}` }}</h3>
      </div>
  </RouterLink>

  <button v-if="isOwner" @click.stop="handleRemoveGameFromList(gameEntry.internal_id)"
    class="remove-game-button" title="Quitar de la lista" :disabled="isLoadingActionOnGame">
    <span class="remove-icon">&times;</span>
  </button>
</div>
        </div>
      </section>
    </div>

    <!-- Reemplazar el modal de añadir juegos existente con esta nueva versión -->
    <div v-if="showAddGamesModal && isOwner" class="modal-overlay" @click.self="closeAddGamesModal">
      <div class="modal-panel add-games-modal-panel">
        <div class="modal-header">
          <h3>Añadir Juegos a "{{ gameListDetails?.name }}"</h3>
          <button type="button" @click="closeAddGamesModal" class="modal-close-button" aria-label="Cerrar">&times;</button>
        </div>
        <div class="modal-body">
          <div class="modal-filters-panel">
            <div class="modal-filter-group search-group">
              <input type="search" v-model="modalFilters.searchQuery" placeholder="Buscar en mi biblioteca..."
                class="modal-search-input">
            </div>
            <div class="modal-filter-group">
              <select v-model="modalFilters.status">
                <option v-for="opt in gameStatusOptions" :key="opt.value" :value="opt.value">{{ opt.text }}</option>
              </select>
            </div>
            <div class="modal-filter-group">
              <select v-model="modalFilters.platform">
                <option v-for="opt in personalPlatformOptions" :key="opt.value" :value="opt.value">{{ opt.text }}</option>
              </select>
            </div>
          </div>

          <div v-if="isLoadingLibraryForSelection" class="loading-message">Cargando tu biblioteca...</div>
          <div v-if="addGamesErrorMessage" class="error-message modal-error">{{ addGamesErrorMessage }}</div>

          <div v-if="!isLoadingLibraryForSelection && filteredLibraryForModal.length === 0 && !addGamesErrorMessage"
            class="empty-message">
            No hay juegos que coincidan con tus filtros.
          </div>

          <div class="modal-games-grid" v-if="filteredLibraryForModal.length > 0">
            <div v-for="game in filteredLibraryForModal" :key="game.internal_id"
              class="library-game-card selectable"
              :class="{ 'selected-for-add': gamesToAdd.has(game.internal_id) }"
              @click="toggleGameForAddition(game.internal_id)">
              
              <div class="card-cover-container">
                <img :src="getCoverUrl(game.game_cover, 'cover_big')"
                  :alt="`Carátula de ${game.game_name || 'Juego'}`"
                  class="library-game-cover"
                  @error="onListGameCoverError" />
                <div v-if="gamesToAdd.has(game.internal_id)" class="selection-overlay">
                  <span class="checkmark-icon">✔</span>
                </div>
              </div>
              
              <div class="card-content">
                <h3 class="game-title">{{ game.game_name }}</h3>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div v-if="addGamesErrorMessage && !isLoadingLibraryForSelection"
            class="action-message modal-action-message error-message">
            {{ addGamesErrorMessage }}
          </div>
          <button type="button" @click="closeAddGamesModal" class="action-button secondary"
            :disabled="isLoadingActionOnGame || isLoadingLibraryForSelection">
            Cancelar
          </button>
          <button @click="handleAddSelectedGamesToList"
            :disabled="isLoadingActionOnGame || isLoadingLibraryForSelection || gamesToAdd.size === 0"
            class="action-button primary">
            {{ isLoadingActionOnGame ? 'Añadiendo...' : `Añadir (${gamesToAdd.size})` }}
          </button>
        </div>
      </div>
    </div>
    <div v-if="!isLoading && !gameListDetails && errorMessageApi" class="no-results-message">
      No se pudo cargar la lista de juegos o no tienes permiso para verla.
    </div>
  </div>
</template>
<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN INICIAL ---
import { ref, onMounted, watch, reactive, computed } from 'vue';
import { useRoute, RouterLink, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import {
  getMySpecificGameListDetails,
  viewPublicGameListDetails,
  updateMyUserGameList,
  addGameToMyGameList,
  removeGameFromMyGameList,
  getMyUserGameLibrary,
  deleteMyGameList,
  getOrCreateTierListFromGameList
} from '@/services/apiInstances';
import defaultLibraryCover from '@/assets/img/default-game-cover.svg';

// Definición de las props que el componente espera recibir del componente padre.
const props = defineProps({
  listPublicId: {
    type: String,
    required: true,
  },
});

// Inicialización de hooks de Vue Router y el store de autenticación.
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();


// --- 2. ESTADO DEL COMPONENTE ---
// -- Estado principal de la vista --
const gameListDetails = ref(null);
const isLoading = ref(true); // Controla el loader principal de la página.
const errorMessageApi = ref('');

// -- Estado para el modal de edición de metadatos --
const showEditMetadataModal = ref(false);
const isLoadingMetadataUpdate = ref(false);
const editMetadataMessage = ref('');
const editMetadataError = ref(false);
const editListForm = reactive({
  name: '',
  description: null,
  is_public: false,
});

// -- Estado para la creación/obtención de TierList --
const isLoadingTierListCreation = ref(false);
const tierListCreationError = ref('');

// -- Estado para el modal de "Añadir Juegos" --
const showAddGamesModal = ref(false);
const isLoadingLibraryForSelection = ref(false);
const libraryForSelection = ref([]);
const gamesToAdd = ref(new Set()); // Un Set es ideal para manejar selecciones únicas.
const addGamesErrorMessage = ref('');
const isLoadingActionOnGame = ref(false); // Loader para acciones individuales (añadir/quitar).

// -- Estado para los filtros del modal "Añadir Juegos" --
const modalFilters = reactive({
  searchQuery: '',
  status: null,
  platform: null
});


// --- 3. DATOS ESTÁTICOS (Opciones para Selects) ---
// Opciones para el filtro de estado de juego en el modal.
const gameStatusOptions = [
  { value: null, text: 'Todos los Estados' },
  { value: 'PLAYING', text: 'Jugando' },
  { value: 'PLAYING_PAUSED', text: 'En Pausa' },
  { value: 'PLAYING_ENDLESS', text: 'Sin Fin' },
  { value: 'COMPLETED_MAIN_STORY', text: 'Completado' },
  { value: 'COMPLETED_100_PERCENT', text: 'Completado 100%' },
  { value: 'WISHLIST', text: 'En Deseos' },
  { value: 'ARCHIVED_ABANDONED', text: 'Abandonado' }
];

// Opciones para el filtro de plataforma en el modal.
const personalPlatformOptions = [
  { value: null, text: 'Todas las Plataformas' },
  { value: 'STEAM', text: 'Steam' },
  { value: 'EPIC_GAMES', text: 'Epic Games Store' },
  { value: 'GOG_GALAXY', text: 'GOG Galaxy' },
  { value: 'XBOX', text: 'Xbox' },
  { value: 'PLAYSTATION', text: 'PlayStation' },
  { value: 'NINTENDO', text: 'Nintendo' },
  { value: 'OTHER', text: 'Otra' }
];


// --- 4. PROPIEDADES COMPUTADAS ---
/**
 * @description Filtra la biblioteca del usuario para mostrarla en el modal "Añadir Juegos".
 * Se recalcula automáticamente cuando cambian los filtros o la biblioteca cargada.
 */
const filteredLibraryForModal = computed(() => {
  if (!libraryForSelection.value) return [];

  return libraryForSelection.value.filter(game => {
    const query = modalFilters.searchQuery.toLowerCase();
    const status = modalFilters.status;
    const platform = modalFilters.platform;

    const nameMatch = !query || (game.game_name && game.game_name.toLowerCase().includes(query));
    const statusMatch = !status || game.status === status;
    const platformMatch = !platform || game.personal_platform === platform;

    return nameMatch && statusMatch && platformMatch;
  });
});

/**
 * @description Determina si el usuario logueado es el propietario de la lista.
 * Es fundamental para la lógica de permisos (mostrar botones de edición, etc.).
 */
const isOwner = computed(() => {
  if (!authStore.isAuthenticated || !authStore.currentUser || !gameListDetails.value) {
    return false;
  }
  return authStore.currentUser.nombre_usuario === gameListDetails.value.owner_username;
});


// --- 5. OBTENCIÓN DE DATOS Y CICLO DE VIDA ---
/**
 * @description Carga los detalles de la lista desde la API.
 * Primero intenta cargarla como una lista del propietario. Si falla por permisos,
 * intenta cargarla como una lista pública.
 * @param {string} id - El ID público de la lista.
 */
const fetchListDetails = async (id) => {
  if (!id) {
    errorMessageApi.value = "ID de lista no proporcionado.";
    isLoading.value = false;
    return;
  }
  isLoading.value = true;
  errorMessageApi.value = '';
  try {
    // Primer intento: obtener la lista como si fuera del usuario actual.
    const response = await getMySpecificGameListDetails(id);
    gameListDetails.value = response.data;
  } catch (error) {
    // Si falla (ej. 403 No eres el dueño), intenta obtenerla como pública.
    if (error.response && (error.response.status === 403 || error.response.status === 404 || error.response.status === 401)) {
      try {
        const publicResponse = await viewPublicGameListDetails(id);
        gameListDetails.value = publicResponse.data;
        // Medida de seguridad: si la API devolviera una lista privada, no la mostramos.
        if (!gameListDetails.value.is_public) {
          errorMessageApi.value = "Esta lista no es pública.";
          gameListDetails.value = null;
          router.push({ name: 'home' });
          return;
        }
      } catch (publicError) {
        if (publicError.response) {
          errorMessageApi.value = `Error ${publicError.response.status}: ${publicError.response.data.message || publicError.response.data.error || 'No se pudo cargar la lista pública.'}`;
        } else {
          errorMessageApi.value = 'Error de red al cargar la lista pública.';
        }
        gameListDetails.value = null;
      }
    } else { // Otro tipo de error en la primera llamada (ej. 500).
      if (error.response) {
        errorMessageApi.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudieron cargar los detalles de la lista.'}`;
      } else {
        errorMessageApi.value = 'Error de red al cargar los detalles de la lista.';
      }
      gameListDetails.value = null;
    }
  } finally {
    isLoading.value = false;
  }
};

// Hook que se ejecuta cuando el componente se monta en el DOM.
onMounted(() => {
  fetchListDetails(props.listPublicId);
});

// Observador que se activa si cambia el ID de la lista en la URL.
// Esto permite recargar los datos sin tener que recargar toda la página.
watch(() => props.listPublicId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchListDetails(newId);
  }
});


// --- 6. MÉTODOS PARA MODAL "EDITAR METADATOS" ---
const openEditMetadataModal = () => {
  if (!gameListDetails.value) return;
  editListForm.name = gameListDetails.value.name || '';
  editListForm.description = gameListDetails.value.description || null;
  editListForm.is_public = gameListDetails.value.is_public || false;
  editMetadataMessage.value = '';
  editMetadataError.value = false;
  showEditMetadataModal.value = true;
};

const closeEditMetadataModal = () => {
  showEditMetadataModal.value = false;
};

const handleUpdateListMetadata = async () => {
  if (!editListForm.name || editListForm.name.trim() === '') {
    editMetadataMessage.value = "El nombre de la lista es obligatorio.";
    editMetadataError.value = true;
    return;
  }
  isLoadingMetadataUpdate.value = true;
  editMetadataMessage.value = '';
  editMetadataError.value = false;

  const dto = {
    name: editListForm.name,
    description: editListForm.description,
    is_public: editListForm.is_public,
  };
  
  // Optimización: se comprueba si ha habido cambios antes de llamar a la API.
  let hasChanges = false;
  if (dto.name !== gameListDetails.value.name) { hasChanges = true; }
  if (dto.description !== (gameListDetails.value.description || null)) { hasChanges = true; }
  if (dto.is_public !== gameListDetails.value.is_public) { hasChanges = true; }

  if (!hasChanges) {
    editMetadataMessage.value = "No se han realizado cambios.";
    editMetadataError.value = false;
    isLoadingMetadataUpdate.value = false;
    return;
  }

  try {
    const response = await updateMyUserGameList(props.listPublicId, dto);
    gameListDetails.value = response.data; // Actualiza los datos locales con la respuesta de la API.
    editMetadataMessage.value = "¡Detalles de la lista actualizados!";
    editMetadataError.value = false;
    setTimeout(() => closeEditMetadataModal(), 1500); // Cierra el modal tras 1.5s.
  } catch (error) {
    editMetadataError.value = true;
    if (error.response?.data) {
      editMetadataMessage.value = error.response.data.errors?.join(', ') || error.response.data.message || error.response.data.error || "No se pudo actualizar la lista.";
    } else {
      editMetadataMessage.value = "Error de red al actualizar la lista.";
    }
  } finally {
    isLoadingMetadataUpdate.value = false;
  }
};


// --- 7. MÉTODOS PARA MODAL "AÑADIR JUEGOS" ---
// Carga la biblioteca del usuario, excluyendo los juegos que ya están en la lista actual.
const fetchLibraryForSelection = async () => {
  isLoadingLibraryForSelection.value = true;
  addGamesErrorMessage.value = '';
  try {
    const response = await getMyUserGameLibrary();
    const currentGameIdsInList = new Set(gameListDetails.value?.games_in_list?.map(g => g.internal_id) || []);
    libraryForSelection.value = response.data.filter(game => !currentGameIdsInList.has(game.internal_id));
  } catch (error) {
    addGamesErrorMessage.value = "No se pudo cargar tu biblioteca para seleccionar juegos.";
  } finally {
    isLoadingLibraryForSelection.value = false;
  }
};

const openAddGamesModal = () => {
  gamesToAdd.value.clear();
  fetchLibraryForSelection();
  showAddGamesModal.value = true;
};

const closeAddGamesModal = () => {
  showAddGamesModal.value = false;
};

// Añade o quita un juego del Set de selección.
const toggleGameForAddition = (userGameInternalId) => {
  if (gamesToAdd.value.has(userGameInternalId)) {
    gamesToAdd.value.delete(userGameInternalId);
  } else {
    gamesToAdd.value.add(userGameInternalId);
  }
};

// Envía a la API la petición de añadir todos los juegos seleccionados.
const handleAddSelectedGamesToList = async () => {
  if (gamesToAdd.value.size === 0) {
    addGamesErrorMessage.value = "Selecciona al menos un juego para añadir.";
    return;
  }
  isLoadingActionOnGame.value = true;
  addGamesErrorMessage.value = '';

  // Se crea un array de promesas para ejecutarlas en paralelo.
  const promises = [];
  gamesToAdd.value.forEach(userGameId => {
    promises.push(addGameToMyGameList(props.listPublicId, { user_game_id: userGameId }));
  });

  try {
    await Promise.all(promises); // Las peticiones se envían a la vez.
    await fetchListDetails(props.listPublicId); // Recarga los detalles para ver los cambios.
    closeAddGamesModal();
  } catch (error) {
    if (error.response?.data) {
      addGamesErrorMessage.value = error.response.data.message || error.response.data.error || "Error al añadir algunos juegos.";
    } else {
      addGamesErrorMessage.value = "Error de red al añadir juegos.";
    }
  } finally {
    isLoadingActionOnGame.value = false;
  }
};


// --- 8. MÉTODOS DE ACCIONES (TIER LIST, QUITAR JUEGO, ELIMINAR LISTA) ---
const handleGetOrCreateTierListFromGameList = async () => {
  if (!gameListDetails.value?.public_id) {
    tierListCreationError.value = "No se puede procesar la Tier List: ID de GameList no disponible.";
    return;
  }
  isLoadingTierListCreation.value = true;
  tierListCreationError.value = '';
  try {
    const response = await getOrCreateTierListFromGameList(props.listPublicId);
    const tierListData = response.data;

    if (tierListData && tierListData.public_id) {
      // Redirige a la vista de detalle de la Tier List.
      router.push({ name: 'view-public-tierlist', params: { tierListPublicId: tierListData.public_id } });
    } else {
      throw new Error("La respuesta de la API no contiene el ID público de la Tier List.");
    }
  } catch (error) {
    if (error.response) {
      tierListCreationError.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudo procesar la Tier List.'}`;
    } else {
      tierListCreationError.value = "Error de red o inesperado al procesar la Tier List.";
    }
  } finally {
    isLoadingTierListCreation.value = false;
  }
};

const handleRemoveGameFromList = async (userGameInternalId) => {
  if (!userGameInternalId) {
    errorMessageApi.value = "Error: No se pudo identificar el juego a eliminar.";
    return;
  }
  isLoadingActionOnGame.value = true;
  errorMessageApi.value = '';
  try {
    await removeGameFromMyGameList(props.listPublicId, userGameInternalId);
    // Actualización optimista: se modifica el estado local para una respuesta visual instantánea.
    if (gameListDetails.value?.games_in_list) {
      gameListDetails.value.games_in_list = gameListDetails.value.games_in_list.filter(
        game => game.internal_id !== userGameInternalId
      );
      gameListDetails.value.game_count = gameListDetails.value.games_in_list.length;
    }
  } catch (error) {
    if (error.response?.data) {
      errorMessageApi.value = error.response.data.message || error.response.data.error || "No se pudo quitar el juego.";
    } else {
      errorMessageApi.value = "Error de red al quitar el juego.";
    }
  } finally {
    isLoadingActionOnGame.value = false;
  }
};

const handleDeleteList = async () => {
  if (!gameListDetails.value) return;
  if (window.confirm(`¿Estás seguro de que quieres eliminar la lista "${gameListDetails.value.name}" permanentemente? Esta acción no se puede deshacer.`)) {
    isLoading.value = true;
    errorMessageApi.value = '';
    try {
      if (!props.listPublicId) {
        errorMessageApi.value = "Error: ID de lista no disponible para eliminación.";
        isLoading.value = false;
        return;
      }
      await deleteMyGameList(props.listPublicId);
      // Redirección al perfil del usuario.
      if (authStore.currentUser?.public_id) {
        router.push({ name: 'profile', params: { publicId: authStore.currentUser.public_id } });
      } else {
        router.push({ name: 'home' }); // Fallback.
      }
    } catch (error) {
      if (error.response?.data) {
        errorMessageApi.value = error.response.data.message || error.response.data.error || "No se pudo eliminar la lista.";
      }
      else {
        errorMessageApi.value = "Error de red o inesperado al eliminar la lista.";
      }
    } finally {
      isLoading.value = false;
    }
  }
};


// --- 9. FUNCIONES HELPER DE FORMATO ---
const getCoverUrl = (coverData, targetSize = 't_cover_small') => {
  const currentPlaceholder = defaultLibraryCover;
  if (coverData && typeof coverData.url === 'string' && coverData.url.trim() !== '') {
    let imageUrl = coverData.url;
    if (imageUrl.startsWith('//')) { imageUrl = `https:${imageUrl}`; }
    const originalUrlBeforeReplace = imageUrl;
    imageUrl = imageUrl.replace(/(\/t_)[a-zA-Z0-9_-]+(\/)/, `$1${targetSize}$2`);
    if (imageUrl === originalUrlBeforeReplace && !imageUrl.includes(`/${targetSize}/`) && imageUrl.includes('/upload/') && !imageUrl.includes('/igdb/image/upload/' + targetSize + '/')) {
      imageUrl = imageUrl.replace('/upload/', `/upload/${targetSize}/`);
    }
    return imageUrl;
  }
  return currentPlaceholder;
};

// Construye la URL del logo de la plataforma dinámicamente.
const getPlatformLogoUrl = (platform) => {
  if (!platform) return '';
  const platformName = platform.toLowerCase();
  try {
    return new URL(`/src/assets/logo-personal-platform/${platformName}.svg`, import.meta.url).href;
  } catch (error) {
    return new URL(`/src/assets/logo-personal-platform/other.svg`, import.meta.url).href;
  }
};

const onListGameCoverError = (event) => { event.target.src = defaultLibraryCover; };

const formatReadableDate = (isoDateString) => {
  if (!isoDateString) return '';
  return new Date(isoDateString).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
};
</script>
<style src="./GameListDetailView.css" scoped></style>