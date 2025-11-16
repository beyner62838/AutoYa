<template>
  <!-- Usamos la grid global + transición fade global -->
  <transition-group name="fade" tag="div" class="grid mt-2">
    <CarCard
      v-for="car in cars"
      :key="car.id"
      :car="car"
      @select="$emit('select', car)"
    />
  </transition-group>
</template>

<script>
import CarCard from './CarCard.vue'

export default {
  name: 'CarList',
  components: { CarCard },
  props: {
    cars: { type: Array, required: true }
  }
}
</script>
