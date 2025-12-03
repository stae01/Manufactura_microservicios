<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

// pestaña activa: 'tipo' | 'severidad' | 'detalle'
const activeTab = ref('tipo');

// catálogo de tipos de defecto (mismo que en DefectForm)
const defectTypes = [
  'Fisura en carcasa',
  'Desalineación',
  'Falta de componente',
  'Daño superficial',
  'Error de ensamblaje',
  'Medida fuera de tolerancia',
];

// resumen por tipo de defecto
const resumenTipo = ref([]);
const loadingTipo = ref(false);
const errorTipo = ref('');

// resumen por severidad
const resumenSeveridad = ref([]);
const loadingSeveridad = ref(false);
const errorSeveridad = ref('');

// detalle de defectos
const detalle = ref([]);
const loadingDetalle = ref(false);
const errorDetalle = ref('');
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

// filtros para detalle
const filtroTipo = ref('');
const filtroSeveridad = ref('');

// helpers
const totalPages = computed(() => {
  if (!total.value || !pageSize.value) return 1;
  return Math.max(1, Math.ceil(total.value / pageSize.value));
});

const formatCurrency = (value) => {
  const num = Number(value) || 0;
  return num.toFixed(2);
};

const formatDateTime = (value) => {
  if (!value) return '';
  const d = new Date(value);
  return d.toLocaleString();
};

//  Fetch resumen por tipo
const fetchResumenTipo = async () => {
  loadingTipo.value = true;
  errorTipo.value = '';
  try {
    const res = await axios.get('/api/reportes/resumen-defectos');
    resumenTipo.value = res.data || [];
  } catch (e) {
    console.error('Error cargando resumen por tipo', e);
    errorTipo.value = 'No se pudo cargar el resumen por tipo de defecto.';
  } finally {
    loadingTipo.value = false;
  }
};

//  Fetch resumen por severidad
const fetchResumenSeveridad = async () => {
  loadingSeveridad.value = true;
  errorSeveridad.value = '';
  try {
    const res = await axios.get('/api/reportes/resumen-severidad');
    resumenSeveridad.value = res.data || [];
  } catch (e) {
    console.error('Error cargando resumen por severidad', e);
    errorSeveridad.value = 'No se pudo cargar el resumen por severidad.';
  } finally {
    loadingSeveridad.value = false;
  }
};

//  Fetch detalle
const fetchDetalle = async () => {
  loadingDetalle.value = true;
  errorDetalle.value = '';

  const params = {
    page: page.value,
    pageSize: pageSize.value,
  };

  if (filtroTipo.value.trim() !== '') {
    params.tipo = filtroTipo.value.trim();
  }
  if (filtroSeveridad.value !== '') {
    params.severidad = filtroSeveridad.value;
  }

  try {
    const res = await axios.get('/api/reportes/detalle-defectos', { params });
    detalle.value = res.data.items || [];
    total.value = res.data.total || 0;
    page.value = res.data.page || 1;
    pageSize.value = res.data.pageSize || 10;
  } catch (e) {
    console.error('Error cargando detalle de defectos', e);
    errorDetalle.value = 'No se pudo cargar el detalle de defectos.';
  } finally {
    loadingDetalle.value = false;
  }
};

const cambiarPagina = (n) => {
  const target = page.value + n;
  if (target < 1 || target > totalPages.value) return;
  page.value = target;
  fetchDetalle();
};

const irAPrimeraPagina = () => {
  if (page.value === 1) return;
  page.value = 1;
  fetchDetalle();
};

const irAUltimaPagina = () => {
  if (page.value === totalPages.value) return;
  page.value = totalPages.value;
  fetchDetalle();
};

// aplicar filtros (reset a página 1 + refrescar)
const aplicarFiltros = () => {
  page.value = 1;
  fetchDetalle();
};

// cuando se monta, cargamos la primera pestaña (tipo)
onMounted(() => {
  fetchResumenTipo();
});

