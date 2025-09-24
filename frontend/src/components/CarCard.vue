<template>
  <div class="car-card" @click="$emit('select', car)">
    <img :src="car.coverPhotoUrl || placeholder" class="car-image" :alt="car.model" />
    <div class="car-info">
      <div class="car-title">{{ car.brand }} {{ car.model }}</div>
      <div class="car-details">
        <div>📅 Año: {{ car.year }}</div>
        <div>🎨 Color: {{ car.color }}</div>
        <div>⚙️ Transmisión: {{ car.transmission }}</div>
        <div>⛽ Combustible: {{ car.fuelType }}</div>
      </div>
      <div class="car-price">${{ car.pricePerDay }}/día</div>
    </div>
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
