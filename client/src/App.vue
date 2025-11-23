<script setup>
import DefectForm from './components/DefectForm.vue';
import AlertList from './components/AlertList.vue';
</script>

<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <header class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800">Sistema de Control de Calidad</h1>
    </header>
    
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <section>
        <DefectForm />
      </section>
      <section>
        <AlertList />
      </section>
    </div>
  </div>
</template>
