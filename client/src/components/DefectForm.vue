<script setup>
import { ref } from 'vue';
import axios from 'axios';

const emit = defineEmits(['defect-created']);

const tipo = ref('');
const severidad = ref('BAJA');
const descripcion = ref('');
const loading = ref(false);

const submit = async () => {
  loading.value = true;
  try {
    await axios.post('/api/defectos', {
      tipo: tipo.value,
      severidad: severidad.value,
      descripcion: descripcion.value,
      inspectorId: 1 // Mock
    });
    // Clear form
    tipo.value = '';
    severidad.value = 'BAJA';
    descripcion.value = '';
    alert('Defecto reportado con éxito');
    emit('defect-created');
  } catch (e) {
    alert('Error al reportar defecto');
    console.error(e);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="bg-white p-6 rounded shadow-md">
    <h2 class="text-xl font-bold mb-4">Reportar Defecto</h2>
    <form @submit.prevent="submit" class="space-y-4">
      <div>
        <label class="block text-sm font-medium">Tipo de Defecto</label>
        <input v-model="tipo" type="text" class="w-full border p-2 rounded" required />
      </div>
      <div>
        <label class="block text-sm font-medium">Severidad</label>
        <select v-model="severidad" class="w-full border p-2 rounded">
          <option value="BAJA">Baja</option>
          <option value="MEDIA">Media</option>
          <option value="ALTA">Alta</option>
          <option value="CRITICA">Crítica</option>
        </select>
      </div>
      <div>
        <label class="block text-sm font-medium">Descripción</label>
        <textarea v-model="descripcion" class="w-full border p-2 rounded" required></textarea>
      </div>
      <button type="submit" :disabled="loading" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50">
        {{ loading ? 'Enviando...' : 'Registrar Defecto' }}
      </button>
    </form>
  </div>
</template>

