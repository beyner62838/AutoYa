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
          <input id="start" type="date" v-model="startDate" :min="today" />
        </div>

        <div class="form-group">
          <label for="end">Fecha Fin</label>
          <input id="end" type="date" v-model="endDate" :min="startDate || today" />
        </div>

        <div class="mb-2" v-if="nights > 0">
          <strong>Noches:</strong> {{ nights }} &nbsp;·&nbsp;
          <strong>Total estimado:</strong> ${{ totalEstimated }}
        </div>

        <!-- Estado de disponibilidad -->
        <div class="mb-2" aria-live="polite">
          <span v-if="available === true" class="badge ok">Disponible</span>
          <span v-else-if="available === false" class="badge bad">No disponible</span>
        </div>

        <div class="flex gap-2">
          <button class="btn btn-primary" type="button" @click="checkAvailability">
            Verificar Disponibilidad
          </button>

          <!-- Mostrar SOLO si hay disponibilidad y noches > 0 -->
          <button class="btn btn-success" type="button" v-if="available === true && nights > 0"
            @click="confirmReservation">
            Confirmar Reserva
          </button>
        </div>

        <!-- Sugerencia de próxima fecha disponible -->
        <div class="mt-2" v-if="available === false && nextAvailableStartDate.length > 0">
          <small class="text-muted">
            Las próxima(s) fecha(s) disponible(s) para reservar desde el {{ nextAvailableStartDate[0] }}
            hasta el {{ nextAvailableEndDate[0] }}
            <span v-if="nextAvailableStartDate[1]"> y desde el {{ nextAvailableStartDate[1] }}
              hasta el {{ nextAvailableEndDate[1] }}</span>
          </small>
        </div>
      </div>

      <!-- Sección de calificaciones -->
      <div class="card">
        <RatingSection :carId="car.id" @show-alert="handleShowAlert" />
      </div>

      <!-- Botón de reserva (opcional): visible solo si hay disponibilidad -->
      <div class="actions" v-if="userInfo && userInfo.role !== 'ADMIN' && available === true && nights > 0">
        <button class="btn btn-primary" @click="reserve">
          Reservar ahora
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
import RatingSection from './RatingSection.vue'

export default {
  name: 'CarModal',
  components: {
    RatingSection
  },
  emits: ['close', 'show-alert', 'reserved'],
  props: {
    car: { type: Object, required: true },
    userInfo: { type: Object }
  },
  data() {
    return {
      startDate: '',
      endDate: '',
      available: null,
      today: new Date().toISOString().slice(0, 10),
      nextAvailableStartDate: [],
      nextAvailableEndDate: []

    }
  },
  computed: {
    nights() {
      if (!this.startDate || !this.endDate) return 0
      const s = new Date(this.startDate)
      const e = new Date(this.endDate)
      const ms = e - s
      let d = Math.floor(ms / (1000 * 60 * 60 * 24))
      if (d <= 0) d = 1
      return isNaN(d) ? 0 : d
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
  watch: {
    startDate() { this.available = [] },
    endDate() { this.available = [] }
  },
  methods: {

    formatDateLocal(d) {
      if (!d) return ''
      // Espera 'YYYY-MM-DD' o 'YYYY-MM-DDTHH:mm:ssZ'
      const dateStr = d.split('T')[0]
      const [y, m, day] = String(dateStr).split('-')
      const months = [
        'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
        'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'
      ]
      const monthName = months[parseInt(m, 10) - 1] || m
      return `${day} ${monthName} ${y}`
    },
    async checkAvailability() {
      this.available = null
      this.nextAvailableStartDate = []
      this.nextAvailableEndDate = []

      if (!this.startDate || !this.endDate) {
        this.$emit('show-alert', 'error', 'Selecciona ambas fechas')
        this.available = false
        return
      }

      if (this.nights < 0) {
        this.$emit('show-alert', 'error', 'La fecha fin debe ser posterior a la fecha inicio')
        this.available = false
        return
      }

      try {
        const resp = await api.get(`/api/cars/${this.car.id}/availability`, {
          params: { from: this.startDate, to: this.endDate }
        })
        const data = resp.data

        console.log('[DEBUG] checkAvailability:', {
          carId: this.car.id,
          from: this.startDate,
          to: this.endDate,
          response: data
        })

        if (Array.isArray(data)) {
          const found = data.find(a =>
            a.startDate <= this.startDate && a.endDate >= this.endDate
          )
          
          this.available = !!found

          console.log('[DEBUG] Disponibilidad encontrada:', !!found)

          if (!this.available && data.length > 0) {
            // ✅ Filtrar solo los rangos realmente disponibles DESPUÉS del rango seleccionado
            const limitedData = data
              .filter(a => new Date(a.startDate) >= new Date(this.endDate))
              .slice(0, 2)

            this.nextAvailableStartDate = []
            this.nextAvailableEndDate = []

            for (let i = 0; i < data.length; i++) {
              this.nextAvailableStartDate.push(this.formatDateLocal(data[i].startDate))
              this.nextAvailableEndDate.push(this.formatDateLocal(data[i].endDate))
            }

            console.warn("debug")
            console.log('[DEBUG] Fechas sugeridas:', {
              nextAvailableStartDate: this.nextAvailableStartDate,
              nextAvailableEndDate: this.nextAvailableEndDate
            })

            // ✅ Mostrar mensaje según cantidad de fechas sugeridas
            const message = `No disponible. Dentro del rango seleccionado la próxima fecha disponible es del ${this.nextAvailableStartDate[0]} hasta el ${this.nextAvailableEndDate[0]}`
              + (data[1]
                ? ` y desde el ${this.nextAvailableStartDate[1]} hasta el ${this.nextAvailableEndDate[1]}`
                : '')

            this.$emit('show-alert', 'error', message)
            return
          }else {
            this.nextAvailableStartDate = []
            this.nextAvailableEndDate = []
            this.$emit('show-alert', 'success', 'Disponible')
          }
        }


        
        

      } catch (err) {
        console.log('[DEBUG] checkAvailability error:', err)
        this.available = false
        this.$emit('show-alert', 'error', 'Error al verificar disponibilidad')
      }
    },

    async confirmReservation() {
      if (this.available !== true || this.nights <= 0) return
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
    },
    reserve() {
      if (this.available === true && this.nights > 0) {
        this.$emit('reserved')
      }
    },
    handleShowAlert(type, message) {
      this.$emit('show-alert', type, message)
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #1e293b;
  border-radius: 16px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  margin: 0;
  font-size: 1.5rem;
  color: #fff;
}

.btn-secondary {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #fff;
  cursor: pointer;
}

.card {
  background: #2a3748;
  border-radius: 8px;
  margin: 1rem 0;
  padding: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  color: #fff;
  margin-bottom: 0.5rem;
  display: block;
}

input[type="date"] {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  background: #1e293b;
  color: #fff;
}

input[type="date"]::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.flex {
  display: flex;
  gap: 0.5rem;
}

.btn-primary {
  background: #007bff;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
}

.btn-success {
  background: #28a745;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
}

.actions {
  margin-top: 1rem;
  text-align: right;
}

.car-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.car-image {
  width: 100%;
  height: 300px;
  overflow: hidden;
  border-radius: 8px;
}

.car-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.car-info {
  color: #fff;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #fff;
  cursor: pointer;
}

/* Etiquetas de estado */
.badge {
  pointer-events: none;
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-weight: 600;
}

.badge.ok {
  background: #1f8f36;
  color: #fff;
}

.badge.bad {
  background: #b3261e;
  color: #fff;
}
</style>
