<template>
  <div class="search-form">
    <h2>Buscar Autos Disponibles</h2>
    <div class="fields">
      <div>
        <label>Fecha Inicio</label>
        <input type="date" v-model="startDate" />
      </div>
      <div>
        <label>Fecha Fin</label>
        <input type="date" v-model="endDate" />
      </div>
    </div>
    <div class="actions">
      <button @click="$emit('search', { startDate, endDate })" class="primary">Buscar Disponibles</button>
      <button @click="$emit('showAll')" class="secondary">Ver Todos</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      startDate: '',
      endDate: ''
    };
  }
};
</script>

<style scoped>
.search-form {
  background: #f8fafc;
  border-radius: 1.2rem;
  box-shadow: 0 8px 32px rgba(60, 60, 120, 0.08);
  padding: 2rem;
  margin-bottom: 2rem;
  animation: fadeIn 0.7s;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px);}
  to { opacity: 1; transform: none;}
}
.fields {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.2rem;
}
.fields label {
  font-weight: 500;
  color: #312e81;
  margin-bottom: 0.3rem;
  display: block;
}
.fields input {
  width: 100%;
  padding: 0.7rem;
  border-radius: 0.6rem;
  border: 1px solid #e5e7eb;
  background: #fff;
  font-size: 1rem;
}
.actions {
  display: flex;
  gap: 1rem;
}
.primary {
  background: linear-gradient(90deg, #6366f1 60%, #a5b4fc 100%);
  color: #fff;
  border: none;
  padding: 0.7rem 1.5rem;
  border-radius: 0.7rem;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 600;
  transition: box-shadow 0.2s;
}
.primary:hover {
  box-shadow: 0 2px 8px #6366f133;
}
.secondary {
  background: #e5e7eb;
  color: #312e81;
  border: none;
  padding: 0.7rem 1.5rem;
  border-radius: 0.7rem;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 600;
}
</style>