// cuando cambias de tab, hacemos fetch si hace falta
const setTab = (tab) => {
  activeTab.value = tab;

  if (tab === 'tipo' && resumenTipo.value.length === 0 && !loadingTipo.value) {
    fetchResumenTipo();
  }
  if (tab === 'severidad' && resumenSeveridad.value.length === 0 && !loadingSeveridad.value) {
    fetchResumenSeveridad();
  }
  if (tab === 'detalle' && detalle.value.length === 0 && !loadingDetalle.value) {
    fetchDetalle();
  }
};
</script>

<template>
  <div class="flex flex-col gap-6">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-3">
      <div>
        <h2 class="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <span class="inline-flex items-center justify-center rounded-lg bg-primary/10 text-primary p-2">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M11 11V3a1 1 0 012 0v8h3l-4 4-4-4h3zm-4 6h10"
              />
            </svg>
          </span>
          Reportes de Calidad
        </h2>
        <p class="text-sm text-secondary mt-1 max-w-xl">
          Explora los rechazos por tipo de defecto, severidad y consulta el detalle de cada pieza inspeccionada.
        </p>
      </div>
    </div>

    <!-- Tabs de reportes -->
    <div class="bg-white border border-gray-200 rounded-xl p-1 inline-flex w-full sm:w-auto">
      <button
        type="button"
        class="flex-1 sm:flex-none px-4 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === 'tipo'
          ? 'bg-primary text-white shadow-sm'
          : 'text-slate-600 hover:bg-slate-50'"
        @click="setTab('tipo')"
      >
        Resumen por tipo
      </button>
      <button
        type="button"
        class="flex-1 sm:flex-none px-4 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === 'severidad'
          ? 'bg-primary text-white shadow-sm'
          : 'text-slate-600 hover:bg-slate-50'"
        @click="setTab('severidad')"
      >
        Resumen por severidad
      </button>
      <button
        type="button"
        class="flex-1 sm:flex-none px-4 py-2 rounded-lg text-sm font-medium transition-all"
        :class="activeTab === 'detalle'
          ? 'bg-primary text-white shadow-sm'
          : 'text-slate-600 hover:bg-slate-50'"
        @click="setTab('detalle')"
      >
        Detalle de piezas
      </button>
    </div>

    <!-- Contenido de cada reporte -->
    <!-- 1) Resumen por tipo -->
    <div v-if="activeTab === 'tipo'" class="card">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-bold text-slate-800">Totales por tipo de defecto</h3>
        <button
          @click="fetchResumenTipo"
          class="text-xs px-3 py-1 rounded-full border border-gray-200 text-slate-500 hover:bg-gray-50 transition-colors"
          :disabled="loadingTipo"
        >
          {{ loadingTipo ? 'Actualizando…' : 'Refrescar' }}
        </button>
      </div>

      <div v-if="errorTipo" class="text-sm text-red-600 bg-red-50 border border-red-100 px-3 py-2 rounded mb-3">
        {{ errorTipo }}
      </div>

      <div v-else-if="loadingTipo && resumenTipo.length === 0" class="py-6 text-sm text-secondary flex items-center">
        <svg class="w-5 h-5 mr-2 animate-spin text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle>
          <path class="opacity-75" stroke-width="4" d="M4 12a8 8 0 018-8"></path>
        </svg>
        Cargando resumen de defectos...
      </div>

      <div v-else-if="resumenTipo.length === 0" class="py-6 text-sm text-secondary">
        No hay datos suficientes para generar el resumen.
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full text-sm">
          <thead>
            <tr class="text-left text-slate-500 border-b border-gray-200">
              <th class="py-2 pr-4">Tipo de defecto</th>
              <th class="py-2 pr-4">Cantidad</th>
              <th class="py-2 pr-4">Total MXN</th>
              <th class="py-2 pr-4">Total USD</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in resumenTipo"
              :key="row.tipo"
              class="border-b last:border-0 border-gray-100 hover:bg-slate-50/60 transition-colors"
            >
              <td class="py-2 pr-4 font-medium text-slate-800">
                {{ row.tipo }}
              </td>
              <td class="py-2 pr-4">
                {{ row.cantidad }}
              </td>
              <td class="py-2 pr-4">
                $ {{ formatCurrency(row.totalMXN) }} MXN
              </td>
              <td class="py-2 pr-4">
                $ {{ formatCurrency(row.totalUSD) }} USD
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 2) Resumen por severidad -->
    <div v-else-if="activeTab === 'severidad'" class="card">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-bold text-slate-800">Totales por severidad</h3>
        <button
          @click="fetchResumenSeveridad"
          class="text-xs px-3 py-1 rounded-full border border-gray-200 text-slate-500 hover:bg-gray-50 transition-colors"
          :disabled="loadingSeveridad"
        >
          {{ loadingSeveridad ? 'Actualizando…' : 'Refrescar' }}
        </button>
      </div>

      <div v-if="errorSeveridad" class="text-sm text-red-600 bg-red-50 border border-red-100 px-3 py-2 rounded mb-3">
        {{ errorSeveridad }}
      </div>

      <div
        v-else-if="loadingSeveridad && resumenSeveridad.length === 0"
        class="py-6 text-sm text-secondary flex items-center"
      >
        <svg class="w-5 h-5 mr-2 animate-spin text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle>
          <path class="opacity-75" stroke-width="4" d="M4 12a8 8 0 018-8"></path>
        </svg>
        Cargando resumen por severidad...
      </div>

      <div v-else-if="resumenSeveridad.length === 0" class="py-6 text-sm text-secondary">
        No hay datos suficientes para generar el resumen.
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full text-sm">
          <thead>
            <tr class="text-left text-slate-500 border-b border-gray-200">
              <th class="py-2 pr-4">Severidad</th>
              <th class="py-2 pr-4">Cantidad</th>
              <th class="py-2 pr-4">Total MXN</th>
              <th class="py-2 pr-4">Total USD</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in resumenSeveridad"
              :key="row.severidad"
              class="border-b last:border-0 border-gray-100 hover:bg-slate-50/60 transition-colors"
            >
              <td class="py-2 pr-4 font-medium text-slate-800 uppercase">
                {{ row.severidad }}
              </td>
              <td class="py-2 pr-4">
                {{ row.cantidad }}
              </td>
              <td class="py-2 pr-4">
                $ {{ formatCurrency(row.totalMXN) }} MXN
              </td>
              <td class="py-2 pr-4">
                $ {{ formatCurrency(row.totalUSD) }} USD
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 3) Detalle de piezas -->
    <div v-else class="card flex flex-col">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h3 class="text-lg font-bold text-slate-800">Detalle de piezas rechazadas</h3>
          <p class="text-xs text-secondary">
            Filtra por tipo de defecto y severidad. Usa la paginación para recorrer el historial.
          </p>
        </div>
        <button
          @click="fetchDetalle"
          class="text-xs px-3 py-1 rounded-full border border-gray-200 text-slate-500 hover:bg-gray-50 transition-colors"
          :disabled="loadingDetalle"
        >
          {{ loadingDetalle ? 'Actualizando…' : 'Refrescar' }}
        </button>
      </div>

      <!-- Filtros -->
      <div class="mb-4 grid grid-cols-1 md:grid-cols-3 gap-3">
        <!-- Tipo de defecto: SELECT como en el form -->
        <div>
          <label class="block text-xs font-semibold text-slate-600 mb-1">Tipo de defecto</label>
          <div class="relative">
            <select
              v-model="filtroTipo"
              class="input-field appearance-none bg-slate-900/5 cursor-pointer"
            >
              <option value="">Todos los tipos</option>
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

        <!-- Severidad: mismo estilo de select -->
        <div>
          <label class="block text-xs font-semibold text-slate-600 mb-1">Severidad</label>
          <div class="relative">
            <select
              v-model="filtroSeveridad"
              class="input-field appearance-none bg-slate-900/5 cursor-pointer"
            >
              <option value="">Todas</option>
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

        <div class="flex items-end">
          <button
            type="button"
            class="btn btn-primary w-full text-sm"
            @click="aplicarFiltros"
          >
            Aplicar filtros
          </button>
        </div>
      </div>

      <!-- Mensajes -->
      <div v-if="errorDetalle" class="text-sm text-red-600 bg-red-50 border border-red-100 px-3 py-2 rounded mb-3">
        {{ errorDetalle }}
      </div>

      <div
        v-else-if="loadingDetalle && detalle.length === 0"
        class="py-6 text-sm text-secondary flex items-center"
      >
        <svg class="w-5 h-5 mr-2 animate-spin text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle>
          <path class="opacity-75" stroke-width="4" d="M4 12a8 8 0 018-8"></path>
        </svg>
        Cargando detalle de defectos...
      </div>

      <div v-else-if="detalle.length === 0" class="py-6 text-sm text-secondary">
        No hay registros que coincidan con los filtros.
      </div>

      <!-- Tabla detalle -->
      <div v-else class="flex-1 min-h-0 overflow-x-auto">
        <table class="min-w-full text-xs md:text-sm">
          <thead>
            <tr class="text-left text-slate-500 border-b border-gray-200">
              <th class="py-2 pr-3">Fecha</th>
              <th class="py-2 pr-3">Tipo</th>
              <th class="py-2 pr-3">Severidad</th>
              <th class="py-2 pr-3">Lote</th>
              <th class="py-2 pr-3">Costo MXN</th>
              <th class="py-2 pr-3">Costo USD</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in detalle"
              :key="row.id"
              class="border-b last:border-0 border-gray-100 hover:bg-slate-50/60 transition-colors"
            >
              <td class="py-2 pr-3 whitespace-nowrap">
                {{ formatDateTime(row.createdAt) }}
              </td>
              <td class="py-2 pr-3 font-medium text-slate-800">
                {{ row.tipo }}
              </td>
              <td class="py-2 pr-3 uppercase">
                {{ row.severidad }}
              </td>
              <td class="py-2 pr-3">
                {{ row.lote || '-' }}
              </td>
              <td class="py-2 pr-3">
                $ {{ formatCurrency(row.costoMXN) }}
              </td>
              <td class="py-2 pr-3">
                $ {{ formatCurrency(row.costoUSD) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Paginación -->
      <div
        v-if="detalle.length > 0"
        class="mt-4 flex items-center justify-between text-xs text-secondary gap-2 flex-wrap"
      >
        <div>
          Mostrando
          <span class="font-semibold text-slate-700">
            {{ (page - 1) * pageSize + 1 }} - {{ Math.min(page * pageSize, total) }}
          </span>
          de
          <span class="font-semibold text-slate-700">{{ total }}</span>
          registros
        </div>

        <div class="flex items-center gap-1">
          <button
            class="px-2 py-1 rounded border border-gray-200 bg-white hover:bg-gray-50 disabled:opacity-40"
            :disabled="page === 1"
            @click="irAPrimeraPagina"
          >
            «
          </button>
          <button
            class="px-2 py-1 rounded border border-gray-200 bg-white hover:bg-gray-50 disabled:opacity-40"
            :disabled="page === 1"
            @click="cambiarPagina(-1)"
          >
            Anterior
          </button>
          <span class="px-2">
            Página
            <span class="font-semibold text-slate-700">{{ page }}</span>
            de
            <span class="font-semibold text-slate-700">{{ totalPages }}</span>
          </span>
          <button
            class="px-2 py-1 rounded border border-gray-200 bg-white hover:bg-gray-50 disabled:opacity-40"
            :disabled="page === totalPages"
            @click="cambiarPagina(1)"
          >
            Siguiente
          </button>
          <button
            class="px-2 py-1 rounded border border-gray-200 bg-white hover:bg-gray-50 disabled:opacity-40"
            :disabled="page === totalPages"
            @click="irAUltimaPagina"
          >
            »
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
