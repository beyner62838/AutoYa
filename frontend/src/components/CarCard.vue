<template>
  <div
    class="card-car"
    role="button"
    tabindex="0"
    @click="$emit('select', car)"
    @keyup.enter="$emit('select', car)"
    :aria-label="`Ver detalles de ${car.brand} ${car.model}`"
  >
    <!-- Media (imagen del auto) -->
    <div class="media">
      <img
        :src="car.imageUrl || placeholder"
        alt="Imagen del vehículo"
        class="media-img"
        loading="lazy"
      />
    </div>

    <!-- Cuerpo -->
    <div class="body">
      <div class="title">
        <span class="car-brand">{{ car.brand }}</span>
        &nbsp;·&nbsp;
        <span class="car-model">{{ car.model }}</span>
      </div>

      <div class="meta">
        Año: {{ car.year }}
      </div>

      <div class="price" v-if="car.pricePerDay != null">
        ${{ formatCurrency(car.pricePerDay) }} / día
      </div>

      <div class="mt-2" style="display:flex; justify-content:flex-end">
        <button
          class="btn btn-primary"
          type="button"
          @click.stop="$emit('select', car)"
        >
          Ver detalles
        </button>
      </div>
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
      placeholder: 'https://via.placeholder.com/640x360?text=Auto'
    }
  },
  methods: {
    formatCurrency(n) {
      try {
        return Number(n).toLocaleString('es-CO', { minimumFractionDigits: 0 })
      } catch {
        return n
      }
    }
  }
}
</script>
