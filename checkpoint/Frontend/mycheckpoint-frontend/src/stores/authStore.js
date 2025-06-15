import { defineStore } from 'pinia';
import { getCurrentAuthenticatedUser } from '@/services/apiInstances'; 
import router from '@/router'; 

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token_acceso: localStorage.getItem('token_acceso') || null,
    /** @type {import('@/api-client').UserDTO | null} */
    user: JSON.parse(localStorage.getItem('user')) || null,
    imageUpdateTrigger: 0, // Para forzar la actualización de la imagen del usuario
  }),
  getters: {
    isAuthenticated: (state) => !!state.token_acceso && !!state.user,
    /** @returns {import('@/api-client').UserDTO | null} */
    currentUser: (state) => state.user,
    getUserToken: (state) => state.token_acceso,
  },
  actions: {
    /**
     * Establece el token de acceso y guarda la información del usuario.
     * @param {import('@/api-client').JwtResponseDTO} authData - Los datos de autenticación, incluyendo el token.
     */
    async loginSuccess(authData) {
      this.token_acceso = authData.token_acceso;
      localStorage.setItem('token_acceso', authData.token_acceso);
      await this.fetchCurrentUser();
    },

    async fetchCurrentUser() {
      if (this.token_acceso) {
        try {
          const response = await getCurrentAuthenticatedUser(); 
          this.user = response.data;
          localStorage.setItem('user', JSON.stringify(response.data));
        } catch (error) {
          console.error("Error al obtener los datos del usuario:", error);
          this.logout();
        }
      }
    },

    logout() {
      this.token_acceso = null;
      this.user = null;
      localStorage.removeItem('token_acceso');
      localStorage.removeItem('user');
      router.push('/login');
    },

    triggerImageUpdate() {
      this.imageUpdateTrigger++;
    },

    updateUser(userData) {
      this.user = userData;
      if (userData?.foto_perfil) {
        this.triggerImageUpdate();
      }
    },
  },
});