<script setup>
import { ref } from 'vue';
import axios from 'axios';

const emit = defineEmits(['login-success']);

const isLogin = ref(true);
const username = ref('');
const password = ref('');
const role = ref('user'); 
const error = ref('');
const loading = ref(false);

const toggleMode = () => {
  isLogin.value = !isLogin.value;
  error.value = '';
  // opcional: limpiar contraseña al cambiar de modo
  password.value = '';
};

// helper para construir mensaje de errores del backend
const extractBackendError = (e) => {
  const data = e.response?.data;

  if (!data) return 'Error en la operación';

  // si viene como { errors: { campo: "mensaje" } }
  if (data.errors && typeof data.errors === 'object') {
    const firstKey = Object.keys(data.errors)[0];
    if (firstKey) return data.errors[firstKey];
  }

  // si viene como { message: "mensaje" }
  if (data.message) return data.message;

  return 'Error en la operación';
};

const submit = async () => {
  error.value = '';

  const trimmedUser = username.value.trim();

  // Validación diferente según modo
  if (isLogin.value) {
    // LOGIN → solo que no estén vacíos
    if (!trimmedUser) {
      error.value = 'El usuario es requerido.';
      return;
    }
    if (!password.value) {
      error.value = 'La contraseña es requerida.';
      return;
    }
  } else {
    // REGISTRO → validación fuerte
    if (trimmedUser.length < 3) {
      error.value = 'El usuario debe tener al menos 3 caracteres.';
      return;
    }

    if (password.value.length < 6) {
      error.value = 'La contraseña debe tener al menos 6 caracteres.';
      return;
    }
  }

  loading.value = true;
  try {
    if (isLogin.value) {
      // LOGIN
      const res = await axios.post('/api/auth/login', {
        username: trimmedUser,
        password: password.value
      });

      const token = res.data.token;
      const payload = JSON.parse(atob(token.split('.')[1]));
      // payload.userId viene del backend

      emit('login-success', { 
        token, 
        username: trimmedUser, 
        role: payload.role,
        userId: payload.userId 
      });

    } else {
      // REGISTRO
      await axios.post('/api/auth/registro', {
        username: trimmedUser,
        password: password.value,
        role: role.value        // "user" o "admin"
      });

      isLogin.value = true;
      alert('Registro exitoso. Por favor inicia sesión.');
      password.value = '';
    }
  } catch (e) {
    error.value = extractBackendError(e);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-background p-4">
    <div class="card w-full max-w-md animate-fade-in-up">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold mb-2">Control de Calidad</h1>
        <p class="text-secondary">
          {{ isLogin ? 'Bienvenido de nuevo' : 'Crear nueva cuenta' }}
        </p>
      </div>
      
      <form @submit.prevent="submit" class="space-y-5">
        <div>
          <label class="block text-sm font-medium mb-1 text-slate-700">
            Usuario
          </label>
          <input
            v-model="username"
            type="text"
            class="input-field"
            placeholder="Ej. operador1"
            required
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium mb-1 text-slate-700">
            Contraseña
          </label>
          <input
            v-model="password"
            type="password"
            class="input-field"
            placeholder="••••••••"
            required
          />
        </div>

        <div v-if="!isLogin" class="animate-fade-in">
          <label class="block text-sm font-medium mb-1 text-slate-700">
            Rol
          </label>
          <select v-model="role" class="input-field appearance-none bg-white">
            <option value="user">Usuario (Operador)</option>
            <option value="admin">Administrador</option>
          </select>
        </div>

        <div
          v-if="error"
          class="bg-red-50 text-status-danger text-sm p-3 rounded-md border border-red-100 flex items-center"
        >
          <svg
            class="w-4 h-4 mr-2 flex-shrink-0"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              fill-rule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
              clip-rule="evenodd"
            />
          </svg>
          {{ error }}
        </div>

        <button
          type="submit"
          class="btn btn-primary w-full shadow-lg shadow-blue-900/20"
          :disabled="loading"
        >
          <span v-if="loading" class="flex items-center justify-center">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              />
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
            Procesando...
          </span>
          <span v-else>
            {{ isLogin ? 'Iniciar Sesión' : 'Crear Cuenta' }}
          </span>
        </button>
      </form>
      
      <div class="mt-6 text-center text-sm border-t pt-6 border-gray-100">
        <span class="text-secondary">
          {{ isLogin ? '¿No tienes cuenta?' : '¿Ya tienes cuenta?' }}
        </span>
        <button
          @click="toggleMode"
          class="text-primary font-semibold ml-1 hover:underline transition-all"
        >
          {{ isLogin ? 'Regístrate' : 'Inicia Sesión' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.5s ease-out;
}
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
