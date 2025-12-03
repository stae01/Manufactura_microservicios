<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import DefectForm from './components/DefectForm.vue';
import AlertList from './components/AlertList.vue';
import AuthForm from './components/AuthForm.vue';

// estado inicial desde localStorage
const token = ref(localStorage.getItem('token') || null);
const userRole = ref(localStorage.getItem('role') || null);
const username = ref(localStorage.getItem('username') || null);
const userId = ref(localStorage.getItem('userId') || null); 

// Configure axios default header if token exists
if (token.value) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`;
}

// computed helper
const isAdmin = computed(() => userRole.value === 'admin');

const handleLoginSuccess = (data) => {
  token.value = data.token;
  userRole.value = data.role;
  username.value = data.username;
  userId.value = data.userId;
  
  localStorage.setItem('token', data.token);
  localStorage.setItem('role', data.role);
  localStorage.setItem('username', data.username);
  localStorage.setItem('userId', data.userId); 
  
  axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
};

const logout = () => {
  token.value = null;
  userRole.value = null;
  username.value = null;
  userId.value = null;

  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('username');
  localStorage.removeItem('userId');

  delete axios.defaults.headers.common['Authorization'];
};
</script>

<template>
  <!-- Show Auth Form if not logged in -->
  <AuthForm v-if="!token" @login-success="handleLoginSuccess" />

  <!-- Show Main App if logged in -->
  <div v-else class="min-h-screen bg-background flex flex-col">
    <!-- Navbar -->
    <header class="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-primary rounded p-1.5 mr-3">
              <svg class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"
                />
              </svg>
            </div>
            <h1 class="text-xl font-bold text-slate-800 tracking-tight">
              Manufactura<span class="text-primary-light">QC</span>
            </h1>
          </div>
          
          <div class="flex items-center space-x-4">
            <div class="hidden md:flex flex-col items-end mr-2">
              <span class="text-sm font-semibold text-slate-700">{{ username }}</span>
              <span
                class="text-xs uppercase tracking-wider font-bold px-2 py-0.5 rounded-full"
                :class="isAdmin ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'"
              >
                {{ userRole }}
              </span>
            </div>
            <button
              @click="logout"
              class="bg-white border border-gray-200 text-slate-600 hover:bg-gray-50 hover:text-red-600 px-4 py-2 rounded-lg text-sm font-medium transition-colors duration-200 flex items-center"
            >
              <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                />
              </svg>
              Salir
            </button>
          </div>
        </div>
      </div>
    </header>
    
    <!-- Main Content -->
    <main class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 h-full">
        <!-- Left Column: Actions -->
        <section class="h-full">
          <!-- 👇 le pasamos el inspector-id al formulario -->
          <DefectForm :inspector-id="userId" />
        </section>
        
        <!-- Right Column: Information/Alerts -->
        <section class="h-full">
          <AlertList v-if="isAdmin" />
          <div
            v-else
            class="card h-full flex flex-col items-center justify-center text-center p-12 bg-white/50 border-dashed border-2 border-gray-200"
          >
            <div class="bg-gray-100 p-4 rounded-full mb-4">
              <svg class="w-12 h-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                />
              </svg>
            </div>
            <h3 class="text-lg font-bold text-slate-700 mb-2">Acceso Restringido</h3>
            <p class="text-secondary max-w-xs mx-auto">
              El panel de alertas está reservado exclusivamente para administradores.
            </p>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>
