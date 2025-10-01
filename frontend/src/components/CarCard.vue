<template>
  <div class="car-card" @click="$emit('select', car)">
    <div class="car-header">
      <span class="car-brand">{{ car.marca }}</span>
      <span class="car-model">{{ car.modelo }}</span>
    </div>
    <div class="car-info">
      <span class="car-year">Año: {{ car.año }}</span>
      <span class="car-price" v-if="car.precio">Precio: ${{ car.precio }}</span>
    </div>
    <button class="details-btn">Ver detalles</button>
  </div>
</template>

<script>
export default {
  name: 'CarCard',
  props: {
    car: { type: Object, required: true }
  },
  data() {
    return {
      placeholder: 'https://via.placeholder.com/350x200?text=No+Image'
    }
  }
}
</script>

<style scoped>
.car-card {
  background: #fff;
  border-radius: 1.2rem;
  box-shadow: 0 4px 24px #6366f133;
  padding: 1.5rem 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  cursor: pointer;
  transition: transform 0.18s, box-shadow 0.18s;
  border: 2px solid transparent;
  position: relative;
  animation: fadeIn 0.6s;
}
.car-card:hover {
  transform: translateY(-4px) scale(1.03);
  box-shadow: 0 8px 32px #6366f144;
  border-color: #a5b4fc;
}
.car-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.15rem;
  color: #6366f1;
}
.car-model {
  color: #312e81;
  font-weight: 500;
}
.car-info {
  display: flex;
  justify-content: space-between;
  color: #6b7280;
  font-size: 1rem;
}
.details-btn {
  align-self: flex-end;
  background: linear-gradient(90deg, #6366f1 60%, #a5b4fc 100%);
  color: #fff;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 0.7rem;
  font-size: 0.98rem;
  cursor: pointer;
  font-weight: 600;
  transition: box-shadow 0.2s;
  margin-top: 0.5rem;
}
.details-btn:hover {
  box-shadow: 0 2px 8px #6366f133;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px);}
  to { opacity: 1; transform: none;}
}
</style>
