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
            <input id="firstname" v-model="registerForm.firstname" required autocomplete="given-name" />
          </div>
          <div class="form-group">
            <label for="lastname">Apellido</label>
            <input id="lastname" v-model="registerForm.lastname" required autocomplete="family-name" />
          </div>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input id="phone" v-model="registerForm.phone" required autocomplete="tel" />
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" v-model="registerForm.email" required autocomplete="email" />
          </div>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label for="password">Contraseña</label>
            <input id="password" type="password" v-model="registerForm.password" required autocomplete="new-password" />
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
    validarRegistro(){
      const f=this.registerForm;
      const name=/^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$/;
      const phone=/^\d{10}$/;
      const email=/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
      if(!name.test(f.firstname||'')) return this.$emit('show-alert','error','El nombre solo debe contener letras'),false;
      if(!name.test(f.lastname||'')) return this.$emit('show-alert','error','El apellido solo debe contener letras'),false;
      if(!phone.test(f.phone||'')) return this.$emit('show-alert','error','El teléfono debe tener 10 dígitos y solo datos numericos'),false;
      if(!email.test(f.email||'')) return this.$emit('show-alert','error','Ingrese un correo válido'),false;
      if(!f.password || f.password.length<6) return this.$emit('show-alert','error','La contraseña debe tener mínimo 6 caracteres'),false;
      return true;
    },
    async register() {
      if(!this.validarRegistro()) return;
      this.loading = true
      try {
        await api.post('/auth/register', this.registerForm)
        this.$emit('show-alert', 'success', 'Registro exitoso. Inicia sesión.')
        this.$router.push('/login')
      } catch (err) {
        console.error(err)
        this.$emit('show-alert', 'error', 'Error al registrarse')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
/* ==== Modo oscuro del formulario ==== */
.form-dark input,
.form-dark select {
  background: #1e293b; /* gris oscuro */
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
