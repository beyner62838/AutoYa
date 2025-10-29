<template>
  <div class="card-car" role="button" tabindex="0" @click="$emit('select', car)" @keyup.enter="$emit('select', car)"
    :aria-label="`Ver detalles de ${car.brand} ${car.model}`">
    <!-- Imagen del auto -->
    <div class="media">
      <!-- Mostrar GIF si está cargando -->
      <img v-if="loading" src="../assets/loading.gif" alt="Cargando..." class="media-img loading-gif" />

      <!-- Mostrar imagen del auto si ya cargó -->
      <img v-else :src="photoUrl || placeholder" alt="Imagen del vehículo" class="media-img" loading="lazy" />
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
      placeholder: '', // ya no lo usas como loading
      photos: [],
      loading: false
    }
  },
  computed: {
    photoUrl() {
      return this.photos.length > 0 ? this.photos[0].url || this.photos[0] : null
    }
  },
  async mounted() {
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
      this.loading = true
      try {
        const res = await api.get(`/api/cars/${carId}/photos`)
        this.photos = res.data || []
      } catch {
        console.warn('Error al cargar fotos del auto', carId)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.card-car {
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

.media-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.loading-gif {
  object-fit: contain;
  background: rgba(0, 0, 0, 0.3);
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
