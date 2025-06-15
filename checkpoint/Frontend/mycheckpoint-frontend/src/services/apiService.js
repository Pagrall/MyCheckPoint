import axios from 'axios';
import { useAuthStore } from '@/stores/authStore'; 
import { BASE_PATH } from '@/api-client/base'; 
import router from '@/router'; 

const apiClient = axios.create({
  baseURL: BASE_PATH //
});

// Interceptor de solicitud para añadir el token JWT
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token = authStore.token_acceso; 
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

//Interceptor de respuesta para manejar errores comunes como 401
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const authStore = useAuthStore();
    if (error.response && error.response.status === 401) {
      // Token inválido o expirado
      console.error("Error 401: No autorizado. Deslogueando...");
      authStore.logout(); 
       router.push('/login');
    }
    return Promise.reject(error);
  }
);

export default apiClient;