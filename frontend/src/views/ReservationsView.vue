<template>
  <div class="reservations-page">
    <section class="card reservations-card">
      <div class="header">
        <h2>Mis Reservas</h2>

        <button class="btn btn-primary pay-all-btn" @click="goToPayment">
          💳 Pagar Reserva
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
              <td>{{ r.carBrand }} {{ r.carModel }}</td>
              <td>{{ formatDate(r.startDate) }}</td>
              <td>{{ formatDate(r.endDate) }}</td>
              <td>{{ formatMoney(r.totalPrice) }}</td>
              <td>
                <span class="badge" :class="statusClass(r.status)">
                  {{ r.status }}
                </span>
              </td>
              <td>
                <button
                  class="btn btn-success"
                  v-if="r.status === 'PENDING'"
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
      return new Date(d).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: '2-digit'
      })
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
    async createPayment(res) {
      const method = prompt('Método de pago (CREDIT_CARD, DEBIT_CARD, CASH, TRANSFER):')
      if (!method) return
      try {
        await api.post('/api/payments', null, {
          params: {
            reservationId: res.id,
            amount: res.totalPrice,
            method
          }
        })
        this.$emit('show-alert', 'success', 'Pago procesado')
        this.loadReservations()
      } catch {
        this.$emit('show-alert', 'error', 'Error al procesar pago')
      }
    },
    goToPayment() {
      alert('Selecciona una reserva pendiente y presiona “Pagar” para continuar.')
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
</style>
