<template>
  <div class="my-game-lists-view section-block">
    <div class="header-actions">
      <h2>Mis Listas de Juegos</h2>
      <button @click="openCreateListModal" class="action-button primary">Crear Nueva Lista</button>
    </div>

    <div v-if="isLoading" class="loading-message">Cargando mis listas...</div>
    <div v-if="errorMessageApi" class="error-message">{{ errorMessageApi }}</div>

    <div v-if="!isLoading && gameLists.length === 0 && !errorMessageApi" class="empty-message">
      Aún no has creado ninguna lista de juegos.
    </div>

    <div class="gamelists-grid" v-if="!isLoading && gameLists.length > 0">
      <div v-for="list in gameLists" :key="list.public_id" class="gamelist-card-mylists">
        <RouterLink :to="{ name: 'gamelist-detail', params: { listPublicId: list.public_id } }" class="card-link-mylists">
          <div class="card-header-mylists">
            <h3 class="list-name-mylists">{{ list.name }}</h3>
            <span class="list-status-chip" :class="list.is_public ? 'public' : 'private'">
              {{ list.is_public ? 'Pública' : 'Privada' }}
            </span>
          </div>
          <p v-if="list.description" class="list-description-mylists">{{ truncateText(list.description, 120) }}</p>
          <p v-else class="list-description-empty-mylists"><em>Sin descripción.</em></p>
          <div class="list-footer-mylists">
            <span class="game-count-mylists">{{ list.game_count }} juego(s)</span>
            <span class="last-updated-mylists" v-if="list.updated_at">Última act.: {{ formatReadableDate(list.updated_at) }}</span>
          </div>
        </RouterLink>
      </div>
    </div>

    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeCreateListModal">
      <div class="modal-panel">
        <form @submit.prevent="handleCreateList" class="create-list-form">
          <div class="modal-header">
            <h3>Crear Nueva Lista de Juegos</h3>
            <button type="button" @click="closeCreateListModal" class="modal-close-button" aria-label="Cerrar">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label for="listName">Nombre de la Lista:</label>
              <input type="text" id="listName" v-model="newListForm.name" required maxlength="25">
            </div>
            <div class="form-group">
              <label for="listDescription">Descripción (opcional):</label>
              <textarea id="listDescription" v-model="newListForm.description" rows="3" maxlength="150"></textarea>
            </div>
            <div class="form-group checkbox-group">
              <input type="checkbox" id="listIsPublic" v-model="newListForm.is_public">
              <label for="listIsPublic">¿Hacer esta lista pública?</label>
            </div>
            <div v-if="createErrorMessage" class="error-message modal-error">{{ createErrorMessage }}</div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeCreateListModal" class="action-button secondary" :disabled="isCreatingList">
              Cancelar
            </button>
            <button type="submit" :disabled="isCreatingList || !newListForm.name" class="action-button primary">
              {{ isCreatingList ? 'Creando...' : 'Crear Lista' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script setup>
// --- 1. IMPORTACIONES ---
import { ref, onMounted, reactive } from 'vue';
import { RouterLink } from 'vue-router';
import { getMyGameLists, createMyGameList } from '@/services/apiInstances';


// --- 2. ESTADO DEL COMPONENTE ---
// -- Estado para la vista principal --
const gameLists = ref([]); // Almacenará el array de listas de juegos del usuario.
const isLoading = ref(true); // Controla el mensaje de carga principal.
const errorMessageApi = ref(''); // Muestra errores relacionados con la carga de listas.

// -- Estado para el modal de creación de listas --
const showCreateModal = ref(false); // Controla la visibilidad del modal.
const isCreatingList = ref(false); // Controla el estado de carga del botón "Crear".
const createErrorMessage = ref(''); // Muestra errores dentro del modal de creación.
const newListForm = reactive({
  name: '',
  description: null,
  is_public: false,
});


// --- 3. CICLO DE VIDA ---
// onMounted se ejecuta cuando el componente está listo en el DOM.
// Es el lugar ideal para realizar la carga inicial de datos.
onMounted(() => {
  loadMyLists();
});


// --- 4. MÉTODOS DE DATOS ---
/**
 * @description Carga todas las listas de juegos del usuario desde la API.
 * Gestiona los estados de carga y error de la vista principal.
 */
const loadMyLists = async () => {
  isLoading.value = true;
  errorMessageApi.value = '';
  try {
    const response = await getMyGameLists();
    gameLists.value = response.data;
  } catch (error) {
    console.error("Error cargando mis listas de juegos:", error);
    errorMessageApi.value = "Error al cargar tus listas de juegos.";
    // Se intenta dar un mensaje de error más específico si la API lo proporciona.
    if (error.response) {
        errorMessageApi.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudieron cargar las listas.'}`;
    }
  } finally {
    // Este bloque se ejecuta siempre, asegurando que el estado de carga se desactive.
    isLoading.value = false;
  }
};


// --- 5. MANEJADORES DE EVENTOS Y MODALES ---
/**
 * @description Abre el modal de creación y resetea el formulario a sus valores por defecto.
 */
const openCreateListModal = () => {
  newListForm.name = '';
  newListForm.description = null;
  newListForm.is_public = false;
  createErrorMessage.value = '';
  showCreateModal.value = true;
};

/**
 * @description Cierra el modal de creación.
 */
const closeCreateListModal = () => {
  showCreateModal.value = false;
};

/**
 * @description Maneja la lógica de envío del formulario para crear una nueva lista.
 */
const handleCreateList = async () => {
  // Validación básica para asegurar que el nombre no esté vacío.
  if (!newListForm.name || newListForm.name.trim() === '') {
    createErrorMessage.value = "El nombre de la lista es obligatorio.";
    return;
  }
  isCreatingList.value = true;
  createErrorMessage.value = '';

  // Se construye el DTO (Data Transfer Object) para la petición a la API.
  const requestDTO = {
    name: newListForm.name,
    description: newListForm.description || null, // Se asegura de enviar null si la descripción está vacía.
    is_public: newListForm.is_public,
  };

  try {
    const response = await createMyGameList(requestDTO);
    // Actualización optimista: en lugar de volver a cargar todas las listas,
    // se añade la nueva lista directamente al principio del array local.
    // Esto proporciona una experiencia de usuario más rápida.
    gameLists.value.unshift(response.data);
    closeCreateListModal();
  } catch (error) {
    console.error("Error creando lista de juegos:", error);
    // Manejo de errores detallado según la respuesta de la API.
    if (error.response && error.response.data) {
      if (error.response.data.errors && Array.isArray(error.response.data.errors)) {
        createErrorMessage.value = error.response.data.errors.join(', ');
      } else {
        createErrorMessage.value = error.response.data.message || error.response.data.error || "No se pudo crear la lista.";
      }
    } else {
      createErrorMessage.value = "Error de red al crear la lista.";
    }
  } finally {
    isCreatingList.value = false;
  }
};


// --- 6. FUNCIONES DE UTILIDAD ---
/**
 * @description Formatea una fecha en formato ISO a un string legible (ej: 'jun 15, 2025').
 * @param {string} isoDateString - La fecha en formato ISO.
 * @returns {string} - La fecha formateada.
 */
const formatReadableDate = (isoDateString) => {
  if (!isoDateString) return '';
  return new Date(isoDateString).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
};

/**
 * @description Trunca un texto si excede una longitud máxima, añadiendo puntos suspensivos.
 * @param {string} text - El texto a truncar.
 * @param {number} maxLength - La longitud máxima permitida.
 * @returns {string} - El texto truncado o el original si no excede la longitud.
 */
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};
</script>
<style src="./MyGameListsView.css" scoped>

</style>