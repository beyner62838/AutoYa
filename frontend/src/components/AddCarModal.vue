<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="addcar-title">
      <div class="section-header">
        <h2 id="addcar-title" class="section-title">{{ isEdit ? 'Editar Auto' : 'Agregar Nuevo Auto' }}</h2>
        <button type="button" class="btn btn-secondary" @click="$emit('close')" aria-label="Cerrar">✕</button>
      </div>

      <div class="modal-body">
        <!-- Mostrar formulario sólo si es ADMIN -->
        <div v-if="isAdmin">
          <form @submit.prevent="addCar" novalidate>
            <!-- Campos del auto -->
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
              <input id="year" type="number" v-model.number="newCar.year" :min="1980" :max="currentYear + 1" required />
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
                <option value="PICKUP">Camioneta</option>
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
              <input id="price" type="number" v-model.number="newCar.pricePerDay" step="0.01" min="0" required
                placeholder="Ej: 80.00" />
            </div>

            <div class="form-group">
              <label for="plate">Placa</label>
              <input id="plate" type="text" v-model.trim="newCar.licensePlate" required placeholder="Ej: ABC123" />
            </div>

            <!-- 📸 Fotos -->
            <div class="card mb-2">
              <h3 class="section-title">Fotos del auto</h3>
              <div class="form-group">
                <label for="photos">Selecciona Imagenes</label>
                <input id="photos" type="file" accept="image/*" multiple @change="onPickPhotos" />
                <small>(JPG/PNG/WEBP; máx. 5 MB c/u)</small>
              </div>

              <div v-if="photos.length" class="thumbs">
                <div v-for="(p, i) in photos" :key="i" class="thumb">
                  <img :src="p.preview" :alt="p.file.name" />
                  <div class="thumb-meta">
                    <span class="name" :title="p.file.name">“{{ p.file.name }}” ({{ prettySize(p.file.size) }})</span>
                    <label class="cover">
                      <input type="radio" name="cover" :checked="p.cover" @change="setCover(i)" /> Portada
                    </label>
                    <button class="btn btn-secondary btn-xs" type="button" @click="removePhoto(i)">Quitar</button>
                  </div>
                </div>
              </div>

              <div v-if="uploading" class="mt-2" aria-live="polite">Subiendo fotos…</div>
            </div>

            <div class="mt-2">
              <button class="btn btn-primary" type="submit" :disabled="isSubmitting || uploading">
                {{ isSubmitting ? 'Guardando…' : submitLabel }}
              </button>
            </div>
          </form>
        </div>

        <!-- Si no es ADMIN, mostrar mensaje y cerrar -->
        <div v-else class="card">
          <h3 class="section-title">Acceso restringido</h3>
          <p>
            Solo los administradores pueden agregar o editar autos. Si eres cliente, para reservar un auto utiliza
            la ficha del vehículo en el listado.
          </p>
          <div class="mt-2">
            <button class="btn btn-primary" type="button" @click="$emit('close')">Cerrar</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'AddCarModal',
  emits: ['close', 'car-added', 'car-updated', 'show-alert'],
  props: {
    userInfo: { type: Object, required: true },
    car: { type: Object, required: false } // nuevo: prop opcional para editar
  },
  data() {
    return {
      isSubmitting: false,
      uploading: false,
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
      },
      photos: [] // [{ file, preview, cover:false }]
    }
  },
  computed: {
    // Nuevo: determina si el usuario es administrador
    isAdmin() {
      return String(this.userInfo?.role || '').toUpperCase() === 'ADMIN'
    },
    // Modo edición
    isEdit() {
      return !!(this.car && this.car.id)
    },
    submitLabel() {
      return this.isEdit ? 'Guardar cambios' : 'Agregar Auto'
    }
  },
  watch: {
    // Si se pasa una car para editar, poblar el formulario
    car: {
      immediate: true,
      handler(c) {
        if (c) {
          this.newCar = {
            brand: c.brand || '',
            model: c.model || '',
            year: c.year || new Date().getFullYear(),
            color: c.color || '',
            category: c.category || 'SEDAN',
            status: c.status || 'AVAILABLE',
            fuelType: c.fuelType || 'GASOLINE',
            transmissionType: c.transmissionType || 'MANUAL',
            pricePerDay: c.pricePerDay ?? c.price ?? 50,
            licensePlate: c.licensePlate || c.plate || ''
          }
          // photos are not preloaded; keep photos array empty (could be extended)
        } else {
          // reset if no car
          this.resetForm()
        }
      }
    }
  },
  methods: {
    /* ======== Fotos ======== */
    onPickPhotos(e) {
      const files = Array.from(e.target.files || [])
      const max = 5 * 1024 * 1024
      for (const f of files) {
        if (!/^image\//.test(f.type) || f.size > max) continue
        this.photos.push({
          file: f,
          preview: URL.createObjectURL(f),
          cover: false
        })
      }
      e.target.value = ''
    },
    removePhoto(i) {
      const p = this.photos[i]
      if (p?.preview) URL.revokeObjectURL(p.preview)
      this.photos.splice(i, 1)
    },
    setCover(i) {
      this.photos.forEach((p, idx) => (p.cover = idx === i))
    },
    prettySize(bytes) {
      if (bytes < 1024) return bytes + ' B'
      if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
      return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
    },

    /* ======== Subida directa al backend ======== */
    async uploadMultipart(carId) {
      const fd = new FormData()
      this.photos.forEach(p => fd.append('file', p.file))
      const res = await api.post(`/api/cars/${carId}/photos`, fd, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      console.log('Respuesta de subida de fotos:', res)
    },

    /* ======== Crear o Actualizar auto ======== */
    async addCar() {
      if (!this.newCar.brand || !this.newCar.model || !this.newCar.licensePlate)
        return this.$emit('show-alert', 'error', 'Completa los campos obligatorios')

      const ownerId = this.userInfo?.id || localStorage.getItem('userId')
      if (!ownerId)
        return this.$emit('show-alert', 'error', 'No se pudo determinar el propietario (ownerId)')

      this.isSubmitting = true
      try {
        const payload = { ...this.newCar, ownerId }

        let resp, car, carId
        if (this.isEdit) {
          // PUT para actualización
          carId = this.car.id
          resp = await api.put(`/api/cars/${carId}`, payload, { validateStatus: s => [200, 204].includes(s) })
          // intentar obtener objeto actualizado desde respuesta si viene
          car = resp.data || { id: carId, ...payload }
        } else {
          // Crear auto
          resp = await api.post('/api/cars', payload, { validateStatus: s => [200, 201].includes(s) })
          car = resp.data || {}
          carId = car.id
          if (!carId) {
            const loc = resp.headers?.location
            carId = loc ? loc.split('/').pop() : null
            if (!carId) throw new Error('No se obtuvo el ID del auto')
          }
        }

        // Subida de fotos
        if (this.photos.length) {
          try {
            if (!this.photos.some(p => p.cover)) this.photos[0].cover = true
            this.uploading = true
            await this.uploadMultipart(carId)
            this.$emit('show-alert', 'success', 'Fotos subidas correctamente')
          } catch (err) {
            console.error('Error subiendo fotos:', err)
            this.$emit('show-alert', 'error', this.isEdit ? 'Auto editado, pero no se pudieron subir las fotos' : 'Auto creado, pero no se pudieron subir las fotos')
          } finally {
            this.uploading = false
          }
        }

        if (this.isEdit) {
          this.$emit('car-updated', { id: carId, ...car })
          this.$emit('show-alert', 'success', 'Auto actualizado correctamente')
        } else {
          this.$emit('car-added', car)
          this.$emit('show-alert', 'success', 'Auto agregado correctamente')
        }

        this.resetForm()
        this.photos.forEach(p => p.preview && URL.revokeObjectURL(p.preview))
        this.photos = []
        this.$emit('close')
      } catch (err) {
        console.error(err)
        this.$emit('show-alert', 'error', this.isEdit ? 'Error al actualizar auto' : 'Error al agregar auto')
      } finally {
        this.isSubmitting = false
        this.uploading = false
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

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, .5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.modal {
  width: 100%;
  max-width: 720px;
  background: #111827;
  color: #fff;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, .35);
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: .5rem;
}

.section-title {
  margin: 0;
}

.modal-body {
  flex: 1;
  max-height: 80vh;
  overflow-y: auto;
  padding-right: 6px;
}

.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-thumb {
  background: #4b5563;
  border-radius: 4px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: #6b7280;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: .25rem;
  margin-bottom: .5rem;
}

.card {
  background: #1f2937;
  border: 1px solid #374151;
  border-radius: 10px;
  padding: 12px;
}

.mb-2 {
  margin-bottom: .75rem;
}

.mt-2 {
  margin-top: .75rem;
}

.btn {
  border: 0;
  border-radius: 8px;
  padding: .5rem .75rem;
  cursor: pointer;
}

.btn-xs {
  padding: .25rem .5rem;
  font-size: .8rem;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-secondary {
  background: #374151;
  color: #fff;
}

.thumbs {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: .5rem;
}

.thumb {
  border: 1px solid #374151;
  border-radius: 8px;
  overflow: hidden;
  background: #0b1220;
}

.thumb img {
  width: 100%;
  height: 110px;
  object-fit: cover;
  display: block;
}

.thumb-meta {
  display: flex;
  align-items: center;
  gap: .5rem;
  justify-content: space-between;
  padding: .35rem .4rem;
}

.thumb-meta .name {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: .85rem;
  opacity: .9;
}

.cover {
  display: flex;
  align-items: center;
  gap: .25rem;
  font-size: .85rem;
  opacity: .9;
}
</style>
