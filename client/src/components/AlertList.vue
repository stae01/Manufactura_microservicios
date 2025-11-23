<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

const alerts = ref([]);
let interval;

const fetchAlerts = async () => {
  try {
    const res = await axios.get('/api/alertas');
    alerts.value = res.data;
  } catch (e) {
    console.error("Error fetching alerts", e);
  }
};

onMounted(() => {
  fetchAlerts();
  interval = setInterval(fetchAlerts, 5000); // Poll every 5 seconds
});

onUnmounted(() => {
  clearInterval(interval);
});
</script>

<template>
  <div class="bg-white p-6 rounded shadow-md">
    <h2 class="text-xl font-bold mb-4 text-red-600">Alertas de Calidad</h2>
    <div v-if="alerts.length === 0" class="text-gray-500">No hay alertas activas.</div>
    <ul v-else class="space-y-2">
      <li v-for="alerta in alerts" :key="alerta.id" class="border-l-4 border-red-500 bg-red-50 p-3">
        <div class="font-bold">{{ alerta.severidad }}</div>
        <p>{{ alerta.mensaje }}</p>
        <div class="text-xs text-gray-500">{{ new Date(alerta.fecha).toLocaleString() }}</div>
      </li>
    </ul>
  </div>
</template>

