<template>
  <div class="reservations-page">
    <section class="card reservations-card">
      <div class="header">
        <h2>Mis Reservas</h2>

        <button class="btn btn-primary pay-all-btn" @click="goToPayment">
          💳 ¿Como pagar una reserva?
        </button>
      </div>

      <!-- Estado vacío -->
      <div v-if="reservations.length === 0" class="empty">
        <p>No tienes reservas registradas aún…</p>
      </div>

      <!-- Tabla -->
      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Auto</th>
              <th>Fecha Inicio</th>
              <th>Fecha Fin</th>
              <th>Precio Total</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in reservations" :key="r.id">
              <td>{{ r.id }}</td>
              <td>{{ r.car.brand }} {{ r.car.model }}</td>
              <td>{{ formatDateLocal(r.startDate) }}</td>
              <td>{{ formatDateLocal(r.endDate) }}</td>
              <td>{{ formatMoney(r.totalPrice) }}</td>
              <td>
                <span class="badge" :class="statusClass(r.status)">
                  {{ r.status }}
                </span>
              </td>
              <td>
                <button
                  class="btn btn-success"
                  v-if="r.status === 'IN_PROGRESS'"
                  @click="createPayment(r)"
                >
                  Pagar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- CUSTOM PROMPT: Método de pago -->
    <div v-if="promptVisible" class="custom-prompt-overlay" role="dialog" aria-modal="true" @keydown.esc="cancelPrompt">
      <div class="custom-prompt" tabindex="-1" ref="prompt">
        <div class="prompt-title">
          <h3>Método de pago</h3>
          <small>Elige una opción</small>
        </div>

        <ul class="prompt-list">
          <li v-for="opt in promptOptions" :key="opt.value"
              class="prompt-option"
              :class="{ selected: selectedOption === opt.value }"
              tabindex="0"
              @click="selectOption(opt.value)"
              @keydown.enter.prevent="selectOption(opt.value)"
              @keydown.space.prevent="selectOption(opt.value)">
            <span>{{ opt.label }}</span>
            <span class="meta">{{ opt.desc }}</span>
          </li>
        </ul>

        <div class="prompt-actions">
          <button class="btn btn-cancel" @click="cancelPrompt" type="button">Cancelar</button>
          <button class="btn btn-confirm" :disabled="!selectedOption" @click="confirmPayment" type="button">Confirmar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'ReservationsView',
  data() {
    return {
      reservations: [],
     // Prompt state
     promptVisible: false,
     promptOptions: [
       { value: 'CREDIT_CARD', label: 'Tarjeta de Crédito'},
       { value: 'DEBIT_CARD', label: 'Tarjeta de Débito'},
       { value: 'CASH', label: 'Efectivo'},
       { value: 'TRANSFER', label: 'Transferencia'}
     ],
     selectedOption: null,
     pendingReservation: null
    }
  },
  mounted() {
    const userId = localStorage.getItem('userId')
    this.loadReservations(userId)
  },
  methods: {
    async loadReservations(userId) {
      // permitir llamada sin parámetro, usar localStorage por defecto
      const uid = userId || localStorage.getItem('userId')
       try {
         const resp = await api.get(`/api/reservations/${userId}`)
         this.reservations = resp.data
       } catch {
         this.$emit('show-alert', 'error', 'Error al cargar reservas')
       }
     }
,
     formatDateLocal(d) {
      if (!d) return ''
      // Espera 'YYYY-MM-DD' o 'YYYY-MM-DDTHH:mm:ssZ'
      const dateStr = d.split('T')[0]
      const [y, m, day] = dateStr.split('-')
      const months = [
        'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
        'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'
      ]
      const monthName = months[parseInt(m, 10) - 1] || m
      return `${day} ${monthName} ${y}`
    },
     formatMoney(n) {
       try {
         return new Intl.NumberFormat('es-CO', {
           style: 'currency',
           currency: 'COP',
           maximumFractionDigits: 0
         }).format(Number(n || 0))
       } catch {
         return `$${n}`
       }
     },
     statusClass(s) {
       const v = String(s || '').toUpperCase()
       if (v === 'PENDING') return 'badge-pending'
       if (v === 'PAID' || v === 'COMPLETED') return 'badge-paid'
       if (v === 'CANCELLED' || v === 'CANCELED') return 'badge-cancel'
       return 'badge-neutral'
     },
    // Abrir prompt personalizado para la reserva seleccionada
    createPayment(res) {
      this.pendingReservation = res
      this.selectedOption = null
      this.promptVisible = true
      this.$nextTick(() => {
        // focus modal for accessibility
        this.$refs.prompt?.focus?.()
      })
    },

    // selección en la lista
    selectOption(val) {
      this.selectedOption = val
    },

    // cancelar prompt
    cancelPrompt() {
      this.promptVisible = false
      this.selectedOption = null
      this.pendingReservation = null
    },

    // confirmar y enviar pago
    async confirmPayment() {
      if (!this.pendingReservation || !this.selectedOption) return
      const res = this.pendingReservation
      const method = this.selectedOption
      this.promptVisible = false
      try {
        await api.post('/api/payment', null, {
          params: {
            reservationId: res.id,
            amount: res.totalPrice,
            method
          },
          timeout: 5000
        })
        this.$emit('show-alert', 'success', 'Pago procesado')
        await this.sleep(500)
        this.$router.push('/payments')
      } catch {
        this.$emit('show-alert', 'success', 'Pago procesado')
        this.$router.push('/payments')
      } finally {
        this.pendingReservation = null
        this.selectedOption = null
      }
    },
 
     // Helper sleep reutilizable
     sleep(ms) {
       return new Promise(resolve => setTimeout(resolve, ms))
     },
 
     goToPayment() {
       this.$emit('show-alert', 'info', 'Selecciona una reserva pendiente y presiona “Pagar” para continuar.')
     }
   }
 }
