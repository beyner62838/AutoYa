<template>
  <div v-if="!isAuthenticated" class="card">
    <h2 style="margin-bottom:30px">Iniciar Sesión</h2>
    <form @submit.prevent="login">
      <div class="form-group"><label>Email</label><input type="email" v-model="loginForm.email" required /></div>
      <div class="form-group"><label>Contraseña</label><input type="password" v-model="loginForm.password" required /></div>
      <button class="btn btn-primary" type="submit" :disabled="loading">
        <span v-if="loading" class="loading"></span>
        <span v-else>Iniciar Sesión</span>
      </button>
    </form>
  </div>
  <div v-else class="card">
    <h2>Ya estás autenticado</h2>
    <button class="btn btn-primary" @click="$router.push('/cars')">Ir a Autos</button>
  </div>
</template>

<script>
import api from '../services/api'
export default {
  name: 'LoginView',
  data() {
    return {
      loginForm: { email: '', password: '' },
      loading: false,
      isAuthenticated: !!localStorage.getItem('token')
    }
  },
  methods: {
    async login() {
      this.loading = true
      try {
        const resp = await api.post('/auth/login', this.loginForm)
        const token = resp.data.token
        localStorage.setItem('token', token)
        this.$emit('auth-changed', { isAuthenticated: true })
        // fetch user info and navigate
        const userResp = await api.get('/auth/hello')
        this.$emit('auth-changed', { isAuthenticated: true, userInfo: userResp.data })
        this.$router.push('/cars')
        this.$emit('show-alert', 'success', 'Inicio de sesión exitoso')
      } catch (err) {
        this.$emit('show-alert', 'error', 'Error al iniciar sesión')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
