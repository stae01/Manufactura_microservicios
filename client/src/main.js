// client/src/main.js
import { createApp } from 'vue';
import axios from 'axios';
import './style.css';
import App from './App.vue';

// 1) Si ya hay token guardado, lo ponemos en el header
const savedToken = localStorage.getItem('token');
if (savedToken) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
}

// 2) Interceptor global de respuestas
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const config = error.config || {};

    // Rutas de auth: NO queremos tratarlas como "sesión expirada"
    const isAuthRoute =
      typeof config.url === 'string' &&
      (config.url.startsWith('/api/auth/login') ||
       config.url.startsWith('/api/auth/registro'));

    // Solo consideramos "sesión expirada" si:
    // - es 401
    // - NO es una ruta de auth
    if (status === 401 && !isAuthRoute) {
      // Limpiamos sesión
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('username');
      localStorage.removeItem('userId');
      delete axios.defaults.headers.common['Authorization'];

      alert('Tu sesión ha expirado o el token no es válido. Por favor inicia sesión de nuevo.');
      window.location.reload();
    }

    return Promise.reject(error);
  }
);

createApp(App).mount('#app');
