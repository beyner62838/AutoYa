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
          <label>Transmisión</label>
          <select v-model="newCar.transmission" required>
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
          <label>Número de Asientos</label>
          <input type="number" v-model="newCar.numberOfSeats" required />
        </div>
        <div class="form-group">
          <label>Precio por Día</label>
          <input type="number" v-model="newCar.pricePerDay" step="0.01" required />
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
  data() {
    return {
      newCar: {
        brand: '',
        model: '',
        year: new Date().getFullYear(),
        color: '',
        transmission: 'MANUAL',
        fuelType: 'GASOLINE',
        numberOfSeats: 5,
        pricePerDay: 50
      }
    }
  },
  methods: {
    async addCar() {
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
