<template>
  <nav class="navbar">
    <div class="nav-brand">🚗 AutoYa</div>
    <div class="nav-links" v-if="!isAuthenticated">
      <button class="nav-link" @click="$router.push('/login')">Iniciar Sesión</button>
      <button class="nav-link" @click="$router.push('/register')">Registrarse</button>
    </div>
    <div class="nav-links" v-else>
      <button class="nav-link" :class="{ active: $route.path === '/cars' }" @click="$router.push('/cars')">Autos</button>
      <button class="nav-link" :class="{ active: $route.path === '/reservations' }" @click="$router.push('/reservations')">Reservas</button>
      <button class="nav-link" :class="{ active: $route.path === '/payments' }" @click="$router.push('/payments')">Pagos</button>
      <div class="user-profile" style="display:flex;align-items:center;gap:12px;">
        <div class="user-avatar">{{ (userInfo?.firstname?.[0] || 'U') }}</div>
        <span>{{ userInfo?.firstname }} {{ userInfo?.lastname }}</span>
        <button class="btn btn-secondary" @click="handleLogout">Cerrar Sesión</button>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'NavBar',
  props: {
    isAuthenticated: { type: Boolean, default: false },
    userInfo: { type: Object, default: null }
  },
  methods: {
    handleLogout() {
      this.$emit('logout')
    }
  }
}
</script>
