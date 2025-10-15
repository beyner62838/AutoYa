<template>
  <div class="page-bg login-bg">
    <!-- Toast de bienvenida -->
    <transition name="fade">
      <div v-if="showWelcome" class="welcome-toast" role="alert">
        <div class="toast-row">
          <span class="toast-icon">🚗</span>
          <div class="toast-text">
            <strong>Bienvenido de nuevo</strong>
            <small>Alquila, reserva y arranca.</small>
          </div>
          <button class="toast-close" @click="closeWelcome" aria-label="Cerrar aviso">×</button>
        </div>
      </div>
    </transition>

    <!-- Card principal -->
    <div class="login-card card" role="dialog" aria-labelledby="login-title">
      <!-- LADO IZQUIERDO -->
      <div class="left">
        <div class="brand-row">
          <span class="brand-icon">🚗</span>
          <h1 class="brand-name" id="login-title">AutoYa</h1>
        </div>

        <p class="tagline">Tu viaje empieza aquí.</p>

        <div class="social under-brand">
          <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
          <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
          <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
        </div>
      </div>

      <!-- FORMULARIO -->
      <form class="form" @submit.prevent="handleLogin" :class="{ 'loading': isLoading }">
        <h2 class="form-title">Iniciar Sesión</h2>

        <input v-model="email" type="email" placeholder="Correo electrónico" required autocomplete="email" />
        <input v-model="password" type="password" placeholder="Contraseña" required autocomplete="current-password" />

        <label class="remember">
          <input type="checkbox" v-model="remember" />
          Recuérdame
        </label>

        <button type="submit" :disabled="isLoading" class="btn btn-primary">
          <span v-if="!isLoading">Entrar</span>
          <span v-else class="loader"></span>
        </button>

        <a class="forgot" href="#">¿Olvidaste tu contraseña?</a>

        <p v-if="error" class="error">{{ error }}</p>
        <p class="terms">Al iniciar sesión aceptas nuestros <a href="#">Términos</a> y <a href="#">Privacidad</a>.</p>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
import '../assets/styles.css'

export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      remember: false,
      isLoading: false,
      error: '',
      showWelcome: true
    }
  },
  mounted() {
    if (sessionStorage.getItem('autoya_welcome_dismissed') === '1') {
      this.showWelcome = false
    }
  },
  methods: {
    closeWelcome() {
      this.showWelcome = false
      sessionStorage.setItem('autoya_welcome_dismissed', '1')
    },
    async handleLogin() {
      this.isLoading = true
      this.error = ''
      try {
        const { data } = await api.post('/auth/login', { email: this.email, password: this.password })
        const token = data.token
        localStorage.setItem('token', token)

        const userResp = await api.get('/auth/hello', {
          headers: { Authorization: `Bearer ${token}` }
        })

        this.$emit('auth-changed', { isAuthenticated: true, userInfo: userResp.data })
        this.$router.push('/cars')
        this.$emit('show-alert', 'success', 'Inicio de sesión exitoso')
      } catch (e) {
        this.error = 'Credenciales incorrectas. Intenta de nuevo.'
        this.$emit('show-alert', 'error', 'Error al iniciar sesión')
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>
