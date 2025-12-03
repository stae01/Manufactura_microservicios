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

// catálogo de tipos de defecto (ajústalo a tu proceso real)
const defectTypes = [
  'Fisura en carcasa',
  'Desalineación',
  'Falta de componente',
  'Daño superficial',
  'Error de ensamblaje',
  'Medida fuera de tolerancia',
];

const tipo = ref(defectTypes[0]);
const severidad = ref('BAJA');
const descripcion = ref('');
const lote = ref('');
const costoMXN = ref(''); 

const loading = ref(false);
const error = ref('');
const success = ref('');

// 🔹 Sanitizar el input de costo: solo números, un punto, máx 2 decimales
const handleCostoInput = (event) => {
  let value = event.target.value;

  // 1) Quitar todo lo que no sea dígito o punto
  value = value.replace(/[^0-9.]/g, '');

  // 2) Permitir solo un punto
  const parts = value.split('.');
  if (parts.length > 2) {
    value = parts[0] + '.' + parts.slice(1).join('');
  }

  // 3) Limitar a 2 decimales
  let [intPart, decPart] = value.split('.');
  if (decPart && decPart.length > 2) {
    decPart = decPart.slice(0, 2);
    value = intPart + '.' + decPart;
  }

  // actualizar input y modelo
  event.target.value = value;
  costoMXN.value = value;
};

const submit = async () => {
  error.value = '';
  success.value = '';

  // Validaciones básicas extra
  if (!lote.value.trim()) {
    error.value = 'El lote es requerido.';
    return;
  }

  const numericCosto = Number(costoMXN.value);

  if (!costoMXN.value || Number.isNaN(numericCosto) || numericCosto <= 0) {
    error.value = 'El costo debe ser un número mayor a 0 con hasta 2 decimales.';
    return;
  }

  loading.value = true;

  try {
    await axios.post('/api/defectos', {
      tipo: tipo.value,
      severidad: severidad.value,
      descripcion: descripcion.value,
      lote: lote.value.trim(),
      costoMXN: numericCosto,
      inspectorId: props.inspectorId || 1, // fallback por si acaso
    });

    // Clear form
    tipo.value = defectTypes[0];
    severidad.value = 'BAJA';
    descripcion.value = '';
    lote.value = '';
    costoMXN.value = '';

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
  <div class="card flex flex-col">
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
      <!-- Tipo de defecto -->
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">
          Tipo de Defecto
        </label>
        <div class="relative">
          <select
            v-model="tipo"
            class="input-field select-field appearance-none bg-white cursor-pointer"
            required
          >
            <option
              v-for="t in defectTypes"
              :key="t"
              :value="t"
            >
              {{ t }}
            </option>
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

      <!-- Severidad -->
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">Severidad</label>
        <div class="relative">
          <select
            v-model="severidad"
            class="input-field select-field appearance-none bg-white cursor-pointer"
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

      <!-- Lote -->
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">Lote</label>
        <input
          v-model="lote"
          type="text"
          class="input-field"
          placeholder="Ej. L-20251203-01"
          required
        />
      </div>

      <!-- Costo por pieza (MXN) -->
      <div>
        <label class="block text-sm font-medium mb-1 text-slate-700">
          Costo por pieza (MXN)
        </label>
        <input
          :value="costoMXN"
          @input="handleCostoInput"
          inputmode="decimal"
          class="input-field"
          placeholder="Ej. 100.00"
          required
        />
      </div>
      
      <!-- Descripcion -->
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

<style scoped>
.select-field {
  background-color: #f8fafc;
}

.select-field option {
  background-color: #0f172a;
  color: #e2e8f0;
}
</style>
