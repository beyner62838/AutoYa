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

          <button
            v-if="userInfo?.role === 'ADMIN'"
            class="btn btn-success"
            @click="showAdd = true"
            type="button"
          >
            + Agregar auto
          </button>
        </div>
      </div>

      <div class="form-grid">
        <div class="form-group">
          <label for="from">Fecha inicio</label>
          <input id="from" type="date" v-model="searchDates.startDate" />
        </div>

        <div class="form-group">
          <label for="to">Fecha fin</label>
          <input id="to" type="date" v-model="searchDates.endDate" />
        </div>
      </div>
    </section>

    <!-- Grilla de autos -->
    <section class="grid">
      <CarCard
        v-for="car in cars"
        :key="car.id"
        :car="car"
        @select="selectCar"
      />
    </section>

    <!-- Estado vacío bonito -->
    <div v-if="!loading && cars.length === 0" class="card empty">
      <h3 class="empty-title">Sin resultados</h3>
      <p class="empty-text">
        No encontramos autos para ese rango de fechas. Ajusta las fechas o ve todo el catálogo.
      </p>
    </div>

    <!-- Modales -->
    <CarModal
      v-if="showCarModal"
      :car="selectedCar"
      :userInfo="userInfo"
      @close="closeModal"
      @show-alert="showAlert"
      @reserved="onReserved"
    />
    <AddCarModal
      v-if="showAdd"
      :userInfo="userInfo"
      @close="showAdd=false"
      @car-added="onCarAdded"
      @show-alert="showAlert"
    />
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
      userInfo: null
    }
  },
  async mounted() {
    await this.getAllCars()
    try {
      const res = await api.get('/auth/hello')
      this.userInfo = res.data
      if (this.userInfo?.id) localStorage.setItem('userId', this.userInfo.id)
    } catch {/* silencioso */}
  },
  methods: {
    async getAllCars() {
      this.loading = true
      try {
        const resp = await api.get('/api/cars')
        this.cars = resp.data
      } catch {
        this.showAlert('error', 'Error al cargar autos')
      } finally {
        this.loading = false
      }
    },
    async searchAvailableCars() {
      if (!this.searchDates.startDate || !this.searchDates.endDate) {
        this.showAlert('error', 'Por favor selecciona ambas fechas')
        return
      }
      this.loading = true
      try {
        // Si tu backend realmente es /api/cars/available, cambia aquí.
        const resp = await api.get('/api/cars/avalible', {
          params: {
            startDate: this.searchDates.startDate,
            endDate: this.searchDates.endDate
          }
        })
        this.cars = resp.data
        this.showAlert('success', `Se encontraron ${this.cars.length} autos disponibles`)
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
