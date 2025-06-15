<template>
  <div class="edit-tierlist-view">
    <div v-if="isLoading && !tierListDetails" class="loading-message">
      Cargando Tier List...
    </div>
    <div v-else-if="errorMessageApi" class="error-message global-error">
      {{ errorMessageApi }}
    </div>

    <div v-if="tierListDetails" class="tierlist-content-wrapper">
      <section class="tierlist-header-section section-block">
        <div class="header-content">
          <div class="header-info">
            <h1>{{ tierListDetails.name }}</h1>
            <p v-if="tierListDetails.description" class="list-description-detail">
              {{ tierListDetails.description }}
            </p>
            <p v-else class="list-description-detail-empty">
              <em>Esta tier list no tiene descripción.</em>
            </p>
            <div class="list-meta-detail">
              <span :class="[
                'status-chip',
                tierListDetails.is_public ? 'public' : 'private',
              ]">
                {{ tierListDetails.is_public ? "Pública" : "Privada" }}
              </span>
              <span class="meta-separator">|</span>
              <span>Tipo: {{ formatTierListType(tierListDetails.type) }}</span>
              <span v-if="
                tierListDetails.type === 'FROM_GAMELIST' &&
                tierListDetails.source_game_list_public_id
              " class="meta-separator">|</span>
              <span v-if="
                tierListDetails.type === 'FROM_GAMELIST' &&
                tierListDetails.source_game_list_public_id
              ">
                <RouterLink :to="{
                  name: 'gamelist-detail',
                  params: {
                    listPublicId: tierListDetails.source_game_list_public_id,
                  },
                }">
                  Ver GameList Origen
                </RouterLink>
              </span>
              <span class="meta-separator">|</span>
              <span>Creada por: {{ tierListDetails.owner_username }}</span>
              <span v-if="tierListDetails.updated_at" class="meta-separator">|</span>
              <span v-if="tierListDetails.updated_at">
                Última act.:
                {{ formatReadableDate(tierListDetails.updated_at) }}
              </span>
            </div>
          </div>
          <div v-if="isOwner" class="header-actions-tierlist">
            <button v-if="isEditableTierList" @click="openEditTierListMetadataModal"
              class="action-button add-tier-section">
              Editar Detalles
            </button>
            <button @click="handleDeleteTierList" :disabled="isLoading" class="action-button danger">
              Eliminar Tier List
            </button>
          </div>
        </div>
      </section>

      <div class="tier-maker-container section-block">
        <div class="add-section-controls" v-if="isOwner">
          <button @click="openAddSectionModal" class="action-button edit-tierlist">
            Añadir Nueva Tier (Fila)
          </button>
        </div>

        <div v-for="section in sortedCustomSections" :key="section.internal_id" class="tier-row">
          <div class="tier-label" :style="{
            backgroundColor: section.color || getTierColor(section.name),
          }">
            <span v-if="
              !editingSectionName || editingSectionId !== section.internal_id
            ">{{ section.name }}</span>

            <input v-if="
              isOwner &&
              editingSectionName &&
              editingSectionId === section.internal_id &&
              !section.is_default_unclassified
            " v-model="currentSectionNameEdit" @keyup.enter="saveSectionName(section)"
              @blur="saveSectionName(section)" @keyup.esc="cancelEditSectionName" class="section-name-input" v-focus />

            <div v-if="isOwner && !section.is_default_unclassified" class="tier-actions">
              <button @click="startEditSectionName(section)" class="icon-button" title="Editar nombre tier">
                &#9998;
              </button>
              <label class="icon-button" title="Cambiar color">
                🎨
                <input type="color" v-model="section.color" @change="handleUpdateSectionDetails(section)"
                  class="color-input-hidden" />
              </label>
            </div>
          </div>

          <div class="tier-items-droppable-area" :class="{
            'drag-over-active':
              dragOverSectionId === section.internal_id && isOwner,
          }" @dragover.prevent="
              isOwner ? handleDragOver($event, section.internal_id) : null
              " @dragleave.prevent="
              isOwner ? handleDragLeaveItemOrSection($event) : null
              " @drop.prevent="
              isOwner ? handleDrop($event, section.internal_id, null) : null
              ">
            <div v-if="!section.items || section.items.length === 0" class="tier-empty-placeholder">
              {{ isOwner ? "Arrastra un juego aquí" : "(Vacío)" }}
            </div>
            <div v-else class="tier-items-grid-horizontal">
              <div v-for="item in section.items" :key="item.tier_list_item_id" class="tier-item-compact" :class="{
                'dragging-item':
                  draggedItemInfo?.tierListItemId === item.tier_list_item_id,
                'drag-over-item-target':
                  dragOverItemId === item.tier_list_item_id && isOwner,
              }" :draggable="isOwner" @dragstart="
                  isOwner
                    ? handleDragStart($event, item, section.internal_id)
                    : null
                  " @dragend="isOwner ? handleDragEnd($event) : null" @dragover.prevent.stop="
                  isOwner
                    ? handleDragOver(
                      $event,
                      section.internal_id,
                      item.tier_list_item_id
                    )
                    : null
                  " @dragleave="
                  isOwner ? handleDragLeaveItemOrSection($event) : null
                  " @drop.stop="
                  isOwner
                    ? handleDrop(
                      $event,
                      section.internal_id,
                      item.tier_list_item_id
                    )
                    : null
                  ">
                <RouterLink :to="{
                  name: 'game-details',
                  params: { igdbId: item.game_igdb_id },
                }" :title="item.game_name">
                  <img :src="getItemCoverUrl(item.game_cover_url, 'cover_big')" :alt="item.game_name"
                    class="tier-item-cover-compact" @error="onTierItemCoverError" />
                </RouterLink>
                <button v-if="isEditableTierList" @click.stop="
                  handleRemoveItemFromTierList(item.tier_list_item_id)
                  " class="remove-item-button-compact" title="Quitar ítem de la tier list">
                  &times;
                </button>
              </div>
            </div>
          </div>

          <button v-if="isOwner && !section.is_default_unclassified" @click="confirmRemoveSection(section.internal_id)"
            class="tier-row-delete-button" title="Eliminar Tier">
            <img src="/src/assets/icons/bing.svg" alt="" width="20" height="20" fill="currentColor" />
          </button>
        </div>

        <div class="tier-row unclassified-tier-row" v-if="
          tierListDetails.unclassified_section ||
          (isEditableTierList &&
              /*lógica para mostrar juegos de la biblioteca no añadidos*/ true)
        ">
          <div class="tier-label unclassified-label">
            <span>{{
              tierListDetails.unclassified_section
                ? tierListDetails.unclassified_section.name
                : "Juegos Disponibles"
            }}</span>
            <button v-if="isEditableTierList" @click="openAddGamesToUnclassifiedModal"
              class="action-button-small tertiary" title="Añadir juegos de tu biblioteca a esta Tier List">
              Añadir Juegos de Biblioteca
            </button>
          </div>
          <div class="tier-items-droppable-area" :class="{
            'drag-over-active':
              dragOverSectionId ===
              tierListDetails.unclassified_section.internal_id &&
              dragOverItemId === null &&
              isOwner,
            'can-drop': isOwner && draggedItemInfo,
          }" @dragover.prevent="
              isOwner
                ? handleDragOver(
                  $event,
                  tierListDetails.unclassified_section.internal_id
                )
                : null
              " @dragleave="
              isOwner
                ? handleDragLeaveSection(
                  $event,
                  tierListDetails.unclassified_section.internal_id
                )
                : null
              " @drop="
              isOwner
                ? handleDropOnSection(
                  $event,
                  tierListDetails.unclassified_section.internal_id
                )
                : null
              ">
            <div v-if="
              !tierListDetails.unclassified_section ||
              !tierListDetails.unclassified_section.items ||
              tierListDetails.unclassified_section.items.length === 0
            " class="tier-empty-placeholder">
              {{
                tierListDetails.type === "FROM_GAMELIST"
                  ? "Arrastra juegos aquí o añádelos desde tu biblioteca."
                  : "(Vacìo)"
              }}
            </div>
            <div v-else class="tier-items-grid-horizontal">
              <div v-for="item in tierListDetails.unclassified_section.items" :key="item.tier_list_item_id"
                class="tier-item-compact" :class="{
                  'drag-over-item-target':
                    dragOverItemId === item.tier_list_item_id && isOwner,
                }" :draggable="isOwner" @dragstart="
                  isOwner
                    ? handleDragStart(
                      $event,
                      item,
                      tierListDetails.unclassified_section.internal_id
                    )
                    : null
                  " @dragend="isOwner ? handleDragEnd($event) : null" @dragover.prevent.stop="
                  isOwner
                    ? handleDragOver(
                      $event,
                      tierListDetails.unclassified_section.internal_id,
                      item.tier_list_item_id
                    )
                    : null
                  " @dragleave="
                  isOwner ? handleDragLeaveItemOrSection($event) : null
                  " @drop.stop="
                  isOwner
                    ? handleDrop(
                      $event,
                      tierListDetails.unclassified_section.internal_id,
                      item.tier_list_item_id
                    )
                    : null
                  ">
                <RouterLink :to="{
                  name: 'game-details',
                  params: { igdbId: item.game_igdb_id },
                }" :title="item.game_name">
                  <img :src="getItemCoverUrl(item.game_cover_url, 'cover_big')" :alt="item.game_name"
                    class="tier-item-cover-compact" @error="onTierItemCoverError" />
                </RouterLink>
                <button v-if="isEditableTierList" @click.stop="
                  handleRemoveItemFromTierList(item.tier_list_item_id)
                  " class="remove-item-button-compact" title="Quitar ítem de la tier list (si es PROFILE_GLOBAL)">
                  &times;
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAddGamesToUnclassifiedModal && isEditableTierList" class="modal-overlay"
      @click.self="closeAddGamesToUnclassifiedModal">
      <div class="modal-panel add-games-modal-panel">
        <div class="modal-header">
          <h3>
            Añadir Juegos a "{{
              tierListDetails?.unclassified_section?.name || "Sin Clasificar"
            }}"
          </h3>
          <button type="button" @click="closeAddGamesToUnclassifiedModal" class="modal-close-button"
            aria-label="Cerrar">
            &times;
          </button>
        </div>
        <div class="modal-body">
          <div class="modal-filters-panel">
            <div class="modal-filter-group search-group">
              <input type="search" v-model="modalFilters.searchQuery" placeholder="Buscar en mi biblioteca..."
                class="modal-search-input" />
            </div>
            <div class="modal-filter-group">
              <select v-model="modalFilters.status">
                <option v-for="opt in gameStatusOptions" :key="opt.value" :value="opt.value">
                  {{ opt.text }}
                </option>
              </select>
            </div>
            <div class="modal-filter-group">
              <select v-model="modalFilters.platform">
                <option v-for="opt in personalPlatformOptions" :key="opt.value" :value="opt.value">
                  {{ opt.text }}
                </option>
              </select>
            </div>
          </div>

          <div v-if="isLoadingLibraryForSelection" class="loading-message">
            Cargando tu biblioteca...
          </div>
          <div v-if="addItemsErrorMessage" class="error-message modal-error">
            {{ addItemsErrorMessage }}
          </div>
          <div v-if="
            !isLoadingLibraryForSelection &&
            filteredLibraryForModal.length === 0 &&
            !addItemsErrorMessage
          " class="empty-message">
            No hay juegos que coincidan con tus filtros.
          </div>

          <div class="library-grid modal-games-grid" v-if="filteredLibraryForModal.length > 0">
            <div v-for="game in filteredLibraryForModal" :key="game.internal_id" class="library-game-card selectable"
              :class="{ 'selected-for-add': gamesToAdd.has(game.internal_id) }"
              @click="toggleGameForAdditionInternal(game.internal_id)">
              <div class="card-cover-container">
                <img :src="getItemCoverUrl(game.game_cover?.url, 'cover_big')" :alt="game.game_name"
                  class="library-game-cover" @error="onTierItemCoverError" />
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
          <div v-if="addItemsErrorMessage && !isLoadingLibraryForSelection"
            class="action-message modal-action-message error-message">
            {{ addItemsErrorMessage }}
          </div>
          <button type="button" @click="closeAddGamesToUnclassifiedModal" class="action-button secondary"
            :disabled="isLoadingTierItemAction || isLoadingLibraryForSelection">
            Cancelar
          </button>
          <button @click="handleAddSelectedGamesToUnclassified" :disabled="isLoadingTierItemAction ||
            isLoadingLibraryForSelection ||
            gamesToAdd.size === 0
            " class="action-button primary">
            {{
              isLoadingTierItemAction
                ? "Añadiendo..."
                : `Añadir (${gamesToAdd.size})`
            }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="!isLoading && !tierListDetails && errorMessageApi" class="no-results-message">
      No se pudo cargar la Tier List o no es accesible.
    </div>
    <div v-if="showEditTierListMetadataModal && isEditableTierList" class="modal-overlay"
      @click.self="closeEditTierListMetadataModal">
      <div class="modal-panel">
        <form @submit.prevent="handleUpdateTierListMetadata" class="edit-tierlist-form-modal">
          <div class="modal-header">
            <h3>Editar Detalles de la Tier List</h3>
            <button type="button" @click="closeEditTierListMetadataModal" class="modal-close-button"
              aria-label="Cerrar">
              &times;
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label for="editTierListName">Nombre de la Tier List:</label>
              <input type="text" id="editTierListName" v-model="editTierListForm.name" required maxlength="150" />
            </div>
            <div class="form-group">
              <label for="editTierListDescription">Descripción (opcional):</label>
              <textarea id="editTierListDescription" v-model="editTierListForm.description" rows="3"
                maxlength="1000"></textarea>
            </div>
            <div class="form-group checkbox-group">
              <input type="checkbox" id="editTierListIsPublic" v-model="editTierListForm.is_public" />
              <label for="editTierListIsPublic">¿Hacer esta Tier List pública?</label>
            </div>
          </div>
          <div class="modal-footer">
            <div v-if="editTierListMetadataMessage" :class="editTierListMetadataError ? 'error-message' : 'success-message'
              " class="action-message modal-action-message">
              {{ editTierListMetadataMessage }}
            </div>
            <button type="button" @click="closeEditTierListMetadataModal" class="action-button secondary"
              :disabled="isLoadingTierListMetadataUpdate">
              Cancelar
            </button>
            <button type="submit" :disabled="isLoadingTierListMetadataUpdate || !editTierListForm.name.trim()
              " class="action-button primary">
              {{
                isLoadingTierListMetadataUpdate
                  ? "Guardando..."
                  : "Guardar Cambios"
              }}
            </button>
          </div>
        </form>
      </div>
    </div>
    <div v-if="showAddSectionModal && isOwner" class="modal-overlay" @click.self="closeAddSectionModal">
      <div class="modal-panel add-section-modal-panel">
        <form @submit.prevent="handleAddSection" class="add-section-form">
          <div class="modal-header">
            <h3>Añadir Nueva Tier</h3>
            <button type="button" @click="closeAddSectionModal" class="modal-close-button" aria-label="Cerrar">
              &times;
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label for="newSectionName">Nombre de la Tier:</label>
              <input type="text" id="newSectionName" v-model="newSectionForm.name" required maxlength="30" />
              <small>Ej: S, A, Buenos, Mis Favoritos...</small>
            </div>
            <div v-if="addSectionErrorMessage" class="error-message modal-error">
              {{ addSectionErrorMessage }}
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeAddSectionModal" class="action-button secondary"
              :disabled="isAddingSection">
              Cancelar
            </button>
            <button type="submit" :disabled="isAddingSection || !newSectionForm.name.trim()"
              class="action-button primary">
              {{ isAddingSection ? "Añadiendo..." : "Añadir Tier" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script setup>
// --- 1. IMPORTACIONES Y CONFIGURACIÓN ---
import { ref, onMounted, watch, reactive, computed, nextTick } from "vue";
import { useRoute, RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import {
  getTierListDetailsByPublicId,
  updateMyTierListMetadata,
  deleteMyTierList,
  addSectionToMyTierList,
  updateMyTierSection,
  removeSectionFromMyTierList,
  addItemToMyUnclassifiedSection,
  removeItemFromMyTierList,
  moveItemInMyTierList,
  getMyUserGameLibrary,
} from "@/services/apiInstances";
import defaultTierItemCover from "@/assets/img/default-game-cover.svg";

const props = defineProps({
  tierListPublicId: {
    type: String,
    required: true,
  },
});

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();


// --- 2. ESTADO DEL COMPONENTE ---

// -- Estado principal de la vista --
const tierListDetails = ref(null);
const isLoading = ref(true);
const errorMessageApi = ref("");

// -- Estado para Drag and Drop --
const draggedItemInfo = ref(null);
const dragOverSectionId = ref(null);
const dragOverItemId = ref(null);

// -- Estado para Modal de Edición de Metadatos --
const showEditTierListMetadataModal = ref(false);
const isLoadingTierListMetadataUpdate = ref(false);
const editTierListMetadataMessage = ref("");
const editTierListMetadataError = ref(false);
const editTierListForm = reactive({
  name: "",
  description: null,
  is_public: false,
});

// -- Estado para Edición de Nombre de Sección (en línea) --
const editingSectionName = ref(false);
const editingSectionId = ref(null);
const currentSectionNameEdit = ref("");
const sectionEditMessage = ref("");
const sectionEditError = ref(false);
const isLoadingSectionAction = ref(false);

// -- Estado para Modal de Añadir Nueva Sección --
const showAddSectionModal = ref(false);
const newSectionForm = reactive({ name: "", color: "" });
const isAddingSection = ref(false);
const addSectionErrorMessage = ref("");

// -- Estado para Modal de Añadir Juegos a "Sin Clasificar" --
const showAddGamesToUnclassifiedModal = ref(false);
const isLoadingLibraryForSelection = ref(false);
const libraryForSelection = ref([]);
const gamesToAdd = ref(new Set());
const addItemsErrorMessage = ref("");
const isLoadingTierItemAction = ref(false);
const modalFilters = reactive({
  searchQuery: "",
  status: null,
  platform: null,
});


// --- 3. DATOS ESTÁTICOS (Opciones para Filtros) ---
const gameStatusOptions = [
  { value: null, text: "Todos los Estados" },
  { value: "PLAYING", text: "Jugando" },
  { value: "PLAYING_PAUSED", text: "En Pausa" },
  { value: "PLAYING_ENDLESS", text: "Sin Fin" },
  { value: "COMPLETED_MAIN_STORY", text: "Completado" },
  { value: "COMPLETED_100_PERCENT", text: "Completado 100%" },
  { value: "WISHLIST", text: "En Deseos" },
  { value: "ARCHIVED_ABANDONED", text: "Abandonado" },
];
const personalPlatformOptions = [
  { value: null, text: "Todas las Plataformas" },
  { value: "STEAM", text: "Steam" },
  { value: "EPIC_GAMES", text: "Epic Games Store" },
  { value: "GOG_GALAXY", text: "GOG Galaxy" },
  { value: "XBOX", text: "Xbox" },
  { value: "PLAYSTATION", text: "PlayStation" },
  { value: "NINTENDO", text: "Nintendo" },
  { value: "OTHER", text: "Otra" },
];


// --- 4. PROPIEDADES COMPUTADAS ---
/**
 * @description Filtra la biblioteca del usuario en el modal de "Añadir Juegos".
 */
const filteredLibraryForModal = computed(() => {
  if (!libraryForSelection.value) return [];
  return libraryForSelection.value.filter((game) => {
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
 * @description Determina si el usuario actual es el propietario de la Tier List.
 */
const isOwner = computed(() => {
  if (!authStore.isAuthenticated || !authStore.currentUser || !tierListDetails.value) return false;
  return authStore.currentUser.nombre_usuario === tierListDetails.value.owner_username;
});

/**
 * @description Determina si la Tier List es editable (propietario y de tipo 'PROFILE_GLOBAL').
 */
const isEditableTierList = computed(() => {
  return isOwner.value && tierListDetails.value?.type === "PROFILE_GLOBAL";
});

/**
 * @description Devuelve las secciones personalizadas ordenadas para su renderización.
 */
const sortedCustomSections = computed(() => {
  if (tierListDetails.value && tierListDetails.value.sections) {
    return [...tierListDetails.value.sections]
      .filter((section) => !section.is_default_unclassified)
      .sort((a, b) => (a.order || 0) - (b.order || 0));
  }
  return [];
});


// --- 5. DIRECTIVA VUE PERSONALIZADA ---
/**
 * @description Directiva para hacer foco automático en un input cuando se muestra.
 */
const vFocus = {
  mounted: (el) => { nextTick(() => { el.focus(); }); },
  updated: (el, binding) => { if (binding.value && binding.oldValue !== binding.value) { nextTick(() => { el.focus(); }); } },
};


// --- 6. CICLO DE VIDA Y WATCHERS ---
onMounted(() => {
  fetchTierListDetails(props.tierListPublicId);
});

watch(() => props.tierListPublicId, (newId, oldId) => {
    if (newId && newId !== oldId) {
      fetchTierListDetails(newId);
    }
  }
);

watch(() => newSectionForm.name, (newName) => {
    newSectionForm.color = getTierColor(newName);
  }
);


// --- 7. MÉTODOS DE DATOS ---
/**
 * @description Carga los detalles de la Tier List desde la API.
 */
const fetchTierListDetails = async (id) => {
  if (!id) {
    errorMessageApi.value = "ID de Tier List no proporcionado.";
    isLoading.value = false;
    return;
  }
  isLoading.value = true;
  errorMessageApi.value = "";
  tierListDetails.value = null;
  try {
    const response = await getTierListDetailsByPublicId(id);
    tierListDetails.value = response.data;
  } catch (error) {
    console.error(`Error cargando detalles de la Tier List (ID: ${id}):`, error);
    if (error.response) {
      errorMessageApi.value = `Error ${error.response.status}: ${error.response.data.message || "No se pudieron cargar los detalles."}`;
      if (error.response.status === 403 || error.response.status === 404) {
        router.push({ name: "home" });
      }
    } else {
      errorMessageApi.value = "Error de red al cargar los detalles.";
    }
  } finally {
    isLoading.value = false;
  }
};


// --- 8. MÉTODOS DE GESTIÓN DE LA TIER LIST ---
const openEditTierListMetadataModal = () => {
  if (!isEditableTierList.value || !tierListDetails.value) return;
  editTierListForm.name = tierListDetails.value.name || "";
  editTierListForm.description = tierListDetails.value.description || null;
  editTierListForm.is_public = tierListDetails.value.is_public || false;
  editTierListMetadataMessage.value = "";
  editTierListMetadataError.value = false;
  showEditTierListMetadataModal.value = true;
};

const closeEditTierListMetadataModal = () => {
  showEditTierListMetadataModal.value = false;
};

const handleUpdateTierListMetadata = async () => {
  if (!isEditableTierList.value) return;
  if (!editTierListForm.name || editTierListForm.name.trim() === "") {
    editTierListMetadataMessage.value = "El nombre de la Tier List es obligatorio.";
    editTierListMetadataError.value = true;
    return;
  }
  isLoadingTierListMetadataUpdate.value = true;
  editTierListMetadataMessage.value = "";
  editTierListMetadataError.value = false;
  const requestDTO = { name: editTierListForm.name, description: editTierListForm.description || null, is_public: editTierListForm.is_public };
  let hasChanges = false;
  if (requestDTO.name !== tierListDetails.value.name) hasChanges = true;
  if (requestDTO.description !== (tierListDetails.value.description || null)) hasChanges = true;
  if (requestDTO.is_public !== tierListDetails.value.is_public) hasChanges = true;
  if (!hasChanges) {
    editTierListMetadataMessage.value = "No se han realizado cambios.";
    isLoadingTierListMetadataUpdate.value = false;
    return;
  }
  try {
    const response = await updateMyTierListMetadata(props.tierListPublicId, requestDTO);
    tierListDetails.value = response.data;
    editTierListMetadataMessage.value = "¡Detalles de la Tier List actualizados!";
    setTimeout(() => closeEditTierListMetadataModal(), 1500);
  } catch (error) {
    console.error("Error actualizando metadatos de la Tier List:", error);
    editTierListMetadataError.value = true;
    if (error.response?.data) {
      editTierListMetadataMessage.value = error.response.data.errors?.join(", ") || error.response.data.message || "No se pudo actualizar la Tier List.";
    } else {
      editTierListMetadataMessage.value = "Error de red al actualizar la Tier List.";
    }
  } finally {
    isLoadingTierListMetadataUpdate.value = false;
  }
};

const handleDeleteTierList = async () => {
  if (!tierListDetails.value || !isOwner.value) return;
  if (window.confirm(`¿Estás seguro de que quieres eliminar la Tier List "${tierListDetails.value.name}"?`)) {
    isLoading.value = true;
    errorMessageApi.value = "";
    try {
      if (!props.tierListPublicId) {
        errorMessageApi.value = "Error: ID de Tier List no disponible para eliminación.";
        isLoading.value = false;
        return;
      }
      await deleteMyTierList(props.tierListPublicId);
      alert("Tier List eliminada exitosamente.");
      if (authStore.currentUser?.public_id) {
        router.push({ name: "profile", params: { publicId: authStore.currentUser.public_id }, query: { tab: "my-tierlists" } });
      } else {
        router.push({ name: "home" });
      }
    } catch (error) {
      console.error("Error eliminando la Tier List:", error);
      if (error.response?.data) {
        errorMessageApi.value = error.response.data.message || "No se pudo eliminar la Tier List.";
      } else {
        errorMessageApi.value = "Error de red o inesperado al eliminar la Tier List.";
      }
    } finally {
      isLoading.value = false;
    }
  }
};


// --- 9. MÉTODOS DE GESTIÓN DE SECCIONES (TIERS) ---
const startEditSectionName = (section) => {
  if (!isOwner.value) return;
  editingSectionId.value = section.internal_id;
  currentSectionNameEdit.value = section.name;
  editingSectionName.value = true;
  sectionEditMessage.value = "";
  sectionEditError.value = false;
};

const cancelEditSectionName = () => {
  editingSectionName.value = false;
  editingSectionId.value = null;
  currentSectionNameEdit.value = "";
  sectionEditMessage.value = "";
  sectionEditError.value = false;
};

const handleUpdateSectionDetails = async (section) => {
  if (!isOwner.value || section.is_default_unclassified || !section.internal_id) {
    cancelEditSectionName();
    return;
  }
  const newName = editingSectionId.value === section.internal_id ? currentSectionNameEdit.value.trim() : section.name;
  if (newName === "") {
    alert("El nombre de la sección no puede estar vacío.");
    return;
  }
  if (editingSectionId.value === section.internal_id) {
    editingSectionName.value = false;
    editingSectionId.value = null;
  }
  const requestDTO = { name: newName, color: section.color || "#CCCCCC" };
  isLoadingSectionAction.value = true;
  sectionEditMessage.value = "";
  try {
    const response = await updateMyTierSection(props.tierListPublicId, section.internal_id, requestDTO);
    tierListDetails.value = response.data;
    sectionEditMessage.value = "Sección actualizada.";
    setTimeout(() => { sectionEditMessage.value = ""; }, 3000);
  } catch (error) {
    console.error(`Error actualizando sección ID ${section.internal_id}:`, error);
    sectionEditError.value = true;
    sectionEditMessage.value = error.response?.data?.message || "No se pudo actualizar la sección.";
  } finally {
    isLoadingSectionAction.value = false;
  }
};

const saveSectionName = (section) => {
  handleUpdateSectionDetails(section);
};

const confirmRemoveSection = async (sectionId) => {
  if (!isOwner.value) return;
  const section = tierListDetails.value?.sections?.find((s) => s.internal_id === sectionId);
  if (!section) return;
  if (sortedCustomSections.value.length <= 1) {
    alert("No puedes eliminar la última sección.");
    return;
  }
  if (window.confirm(`¿Seguro que quieres eliminar la tier "${section.name}"?`)) {
    isLoadingSectionAction.value = true;
    errorMessageApi.value = "";
    try {
      const response = await removeSectionFromMyTierList(props.tierListPublicId, sectionId);
      tierListDetails.value = response.data;
    } catch (error) {
      console.error(`Error eliminando sección ID ${sectionId}:`, error);
      alert(`Error: ${error.response?.data?.message || "No se pudo eliminar la sección."}`);
    } finally {
      isLoadingSectionAction.value = false;
    }
  }
};

const openAddSectionModal = () => {
  if (!isOwner.value) return;
  newSectionForm.name = "";
  newSectionForm.color = "#CCCCCC";
  addSectionErrorMessage.value = "";
  showAddSectionModal.value = true;
};

const closeAddSectionModal = () => {
  showAddSectionModal.value = false;
};

const handleAddSection = async () => {
  if (!newSectionForm.name || newSectionForm.name.trim() === "") {
    addSectionErrorMessage.value = "El nombre de la sección es obligatorio.";
    return;
  }
  isAddingSection.value = true;
  addSectionErrorMessage.value = "";
  const requestDTO = { name: newSectionForm.name.trim(), color: newSectionForm.color };
  try {
    const response = await addSectionToMyTierList(props.tierListPublicId, requestDTO);
    tierListDetails.value = response.data;
    closeAddSectionModal();
  } catch (error) {
    console.error("Error añadiendo sección:", error);
    if (error.response?.data) {
      addSectionErrorMessage.value = error.response.data.errors?.join(", ") || error.response.data.message || "No se pudo añadir la sección.";
    } else {
      addSectionErrorMessage.value = "Error de red al añadir la sección.";
    }
  } finally {
    isAddingSection.value = false;
  }
};


// --- 10. MÉTODOS DE GESTIÓN DE ÍTEMS (JUEGOS) ---
const fetchLibraryForUnclassifiedSelection = async () => {
  isLoadingLibraryForSelection.value = true;
  addItemsErrorMessage.value = "";
  libraryForSelection.value = [];
  try {
    const libraryResponse = await getMyUserGameLibrary();
    if (!libraryResponse.data) throw new Error("No se recibieron datos de la biblioteca.");
    const allItemsInTierListSet = new Set();
    tierListDetails.value?.unclassified_section?.items?.forEach((item) => allItemsInTierListSet.add(item.user_game_id));
    tierListDetails.value?.sections?.forEach((section) => {
      section.items?.forEach((item) => allItemsInTierListSet.add(item.user_game_id));
    });
    libraryForSelection.value = libraryResponse.data.filter((libraryGame) => {
      return libraryGame.internal_id != null && !allItemsInTierListSet.has(libraryGame.internal_id);
    });
    if (libraryForSelection.value.length === 0) {
      addItemsErrorMessage.value = libraryResponse.data.length > 0 ? "Todos los juegos de tu biblioteca ya están en esta Tier List." : "No hay juegos en tu biblioteca para añadir.";
    }
  } catch (error) {
    console.error("Error cargando biblioteca para selección:", error);
    if (error.response?.data) {
      addItemsErrorMessage.value = error.response.data.message || "No se pudo cargar la biblioteca.";
    } else {
      addItemsErrorMessage.value = "Error de red al cargar la biblioteca.";
    }
  } finally {
    isLoadingLibraryForSelection.value = false;
  }
};

const openAddGamesToUnclassifiedModal = () => {
  if (!isEditableTierList.value) return;
  gamesToAdd.value.clear();
  fetchLibraryForUnclassifiedSelection();
  showAddGamesToUnclassifiedModal.value = true;
};

const closeAddGamesToUnclassifiedModal = () => {
  showAddGamesToUnclassifiedModal.value = false;
};

const toggleGameForAdditionInternal = (userGameInternalId) => {
  if (!userGameInternalId) return;
  if (gamesToAdd.value.has(userGameInternalId)) {
    gamesToAdd.value.delete(userGameInternalId);
  } else {
    gamesToAdd.value.add(userGameInternalId);
  }
};

const handleAddSelectedGamesToUnclassified = async () => {
  if (gamesToAdd.value.size === 0) return;
  isLoadingTierItemAction.value = true;
  addItemsErrorMessage.value = "";
  const promises = [];
  gamesToAdd.value.forEach((userGameId) => {
    const dto = { user_game_id: userGameId, order: null };
    promises.push(addItemToMyUnclassifiedSection(props.tierListPublicId, dto));
  });
  try {
    const results = await Promise.allSettled(promises);
    if (results.some((r) => r.status === "fulfilled")) {
      await fetchTierListDetails(props.tierListPublicId);
      closeAddGamesToUnclassifiedModal();
    }
    const failedCount = results.filter((r) => r.status === "rejected").length;
    if (failedCount > 0) {
      addItemsErrorMessage.value = `No se pudieron añadir ${failedCount} juego(s).`;
    }
  } catch (error) {
    console.error("Error añadiendo juegos a 'Sin Clasificar':", error);
    addItemsErrorMessage.value = error.response?.data?.message || "No se pudieron añadir los juegos.";
    if (error.response?.status === 403) {
      alert("No tienes permiso para añadir juegos a esta Tier List.");
    }
  } finally {
    isLoadingTierItemAction.value = false;
  }
};

const handleRemoveItemFromTierList = async (tierListItemId) => {
  if (!isEditableTierList.value) return;
  if (!tierListItemId) return;
  isLoadingTierItemAction.value = true;
  try {
    const response = await removeItemFromMyTierList(props.tierListPublicId, tierListItemId);
    tierListDetails.value = response.data;
  } catch (error) {
    console.error(`Error quitando ítem ${tierListItemId}:`, error);
    alert(`Error: ${error.response?.data?.message || "No se pudo quitar el ítem."}`);
  } finally {
    isLoadingTierItemAction.value = false;
  }
};


// --- 11. MÉTODOS DE DRAG & DROP ---
const handleDragStart = (event, item, originalSectionInternalId) => {
  if (!isOwner.value) { event.preventDefault(); return; }
  event.dataTransfer.effectAllowed = "move";
  event.dataTransfer.setData("application/json", JSON.stringify({ tierListItemId: item.tier_list_item_id, originalSectionId: originalSectionInternalId }));
  draggedItemInfo.value = { tierListItemId: item.tier_list_item_id, originalSectionId: originalSectionInternalId };
  event.target.closest(".tier-item-compact")?.classList.add("dragging-item");
};

const handleDragOver = (event, targetSectionId, targetItemId = null) => {
  if (!isOwner.value || !draggedItemInfo.value) return;
  event.preventDefault();
  event.dataTransfer.dropEffect = "move";
  if (targetItemId === draggedItemInfo.value.tierListItemId) {
    event.dataTransfer.dropEffect = "none";
    return;
  }
  dragOverSectionId.value = targetSectionId;
  dragOverItemId.value = targetItemId;
};

const handleDragLeaveItemOrSection = (event) => {
  if (event.relatedTarget && !event.currentTarget.contains(event.relatedTarget)) {
    dragOverSectionId.value = null;
    dragOverItemId.value = null;
  } else if (!event.relatedTarget) {
    dragOverSectionId.value = null;
    dragOverItemId.value = null;
  }
};

const handleDrop = async (event, targetSectionId, beforeItemId = null) => {
  event.preventDefault();
  if (!draggedItemInfo.value || !isOwner.value) return;
  const draggedData = JSON.parse(event.dataTransfer.getData("application/json"));
  const tierListItemIdToMove = draggedData.tierListItemId;
  const originalSectionId = draggedData.originalSectionId;
  if (beforeItemId === tierListItemIdToMove) return;
  let targetSection = tierListDetails.value?.sections?.find((s) => s.internal_id === targetSectionId);
  if (!targetSection) {
    if (tierListDetails.value?.unclassified_section?.internal_id === targetSectionId) {
      targetSection = tierListDetails.value.unclassified_section;
    } else {
      handleDragEnd();
      return;
    }
  }
  const itemsInTargetSection = targetSection.items || [];
  let newOrder;
  if (beforeItemId !== null) {
    const beforeItemIndex = itemsInTargetSection.findIndex((item) => item.tier_list_item_id === beforeItemId);
    if (beforeItemIndex !== -1) {
      if (originalSectionId === targetSectionId) {
        const draggedItemIndex = itemsInTargetSection.findIndex((item) => item.tier_list_item_id === tierListItemIdToMove);
        if (draggedItemIndex < beforeItemIndex) {
          newOrder = itemsInTargetSection[beforeItemIndex].item_order;
        } else {
          newOrder = beforeItemIndex > 0 ? itemsInTargetSection[beforeItemIndex - 1].item_order + 1 : itemsInTargetSection[beforeItemIndex].item_order;
        }
      } else {
        newOrder = itemsInTargetSection[beforeItemIndex].item_order;
      }
    } else {
      newOrder = itemsInTargetSection.length > 0 ? itemsInTargetSection[itemsInTargetSection.length - 1].item_order + 1 : 0;
    }
  } else {
    newOrder = itemsInTargetSection.length > 0 ? itemsInTargetSection[itemsInTargetSection.length - 1].item_order + 1 : 0;
  }
  isLoadingTierItemAction.value = true;
  errorMessageApi.value = "";
  const moveDTO = { target_section_internal_id: targetSectionId, new_order: newOrder };
  try {
    const response = await moveItemInMyTierList(props.tierListPublicId, tierListItemIdToMove, moveDTO);
    tierListDetails.value = response.data;
  } catch (error) {
    console.error("Error moviendo ítem en Tier List:", error);
    if (error.response?.data) {
      errorMessageApi.value = error.response.data.message || "No se pudo mover el ítem.";
    } else {
      errorMessageApi.value = "Error de red al mover el ítem.";
    }
    await fetchTierListDetails(props.tierListPublicId);
  } finally {
    isLoadingTierItemAction.value = false;
    handleDragEnd(event);
  }
};

const handleDragEnd = (event) => {
  document.querySelectorAll(".tier-item-compact.dragging-item").forEach((element) => {
    element.classList.remove("dragging-item");
  });
  draggedItemInfo.value = null;
  dragOverSectionId.value = null;
  dragOverItemId.value = null;
};

const handleDragLeaveSection = (event, sectionId) => {
  if (dragOverSectionId.value === sectionId) {
    dragOverSectionId.value = null;
  }
};

const handleDropOnSection = async (event, targetSectionId) => {
  event.preventDefault();
  if (!draggedItemInfo.value) return;
  const tierListItemIdToMove = draggedItemInfo.value.tierListItemId;
  let newOrder = 0;
  let targetSection;
  if (targetSectionId === tierListDetails.value?.unclassified_section?.internal_id) {
    targetSection = tierListDetails.value.unclassified_section;
  } else {
    targetSection = tierListDetails.value?.sections?.find((section) => section.internal_id === targetSectionId);
  }
  if (targetSection && targetSection.items) {
    newOrder = targetSection.items.length;
    if (draggedItemInfo.value.originalSectionId === targetSectionId) {
      newOrder = Math.max(0, targetSection.items.length - 1);
    }
  }
  isLoadingTierItemAction.value = true;
  errorMessageApi.value = "";
  const moveDTO = { target_section_internal_id: targetSectionId, new_order: newOrder };
  try {
    const response = await moveItemInMyTierList(props.tierListPublicId, tierListItemIdToMove, moveDTO);
    tierListDetails.value = response.data;
  } catch (error) {
    console.error("Error moviendo ítem en Tier List:", error);
    if (error.response?.data) {
      errorMessageApi.value = error.response.data.message || "No se pudo mover el ítem.";
    } else {
      errorMessageApi.value = "Error de red al mover el ítem.";
    }
    await fetchTierListDetails(props.tierListPublicId);
  } finally {
    isLoadingTierItemAction.value = false;
    handleDragEnd(event);
  }
};


// --- 12. FUNCIONES DE UTILIDAD (HELPERS) ---
const getItemCoverUrl = (itemCoverUrl, targetSize = "cover_small") => {
  if (typeof itemCoverUrl === "string" && itemCoverUrl.trim() !== "") {
    let imageUrl = itemCoverUrl;
    if (imageUrl.startsWith("//")) {
      imageUrl = `https:${imageUrl}`;
    }
    const regex = /(\/t_)[a-zA-Z0-9_-]+(\/)/;
    if (regex.test(imageUrl)) {
      imageUrl = imageUrl.replace(regex, `$1${targetSize}$2`);
    } else if (imageUrl.includes("/igdb/image/upload/")) {
      if (!imageUrl.includes("/igdb/image/upload/" + targetSize + "/")) {
        imageUrl = imageUrl.replace("/upload/", `/upload/${targetSize}/`);
      }
    }
    return imageUrl;
  }
  return defaultTierItemCover;
};

const onTierItemCoverError = (event) => {
  event.target.src = defaultTierItemCover;
};

const formatReadableDate = (isoDateString) => {
  if (!isoDateString) return "";
  try {
    return new Date(isoDateString).toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
  } catch (e) {
    return isoDateString;
  }
};

const formatTierListType = (type) => {
  if (!type) return "Tipo Desconocido";
  const typeMap = { PROFILE_GLOBAL: "De Perfil", FROM_GAMELIST: "Desde Lista de Juegos" };
  return typeMap[String(type).toUpperCase()] || String(type);
};

const getTierColor = (sectionName) => {
  if (!sectionName) return "#CCCCCC";
  const name = String(sectionName).toUpperCase();
  if (name.includes("S")) return "#FF7F7F";
  if (name.includes("A")) return "#FFBF7F";
  if (name.includes("B")) return "#FFFF7F";
  if (name.includes("C")) return "#BFFF7F";
  if (name.includes("D")) return "#7FBFFF";
  if (name.includes("E") || name.includes("F")) return "#BDB0D0";
  return "#CCCCCC";
};
</script>

<style src="./TierListDetail.css" scoped></style>
