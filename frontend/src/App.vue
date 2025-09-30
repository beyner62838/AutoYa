<template>
  <div id="app">
    <div class="container">
      <NavBar
        :is-authenticated="isAuthenticated"
        :user-info="userInfo"
        @logout="logout"
        @navigate="navigate"
      />
      <div v-if="alert.show" :class="['alert', `alert-${alert.type}`]">
        {{ alert.message }}
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
      alert: { show: false, type: '', message: '' }
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
    showAlert(type, message) {
      this.alert = { show: true, type, message }
      setTimeout(() => { this.alert.show = false }, 5000)
    },
    onAuthChanged(payload) {
      this.isAuthenticated = payload.isAuthenticated
      if (payload.userInfo) this.userInfo = payload.userInfo
    }
  }
}
</script>
