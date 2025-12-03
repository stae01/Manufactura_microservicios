<script setup>
import { ref } from 'vue';
import axios from 'axios';

const emit = defineEmits(['defect-created']);

// viene desde App.vue como :inspector-id="userId"
const props = defineProps({
  inspectorId: {
    type: [String, Number],
    required: false,
  },
});

const tipo = ref('');
const severidad = ref('BAJA');
const descripcion = ref('');
const loading = ref(false);
const error = ref('');
const success = ref('');

const submit = async () => {
  loading.value = true;
  error.value = '';
  success.value = '';

  try {
    await axios.post('/api/defectos', {
      tipo: tipo.value,
      severidad: severidad.value,
      descripcion: descripcion.value,
      inspectorId: props.inspectorId || 1, // fallback por si acaso
    });

    // Clear form
    tipo.value = '';
    severidad.value = 'BAJA';
    descripcion.value = '';
    success.value = 'Defecto reportado con éxito';
    emit('defect-created');
  } catch (e) {
    console.error('Error al reportar defecto', e);

    const status = e.response?.status;

    if (status === 403) {
      error.value = 'No tienes permisos para reportar defectos.';
    } else {
      error.value = e.response?.data?.message || 'Error al reportar defecto';
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="card h-full flex flex-col">
    <div class="flex items-center space-x-2 mb-6 border-b border-gray-100 pb-4">
      <div class="bg-primary/10 p-2 rounded-lg text-primary">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          />
        </svg>
      </div>
      <h2 class="text-xl font-bold">Reportar Defecto</h2>
    </div>

    <!-- Mensajes de error / éxito -->
    <div
      v-if="error"
      class="mb-3 text-sm text-red-600 bg-red-50 border border-red-100 px-3 py-2 rounded"
    >
      {{ error }}
    </div>
    <div
      v-if="success"
      class="mb-3 text-sm text-emerald-700 bg-emerald-50 border border-emerald-100 px-3 py-2 rounded"
    >
      {{ success }}
    </div>

    <form @submit.prevent="submit" class="space-y-5 flex-1">
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">Tipo de Defecto</label>
        <input
          v-model="tipo"
          type="text"
          class="input-field"
          placeholder="Ej. Fisura en carcasa"
          required
        />
      </div>
      
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">Severidad</label>
        <div class="relative">
          <select
            v-model="severidad"
            class="input-field appearance-none bg-white cursor-pointer"
          >
            <option value="BAJA">Baja</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta</option>
            <option value="CRITICA">Crítica</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 9l-7 7-7-7"
              ></path>
            </svg>
          </div>
        </div>
      </div>
      
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">Descripción Detallada</label>
        <textarea
          v-model="descripcion"
          rows="4"
          class="input-field resize-none"
          placeholder="Describa el problema observado..."
          required
        ></textarea>
      </div>
      
      <div class="pt-2">
        <button
          type="submit"
          :disabled="loading"
          class="btn btn-primary w-full flex justify-center items-center space-x-2"
        >
          <svg
            v-if="!loading"
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>{{ loading ? 'Enviando Reporte...' : 'Registrar Defecto' }}</span>
        </button>
      </div>
    </form>
  </div>
</template>
