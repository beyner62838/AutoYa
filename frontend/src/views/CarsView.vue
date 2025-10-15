<template>
  <div class="cars-page">
    <!-- Filtros -->
    <section class="card">
      <div class="section-header">
        <h2 class="section-title">Buscar Autos Disponibles</h2>

        <div class="actions">
          <button class="btn btn-primary" @click="searchAvailableCars" :disabled="loading">
            <span v-if="!loading">Buscar disponibles</span>
            <span v-else class="loader" aria-label="Cargando"></span>
          </button>

          <button class="btn btn-secondary" @click="getAllCars" :disabled="loading">
            Ver todos
          </button>

          <button v-if="userInfo?.role === 'ADMIN'" class="btn btn-success" @click="showAdd = true" type="button">
            + Agregar auto
          </button>

          <!-- ❌ ELIMINADO: El select que estaba aquí -->
        </div>
      </div>

      <!-- 🔹 Reorganizado: Fechas + rango + ordenar -->
      <div class="form-grid">
        <!-- Columna izquierda -->
        <div>
          <div class="form-group">
            <label for="from">Fecha inicio</label>
            <input id="from" type="date" v-model="searchDates.startDate" />
          </div>

          <!-- 🎚️ Rango de precio -->
          <div class="form-group price-range">
            <label>Rango de precio (USD/día)</label>

            <div class="range-track" :style="rangeTrackStyle" aria-hidden="true"></div>

            <div class="range-row">
              <input type="range" :min="catalogMin" :max="catalogMax" step="1" v-model.number="sliderMin"
                @input="onSlide('min')" />
              <input type="range" :min="catalogMin" :max="catalogMax" step="1" v-model.number="sliderMax"
                @input="onSlide('max')" />
            </div>

            <!-- Inputs manuales -->
            <div class="range-badges">
              <input type="number" v-model.number="sliderMin" :min="catalogMin" :max="sliderMax" class="price-input" />
              <span class="spacer">–</span>
              <input type="number" v-model.number="sliderMax" :min="sliderMin" :max="catalogMax" class="price-input" />
              <button class="clear-btn small" type="button" @click="clearPriceRange">Limpiar</button>
            </div>
          </div>
        </div>

        <!-- Columna derecha -->
        <div>
          <div class="form-group">
            <label for="to">Fecha fin</label>
            <input id="to" type="date" v-model="searchDates.endDate" />
          </div>

          <!-- 🔽 Ordenar por - AHORA DEBAJO DE FECHA FIN -->
          <div class="form-group">
            <label for="sort">Ordenar por</label>
            <select id="sort" v-model="sortOption">
              <option value="none">Sin ordenar</option>
              <option value="price_asc">Precio: menor a mayor</option>
              <option value="price_desc">Precio: mayor a menor</option>
            </select>
          </div>
        </div>
      </div>
    </section>

    <!-- Chips -->
    <div v-if="sortOption !== 'none'" class="order-chip">
      <span>Orden actual: <strong>{{ sortLabel }}</strong></span>
      <button @click="clearSort" class="clear-btn" title="Quitar orden">✕</button>
    </div>

    <!-- Grilla -->
    <section class="grid">
      <CarCard v-for="car in sortedCars" :key="car.id" :car="car" @select="selectCar" />
    </section>

    <!-- Estado vacío -->
    <div v-if="!loading && sortedCars.length === 0" class="card empty">
      <h3 class="empty-title">Sin resultados</h3>
      <p class="empty-text">No encontramos autos para ese rango de precios o fechas.</p>
    </div>

    <!-- Modales -->
    <CarModal v-if="showCarModal" :car="selectedCar" :userInfo="userInfo" @close="closeModal" @show-alert="showAlert"
      @reserved="onReserved" />
    <AddCarModal v-if="showAdd" :userInfo="userInfo" @close="showAdd = false" @car-added="onCarAdded"
      @show-alert="showAlert" />
  </div>
</template>

<script>
import CarCard from '../components/CarCard.vue'
import CarModal from '../components/CarModal.vue'
import AddCarModal from '../components/AddCarModal.vue'
import api from '../services/api'

