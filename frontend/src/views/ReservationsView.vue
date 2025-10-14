<template>
  <div class="card">
    <div class="header">
      <h2>Mis Reservas</h2>
      <!-- 🔹 Botón adicional al lado del título -->
      <button class="btn btn-primary pay-all-btn" @click="goToPayment">
        💳 Pagar Reserva
      </button>
    </div>

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
          <td>{{ r.carBrand }} {{ r.carModel }}</td>
          <td>{{ formatDate(r.startDate) }}</td>
          <td>{{ formatDate(r.endDate) }}</td>
          <td>${{ r.totalPrice }}</td>
          <td>{{ r.status }}</td>
          <td>
            <button class="btn btn-success" v-if="r.status === 'PENDING'" @click="createPayment(r)">
              Pagar
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'ReservationsView',
  data() {
    return { reservations: [] }
  },
  mounted() {
    this.loadReservations()
  },
  methods: {
    async loadReservations() {
      try {
        const resp = await api.get('/api/reservations')
        this.reservations = resp.data
      } catch {
        this.$emit('show-alert', 'error', 'Error al cargar reservas')
      }
    },
    formatDate(d) {
      if (!d) return ''
      return new Date(d).toLocaleDateString('es-ES')
    },
    async createPayment(reservation) {
      const method = prompt(
        'Método de pago (CREDIT_CARD, DEBIT_CARD, CASH, TRANSFER):'
      )
      if (!method) return
      try {
        await api.post('/api/payments', null, {
          params: {
            reservationId: reservation.id,
            amount: reservation.totalPrice,
            method
          }
        })
        this.$emit('show-alert', 'success', 'Pago procesado')
        this.loadReservations()
      } catch {
        this.$emit('show-alert', 'error', 'Error al procesar pago')
      }
    },
    // 🔹 Botón adicional (redirige a vista de pagos o mensaje general)
    goToPayment() {
      alert('Selecciona una reserva pendiente y presiona "Pagar" para continuar.')
    }
  }
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.pay-all-btn {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 8px 14px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pay-all-btn:hover {
  background-color: #0056b3;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.table th {
  background-color: #f5f5f5;
}

.btn-success {
  background-color: #28a745;
  border: none;
  padding: 6px 12px;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-success:hover {
  background-color: #218838;
}
</style>
