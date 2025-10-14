<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">Agregar Nuevo Auto</h2>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <form @submit.prevent="addCar">
        <div class="form-group">
          <label>Marca</label>
          <input type="text" v-model="newCar.brand" required />
        </div>
        <div class="form-group">
          <label>Modelo</label>
          <input type="text" v-model="newCar.model" required />
        </div>
        <div class="form-group">
          <label>Año</label>
          <input type="number" v-model="newCar.year" required />
        </div>
        <div class="form-group">
          <label>Color</label>
          <input type="text" v-model="newCar.color" required />
        </div>
        <div class="form-group">
          <label>Categoría</label>
          <select v-model="newCar.category" required>
            <option value="SEDAN">Sedán</option>
            <option value="SUV">SUV</option>
            <option value="TRUCK">Camioneta</option>
            <option value="COUPE">Coupé</option>
            <option value="HATCHBACK">Hatchback</option>
            <option value="VAN">Van</option>
            <!-- Agrega más si es necesario -->
          </select>
        </div>
        <div class="form-group">
          <label>Estado</label>
          <select v-model="newCar.status" required>
            <option value="AVAILABLE">Disponible</option>
            <option value="RENTED">Rentado</option>
            <option value="MAINTENANCE">Mantenimiento</option>
            <option value="INACTIVE">Inactivo</option>
          </select>
        </div>
        <div class="form-group">
          <label>Transmisión</label>
          <select v-model="newCar.transmissionType" required>
            <option value="MANUAL">Manual</option>
            <option value="AUTOMATIC">Automática</option>
          </select>
        </div>
        <div class="form-group">
          <label>Tipo de Combustible</label>
          <select v-model="newCar.fuelType" required>
            <option value="GASOLINE">Gasolina</option>
            <option value="DIESEL">Diesel</option>
            <option value="ELECTRIC">Eléctrico</option>
            <option value="HYBRID">Híbrido</option>
          </select>
        </div>
        <div class="form-group">
          <label>Precio por Día</label>
          <input type="number" v-model="newCar.pricePerDay" step="0.01" required />
        </div>
        <div class="form-group">
          <label>Placa</label>
          <input type="text" v-model="newCar.licensePlate" required />
        </div>

        <button class="btn btn-primary" type="submit">Agregar Auto</button>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
export default {
  name: 'AddCarModal',
  props: {
    userInfo: { type: Object, required: true }
  },
  data() {
    return {
      newCar: {
        // ownerId se asigna justo antes de enviar la petición
        brand: '',
        model: '',
        year: new Date().getFullYear(),
        color: '',
        category: 'SEDAN',
        status: 'AVAILABLE',
        fuelType: 'GASOLINE',
        transmissionType: 'MANUAL',
        pricePerDay: 50,
        licensePlate: ''
      }
    }
  },
  methods: {
    async addCar() {
      // Asigna ownerId justo antes de enviar
      this.newCar.ownerId = localStorage.getItem('userId')
      try {
        const resp = await api.post('/api/cars', this.newCar)
        this.$emit('car-added', resp.data)
        this.$emit('show-alert', 'success', 'Auto agregado')
        this.$emit('close')
      } catch (err) {
        this.$emit('show-alert', 'error', 'Error al agregar auto')
      }
    }
  }
}
</script>