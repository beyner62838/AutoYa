<template>
  <div id="app">
    <div class="container">
      <NavBar
        :is-authenticated="isAuthenticated"
        :user-info="userInfo"
        @logout="logout"
        @navigate="navigate"
      />
      <!-- ALERTA GLOBAL -->
      <div v-if="alert.show" :class="['site-alert', alert.type]" role="alert" aria-live="assertive">
        <div class="site-alert-inner">
          <div class="site-alert-icon" aria-hidden="true">
            <span v-if="alert.type === 'success'">✓</span>
            <span v-else-if="alert.type === 'error'">⚠</span>
            <span v-else>ℹ</span>
          </div>
          <div class="site-alert-text">
            <div class="site-alert-msg" v-html="alert.message"></div>
          </div>
          <button class="site-alert-close" @click="alert.show = false" aria-label="Cerrar alerta">✕</button>
        </div>
      </div>
      <router-view @show-alert="showAlert" @auth-changed="onAuthChanged" />
    </div>
  </div>
</template>

<script>
import NavBar from './components/NavBar.vue'
import api from './services/api'

export default {
  name: 'App',
  components: { NavBar },
  data() {
    return {
      isAuthenticated: !!localStorage.getItem('token'),
      userInfo: null,
      alert: { show: false, type: '', message: '', timeoutId: null }
    }
  },
  mounted() {
    if (this.isAuthenticated) {
      this.getUserInfo()
    }
  },
  methods: {
    async getUserInfo() {
      try {
        const resp = await api.get('/auth/hello')
        this.userInfo = resp.data
      } catch (err) {
        console.error(err)
      }
    },
    logout() {
      localStorage.removeItem('token')
      this.isAuthenticated = false
      this.userInfo = null
      this.$router.push('/login')
      this.showAlert('info', 'Sesión cerrada')
    },
    navigate(route) {
      this.$router.push(route)
    },
    showAlert(type, message, ms = 4500) {
      if (this.alert.timeoutId) {
        clearTimeout(this.alert.timeoutId)
        this.alert.timeoutId = null
      }
      this.alert = { ...this.alert, show: true, type, message }
      this.alert.timeoutId = setTimeout(() => { this.alert.show = false }, ms)
    },
    onAuthChanged(payload) {
      this.isAuthenticated = payload.isAuthenticated
      if (payload.userInfo) this.userInfo = payload.userInfo
    }
  }
}
</script>
