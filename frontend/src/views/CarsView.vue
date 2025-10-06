<template>
  <div>
    <div class="card">
      <h2 style="margin-bottom:20px">Buscar Autos Disponibles</h2>
      <div class="date-range">
        <div class="form-group" style="flex:1;margin:0">
          <label>Fecha Inicio</label><input type="date" v-model="searchDates.startDate" />
        </div>
        <div class="form-group" style="flex:1;margin:0">
          <label>Fecha Fin</label><input type="date" v-model="searchDates.endDate" />
        </div>
        <button class="btn btn-primary" @click="searchAvailableCars">Buscar Disponibles</button>
        <button class="btn btn-secondary" @click="getAllCars">Ver Todos</button>
        <button class="btn btn-success" v-if="userInfo?.role === 'ADMIN'" @click="showAdd = true">+ Agregar Auto</button>
      </div>
    </div>

    <div class="car-grid">
      <CarCard v-for="car in cars" :key="car.id" :car="car" @select="selectCar" />
    </div>

    <CarModal v-if="showCarModal" :car="selectedCar" :userInfo="userInfo" @close="closeModal" @show-alert="showAlert" @reserved="onReserved" />
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
import CarCard from '../components/CarCard.vue';
import CarModal from '../components/CarModal.vue';
import AddCarModal from '../components/AddCarModal.vue';
import api from '../services/api';
export default {
  name: 'CarsView',
  components: { CarCard, CarModal, AddCarModal },
  data() {
    return {
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
      localStorage.setItem('userId', this.userInfo.id)
      log(this.userInfo)
    } catch {}
  },
  methods: {
    async getAllCars() {
      try {
        const resp = await api.get('/api/cars')
        this.cars = resp.data
      } catch (err) {
        this.showAlert('error', 'Error al cargar autos')
      }
    },
    async searchAvailableCars() {
      if (!this.searchDates.startDate || !this.searchDates.endDate) {
        this.showAlert('error', 'Por favor selecciona ambas fechas')
        return
      }
      try {
        const resp = await api.get('/api/cars/avalible', {
          params: { startDate: this.searchDates.startDate, endDate: this.searchDates.endDate }
        })
        this.cars = resp.data
        this.showAlert('success', 'Se encontraron ${this.cars.length} autos disponibles')
      } catch (err) {
        this.showAlert('error', 'Error al buscar autos disponibles')
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
