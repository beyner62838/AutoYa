<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="carmodal-title">
      <!-- Header -->
      <div class="section-header">
        <h2 id="carmodal-title" class="section-title">
          {{ car.brand }} {{ car.model }}
        </h2>
        <button class="btn btn-secondary" type="button" aria-label="Cerrar" @click="$emit('close')">
          ✕
        </button>
      </div>

      <!-- Detalles -->
      <div class="card mb-2">
        <p><strong>Año:</strong> {{ car.year }}</p>
        <p><strong>Color:</strong> {{ car.color }}</p>
        <p><strong>Transmisión:</strong> {{ car.transmissionType || car.transmission }}</p>
        <p><strong>Tipo de Combustible:</strong> {{ car.fuelType }}</p>
        <p><strong>Número de Asientos:</strong> {{ car.numberOfSeats }}</p>
        <p><strong>Precio por Día:</strong> ${{ car.pricePerDay }}</p>
      </div>

      <!-- Reserva -->
      <div class="card">
        <h3 class="section-title" style="-webkit-text-fill-color:initial;color: #FFFFFF;background:none">
          Hacer Reserva
        </h3>

        <div class="form-group">
          <label for="start">Fecha Inicio</label>
          <input id="start" type="date" v-model="startDate" />
        </div>

        <div class="form-group">
          <label for="end">Fecha Fin</label>
          <input id="end" type="date" v-model="endDate" />
        </div>

        <div class="mb-2" v-if="nights > 0">
          <strong>Noches:</strong> {{ nights }} &nbsp;·&nbsp;
          <strong>Total estimado:</strong> ${{ totalEstimated }}
        </div>

        <div class="flex gap-2">
          <button class="btn btn-primary" type="button" @click="checkAvailability">
            Verificar Disponibilidad
          </button>
          <button class="btn btn-success" type="button" :disabled="!available || nights <= 0" v-if="available"
            @click="confirmReservation">
            Confirmar Reserva
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'CarModal',
  emits: ['close','show-alert','reserved'],
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
  computed: {
    nights() {
      if (!this.startDate || !this.endDate) return 0
      const s = new Date(this.startDate)
      const e = new Date(this.endDate)
      const ms = e - s
      const d = Math.ceil(ms / (1000 * 60 * 60 * 24))
      return isNaN(d) || d <= 0 ? 0 : d
    },
    totalEstimated() {
      if (this.nights <= 0 || !this.car?.pricePerDay) return 0
      try {
        return Number(this.nights * this.car.pricePerDay).toLocaleString('es-CO', { minimumFractionDigits: 0 })
      } catch {
        return this.nights * this.car.pricePerDay
      }
    }
  },
  methods: {
    async checkAvailability() {
      this.available = false
      if (!this.startDate || !this.endDate) {
        this.$emit('show-alert', 'error', 'Selecciona ambas fechas')
        return
      }
      if (this.nights <= 0) {
        this.$emit('show-alert', 'error', 'La fecha fin debe ser posterior a la fecha inicio')
        return
      }
      try {
        const resp = await api.get(`/api/cars/${this.car.id}/availability`, {
          params: { from: this.startDate, to: this.endDate }
        })
        // Soporta booleano o lista (true / length>0)
        const data = resp?.data
        this.available = Boolean(data?.available ?? (Array.isArray(data) ? data.length > 0 : data))
        this.$emit('show-alert', this.available ? 'success' : 'error', this.available ? 'Disponible' : 'No disponible')
      } catch {
        this.$emit('show-alert', 'error', 'Error al verificar disponibilidad')
      }
    },
    async confirmReservation() {
      if (!this.available || this.nights <= 0) return
      try {
        const clientId = this.userInfo?.id || localStorage.getItem('userId')
        if (!clientId) {
          this.$emit('show-alert', 'error', 'No se pudo identificar el usuario')
          return
        }
        await api.post(`/api/reservations`, null, {
          params: {
            clientId,
            carId: this.car.id,
            startDate: this.startDate,
            endDate: this.endDate
          }
        })
        this.$emit('show-alert', 'success', 'Reserva creada')
        this.$emit('reserved')
      } catch {
        this.$emit('show-alert', 'error', 'Error al crear reserva')
      }
    }
  }
}
</script>
