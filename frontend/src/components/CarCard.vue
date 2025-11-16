<template>
  <div class="card-car" role="button" tabindex="0" @click="$emit('select', car)" @keyup.enter="$emit('select', car)"
    :aria-label="`Ver detalles de ${car.brand} ${car.model}`">
    <!-- Badge de ciudad (solo visible en hover del card) -->
    <div class="city-badge">{{ car.city }}</div>

    <!-- Imagen del auto -->
    <div class="media">
      <img :src="photoUrl || placeholder" alt="Imagen del vehículo" class="media-img" loading="lazy" />
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
        <button class="btn btn-primary" type="button" @click.stop="$emit('select', car)">
          Ver detalles
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'CarCard',
  props: {
    car: { type: Object, required: true }
  },
  data() {
    return {
      placeholder: 'https://via.placeholder.com/640x360?text=Auto',
      photos: [],
      loading: false
    }
  },
  computed: {
    photoUrl() {
      // Devuelve la primera foto si existe
      return this.photos.length > 0 ? this.photos[0].url || this.photos[0] : null
    }
  },
  async mounted() {
    // Carga la foto de portada del auto
    await this.getCarPhotoCover(this.car.id)
  },
  methods: {
    formatCurrency(n) {
      try {
        return Number(n).toLocaleString('es-CO', { minimumFractionDigits: 0 })
      } catch {
        return n
      }
    },
    async getCarPhotoCover(carId) {
      try {
        const res = await api.get(`/api/cars/${carId}/photos`)
        this.photos = res.data || []
      } catch {
        console.warn('Error al cargar fotos del auto', carId)
      }
    }
  }
}
</script>

<style scoped>
.card-car {
  position: relative;
  /* 🔹 Para posicionar el badge dentro del card */
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.card-car:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

/* 🔹 Badge de ciudad: oculto por defecto, aparece en hover del card */
.city-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, .6);
  color: #fff;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: .8rem;
  opacity: 0;
  /* oculto */
  transition: opacity .2s ease-in-out;
  pointer-events: none;
  /* evita capturar clics */
}

.card-car:hover .city-badge {
  opacity: 1;
  /* visible solo en hover */
}

.media-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.body {
  padding: 12px 16px;
  color: #fff;
}

.title {
  font-size: 1.1rem;
  font-weight: 600;
}

.meta {
  font-size: 0.9rem;
  color: #ccc;
  margin-top: 4px;
}

.price {
  font-weight: bold;
  color: #4ba3ff;
  margin-top: 8px;
}

.btn {
  font-size: 0.85rem;
}
</style>
