<template>
  <div class="game-details-view">
    <div v-if="isLoading" class="loading-message">Cargando detalles del juego...</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <div v-if="!isLoading && gameDetail && gameDetail.game_info" class="game-content-wrapper">
      <section class="game-main-header section-block">

        <div class="game-cover-container">

          <img :src="getCoverUrl(gameDetail.game_info.cover, '720p', 'mainCover')"
            :alt="`Carátula de ${gameDetail.game_info.name || 'Juego'}`" class="game-cover-main"
            @error="onImageError" />

          <div v-if="gameDetail.game_info.first_release_date || gameDetail.game_info.first_release_status !== null"
            class="game-release-status-on-cover meta-item" :class="getReleaseStatusClass(gameDetail.game_info)">
            <span>{{ formatReleaseStatus(gameDetail.game_info) }}</span>
          </div>
        </div>


        <div class="game-title-meta">
          <h1>{{ gameDetail.game_info.name }}</h1>
          <div class="meta-grid">
            <div v-if="gameDetail.game_info.game_type" class="meta-item">
              <strong>Tipo:</strong>
              <span>{{ formatGameType(gameDetail.game_info.game_type) }}</span>
            </div>
            <div v-if="gameDetail.game_info.first_release_date" class="meta-item">
              <strong>Lanzamiento:</strong>
              <span>{{ formatTimestampToDate(gameDetail.game_info.first_release_date) }}</span>
            </div>
            <div v-if="gameDetail.game_info.total_rating" class="meta-item">
              <strong>Puntuación IGDB:</strong>
              <span>{{ Math.round(gameDetail.game_info.total_rating) }}/100</span>
              <small v-if="gameDetail.game_info.total_rating_count"> ({{ gameDetail.game_info.total_rating_count }}
                votos)</small>
            </div>
          </div>

          <section class="user-game-data-section" v-if="authStore.isAuthenticated">
            <div v-if="gameDetail.user_game_data">
              <h2>Mis Datos del Juego</h2>

              <div class="library-actions">
                <button v-if="!showLibraryForm" @click="openLibraryModal(false)" class="action-button primary">
                  Editar Mis Datos
                </button>
                <button v-if="!showLibraryForm && gameDetail.user_game_data" @click="openPrivateNoteModal"
                  class="action-button secondary">
                  Añadir/Editar Nota Privada
                </button>
              </div>

              <div class="user-data-grid">
                <div v-if="gameDetail.user_game_data.status" class="data-item"><strong>Estado:</strong> {{
                  formatUserGameStatus(gameDetail.user_game_data.status) }}</div>
                <div v-if="gameDetail.user_game_data.score !== null && gameDetail.user_game_data.score !== undefined"
                  class="data-item"><strong>Puntuación:</strong> {{ gameDetail.user_game_data.score }}/10</div>
                <div v-if="gameDetail.user_game_data.personal_platform" class="data-item"><strong>Plataforma:</strong>
                  {{ formatPersonalPlatform(gameDetail.user_game_data.personal_platform) }}</div>
                <div
                  v-if="gameDetail.user_game_data.has_possession !== null && gameDetail.user_game_data.has_possession !== undefined"
                  class="data-item"><strong>Lo Tengo:</strong> {{ gameDetail.user_game_data.has_possession ? 'Sí' :
                    'No' }}</div>
                <div v-if="gameDetail.user_game_data.start_date" class="data-item"><strong>Empezado:</strong> {{
                  formatDateSimple(gameDetail.user_game_data.start_date) }}</div>
                <div v-if="gameDetail.user_game_data.end_date" class="data-item"><strong>Terminado:</strong> {{
                  formatDateSimple(gameDetail.user_game_data.end_date) }}</div>
                <div v-if="gameDetail.user_game_data.story_duration_hours" class="data-item"><strong>Horas
                    (Historia):</strong> {{ gameDetail.user_game_data.story_duration_hours }}h</div>
                <div v-if="gameDetail.user_game_data.story_secondary_duration_hours" class="data-item"><strong>Horas
                    (Main+Sides):</strong> {{ gameDetail.user_game_data.story_secondary_duration_hours }}h</div>
                <div v-if="gameDetail.user_game_data.completionist_duration_hours" class="data-item"><strong>Horas
                    (Completista):</strong> {{ gameDetail.user_game_data.completionist_duration_hours }}h</div>

                <!--<div v-if="gameDetail.user_game_data.private_comment" class="data-item full-width"><strong>Comentario
                    Privado:</strong>
                  <p class="user-comment">{{ gameDetail.user_game_data.private_comment }}</p>
                </div>-->
              </div>

              <div class="library-actions">
                <button v-if="!showLibraryForm" @click="handleRemoveFromLibrary" :disabled="isLoadingLibraryAction"
                  class="action-button danger">
                  {{ isLoadingLibraryAction ? 'Eliminando...' : 'Eliminar de Mi Biblioteca' }}
                </button>
              </div>
            </div>

            <div v-else class="add-to-library-prompt">
              <div class="library-actions">
                <button v-if="!showLibraryForm" @click="openLibraryModal(true)" class="action-button primary">
                  Añadir a Mi Biblioteca
                </button>
              </div>
            </div>
          </section>

          <div class="tags-container header-tags">
            <span v-for="genre in gameDetail.game_info.genres" :key="genre.id" class="tag genre-tag">
              {{ genre.name }}
            </span>
            <span v-for="theme in gameDetail.game_info.themes" :key="theme.id" class="tag theme-tag">
              {{ theme.name }}
            </span>
          </div>
        </div>
      </section>
      <div class="column-right">
        <nav class="tabs-navigation">
          <button @click="setActiveTab('details')" :class="{ 'active-tab': activeTab === 'details' }">
            Detalles
          </button>
          <button @click="setActiveTab('multimedia')" :class="{ 'active-tab': activeTab === 'multimedia' }">
            Multimedia
          </button>
          <button @click="setActiveTab('related')" :class="{ 'active-tab': activeTab === 'related' }">
            Contenido Relacionado
          </button>
          <button @click="setActiveTab('community')" :class="{ 'active-tab': activeTab === 'community' }">
            Comunidad y Mis Datos
          </button>
        </nav>

        <div class="tab-content">
          <div v-show="activeTab === 'details'" class="tab-pane">

            <section class="screenshots-section section-block"
              v-if="gameDetail.game_info.screenshots && gameDetail.game_info.screenshots.length">


              <div class="carousel-container">
                <button v-if="gameDetail.game_info.screenshots && gameDetail.game_info.screenshots.length > 1"
                  @click="prevScreenshot" class="carousel-arrow prev" aria-label="Anterior">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" />
                  </svg>
                </button>

                <div class="carousel-viewport" ref="carouselViewportRef">
                  <div class="carousel-slider" :style="carouselSliderStyle" @transitionend="handleTransitionEnd">
                    <div v-for="(screenshot, index) in gameDetail.game_info.screenshots" :key="screenshot.id"
                      class="carousel-item">
                      <img :src="getCoverUrl(screenshot, 'screenshot_med')" :alt="`Screenshot ${screenshot.id}`"
                        class="gallery-image clickable"
                        @click="openLightbox(gameDetail.game_info.screenshots, index)" />
                    </div>
                  </div>
                </div>

                <button v-if="gameDetail.game_info.screenshots && gameDetail.game_info.screenshots.length > 1"
                  @click="nextScreenshot" class="carousel-arrow next" aria-label="Siguiente">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" />
                  </svg>
                </button>
              </div>
            </section>


            <!--
            <section class="game-summary section-block" v-if="gameDetail.game_info.summary">
              <h2>Resumen</h2>
              <p>{{ gameDetail.game_info.summary }}</p>
            </section>
            -->

            <section class="game-storyline section-block" v-if="gameDetail.game_info.storyline">
              <h2>Argumento</h2>
              <p>{{ gameDetail.game_info.storyline }}</p>
            </section>

            <section class="private-notes-section section-block"
              v-if="gameDetail.user_game_data && gameDetail.user_game_data.private_comment">
              <!--
              <div class="section-header-with-action">
                <h2>Mis Notas Privadas</h2>
                
                <button @click="openPrivateNoteModal" class="action-button secondary small-button">
                  Editar Nota
                </button>
                
              </div>
              -->
              <h2>Mis Notas Privadas</h2>
              <blockquote class="private-comment-quote">
                <p style="white-space: pre-wrap;">{{ displayComment }}</p>
                <button v-if="commentNeedsTruncation" @click="isExpanded = !isExpanded" class="read-more-button">
                  {{ isExpanded ? 'Leer menos' : 'Leer más...' }}
                </button>
              </blockquote>
            </section>

            <section class="metadata-lists section-block">
              <div v-if="gameDetail.game_info.platforms && gameDetail.game_info.platforms.length"
                class="metadata-group">
                <h3>Plataformas</h3>
                <div class="tags-container">
                  <span v-for="platform in gameDetail.game_info.platforms" :key="platform.id" class="tag platform-tag">
                    {{ platform.name }}
                  </span>
                </div>
              </div>
              <div v-if="gameDetail.game_info.game_modes && gameDetail.game_info.game_modes.length"
                class="metadata-group">
                <h3>Modos de Juego</h3>
                <div class="tags-container">
                  <span v-for="mode in gameDetail.game_info.game_modes" :key="mode.id" class="tag mode-tag">
                    {{ mode.name }}
                  </span>
                </div>
              </div>
              <div v-if="gameDetail.game_info.game_engines && gameDetail.game_info.game_engines.length"
                class="metadata-group">
                <h3>Motores Gráficos</h3>
                <div class="tags-container">
                  <span v-for="engine in gameDetail.game_info.game_engines" :key="engine.id" class="tag engine-tag">
                    {{ engine.name }}
                  </span>
                </div>
              </div>
              <div v-if="gameDetail.game_info.franchises && gameDetail.game_info.franchises.length"
                class="metadata-group">
                <h3>Franquicias</h3>
                <div class="tags-container">
                  <span v-for="franchise in gameDetail.game_info.franchises" :key="franchise.id"
                    class="tag franchise-tag">
                    {{ franchise.name }}
                  </span>
                </div>
              </div>
            </section>

            <section class="involved-companies-section section-block"
              v-if="gameDetail.game_info.involved_companies && gameDetail.game_info.involved_companies.length">
              <h2>Compañías Involucradas</h2>
              <ul class="companies-list">
                <li v-for="involvement in gameDetail.game_info.involved_companies" :key="involvement.id"
                  class="company-item">
                  <span class="company-name">{{ involvement.company.name }}</span>
                  <span v-if="getCompanyRoles(involvement)" class="company-roles"> ({{ getCompanyRoles(involvement)
                  }})</span>
                </li>
              </ul>
            </section>

            <section class="keywords-section section-block"
              v-if="gameDetail.game_info.keywords && gameDetail.game_info.keywords.length">
              <h2>Palabras Clave</h2>
              <div class="tags-container">
                <span v-for="keyword in gameDetail.game_info.keywords" :key="keyword.id" class="tag keyword-tag">
                  {{ keyword.name }}
                </span>
              </div>
            </section>

            <section class="websites-section section-block"
              v-if="gameDetail.game_info.websites && gameDetail.game_info.websites.length">
              <h2>Sitios Web</h2>
              <ul v-if="recognizedWebsites.length > 0" class="websites-list recognized-links">
                <li v-for="website in recognizedWebsites" :key="website.id">
                  <a :href="website.url" target="_blank" rel="noopener noreferrer" class="website-link"
                    :title="getWebsiteDisplayName(website.url)"> <img :src="getIconUrl(getWebsiteIconName(website.url))"
                      class="website-icon" alt="" />
                  </a>
                </li>
              </ul>

              <ul v-if="otherWebsites.length > 0" class="websites-list other-links">
                <li v-for="website in otherWebsites" :key="website.id">
                  <a :href="website.url" target="_blank" rel="noopener noreferrer" class="website-link">

                    <img :src="getIconUrl(getWebsiteIconName(website.url))" class="website-icon" alt="" />

                    <span>{{ getWebsiteDisplayName(website.url) }}</span>
                  </a>
                </li>
              </ul>
            </section>
          </div>

          <div v-show="activeTab === 'multimedia'" class="tab-pane">
            <section class="artworks-section section-block"
              v-if="gameDetail.game_info.artworks && gameDetail.game_info.artworks.length">
              <h2>Ilustraciones</h2>
              <div class="gallery-grid">
                <div v-for="(artwork, index) in gameDetail.game_info.artworks" :key="artwork.id" class="gallery-item">
                  <img :src="getCoverUrl(artwork, '720p')" class="gallery-image clickable"
                    @click="openLightbox(gameDetail.game_info.artworks, index)" />
                </div>
              </div>
            </section>



            <section class="videos-section section-block"
              v-if="gameDetail.game_info.videos && gameDetail.game_info.videos.length">
              <h2>Vídeos</h2>
              <div class="videos-grid">
                <div v-for="video in gameDetail.game_info.videos" :key="video.id" class="video-item">
                  <h4 class="video-name" v-if="video.name">{{ video.name }}</h4>
                  <div class="video-embed-container" v-if="video.video_id">
                    <iframe :src="getYouTubeEmbedUrl(video.video_id)" :title="video.name || 'Vídeo del juego'"
                      frameborder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                      allowfullscreen></iframe>
                  </div>
                  <p v-else class="no-video-id">ID de vídeo no disponible.</p>
                </div>
              </div>
            </section>
          </div>

          <div v-show="activeTab === 'related'" class="tab-pane">
            <section class="related-content-section section-block">
              <div class="related-list" v-if="gameDetail.game_info.parent_game">
                <h3>Juego Principal</h3>
                <div class="related-games-grid single-item-grid">
                  <div class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: gameDetail.game_info.parent_game.id } }">
                      <img :src="getCoverUrl(gameDetail.game_info.parent_game.cover, '720p', 'relatedCover')"
                        :alt="gameDetail.game_info.parent_game.name" class="related-game-cover"
                        @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ gameDetail.game_info.parent_game.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>
              <div class="related-list" v-if="gameDetail.game_info.version_parent">
                <h3>Versión Principal</h3>
                <div class="related-games-grid single-item-grid">
                  <div class="related-game-card">
                    <RouterLink
                      :to="{ name: 'game-details', params: { igdbId: gameDetail.game_info.version_parent.id } }">
                      <img :src="getCoverUrl(gameDetail.game_info.version_parent.cover, '720p', 'relatedCover')"
                        :alt="gameDetail.game_info.version_parent.name" class="related-game-cover"
                        @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ gameDetail.game_info.version_parent.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>

              <div class="related-list" v-if="gameDetail.game_info.dlcs && gameDetail.game_info.dlcs.length">
                <h3>DLCs</h3>
                <div class="related-games-grid">
                  <div v-for="dlc in gameDetail.game_info.dlcs" :key="dlc.id" class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: dlc.id } }">
                      <img :src="getCoverUrl(dlc.cover, '720p', 'relatedCover')" :alt="dlc.name"
                        class="related-game-cover" @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ dlc.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>

              <div class="related-list"
                v-if="gameDetail.game_info.expansions && gameDetail.game_info.expansions.length">
                <h3>Expansiones</h3>
                <div class="related-games-grid">
                  <div v-for="expansion in gameDetail.game_info.expansions" :key="expansion.id"
                    class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: expansion.id } }">
                      <img :src="getCoverUrl(expansion.cover, '720p', 'relatedCover')" :alt="expansion.name"
                        class="related-game-cover" @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ expansion.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>

              <div class="related-list" v-if="gameDetail.game_info.bundles && gameDetail.game_info.bundles.length">
                <h3>Paquetes (Bundles)</h3>
                <div class="related-games-grid">
                  <div v-for="bundleItem in gameDetail.game_info.bundles" :key="bundleItem.id"
                    class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: bundleItem.id } }">
                      <img :src="getCoverUrl(bundleItem.cover, '720p', 'relatedCover')" :alt="bundleItem.name"
                        class="related-game-cover" @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ bundleItem.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>

              <div class="related-list" v-if="gameDetail.game_info.remakes && gameDetail.game_info.remakes.length">
                <h3>Remakes</h3>
                <div class="related-games-grid">
                  <div v-for="remake in gameDetail.game_info.remakes" :key="remake.id" class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: remake.id } }">
                      <img :src="getCoverUrl(remake.cover, '720p', 'relatedCover')" :alt="remake.name"
                        class="related-game-cover" @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ remake.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>

              <div class="related-list" v-if="gameDetail.game_info.remasters && gameDetail.game_info.remasters.length">
                <h3>Remasters</h3>
                <div class="related-games-grid">
                  <div v-for="remaster in gameDetail.game_info.remasters" :key="remaster.id" class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: remaster.id } }">
                      <img :src="getCoverUrl(remaster.cover, '720p', 'relatedCover')" :alt="remaster.name"
                        class="related-game-cover" @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ remaster.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>

              <div class="related-list"
                v-if="gameDetail.game_info.similar_games && gameDetail.game_info.similar_games.length">
                <h3>Juegos Similares</h3>
                <div class="related-games-grid">
                  <div v-for="similarGame in gameDetail.game_info.similar_games" :key="similarGame.id"
                    class="related-game-card">
                    <RouterLink :to="{ name: 'game-details', params: { igdbId: similarGame.id } }">
                      <img :src="getCoverUrl(similarGame.cover, '720p', 'relatedCover')" :alt="similarGame.name"
                        class="related-game-cover" @error="onImageErrorSmall" />
                      <span class="related-game-name">{{ similarGame.name }}</span>
                    </RouterLink>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <div v-show="activeTab === 'community'" class="tab-pane">

            <section class="user-game-data-section section-block" v-if="authStore.isAuthenticated">
              <h2>Mi Comentario Público</h2>

              <div v-if="gameDetail.user_game_data && gameDetail.user_game_data.comment">
                <div class="user-data-grid">
                  <div class="data-item full-width">
                    <p class="user-comment">{{ gameDetail.user_game_data.comment }}</p>
                  </div>
                </div>
                <div class="library-actions">
                  <button @click="openCommentModal" class="action-button secondary">
                    Editar Comentario
                  </button>
                </div>
              </div>

              <div v-else-if="gameDetail.user_game_data">
                <div class="add-to-library-prompt">
                  <p>Aún no has añadido un comentario para este juego. ¡Comparte tu opinión con la comunidad!</p>
                </div>
                <div class="library-actions">
                  <button @click="openCommentModal" class="action-button primary">
                    Añadir Comentario
                  </button>
                </div>
              </div>

              <div v-else>
                <div class="add-to-library-prompt">
                  <p>Debes añadir este juego a tu biblioteca para poder dejar un comentario.</p>
                </div>
              </div>
            </section>


            <section class="public-comments-section section-block"
              v-if="gameDetail.public_comments && gameDetail.public_comments.length">
              <h2>Comentarios de la Comunidad</h2>
              <ul class="comments-list">
                <li v-for="comment in gameDetail.public_comments" :key="comment.username + comment.comment_date"
                  class="comment-item">
                  <strong class="comment-author">{{ comment.username }}</strong>
                  <!--
                  <small class="comment-date"> ({{ formatTimestampToDate(comment.comment_date) }})</small>
                  -->
                  <p class="comment-text">{{ comment.comment_text }}</p>
                </li>
              </ul>
            </section>
            <section v-else class="public-comments-section section-block">
              <h2>Comentarios de la Comunidad</h2>
              <p>Aún no hay comentarios para este juego.</p>
            </section>
          </div>
        </div>
      </div>

    </div>
    <div v-if="showLibraryForm" class="modal-overlay" @click.self="closeLibraryModal">
      <div class="modal-panel library-wizard-modal">
        <div class="modal-header">
          <h3>{{ isAddingNewLibraryEntry ? 'Añadir a' : 'Editar en' }} Mi Biblioteca: {{ gameDetail?.game_info?.name }}
          </h3>
          <button type="button" @click="closeLibraryModal" class="modal-close-button" aria-label="Cerrar">×</button>
        </div>

        <div class="modal-body">
          <div v-if="modalStep === 0" class="wizard-step-0">
            <h4>¿Qué quieres hacer con este juego?</h4>
            <div class="status-selection-grid">
              <button @click="setStatusCategoryAndContinue('playing')"
                :class="{ 'selected': mainStatusCategory === 'playing' }">Jugando</button>
              <button @click="setStatusCategoryAndContinue('completed')"
                :class="{ 'selected': mainStatusCategory === 'completed' }">Terminado</button>
              <button @click="setStatusCategoryAndContinue('archived')"
                :class="{ 'selected': mainStatusCategory === 'archived' }">Archivado</button>
              <button @click="setStatusCategoryAndContinue('wishlist')"
                :class="{ 'selected': mainStatusCategory === 'wishlist' }">Deseado</button>
            </div>
          </div>

          <form v-if="modalStep === 1" @submit.prevent="goToStep(2)" class="library-form-modal">
            <div class="form-grid">
              <div class="form-group">
                <label for="lib-status">Estado específico:</label>
                <select id="lib-status" v-model="libraryForm.status">
                  <option v-for="opt in computedStatusOptions" :key="opt.value" :value="opt.value">{{ opt.text }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="lib-score">Puntuación (0-10):</label>
                <input type="number" id="lib-score" v-model.number="libraryForm.score" min="0" max="10" step="0.5" />
              </div>
            </div>
            <div class="form-group full-width-form-group">
              <label for="lib-public-comment">Comentario Público:</label>
              <textarea id="lib-public-comment" v-model="libraryForm.comment" rows="4"></textarea>
            </div>

            <div class="modal-footer">
              <button type="button" @click="goToStep(0)" class="action-button secondary">Atrás</button>
              <button type="submit" class="action-button primary">Continuar</button>
            </div>
          </form>

          <form v-if="modalStep === 2" @submit.prevent="handleSaveToLibrary" class="library-form-modal">
            <div class="form-grid">
              <div class="form-group">
                <label for="lib-platform">Plataforma Personal:</label>
                <select id="lib-platform" v-model="libraryForm.personal_platform">
                  <option :value="null">-- Sin especificar --</option>
                  <option v-for="opt in personalPlatformOptions" :key="opt.value" :value="opt.value">{{ opt.text }}
                  </option>
                </select>
              </div>
              <div class="form-group checkbox-group">
                <input type="checkbox" id="lib-possession" v-model="libraryForm.has_possession" />
                <label for="lib-possession">¿Lo tengo en propiedad?</label>
              </div>
              <div class="form-group">
                <label for="lib-start-date">Fecha de Inicio:</label>
                <input type="date" id="lib-start-date" v-model="libraryForm.start_date" />
              </div>
              <div class="form-group">
                <label for="lib-end-date">Fecha de Fin:</label>
                <input type="date" id="lib-end-date" v-model="libraryForm.end_date" />
              </div>

              <div class="form-group">
                <label for="lib-hours-type">Tipo de Horas:</label>
                <select id="lib-hours-type" v-model="hourInputType">
                  <option value="story_duration_hours">Horas (Historia)</option>
                  <option value="story_secondary_duration_hours">Horas (Main+Sides)</option>
                  <option value="completionist_duration_hours">Horas (Completista)</option>
                </select>
              </div>
              <div class="form-group">
                <label for="lib-hours-value">Horas Jugadas:</label>
                <input type="number" id="lib-hours-value" min="0" v-model.number="libraryForm[hourInputType]" />
              </div>
            </div>
            <div class="form-group full-width-form-group">
              <label for="lib-private-comment">Comentario Privado (Solo visible para ti):</label>
              <textarea id="lib-private-comment" v-model="libraryForm.private_comment" rows="4"></textarea>
            </div>

            <div class="modal-footer">
              <button type="button" @click="goToStep(1)" class="action-button secondary">Atrás</button>
              <button type="submit" :disabled="isLoadingLibraryAction" class="action-button primary">
                {{ isLoadingLibraryAction ? 'Guardando...' : 'Guardar Cambios' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>



    <div v-if="!isLoading && !gameDetail && !errorMessage" class="no-results-message">
      No se pudieron cargar los detalles del juego.
    </div>
  </div>

  <div v-if="showCommentModal" class="modal-overlay" @click.self="showCommentModal = false">
    <div class="modal-panel">
      <form @submit.prevent="handleSaveComment" class="library-form-modal">
        <div class="modal-header">
          <h3>Mi Comentario sobre {{ gameDetail?.game_info?.name }}</h3>
          <button type="button" @click="showCommentModal = false" class="modal-close-button"
            aria-label="Cerrar">×</button>
        </div>

        <div class="modal-body">
          <div class="form-group full-width-form-group">
            <label for="public-comment-input">Tu opinión:</label>
            <textarea id="public-comment-input" v-model="commentFormText" rows="6"
              placeholder="Escribe aquí tu comentario público..."></textarea>
          </div>
        </div>

        <div class="modal-footer">
          <div v-if="commentActionMessage" :class="commentActionError ? 'error-message' : 'success-message'"
            class="action-message modal-action-message">
            {{ commentActionMessage }}
          </div>
          <button type="button" @click="showCommentModal = false" class="action-button secondary"
            :disabled="isSavingComment">
            Cancelar
          </button>
          <button type="submit" :disabled="isSavingComment" class="action-button primary">
            {{ isSavingComment ? 'Guardando...' : 'Guardar Comentario' }}
          </button>
        </div>
      </form>
    </div>
  </div>


  <div v-if="showPrivateNoteModal" class="modal-overlay" @click.self="showPrivateNoteModal = false">
    <div class="modal-panel">
      <form @submit.prevent="handleSavePrivateNote" class="library-form-modal">
        <div class="modal-header">
          <h3>Editar Mi Nota Privada</h3>
          <button type="button" @click="showPrivateNoteModal = false" class="modal-close-button"
            aria-label="Cerrar">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group full-width-form-group">
            <label for="private-note-input">Tu nota privada sobre {{ gameDetail?.game_info?.name }}:</label>
            <textarea id="private-note-input" v-model="privateNoteFormText" rows="8"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" @click="showPrivateNoteModal = false" class="action-button secondary"
            :disabled="isSavingPrivateNote">
            Cancelar
          </button>
          <button type="submit" :disabled="isSavingPrivateNote" class="action-button primary">
            {{ isSavingPrivateNote ? 'Guardando...' : 'Guardar Nota' }}
          </button>
        </div>
      </form>
    </div>
  </div>


  <div v-if="showLightbox && currentImage" class="lightbox-overlay" @click.self="closeLightbox">

    <button v-if="currentGallery.length > 1" @click.stop="prevImage" class="nav-arrow prev"
      aria-label="Imagen anterior">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" />
      </svg>
    </button>

    <div class="lightbox-content">
      <button @click="closeLightbox" class="lightbox-close" aria-label="Cerrar">
        <p>x</p>
      </button>
      <img :src="getCoverUrl(currentImage, '1080p')"
        :alt="`Imagen ampliada ${currentIndex + 1} de ${currentGallery.length}`" class="lightbox-image" />
    </div>

    <button v-if="currentGallery.length > 1" @click.stop="nextImage" class="nav-arrow next"
      aria-label="Siguiente imagen">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" />
      </svg>
    </button>

  </div>
</template>

<script setup>
// --- 1. IMPORTACIONES ---
// Importaciones de Vue y Vue Router
import { ref, onMounted, onUnmounted, reactive, computed, watch } from 'vue';
import { useRoute, RouterLink } from 'vue-router';

// Importaciones de servicios de API
import { fetchGameDetailsByIgdbId, addOrUpdateGameInUserLibrary, removeGameFromUserLibrary } from '@/services/apiInstances.js';

// Importación del store de autenticación (Pinia)
import { useAuthStore } from '@/stores/authStore.js';

// Importación de imágenes estáticas para usar como placeholders
import defaultGameCoverLarge from '@/assets/img/default-game-cover.svg';
import defaultRelatedCover from '@/assets/img/default-game-cover.svg';


// --- 2. CONFIGURACIÓN PRINCIPAL Y ESTADO REACTIVO ---
const route = useRoute(); // Hook para acceder a la información de la ruta actual (p.ej. parámetros como el ID del juego)
const authStore = useAuthStore(); // Hook para acceder al estado de autenticación del usuario

// Estado principal del componente
const gameDetail = ref(null); // Contendrá todos los detalles del juego recibidos de la API
const igdbId = ref(null);     // Almacena el ID de IGDB del juego actual, extraído de la ruta
const isLoading = ref(true);  // Booleano para mostrar el estado de carga inicial
const errorMessage = ref(''); // String para almacenar mensajes de error y mostrarlos al usuario
const activeTab = ref('details'); // Controla qué pestaña de contenido está visible ('details', 'multimedia', etc.)


// --- 3. OBTENCIÓN DE DATOS Y CICLO DE VIDA ---
/**
 * @description Función principal que carga los detalles del juego desde la API usando su ID de IGDB.
 * Gestiona los estados de carga y error.
 * @param {string|number} id - El ID de IGDB del juego a cargar.
 */
const loadGameDetails = async (id) => {
  if (!id) {
    errorMessage.value = "ID del juego no proporcionado.";
    isLoading.value = false;
    return;
  }
  isLoading.value = true;
  errorMessage.value = '';
  gameDetail.value = null; // Resetea los detalles previos antes de una nueva carga

  try {
    const response = await fetchGameDetailsByIgdbId(Number(id));
    gameDetail.value = response.data;
  } catch (error) {
    console.error(`Error cargando detalles del juego (ID: ${id}):`, error);
    if (error.response) {
      errorMessage.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudieron cargar los detalles.'}`;
    } else {
      errorMessage.value = 'Error de red al cargar los detalles del juego.';
    }
  } finally {
    isLoading.value = false;
  }
};

// Hook del ciclo de vida: se ejecuta cuando el componente se monta por primera vez
onMounted(() => {
  igdbId.value = route.params.igdbId;
  loadGameDetails(igdbId.value);
});

// Watcher: observa cambios en el parámetro 'igdbId' de la ruta.
// Esto es crucial para que la página se recargue con un nuevo juego sin tener que abandonarla
// (p.ej., al hacer clic en un juego relacionado).
watch(() => route.params.igdbId, (newId) => {
  if (newId && newId !== igdbId.value) {
    igdbId.value = newId;
    loadGameDetails(newId);
    activeTab.value = 'details'; // Resetea la pestaña activa a 'detalles'
  }
});


// --- 4. GESTIÓN DE PESTAÑAS Y NAVEGACIÓN INTERNA ---
/**
 * @description Cambia la pestaña de contenido activa en la vista.
 * @param {string} tabName - El nombre de la pestaña a activar.
 */
const setActiveTab = (tabName) => {
  activeTab.value = tabName;
};


// --- 5. LÓGICA DEL CARRUSEL DE CAPTURAS DE PANTALLA ---
const screenshotCarouselIndex = ref(0); // Índice de la captura de pantalla actual en el carrusel

const nextScreenshot = () => {
  const screenshots = gameDetail.value?.game_info?.screenshots;
  if (!screenshots || screenshots.length <= 1) return;
  
  if (screenshotCarouselIndex.value >= screenshots.length - 1) {
    screenshotCarouselIndex.value = 0; // Vuelve al inicio si está en la última
  } else {
    screenshotCarouselIndex.value++;
  }
};

const prevScreenshot = () => {
  const screenshots = gameDetail.value?.game_info?.screenshots;
  if (!screenshots || screenshots.length <= 1) return;
  
  if (screenshotCarouselIndex.value <= 0) {
    screenshotCarouselIndex.value = screenshots.length - 1; // Va a la última si está en la primera
  } else {
    screenshotCarouselIndex.value--;
  }
};

// Estilo computado para el desplazamiento del carrusel.
// Mueve el contenedor del carrusel horizontalmente basado en el índice actual.
const carouselSliderStyle = computed(() => {
  if (!gameDetail.value?.game_info?.screenshots?.length) return {};
  const itemWidth = 400; // Ancho fijo de cada item del carrusel
  const position = screenshotCarouselIndex.value * itemWidth;
  return {
    transform: `translateX(-${position}px)`,
    width: `${itemWidth * gameDetail.value.game_info.screenshots.length}px` // Ancho total del slider
  };
});

// --- Lógica para el ancho dinámico del carrusel (no implementada en el template final pero el código está aquí) ---
const carouselViewportRef = ref(null); 
const carouselItemWidth = ref(0);
let isTransitioning = false;

const updateCarouselItemWidth = () => {
  if (carouselViewportRef.value) {
    carouselItemWidth.value = carouselViewportRef.value.offsetWidth;
  }
};
const handleTransitionEnd = () => {
  isTransitioning = false;
};
onMounted(() => {
  updateCarouselItemWidth();
  window.addEventListener('resize', updateCarouselItemWidth);
});
onUnmounted(() => {
  window.removeEventListener('resize', updateCarouselItemWidth);
});


// --- 6. LÓGICA DEL LIGHTBOX (VISOR DE IMÁGENES AMPLIADAS) ---
const showLightbox = ref(false); // Controla la visibilidad del visor
const currentGallery = ref([]);  // Almacena la galería activa (capturas o artworks)
const currentIndex = ref(0);     // Índice de la imagen activa dentro de la galería

// Propiedad computada que devuelve la imagen actual a mostrar en el lightbox.
const currentImage = computed(() => {
  if (currentGallery.value.length > 0) {
    return currentGallery.value[currentIndex.value];
  }
  return null;
});

/**
 * @description Abre el visor de imágenes a pantalla completa.
 * @param {Array} gallery - El array de imágenes (screenshots o artworks) a mostrar.
 * @param {number} index - El índice de la imagen en la que se hizo clic.
 */
const openLightbox = (gallery, index) => {
  currentGallery.value = gallery;
  currentIndex.value = index;
  showLightbox.value = true;
  document.body.style.overflow = 'hidden'; // Evita el scroll del fondo
};

const closeLightbox = () => {
  showLightbox.value = false;
  document.body.style.overflow = ''; // Restaura el scroll del fondo
  currentGallery.value = [];
  currentIndex.value = 0;
};

const nextImage = () => {
  if (currentGallery.value.length > 0) {
    currentIndex.value = (currentIndex.value + 1) % currentGallery.value.length; // Loop circular
  }
};

const prevImage = () => {
  if (currentGallery.value.length > 0) {
    currentIndex.value = (currentIndex.value - 1 + currentGallery.value.length) % currentGallery.value.length; // Loop circular
  }
};


// --- 7. GESTIÓN DE LA BIBLIOTECA DEL USUARIO (MODAL PRINCIPAL) ---

// Estado para controlar el modal y sus pasos (asistente)
const showLibraryForm = ref(false);           // Visibilidad del modal
const modalStep = ref(0);                     // Paso actual del asistente (0: categoría, 1: detalles, 2: más detalles)
const mainStatusCategory = ref(null);         // Categoría principal seleccionada ('playing', 'completed', etc.)
const isAddingNewLibraryEntry = ref(false);   // Distingue entre añadir un juego nuevo y editar uno existente
const isLoadingLibraryAction = ref(false);    // Muestra un estado de carga en los botones de guardado
const libraryActionMessage = ref('');         // Mensajes de feedback (éxito/error)
const libraryActionError = ref(false);        // Booleano para estilizar el mensaje de feedback como error

// Objeto reactivo que contiene todos los campos del formulario de la biblioteca
const libraryForm = reactive({
  status: null, 
  personal_platform: null, 
  has_possession: false, 
  score: null, 
  comment: '', 
  private_comment: '', 
  start_date: '', 
  end_date: '', 
  story_duration_hours: null, 
  story_secondary_duration_hours: null, 
  completionist_duration_hours: null, 
});
const hourInputType = ref('story_duration_hours'); // Controla qué campo de horas está vinculado al input numérico

// Opciones para los 'select' del formulario, organizadas por categoría
const gameStatusOptions = {
  playing: [
    { value: 'PLAYING', text: 'Jugando' },
    { value: 'PLAYING_PAUSED', text: 'Jugando (En Pausa)' },
    { value: 'PLAYING_ENDLESS', text: 'Jugando (Sin Fin)' },
  ],
  completed: [
    { value: 'COMPLETED', text: 'Completado' },
    { value: 'COMPLETED_MAIN_STORY', text: 'Historia Principal Completada' },
    { value: 'COMPLETED_MAIN_AND_SIDES', text: 'Principal + Secundarias' },
    { value: 'COMPLETED_100_PERCENT', text: 'Completado al 100%' },
  ],
  archived: [
    { value: 'ARCHIVED', text: 'Archivado' },
    { value: 'ARCHIVED_ABANDONED', text: 'Archivado (Abandonado)' },
    { value: 'ARCHIVED_NOT_PLAYING', text: 'Archivado (Sin Jugar)' },
  ]
};
const personalPlatformOptions = [
  { value: 'STEAM', text: 'Steam' },
  { value: 'EPIC_GAMES', text: 'Epic Games Store' },
  { value: 'GOG_GALAXY', text: 'GOG Galaxy' },
  { value: 'XBOX', text: 'Xbox' },
  { value: 'PLAYSTATION', text: 'PlayStation' },
  { value: 'NINTENDO', text: 'Nintendo' },
  { value: 'BATTLE_NET', text: 'Battle.net' },
  { value: 'EA_APP', text: 'EA App' },
  { value: 'UBISOFT_CONNECT', text: 'Ubisoft Connect' },
  { value: 'OTHER', text: 'Otra' },
];

// Propiedad computada que filtra las opciones de estado específico según la categoría principal seleccionada.
const computedStatusOptions = computed(() => {
  if (!mainStatusCategory.value) return [];
  return gameStatusOptions[mainStatusCategory.value] || [];
});

/**
 * @description Abre el modal de la biblioteca, resetea su estado y rellena los datos si es una edición.
 * @param {boolean} isNew - True si se está añadiendo un juego, false si se está editando.
 */
const openLibraryModal = (isNew = false) => {
  isAddingNewLibraryEntry.value = isNew;
  libraryActionMessage.value = '';
  libraryActionError.value = false;
  modalStep.value = 0; // Siempre empieza en el paso 0
  mainStatusCategory.value = null;

  // Limpia el formulario a sus valores por defecto
  Object.keys(libraryForm).forEach(key => {
    libraryForm[key] = (typeof libraryForm[key] === 'boolean' ? false : (typeof libraryForm[key] === 'string' ? '' : null));
  });

  // Si es modo edición, rellena el formulario con los datos existentes
  if (!isNew && gameDetail.value?.user_game_data) {
    const data = gameDetail.value.user_game_data;
    Object.keys(libraryForm).forEach(key => {
      if (data[key] !== undefined && data[key] !== null) {
        libraryForm[key] = data[key];
      }
    });
    // Determina la categoría principal para pre-seleccionar la opción en el asistente
    const currentStatus = data.status || '';
    if (currentStatus.includes('PLAYING')) mainStatusCategory.value = 'playing';
    else if (currentStatus.includes('COMPLETED')) mainStatusCategory.value = 'completed';
    else if (currentStatus.includes('ARCHIVED')) mainStatusCategory.value = 'archived';
    else if (currentStatus === 'WISHLIST') mainStatusCategory.value = 'wishlist';
  }
  showLibraryForm.value = true;
};

const closeLibraryModal = () => {
  showLibraryForm.value = false;
};

// Navegación del asistente
const goToStep = (step) => {
  modalStep.value = step;
};

/**
 * @description Maneja el primer paso del asistente. Guarda la categoría y avanza.
 * Si es 'wishlist', guarda directamente.
 * @param {string} category - La categoría seleccionada.
 */
const setStatusCategoryAndContinue = (category) => {
  mainStatusCategory.value = category;
  if (category === 'wishlist') {
    libraryForm.status = 'WISHLIST';
    handleSaveToLibrary(); // Acción directa para 'Lista de Deseos'
  } else {
    // Para otras categorías, pre-selecciona el primer estado y avanza al siguiente paso
    libraryForm.status = gameStatusOptions[category][0].value;
    modalStep.value = 1;
  }
};

/**
 * @description Envía los datos del formulario a la API para guardar o actualizar la entrada en la biblioteca.
 */
const handleSaveToLibrary = async () => {
  if (!igdbId.value) return;
  isLoadingLibraryAction.value = true;
  libraryActionMessage.value = '';
  libraryActionError.value = false;

  const dto = { ...libraryForm }; // Crea un DTO (Data Transfer Object) con los datos del formulario

  // Limpia los campos de horas que NO fueron seleccionados para evitar enviar datos incorrectos
  const hourTypes = ['story_duration_hours', 'story_secondary_duration_hours', 'completionist_duration_hours'];
  hourTypes.forEach(type => {
    if (type !== hourInputType.value) {
      dto[type] = null;
    }
  });

  try {
    const response = await addOrUpdateGameInUserLibrary(Number(igdbId.value), dto);
    gameDetail.value.user_game_data = response.data; // Actualiza los datos locales con la respuesta
    closeLibraryModal(); // Cierra el modal si todo fue bien
  } catch (error) {
    libraryActionError.value = true;
    libraryActionMessage.value = `Error: ${error.response?.data?.message || 'No se pudo guardar.'}`;
  } finally {
    isLoadingLibraryAction.value = false;
  }
};

/**
 * @description Elimina el juego de la biblioteca del usuario tras confirmación.
 */
const handleRemoveFromLibrary = async () => {
  if (!igdbId.value || !gameDetail.value?.user_game_data) return;
  if (!window.confirm("¿Seguro que quieres eliminar este juego de tu biblioteca?")) return;

  isLoadingLibraryAction.value = true;
  libraryActionMessage.value = ''; 
  libraryActionError.value = false;
  try {
    await removeGameFromUserLibrary(Number(igdbId.value));
    gameDetail.value.user_game_data = null; // Elimina los datos del usuario localmente
    showLibraryForm.value = false;
    isAddingNewLibraryEntry.value = true; // Prepara el botón para "Añadir" la próxima vez
  } catch (error) {
    libraryActionError.value = true;
    if (error.response) {
      libraryActionMessage.value = `Error ${error.response.status}: ${error.response.data.message || error.response.data.error || 'No se pudo eliminar.'}`;
    } else { libraryActionMessage.value = 'Error de red.'; }
  } finally {
    isLoadingLibraryAction.value = false;
  }
};



// --- 8. GESTIÓN DEL MODAL DE COMENTARIO PÚBLICO ---
const showCommentModal = ref(false);
const commentFormText = ref('');
const isSavingComment = ref(false);
const commentActionMessage = ref('');
const commentActionError = ref(false);

const openCommentModal = () => {
  commentFormText.value = gameDetail.value?.user_game_data?.comment || '';
  commentActionMessage.value = '';
  commentActionError.value = false;
  showCommentModal.value = true;
};

const handleSaveComment = async () => {
  if (!igdbId.value) return;
  isSavingComment.value = true;
  commentActionMessage.value = '';
  commentActionError.value = false;

  // Se crea un DTO solo con el comentario para asegurar que solo se actualiza este campo
  const commentDto = { comment: commentFormText.value };

  try {
    const response = await addOrUpdateGameInUserLibrary(Number(igdbId.value), commentDto);
    if (gameDetail.value) {
      gameDetail.value.user_game_data = response.data;
    }
    showCommentModal.value = false;
  } catch (error) {
    commentActionError.value = true;
    commentActionMessage.value = `Error: ${error.response?.data?.message || 'No se pudo guardar el comentario.'}`;
    console.error("Error al guardar comentario:", error);
  } finally {
    isSavingComment.value = false;
  }
};


// --- 9. GESTIÓN DEL MODAL DE NOTA PRIVADA ---
const showPrivateNoteModal = ref(false);
const privateNoteFormText = ref('');
const isSavingPrivateNote = ref(false);

const openPrivateNoteModal = () => {
  privateNoteFormText.value = gameDetail.value?.user_game_data?.private_comment || '';
  isSavingPrivateNote.value = false;
  showPrivateNoteModal.value = true;
};

const handleSavePrivateNote = async () => {
  if (!igdbId.value) return;
  isSavingPrivateNote.value = true;
  // DTO solo con la nota privada
  const dto = { private_comment: privateNoteFormText.value };

  try {
    const response = await addOrUpdateGameInUserLibrary(Number(igdbId.value), dto);
    if (gameDetail.value && gameDetail.value.user_game_data) {
      gameDetail.value.user_game_data = response.data;
    }
    showPrivateNoteModal.value = false;
  } catch (error) {
    console.error("Error al guardar la nota privada:", error);
  } finally {
    isSavingPrivateNote.value = false;
  }
};


// --- 10. LÓGICA "LEER MÁS" PARA COMENTARIOS ---
const isExpanded = ref(false); // Controla si el comentario largo está expandido
const MAX_COMMENT_LENGTH = 350; // Límite de caracteres para truncar el texto

// Determina si el comentario es suficientemente largo como para necesitar el botón "Leer más"
const commentNeedsTruncation = computed(() => {
  const comment = gameDetail.value?.user_game_data?.private_comment;
  return comment && comment.length > MAX_COMMENT_LENGTH;
});

// Devuelve el texto a mostrar: truncado o completo, según el estado de 'isExpanded'
const displayComment = computed(() => {
  const comment = gameDetail.value?.user_game_data?.private_comment;
  if (!comment) return '';
  if (commentNeedsTruncation.value && !isExpanded.value) {
    return comment.substring(0, MAX_COMMENT_LENGTH) + '...';
  }
  return comment;
});


// --- 11. UTILIDADES Y FORMATEADORES DE DATOS ---

// -- Imágenes y URLs --
/**
 * @description Construye una URL de imagen de IGDB con un tamaño específico.
 * @param {object} imageInfo - El objeto de imagen de la API de IGDB.
 * @param {string} size - El código de tamaño de IGDB (ej: '720p', 'cover_small').
 * @returns {string} La URL final de la imagen o un placeholder.
 */
const getCoverUrl = (imageInfo, size = 'cover_small') => {
  const isSmall = size.includes('small') || size.includes('thumb');
  const placeholder = isSmall ? defaultRelatedCover : defaultGameCoverLarge;
  if (imageInfo && typeof imageInfo.url === 'string' && imageInfo.url.trim() !== '') {
    let imageUrl = imageInfo.url.replace('/t_thumb/', `/t_${size}/`); // Reemplazo simple
    if (imageUrl.startsWith('//')) {
      imageUrl = `https:${imageUrl}`;
    }
    return imageUrl;
  }
  return placeholder;
};
const onImageError = (event) => { event.target.src = defaultGameCoverLarge; };
const onImageErrorSmall = (event) => { event.target.src = defaultRelatedCover; };
const getYouTubeEmbedUrl = (videoId) => {
  return videoId ? `https://www.youtube.com/embed/${videoId}` : '';
};

// -- Fechas --
const formatTimestampToDate = (timestamp) => {
  if (!timestamp) return 'Fecha desconocida';
  return new Date(Number(timestamp) * 1000).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' });
};
const formatDateSimple = (dateString) => dateString || '';

// -- Enlaces a sitios web --
const getWebsiteDisplayName = (urlString) => {
  if (!urlString) return 'Enlace';
  try {
    const url = new URL(urlString);
    let hostname = url.hostname.replace('www.', '');
    const nameMap = { 'steampowered.com': 'Steam', 'gog.com': 'GOG', 'youtube.com': 'YouTube', 'wikipedia.org': 'Wikipedia', 'epicgames.com': 'Epic Games Store', 'twitch.tv': 'Twitch' };
    return nameMap[hostname] || (hostname.charAt(0).toUpperCase() + hostname.slice(1));
  } catch (e) {
    return urlString;
  }
};
const getWebsiteIconName = (urlString) => {
  if (!urlString) return 'sin-logo.svg';
  const domainIconMap = { 'steam': 'steam.svg', 'gog.com': 'gog.svg', 'epicgames.com': 'epicgames.svg', 'ubisoft.com': 'ubisoft.svg', 'ea.com': 'ea.svg', 'battle.net': 'battle.svg', 'rockstargames.com': 'rockstargames.svg', 'playstation.com': 'playstation.svg', 'xbox.com': 'xbox.svg', 'youtube.com': 'youtube.svg', 'twitch.tv': 'twitch.svg', 'wikipedia.org': 'wikipedia.svg', 'discord': 'discord.svg', 'facebook.com': 'facebook.svg', 'instagram.com': 'instagram.svg', 'x.com': 'x.svg', 'twitter.com': 'x.svg', 'reddit.com': 'reddit.svg', 'apple.com': 'appstore.svg', 'play.google': 'googleplay.svg' };
  try {
    const domain = new URL(urlString).hostname;
    for (const key in domainIconMap) {
      if (domain.includes(key)) return domainIconMap[key];
    }
    return 'sin-logo.svg';
  } catch (e) { return 'sin-logo.svg'; }
};
const getIconUrl = (iconName) => new URL(`/src/assets/icons-website/${iconName}`, import.meta.url).href;
const recognizedWebsites = computed(() => gameDetail.value?.game_info?.websites?.filter(w => getWebsiteIconName(w.url) !== 'sin-logo.svg') || []);
const otherWebsites = computed(() => gameDetail.value?.game_info?.websites?.filter(w => getWebsiteIconName(w.url) === 'sin-logo.svg') || []);

// -- Datos del juego (IGDB) --
const formatGameType = (gameType) => {
  const typeMap = { "0": "Juego Principal", "1": "DLC / Add-on", "2": "Expansión", "3": "Bundle", "4": "Expansión Independiente", "8": "Remake", "9": "Remaster" };
  return typeMap[String(gameType)] || String(gameType);
};
const getCompanyRoles = (involvedCompany) => {
  if (!involvedCompany) return '';
  const roles = [];
  if (involvedCompany.developer) roles.push('Desarrollador');
  if (involvedCompany.publisher) roles.push('Editor');
  if (involvedCompany.porting) roles.push('Porting');
  if (involvedCompany.supporting) roles.push('Soporte');
  return roles.join(', ');
};
const formatReleaseStatus = (gameInfo) => {
  if (gameInfo.first_release_date * 1000 > Date.now()) return 'Próximamente';
  const statusMap = { "-1": "Lanzado", "0": "Lanzado", "2": "Alpha", "3": "Beta", "4": "Acceso Anticipado", "5": "Offline", "6": "Cancelado", "7": "Rumoreado" };
  return statusMap[String(gameInfo.first_release_status)] || 'No especificado';
};
const getReleaseStatusClass = (gameInfo) => {
  if (gameInfo.first_release_date * 1000 > Date.now()) return 'status-upcoming';
  const statusMap = { '-1': 'status-released', '0': 'status-released', '2': 'status-alpha', '3': 'status-beta', '4': 'status-early-access', '5': 'status-offline', '6': 'status-cancelled', '7': 'status-rumored' };
  return statusMap[String(gameInfo.first_release_status)] || 'status-other';
};

// -- Datos del juego (Usuario) --
const formatPersonalPlatform = (platform) => {
  const platformMap = { 'STEAM': 'Steam', 'EPIC_GAMES': 'Epic Games Store', 'GOG_GALAXY': 'GOG Galaxy', 'XBOX': 'Xbox', 'PLAYSTATION': 'PlayStation', 'NINTENDO': 'Nintendo', 'BATTLE_NET': 'Battle.net', 'EA_APP': 'EA App', 'UBISOFT_CONNECT': 'Ubisoft Connect', 'OTHER': 'Otra' };
  return platformMap[platform] || platform;
};
const formatUserGameStatus = (status) => {
  const statusMap = { 'COMPLETED': 'Completado', 'COMPLETED_MAIN_STORY': 'Historia Principal Completada', 'COMPLETED_MAIN_AND_SIDES': 'Principal + Secundarias', 'COMPLETED_100_PERCENT': 'Completado al 100%', 'ARCHIVED': 'Archivado', 'ARCHIVED_ABANDONED': 'Archivado (Abandonado)', 'ARCHIVED_NOT_PLAYING': 'Archivado (Sin Jugar)', 'WISHLIST': 'En Lista de Deseos', 'PLAYING': 'Jugando', 'PLAYING_PAUSED': 'Jugando (En Pausa)', 'PLAYING_ENDLESS': 'Jugando (Sin Fin)' };
  return statusMap[status] || status;
};
</script>

<style src="./GameDetailsView.css" scoped></style>