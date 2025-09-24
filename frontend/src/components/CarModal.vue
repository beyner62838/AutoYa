<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">{{ car.brand }} {{ car.model }}</h2>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="car-details">
        <p><strong>Año:</strong> {{ car.year }}</p>
        <p><strong>Color:</strong> {{ car.color }}</p>
        <p><strong>Transmisión:</strong> {{ car.transmission }}</p>
        <p><strong>Tipo de Combustible:</strong> {{ car.fuelType }}</p>
        <p><strong>Número de Asientos:</strong> {{ car.numberOfSeats }}</p>
        <p><strong>Precio por Día:</strong> ${{ car.pricePerDay }}</p>
      </div>

      <div style="margin-top:20px;">
        <h3>Hacer Reserva</h3>
        <div class="date-range" style="display:flex;gap:12px;">
          <div style="flex:1"><label>Fecha Inicio</label><input type="date" v-model="startDate" /></div>
          <div style="flex:1"><label>Fecha Fin</label><input type="date" v-model="endDate" /></div>
        </div>
        <div style="margin-top:12px">
          <button class="btn btn-primary" @click="checkAvailability">Verificar Disponibilidad</button>
          <button class="btn btn-success" v-if="available" @click="confirmReservation">Confirmar Reserva</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'CarModal',
  props: {
    car: { type: Object, required: true },
    userInfo: { type: Object }
  },
  data() {
    return {
      startDate: '',
      endDate: '',
      available: false
    }
  },
  methods: {
    async checkAvailability() {
      if (!this.startDate || !this.endDate) {
        this.$emit('show-alert', 'error', 'Selecciona ambas fechas')
        return
      }
      try {
        const resp = await api.get(`/api/cars/${this.car.id}/availability`, {
          params: { from: this.startDate, to: this.endDate }
        })
        if (resp.data && resp.data.length > 0) {
          this.available = true
          this.$emit('show-alert', 'success', 'Disponible')
        } else {
          this.$emit('show-alert', 'error', 'No disponible')
        }
      } catch (err) {
        this.$emit('show-alert', 'error', 'Error al verificar disponibilidad')
      }
    },
    async confirmReservation() {
      try {
        await api.post(`/api/reservations`, null, {
          params: {
            clientId: this.userInfo.id,
            carId: this.car.id,
            startDate: this.startDate,
            endDate: this.endDate
          }
        })
        this.$emit('show-alert', 'success', 'Reserva creada')
        this.$emit('reserved')
      } catch (err) {
        this.$emit('show-alert', 'error', 'Error al crear reserva')
      }
    }
  }
}
</script>
