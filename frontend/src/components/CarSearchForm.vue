<template>
  <div class="card">
    <div class="section-header">
      <h2 class="section-title">Buscar Autos Disponibles</h2>
    </div>

    <div class="form-grid">
      <div class="form-group">
        <label for="start">Fecha Inicio</label>
        <input
          id="start"
          type="date"
          v-model="startDate"
          :max="endDate || null"
        />
      </div>

      <div class="form-group">
        <label for="end">Fecha Fin</label>
        <input
          id="end"
          type="date"
          v-model="endDate"
          :min="startDate || null"
        />
      </div>
    </div>

    <div class="actions">
      <button
        class="btn btn-primary"
        type="button"
        :disabled="!canSearch"
        @click="onSearch"
      >
        Buscar Disponibles
      </button>

      <button
        class="btn btn-secondary"
        type="button"
        @click="onShowAll"
      >
        Ver Todos
      </button>

      <button
        class="btn btn-danger"
        type="button"
        @click="onClear"
        title="Limpiar fechas"
      >
        Limpiar
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CarSearchForm',
  emits: ['search', 'showAll', 'show-alert'],
  data() {
    return {
      startDate: '',
      endDate: ''
    }
  },
  computed: {
    isComplete() {
      return !!this.startDate && !!this.endDate
    },
    isRangeValid() {
      if (!this.isComplete) return false
      const s = new Date(this.startDate)
      const e = new Date(this.endDate)
      return e > s
    },
    canSearch() {
      return this.isComplete && this.isRangeValid
    }
  },
  methods: {
    onSearch() {
      if (!this.isComplete) {
        this.$emit('show-alert', 'error', 'Selecciona ambas fechas')
        return
      }
      if (!this.isRangeValid) {
        this.$emit('show-alert', 'error', 'La fecha fin debe ser posterior a la fecha inicio')
        return
      }
      this.$emit('search', { startDate: this.startDate, endDate: this.endDate })
    },
    onShowAll() {
      this.$emit('showAll')
    },
    onClear() {
      this.startDate = ''
      this.endDate = ''
      this.$emit('show-alert', 'info', 'Fechas limpiadas')
    }
  }
}
</script>