export default {
  name: 'CarsView',
  components: { CarCard, CarModal, AddCarModal },
  data() {
    return {
      loading: false,
      cars: [],
      showCarModal: false,
      selectedCar: null,
      searchDates: { startDate: '', endDate: '' },
      showAdd: false,
      userInfo: null,
      sortOption: 'none',
      minPrice: null,
      maxPrice: null,
      sliderMin: 0,
      sliderMax: 0
    }
  },
  async mounted() {
    await this.getAllCars()

    const savedSort = localStorage.getItem('cars_sort_option')
    if (savedSort) this.sortOption = savedSort

    const savedMin = localStorage.getItem('cars_min_price')
    const savedMax = localStorage.getItem('cars_max_price')
    if (savedMin !== null) this.minPrice = Number(savedMin)
    if (savedMax !== null) this.maxPrice = Number(savedMax)

    this.$nextTick(() => {
      this.sliderMin = this.minPrice ?? this.catalogMin
      this.sliderMax = this.maxPrice ?? this.catalogMax
    })

    try {
      const res = await api.get('/auth/hello')
      this.userInfo = res.data
      if (this.userInfo?.id) localStorage.setItem('userId', this.userInfo.id)
    } catch { }
  },
  computed: {
    catalogMin() {
      const prices = this.cars.map(c => Number(c.pricePerDay ?? c.price ?? c.dailyPrice)).filter(p => !isNaN(p))
      return prices.length ? Math.min(...prices) : 0
    },
    catalogMax() {
      const prices = this.cars.map(c => Number(c.pricePerDay ?? c.price ?? c.dailyPrice)).filter(p => !isNaN(p))
      return prices.length ? Math.max(...prices) : 1000
    },
    rangeTrackStyle() {
      const min = this.catalogMin
      const max = this.catalogMax
      const range = Math.max(1, max - min)
      const a = ((this.sliderMin - min) / range) * 100
      const b = ((this.sliderMax - min) / range) * 100
      return { '--a': `${a}%`, '--b': `${b}%` }
    },
    sortedCars() {
      const toNum = v => Number(v?.toString().replace(/[^\d.-]/g, ''))
      const priceOf = c => toNum(c.pricePerDay ?? c.price ?? c.dailyPrice)

      let list = this.cars.filter(c => {
        const p = priceOf(c)
        if (isNaN(p)) return false
        return p >= this.sliderMin && p <= this.sliderMax
      })

      if (this.sortOption === 'none') return list
      const dir = this.sortOption.endsWith('desc') ? -1 : 1
      return [...list].sort((a, b) => (priceOf(a) - priceOf(b)) * dir)
    },
    sortLabel() {
      return {
        none: '',
        price_asc: 'Precio: menor a mayor',
        price_desc: 'Precio: mayor a menor'
      }[this.sortOption]
    }
  },
  methods: {
    onSlide(which) {
      if (which === 'min' && this.sliderMin > this.sliderMax) this.sliderMin = this.sliderMax
      if (which === 'max' && this.sliderMax < this.sliderMin) this.sliderMax = this.sliderMin
      this.minPrice = this.sliderMin
      this.maxPrice = this.sliderMax
    },
    clearPriceRange() {
      this.sliderMin = this.catalogMin
      this.sliderMax = this.catalogMax
      this.minPrice = this.sliderMin
      this.maxPrice = this.sliderMax
      localStorage.removeItem('cars_min_price')
      localStorage.removeItem('cars_max_price')
      this.searchDates.startDate = ''
      this.searchDates.endDate = ''
      this.getAllCars()
    },
    clearSort() {
      this.sortOption = 'none'
      localStorage.removeItem('cars_sort_option')
    },
    async getAllCars() {
      this.loading = true
      try {
        const res = await api.get('/api/cars')
        this.cars = res.data
      } catch {
        this.showAlert('error', 'Error al cargar autos')
      } finally {
        this.loading = false
      }
    },
    async searchAvailableCars() {
      if (!this.searchDates.startDate || !this.searchDates.endDate)
        return this.showAlert('error', 'Por favor selecciona ambas fechas')

      this.loading = true
      try {
        const res = await api.get('/api/cars/avalible', {
          params: this.searchDates
        })
        this.cars = res.data
        this.showAlert('success', `Se encontraron ${this.cars.length} autos`)
      } catch {
        this.showAlert('error', 'Error al buscar autos disponibles')
      } finally {
        this.loading = false
      }
    },
    selectCar(car) {
      this.selectedCar = car
      this.showCarModal = true
    },
    closeModal() {
      this.showCarModal = false
      this.selectedCar = null
    },
    onReserved() {
      this.closeModal()
      this.$router.push('/reservations')
    },
    onCarAdded(car) {
      this.cars.unshift(car)
    },
    showAlert(type, msg) {
      this.$emit('show-alert', type, msg)
    }
  }
}
</script>

