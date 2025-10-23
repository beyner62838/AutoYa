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
      <form class="form" @submit.prevent="handleLogin" :class="{ 'loading': isLoading }" novalidate>
        <h2 class="form-title">Iniciar Sesión</h2>

        <input
          v-model="email"
          type="email"
          placeholder="Correo electrónico"
          required
          autocomplete="email"
          inputmode="email"
          @blur="email = email.trim(); emailTouched = true"
          @input="emailTouched = true"
          :aria-invalid="emailTouched && !isEmailValid"
          title="Ingresa un correo válido (ej: nombre@dominio.com)"
        />

        <input
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          placeholder="Contraseña"
          required
          autocomplete="current-password"
          minlength="6"
          @input="password = password.replace(/\s/g, ''); passwordTouched = true"
          :aria-invalid="passwordTouched && !isPasswordValid"
          title="Mínimo 6 caracteres, sin espacios"
        />

        <div class="row-between">
          <label class="show-pass">
            <input type="checkbox" v-model="showPassword" />
            Mostrar contraseña
          </label>
          <a class="forgot" href="#">¿Olvidaste tu contraseña?</a>
        </div>

        <button
          type="submit"
          :disabled="isLoading || !isFormValid"
          class="btn btn-primary"
          :aria-disabled="isLoading || !isFormValid"
        >
          <span v-if="!isLoading">Entrar</span>
          <span v-else class="loader"></span>
        </button>

        <p v-if="error" class="error" role="alert">{{ error }}</p>
        <p class="terms">
          Al iniciar sesión aceptas nuestros
          <a href="https://loremipsum-org.translate.goog/?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc">Términos</a>
          y
          <a href="https://loremipsum-org.translate.goog/?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc">Privacidad</a>.
        </p>
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
      isLoading: false,
      error: '',
      showWelcome: true,
      showPassword: false,
      emailTouched: false,
      passwordTouched: false,
      emailRegex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    }
  },
  computed: {
    isEmailValid() {
      return this.emailRegex.test(this.email)
    },
    isPasswordValid() {
      return this.password.length >= 6 && !/\s/.test(this.password)
    },
    isFormValid() {
      return this.isEmailValid && this.isPasswordValid
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
      this.error = ''

      // Validaciones previas
      if (!this.isEmailValid) {
        this.error = 'El correo electrónico no es válido.'
        this.$emit('show-alert', 'error', this.error)
        return
      }
      if (!this.isPasswordValid) {
        this.error = 'La contraseña debe tener al menos 6 caracteres y no contener espacios.'
        this.$emit('show-alert', 'error', this.error)
        return
      }

      this.isLoading = true
      try {
        const { data } = await api.post('/auth/login', {
          email: this.email.trim(),
          password: this.password
        })

        const token = data.token
        const userId = data.userId
        localStorage.setItem('token', token)
        localStorage.setItem('userId', userId)

        const userResp = await api.get('/auth/hello', {
          headers: { Authorization: `Bearer ${token}` }
        })

        this.$emit('auth-changed', { isAuthenticated: true, userInfo: userResp.data })
        this.$emit('show-alert', 'success', 'Inicio de sesión exitoso')
        await this.sleep(700)
        this.$router.push('/cars')
      } catch (e) {
        this.error = 'Credenciales incorrectas. Intenta de nuevo.'
        this.$emit('show-alert', 'error', 'Error al iniciar sesión')
      } finally {
        this.isLoading = false
      }
    },
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms))
    }
  }
}
</script>
