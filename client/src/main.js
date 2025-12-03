// client/src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import './style.css';
import axios from 'axios';

// Si ya hay token guardado, lo ponemos en el header
const token = localStorage.getItem('token');
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

// Interceptor para respuestas: maneja errores 401/403
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      // Token inválido o expirado → cerrar sesión y recargar a login
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('username');
      delete axios.defaults.headers.common['Authorization'];

      // opcional: puedes mostrar un alert antes
      alert('Tu sesión ha expirado. Por favor inicia sesión nuevamente.');

      // fuerza recarga a estado inicial
      window.location.reload();
    }

    // Puedes manejar 403 si quieres un mensaje global
    // if (status === 403) { ... }

    return Promise.reject(error);
  }
);

createApp(App).mount('#app');
