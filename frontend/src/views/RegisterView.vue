<template>
  <div class="card">
    <h2 style="margin-bottom:30px">Registrarse</h2>
    <form @submit.prevent="register">
      <div class="form-group"><label>Nombre</label><input v-model="registerForm.firstname" required /></div>
      <div class="form-group"><label>Apellido</label><input v-model="registerForm.lastname" required /></div>
      <div class="form-group"><label>Teléfono</label><input v-model="registerForm.phone" required /></div>
      <div class="form-group"><label>Email</label><input type="email" v-model="registerForm.email" required /></div>
      <div class="form-group"><label>Contraseña</label><input type="password" v-model="registerForm.password" required /></div>
      <div class="form-group"><label>Rol</label>
        <select v-model="registerForm.role"><option value="CLIENT">Cliente</option><option value="ADMIN">Administrador</option></select>
      </div>
      <button class="btn btn-primary" type="submit" :disabled="loading">
        <span v-if="loading" class="loading"></span>
        <span v-else>Registrarse</span>
      </button>
    </form>
  </div>
</template>

<script>
import api from '../services/api'
export default {
  name: 'RegisterView',
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
      this.loading = true
      try {
        await api.post('/auth/register', this.registerForm)
        this.$emit('show-alert', 'success', 'Registro exitoso. Inicia sesión.')
        this.$router.push('/login')
      } catch (err) {
        this.$emit('show-alert', 'error', 'Error al registrarse')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