<style scoped>
.card {
  overflow: visible !important;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.section-title {
  color: #fff;
  font-size: 1.5rem;
  margin: 0;
}

.actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

/* ❌ ELIMINADOS: Los estilos de .actions select ya no son necesarios */

/* 🎚️ Slider doble */
.price-range {
  position: relative;
}

.range-row {
  position: relative;
  height: 30px;
  margin-top: 6px;
}

.range-row input[type="range"] {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  width: 100%;
  background: none;
  -webkit-appearance: none;
  appearance: none;
  pointer-events: none;
}

.range-row input[type="range"]::-webkit-slider-thumb {
  pointer-events: auto;
}

.range-row input[type="range"]::-moz-range-thumb {
  pointer-events: auto;
}

.range-track {
  height: 8px;
  margin-top: 8px;
  border-radius: 999px;
  background: linear-gradient(to right,
      rgba(255, 255, 255, 0.25) 0 var(--a),
      rgba(59, 130, 246, 0.85) var(--a) var(--b),
      rgba(255, 255, 255, 0.25) var(--b) 100%);
}

.range-row input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid rgba(59, 130, 246, 0.9);
  margin-top: -5px;
  cursor: pointer;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.25);
}

.range-row input[type="range"]::-moz-range-thumb {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid rgba(59, 130, 246, 0.9);
  cursor: pointer;
}

.range-badges {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.price-input {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.25);
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 0.9rem;
  width: 80px;
}

.price-input:focus {
  outline: none;
  border-color: #4ba3ff;
  background: rgba(255, 255, 255, 0.2);
}

.range-badges .spacer {
  color: #9fb6ff;
}

.clear-btn.small {
  font-size: 0.85rem;
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  cursor: pointer;
  transition: 0.2s;
}

.clear-btn.small:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 📦 Grid de 2 columnas */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

/* ✅ Separación entre campos del formulario */
.form-group {
  margin-bottom: 16px;
}

/* Labels */
label {
  display: block;
  color: #fff;
  font-size: 0.9rem;
  margin-bottom: 8px;
  font-weight: 500;
}

/* Inputs de fecha */
input[type="date"] {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 0.9rem;
  width: 100%;
  cursor: pointer;
  transition: 0.3s;
}

input[type="date"]:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.35);
}

input[type="date"]:focus {
  outline: none;
  border-color: #4ba3ff;
  background: rgba(255, 255, 255, 0.15);
}

/* ✅ Select de ordenar - NUEVO ESTILO */
#sort {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 0.9rem;
  width: 100%;
  cursor: pointer;
  transition: 0.3s;
}

#sort:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.35);
}

#sort:focus {
  outline: none;
  border-color: #4ba3ff;
  background: rgba(255, 255, 255, 0.15);
}

#sort option {
  background: #1e293b;
  color: #fff;
}

/* 🔹 Chip de orden actual */
.order-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 12px;
  padding: 8px 14px;
  margin: 16px 0;
  width: fit-content;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.order-chip strong {
  color: #4ba3ff;
}

.clear-btn {
  background: none;
  border: none;
  color: #ccc;
  font-size: 18px;
  cursor: pointer;
  transition: 0.2s;
  padding: 0;
  margin: 0;
  line-height: 1;
}

.clear-btn:hover {
  color: #fff;
  transform: scale(1.15);
}

/* Responsive */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
