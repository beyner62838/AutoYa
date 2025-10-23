<template>
  <div class="page-bg register-bg">
    <div class="register-card card" role="dialog" aria-labelledby="register-title">

      <!-- IZQUIERDA -->
      <div class="left">
        <div class="brand-row">
          <span class="brand-icon">🚗</span>
          <h1 class="brand-name" id="register-title">AutoYa</h1>
        </div>
        <p class="tagline">Crea tu cuenta y arranca con estilo.</p>

        <div class="social under-brand">
          <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
          <a href="#" aria-label="Twitter/X"><i class="fab fa-twitter"></i></a>
          <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
        </div>
      </div>

      <!-- FORMULARIO -->
      <form class="form form-dark" @submit.prevent="register" :class="{ loading: loading }">
        <h2>Registrarse</h2>

        <div class="form-grid">
          <div class="form-group">
            <label for="firstname">Nombre</label>
            <input
                id="firstname"
                v-model="registerForm.firstname"
                required
                autocomplete="given-name"
                pattern="[A-Za-zÀ-ÿ\\s]+"
                title="Solo se permiten letras y espacios"
            />
          </div>

          <div class="form-group">
            <label for="lastname">Apellido</label>
            <input
                id="lastname"
                v-model="registerForm.lastname"
                required
                autocomplete="family-name"
                pattern="[A-Za-zÀ-ÿ\\s]+"
                title="Solo se permiten letras y espacios"
            />
          </div>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input
                id="phone"
                v-model="registerForm.phone"
                required
                autocomplete="tel"
                pattern="\\d{7,15}"
                title="Solo se permiten números (mínimo 7 dígitos)"
            />
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input
                id="email"
                type="email"
                v-model="registerForm.email"
                required
                autocomplete="email"
                title="Ingresa un correo electrónico válido"
            />
          </div>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
                id="password"
                type="password"
                v-model="registerForm.password"
                required
                minlength="6"
                autocomplete="new-password"
                title="Debe tener al menos 6 caracteres"
            />
          </div>
          <div class="form-group">
            <label for="role">Rol</label>
            <select id="role" v-model="registerForm.role">
              <option value="CLIENT">Cliente</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>
        </div>

        <div class="actions">
          <button class="btn btn-primary" type="submit" :disabled="loading">
            <span v-if="loading" class="loader"></span>
            <span v-else>Registrarse</span>
          </button>
          <button class="btn btn-secondary" type="button" @click="$router.push('/login')">
            Ya tengo cuenta
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'RegisterView',
  emits: ['show-alert'],
  data() {
    return {
      loading: false,
      registerForm: {
        firstname: '',
        lastname: '',
        phone: '',
        email: '',
        password: '',
        role: 'CLIENT'
      }
    }
  },
  methods: {
    async register() {
      // === VALIDACIONES MANUALES ===
      if (!/^[A-Za-zÀ-ÿ\s]+$/.test(this.registerForm.firstname)) {
        this.$emit('show-alert', 'error', 'El nombre solo puede contener letras.')
        return
      }
      if (!/^[A-Za-zÀ-ÿ\s]+$/.test(this.registerForm.lastname)) {
        this.$emit('show-alert', 'error', 'El apellido solo puede contener letras.')
        return
      }
      if (!/^\d{10}$/.test(this.registerForm.phone)) {
        this.$emit('show-alert', 'error', 'El teléfono debe tener 10 dígitos numéricos.')
        return
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.registerForm.email)) {
        this.$emit('show-alert', 'error', 'El correo electrónico no es válido.')
        return
      }
      if (this.registerForm.password.length < 4) {
        this.$emit('show-alert', 'error', 'La contraseña debe tener al menos 4 caracteres.')
        return
      }

      // === ENVÍO AL BACKEND ===
      this.loading = true
      try {
        await api.post('/auth/register', this.registerForm)
        this.$emit('show-alert', 'success', 'Registro exitoso. Inicia sesión.')
        this.$router.push('/login')
      } catch (err) {
        console.error(err)
        this.$emit('show-alert', 'error', 'Error al registrarse.')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.form-dark input,
.form-dark select {
  background: #1e293b;
  border: 1px solid rgba(148, 163, 184, 0.25);
  color: #0659ff;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 1rem;
  transition: all 0.25s;
}
.form-dark input:focus,
.form-dark select:focus {
  outline: none;
  border-color: #60a5fa;
  box-shadow: 0 0 0 5px rgba(99, 102, 241, 0.25);
  background: #273549;
}
.form-dark label {
  color: #cbd5e1;
  font-weight: 700;
  margin-bottom: 4px;
}
.form-dark select option {
  background-color: #1e293b;
  color: #e5e7eb;
}
.form-dark input::placeholder {
  color: #9ca3af;
}
.form-dark h2 {
  color: #c7d2fe;
  font-weight: 800;
  margin-bottom: 8px;
}
.hint a {
  color: #93c5fd;
  text-decoration: underline;
}
.hint a:hover {
  color: #fff;
}
</style>
