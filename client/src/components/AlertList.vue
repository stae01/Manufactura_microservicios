<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

const alerts = ref([]);
const loading = ref(false);
const error = ref('');
const hasLoadedOnce = ref(false); 
let interval;

const fetchAlerts = async () => {
  loading.value = true;
  error.value = '';

  try {
    const res = await axios.get('/api/alertas');
    alerts.value = res.data;
  } catch (e) {
    console.error('Error fetching alerts', e);

    const status = e.response?.status;

    if (status === 403) {
      error.value = 'No tienes permisos para ver las alertas.';
      clearInterval(interval);
    } else {
      error.value = 'Error al cargar las alertas.';
    }
  } finally {
    loading.value = false;
    hasLoadedOnce.value = true; 
  }
};

onMounted(() => {
  fetchAlerts();
  interval = setInterval(fetchAlerts, 5000); // Poll every 5 seconds
});

onUnmounted(() => {
  clearInterval(interval);
});

const getSeverityClass = (sev) => {
  switch (sev) {
    case 'CRITICA': return 'bg-red-100 text-red-800 border-red-200';
    case 'ALTA':    return 'bg-orange-100 text-orange-800 border-orange-200';
    case 'MEDIA':   return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    default:        return 'bg-blue-100 text-blue-800 border-blue-200';
  }
};
</script>

<template>
  <div class="card flex flex-col max-h-[650px]">
    <div class="flex items-center space-x-2 mb-6 border-b border-gray-100 pb-4">
      <div class="bg-status-danger/10 p-2 rounded-lg text-status-danger animate-pulse">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
      </div>
      <h2 class="text-xl font-bold text-slate-800">Alertas de Calidad</h2>
      <span
        v-if="alerts.length > 0 && !error"
        class="bg-status-danger text-white text-xs font-bold px-2 py-1 rounded-full"
      >
        {{ alerts.length }}
      </span>

      <!-- mini spinner en el header mientras hace polling, sin romper el layout -->
      <svg
        v-if="loading && !error"
        class="w-4 h-4 ml-auto animate-spin text-gray-300"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle>
        <path class="opacity-75" stroke-width="4" d="M4 12a8 8 0 018-8"></path>
      </svg>
    </div>

    <!-- Mensaje de error -->
    <div
      v-if="error"
      class="mb-3 text-sm text-red-600 bg-red-50 border border-red-100 px-3 py-2 rounded"
    >
      {{ error }}
    </div>

    <!-- Estado cargando SOLO en el primer load -->
    <div
      v-else-if="loading && !hasLoadedOnce"
      class="flex-1 flex flex-col items-center justify-center text-secondary py-12 text-sm"
    >
      <svg class="w-6 h-6 mb-3 animate-spin text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle>
        <path class="opacity-75" stroke-width="4"
              d="M4 12a8 8 0 018-8">
        </path>
      </svg>
      <p>Cargando alertas...</p>
    </div>
    
   
    <div
      v-else-if="alerts.length === 0"
      class="flex-1 flex flex-col items-center justify-center text-secondary py-12"
    >
      <svg class="w-16 h-16 mb-4 text-gray-200" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <p>No hay alertas activas en este momento.</p>
    </div>
    
    <-- Lista de alertas -->
    <div
      v-else
      class="flex-1 min-h-0 space-y-3 overflow-y-auto pr-2 custom-scrollbar"
    >
      <div
        v-for="alerta in alerts"
        :key="alerta.id"
        class="p-4 rounded-lg border-l-4 shadow-sm transition-transform hover:scale-[1.01] duration-200 bg-white border border-gray-100"
        :class="[getSeverityClass(alerta.severidad).replace('bg-', 'border-l-')]"
      >
        <div class="flex justify-between items-start mb-2">
          <span
            :class="[
              'px-2 py-1 rounded text-xs font-bold border',
              getSeverityClass(alerta.severidad)
            ]"
          >
            {{ alerta.severidad }}
          </span>
          <span class="text-xs text-secondary flex items-center">
            <svg class="w-3 h-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            {{ new Date(alerta.fecha).toLocaleString() }}
          </span>
        </div>
        
        <p class="text-slate-700 text-sm font-medium leading-relaxed">
          {{ alerta.mensaje }}
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9; 
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1; 
  border-radius: 3px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8; 
}
</style>
