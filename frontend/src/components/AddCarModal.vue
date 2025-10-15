<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="addcar-title">
      <div class="section-header">
        <h2 id="addcar-title" class="section-title">Agregar Nuevo Auto</h2>
        <button type="button" class="btn btn-secondary" @click="$emit('close')" aria-label="Cerrar">
          ✕
        </button>
      </div>

      <form @submit.prevent="addCar" novalidate>
        <div class="form-group">
          <label for="brand">Marca</label>
          <input id="brand" type="text" v-model.trim="newCar.brand" required placeholder="Ej: Toyota" />
        </div>

        <div class="form-group">
          <label for="model">Modelo</label>
          <input id="model" type="text" v-model.trim="newCar.model" required placeholder="Ej: Corolla" />
        </div>

        <div class="form-group">
          <label for="year">Año</label>
          <input
            id="year"
            type="number"
            v-model.number="newCar.year"
            :min="1980"
            :max="currentYear + 1"
            required
          />
        </div>

        <div class="form-group">
          <label for="color">Color</label>
          <input id="color" type="text" v-model.trim="newCar.color" required placeholder="Ej: Azul metálico" />
        </div>

        <div class="form-group">
          <label for="category">Categoría</label>
          <select id="category" v-model="newCar.category" required>
            <option value="SEDAN">Sedán</option>
            <option value="SUV">SUV</option>
            <option value="TRUCK">Camioneta</option>
            <option value="COUPE">Coupé</option>
            <option value="HATCHBACK">Hatchback</option>
            <option value="VAN">Van</option>
          </select>
        </div>

        <div class="form-group">
          <label for="status">Estado</label>
          <select id="status" v-model="newCar.status" required>
            <option value="AVAILABLE">Disponible</option>
            <option value="RENTED">Rentado</option>
            <option value="MAINTENANCE">Mantenimiento</option>
            <option value="INACTIVE">Inactivo</option>
          </select>
        </div>

        <div class="form-group">
          <label for="transmission">Transmisión</label>
          <select id="transmission" v-model="newCar.transmissionType" required>
            <option value="MANUAL">Manual</option>
            <option value="AUTOMATIC">Automática</option>
          </select>
        </div>

        <div class="form-group">
          <label for="fuel">Tipo de Combustible</label>
          <select id="fuel" v-model="newCar.fuelType" required>
            <option value="GASOLINE">Gasolina</option>
            <option value="DIESEL">Diésel</option>
            <option value="ELECTRIC">Eléctrico</option>
            <option value="HYBRID">Híbrido</option>
          </select>
        </div>

        <div class="form-group">
          <label for="price">Precio por Día</label>
          <input
            id="price"
            type="number"
            v-model.number="newCar.pricePerDay"
            step="0.01"
            min="0"
            required
            placeholder="Ej: 80.00"
          />
        </div>

        <div class="form-group">
          <label for="plate">Placa</label>
          <input
            id="plate"
            type="text"
            v-model.trim="newCar.licensePlate"
            required
            placeholder="Ej: ABC123"
          />
        </div>

        <div class="mt-2">
          <button class="btn btn-primary" type="submit" :disabled="isSubmitting">
            {{ isSubmitting ? 'Guardando…' : 'Agregar Auto' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'AddCarModal',
  emits: ['close', 'car-added', 'show-alert'],
  props: {
    userInfo: { type: Object, required: true }
  },
  data() {
    return {
      isSubmitting: false,
      currentYear: new Date().getFullYear(),
      newCar: {
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
      // validación básica en front
      if (!this.newCar.brand || !this.newCar.model || !this.newCar.licensePlate) {
        this.$emit('show-alert', 'error', 'Completa los campos obligatorios')
        return
      }

      // ownerId: primero del userInfo, si no, de localStorage
      const ownerId = this.userInfo?.id || localStorage.getItem('userId')
      if (!ownerId) {
        this.$emit('show-alert', 'error', 'No se pudo determinar el propietario (ownerId)')
        return
      }

      this.isSubmitting = true
      try {
        const payload = { ...this.newCar, ownerId }
        const resp = await api.post('/api/cars', payload)
        this.$emit('car-added', resp.data)
        this.$emit('show-alert', 'success', 'Auto agregado')
        this.resetForm()
        this.$emit('close')
      } catch (err) {
        this.$emit('show-alert', 'error', 'Error al agregar auto')
      } finally {
        this.isSubmitting = false
      }
    },
    resetForm() {
      this.newCar = {
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
  }
}
</script>
