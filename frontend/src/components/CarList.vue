<template>
  <div class="car-list">
    <transition-group name="fade" tag="div">
      <CarCard
        v-for="car in cars"
        :key="car.id"
        :car="car"
        @click="$emit('select', car)"
      />
    </transition-group>
  </div>
</template>

<script>
import CarCard from './CarCard.vue'

export default {
  props: {
    cars: {
      type: Array,
      required: true
    }
  },
  components: {
    CarCard
  }
}
</script>

<style scoped>
.car-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}
.fade-enter-active, .fade-leave-active {
  transition: all 0.4s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>