<template>
  <div class="card">
    <h2 style="margin-bottom:30px">Mis Reservas</h2>
    <table class="table">
      <thead>
        <tr><th>ID</th><th>Auto</th><th>Fecha Inicio</th><th>Fecha Fin</th><th>Precio Total</th><th>Estado</th><th>Acciones</th></tr>
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
            <button class="btn btn-success" v-if="r.status === 'PENDING'" @click="createPayment(r)">Pagar</button>
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
      const method = prompt('Método de pago (CREDIT_CARD, DEBIT_CARD, CASH, TRANSFER):')
      if (!method) return
      try {
        await api.post('/api/payments', null, { params: { reservationId: reservation.id, amount: reservation.totalPrice, method } })
        this.$emit('show-alert', 'success', 'Pago procesado')
        this.loadReservations()
      } catch {
        this.$emit('show-alert', 'error', 'Error al procesar pago')
      }
    }
  }
}
</script>