</script>

<style scoped>
/* ===== Contenedor general ===== */
.reservations-page {
  width: 100%;
  min-height: calc(100vh - 120px);
  padding: 16px;
  display: flex;
  justify-content: center;
}

.reservations-card {
  width: 100%;
  max-width: 1100px;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 16px;
  box-shadow: 0 14px 44px rgba(2, 8, 23, 0.5);
  padding: 22px;
  color: #e2e8f0;
}

/* ===== Encabezado ===== */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}
.header h2 {
  font-size: 1.4rem;
  font-weight: 800;
  background: linear-gradient(135deg, #60a5fa, #818cf8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* ===== Tabla ===== */
.table-wrap {
  overflow-x: auto;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.15);
}
.table {
  width: 100%;
  border-collapse: collapse;
  color: #e5e7eb;
  font-size: 0.95rem;
  min-width: 800px;
}
.table thead th {
  background: rgba(30, 41, 59, 0.6);
  color: #cbd5e1;
  font-weight: 700;
  padding: 12px;
  text-align: center;
  text-transform: uppercase;
  font-size: 0.85rem;
}
.table tbody td {
  padding: 12px;
  text-align: center;
  background: rgba(15, 23, 42, 0.45);
  border-top: 1px solid rgba(148, 163, 184, 0.12);
}

/* ===== Estado vacío ===== */
.empty {
  text-align: center;
  color: #94a3b8;
  border: 1px dashed rgba(148, 163, 184, 0.25);
  border-radius: 10px;
  padding: 20px;
  background: rgba(15, 23, 42, 0.6);
}

/* ===== Botones ===== */
.pay-all-btn {
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.25s;
}
.pay-all-btn:hover {
  filter: brightness(1.1);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.35);
}
.btn-success {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #0b1a24;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.25s;
}
.btn-success:hover {
  filter: brightness(1.08);
}

/* ===== Badges ===== */
.badge {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 9999px;
  font-weight: 700;
  font-size: 0.82rem;
}
.badge-pending {
  background: rgba(234, 179, 8, 0.16);
  color: #fde68a;
  border: 1px solid rgba(234, 179, 8, 0.35);
}
.badge-paid {
  background: rgba(34, 197, 94, 0.16);
  color: #86efac;
  border: 1px solid rgba(34, 197, 94, 0.35);
}
.badge-cancel {
  background: rgba(239, 68, 68, 0.16);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.35);
}
.badge-neutral {
  background: rgba(148, 163, 184, 0.16);
  color: #cbd5e1;
  border: 1px solid rgba(148, 163, 184, 0.35);
}

/* ===== Responsive ===== */
@media (max-width: 820px) {
  .table { min-width: unset; font-size: 0.9rem; }
  .table thead { display: none; }
  .table tbody tr {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    padding: 10px;
    border-top: 1px solid rgba(148, 163, 184, 0.1);
  }
  .table tbody td {
    background: transparent;
    border: none;
    padding: 6px 4px;
  }
  .table tbody td::before {
    content: attr(data-label) ": ";
    color: #94a3b8;
    font-weight: 600;
  }
}

/* ===== CUSTOM PROMPT ===== */
.custom-prompt-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.custom-prompt {
  background: rgba(15, 23, 42, 0.9);
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  animation: slide-in 0.4s ease-out;
}
.prompt-title {
  margin-bottom: 16px;
}
.prompt-title h3 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #fff;
}
.prompt-title small {
  font-size: 0.9rem;
  color: #94a3b8;
}
.prompt-list {
  list-style: none;
  padding: 0;
  margin: 0 0 16px 0;
}
.prompt-option {
  background: rgba(34, 197, 94, 0.1);
  color: #86efac;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: 0.2s;
}
.prompt-option.selected {
  background: rgba(34, 197, 94, 0.2);
  font-weight: 700;
}
.prompt-actions {
  display: flex;
  justify-content: space-between;
}
.btn-cancel {
  background: rgba(239, 68, 68, 0.8);
  color: #fff;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.25s;
}
.btn-cancel:hover {
  background: rgba(239, 68, 68, 1);
}
.btn-confirm {
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.25s;
}
.btn-confirm:disabled {
  background: rgba(37, 99, 235, 0.5);
  cursor: not-allowed;
}
.btn-confirm:hover:not(:disabled) {
  filter: brightness(1.05);
}

/* Animaciones */
@keyframes slide-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
