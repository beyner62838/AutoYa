<template>
  <div class="card">
    <h2 style="margin-bottom:30px">Mis Pagos</h2>
    <table class="table">
      <thead>
        <tr><th>ID</th><th>Reserva ID</th><th>Monto</th><th>Método</th><th>Fecha</th></tr>
      </thead>
      <tbody>
        <tr v-for="p in payments" :key="p.id">
          <td>{{ p.id }}</td>
          <td>{{ p.reservationId }}</td>
          <td>${{ p.amount }}</td>
          <td>{{ p.method }}</td>
          <td>{{ formatDate(p.paymentDate) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import api from '../services/api'
export default {
  name: 'PaymentsView',
  data() {
    return { payments: [] }
  },
  mounted() {
    this.loadPayments()
  },
  methods: {
    async loadPayments() {
      try {
        const resp = await api.get('/api/payments')
        this.payments = resp.data
      } catch {
        this.$emit('show-alert', 'error', 'Error al cargar pagos')
      }
    },
    formatDate(d) {
      if (!d) return ''
      return new Date(d).toLocaleDateString('es-ES')
    }
  }
}
</script>
