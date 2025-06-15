<template>
  <div class="my-tier-lists-view section-block">
    <div class="header-actions">
      <h2>Mis Tier Lists</h2>
      <button @click="openCreateTierListModal" class="action-button primary">Crear Nueva Tier List (Perfil)</button>
    </div>

    <div v-if="isLoading" class="loading-message">Cargando mis tier lists...</div>
    <div v-if="errorMessageApi" class="error-message">{{ errorMessageApi }}</div>

    <div v-if="!isLoading && myTierLists.length === 0 && !errorMessageApi" class="empty-message">
      Aún no has creado ninguna Tier List.
    </div>

    <div class="tierlists-grid-profile" v-if="!isLoading && myTierLists.length > 0">
      <div v-for="list in myTierLists" :key="list.public_id" class="tierlist-card-profile">
        <RouterLink :to="{ name: 'view-public-tierlist', params: { tierListPublicId: list.public_id } }" class="card-link-profile">
          <div class="card-header-profile">
            <h3 class="list-name-profile">{{ list.name }}</h3>
            <span class="list-status-chip-profile" :class="list.is_public ? 'public' : 'private'">
              {{ list.is_public ? 'Pública' : 'Privada' }}
            </span>
          </div>
          <p v-if="list.description" class="list-description-profile">{{ truncateText(list.description, 100) }}</p>
          <p v-else class="list-description-empty-profile"><em>Sin descripción.</em></p>
          <div class="list-footer-profile">
            <span class="type-info">Tipo: {{ formatTierListType(list.type) }}</span>
            <span class="last-updated-profile" v-if="list.updated_at">Última act.: {{ formatReadableDate(list.updated_at) }}</span>
          </div>
        </RouterLink>
      </div>
    </div>

    <div v-if="showCreateTierListModal" class="modal-overlay" @click.self="closeCreateTierListModal">
      <div class="modal-panel">
        <form @submit.prevent="handleCreateTierList" class="create-list-form">
          <div class="modal-header">
            <h3>Crear Nueva Tier List de Perfil</h3>
            <button type="button" @click="closeCreateTierListModal" class="modal-close-button" aria-label="Cerrar">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label for="tierListName">Nombre de la Tier List:</label>
              <input type="text" id="tierListName" v-model="newTierListForm.name" required maxlength="25">
            </div>
            <div class="form-group">
              <label for="tierListDescription">Descripción (opcional):</label>
              <textarea id="tierListDescription" v-model="newTierListForm.description" rows="3" maxlength="150"></textarea>
            </div>
            <div class="form-group checkbox-group">
              <input type="checkbox" id="tierListIsPublic" v-model="newTierListForm.is_public">
              <label for="tierListIsPublic">¿Hacer esta Tier List pública?</label>
            </div>
            <div v-if="createTierListErrorMessage" class="error-message modal-error">{{ createTierListErrorMessage }}</div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeCreateTierListModal" class="action-button secondary" :disabled="isCreatingTierList">
              Cancelar
            </button>
            <button type="submit" :disabled="isCreatingTierList || !newTierListForm.name" class="action-button primary">
              {{ isCreatingTierList ? 'Creando...' : 'Crear Tier List' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN ---
import { ref, onMounted, reactive } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { getMyProfileTierLists, createMyProfileTierList } from '@/services/apiInstances';


// --- 2. ESTADO DEL COMPONENTE ---
// -- Estado para la vista principal --
const myTierLists = ref([]); // Almacenará el array de Tier Lists del usuario.
const isLoading = ref(true); // Controla el mensaje de carga principal.
const errorMessageApi = ref(''); // Muestra errores relacionados con la carga de las listas.

// -- Estado para el modal de creación --
const showCreateTierListModal = ref(false); // Controla la visibilidad del modal.
const isCreatingTierList = ref(false); // Controla el estado de carga del botón "Crear".
const createTierListErrorMessage = ref(''); // Muestra errores dentro del modal de creación.
// Objeto reactivo para vincular los datos del formulario de nueva Tier List.
const newTierListForm = reactive({
  name: '',
  description: null,
  is_public: false,
});


// --- 3. CICLO DE VIDA ---
// onMounted se ejecuta cuando el componente se ha renderizado en el DOM.
// Es el lugar adecuado para la carga inicial de datos.
onMounted(() => {
  loadMyTierLists();
});


// --- 4. MÉTODOS DE DATOS ---
/**
 * @description Carga todas las Tier Lists del usuario desde la API.
 * Gestiona los estados de carga y error para la vista principal.
 */
const loadMyTierLists = async () => {
  isLoading.value = true;
  errorMessageApi.value = '';
  try {
    const response = await getMyProfileTierLists();
    myTierLists.value = response.data;
  } catch (error) {
    console.error("Error cargando mis tier lists:", error);
    errorMessageApi.value = "Error al cargar tus tier lists.";
    // Intenta mostrar un mensaje de error más específico si la API lo proporciona.
    if (error.response) {
      errorMessageApi.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudieron cargar las tier lists.'}`;
    }
  } finally {
    // Este bloque se ejecuta siempre, garantizando que el estado de carga se desactive.
    isLoading.value = false;
  }
};


// --- 5. MANEJADORES DE EVENTOS Y MODALES ---
/**
 * @description Abre el modal para crear una nueva Tier List y resetea el formulario.
 */
const openCreateTierListModal = () => {
  // Se limpian los campos del formulario y los mensajes de error.
  newTierListForm.name = '';
  newTierListForm.description = null;
  newTierListForm.is_public = false;
  createTierListErrorMessage.value = '';
  showCreateTierListModal.value = true;
};

/**
 * @description Cierra el modal de creación.
 */
const closeCreateTierListModal = () => {
  showCreateTierListModal.value = false;
};

/**
 * @description Maneja el envío del formulario para crear una nueva Tier List,
 * incluyendo validación, llamada a la API y manejo de errores.
 */
const handleCreateTierList = async () => {
  // Validación simple para asegurar que el nombre no esté vacío.
  if (!newTierListForm.name || newTierListForm.name.trim() === '') {
    createTierListErrorMessage.value = "El nombre de la tier list es obligatorio.";
    return;
  }
  isCreatingTierList.value = true;
  createTierListErrorMessage.value = '';

  // Se crea el objeto DTO (Data Transfer Object) con los datos del formulario.
  const requestDTO = {
    name: newTierListForm.name,
    description: newTierListForm.description || null,
    is_public: newTierListForm.is_public,
  };

  try {
    const response = await createMyProfileTierList(requestDTO);
    // Actualización optimista: la nueva lista se añade al principio del array local
    // para una respuesta visual inmediata, sin necesidad de recargar todo desde la API.
    myTierLists.value.unshift(response.data);
    closeCreateTierListModal();
  } catch (error) {
    console.error("Error creando tier list:", error);
    // Manejo detallado de errores para dar un feedback más preciso al usuario.
    if (error.response?.data) {
      createTierListErrorMessage.value = error.response.data.errors?.join(', ') || error.response.data.message || error.response.data.error || "No se pudo crear la tier list.";
    } else {
      createTierListErrorMessage.value = "Error de red al crear la tier list.";
    }
  } finally {
    isCreatingTierList.value = false;
  }
};


// --- 6. FUNCIONES DE UTILIDAD ---
/**
 * @description Formatea una fecha en formato ISO a un string legible (ej: 'jun 15, 2025').
 * @param {string} isoDateString - La fecha en formato ISO.
 * @returns {string} - La fecha formateada o el string original en caso de error.
 */
const formatReadableDate = (isoDateString) => {
  if (!isoDateString) return '';
  try {
    return new Date(isoDateString).toLocaleDateString(undefined, {
      year: 'numeric', month: 'short', day: 'numeric'
    });
  } catch (e) {
    return isoDateString;
  }
};

/**
 * @description Trunca un texto si excede una longitud máxima, añadiendo puntos suspensivos.
 * @param {string} text - El texto a truncar.
 * @param {number} maxLength - La longitud máxima permitida.
 * @returns {string} - El texto truncado o el original si es más corto.
 */
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

/**
 * @description Convierte el tipo de Tier List (devuelto por la API) en un texto legible.
 * @param {string} type - El tipo de Tier List (ej. 'PROFILE_GLOBAL').
 * @returns {string} - El texto formateado para mostrar en la UI.
 */
const formatTierListType = (type) => {
  if (!type) return 'Tipo Desconocido';
  // Mapea los valores del backend a texto amigable para el usuario.
  const typeMap = {
    'PROFILE_GLOBAL': 'De Perfil',
    'FROM_GAMELIST': 'Desde Lista de Juegos'
  };
  return typeMap[type.toUpperCase()] || type;
};
</script>
<style src="./MyTierListsView.css" scoped>

</style>