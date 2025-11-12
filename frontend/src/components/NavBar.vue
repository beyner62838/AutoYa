<template>
  <nav class="navbar">
    <!-- Marca -->
    <div class="brand">
      🚗 <span>AutoYa</span>
    </div>

    <!-- No autenticado -->
    <div v-if="!isAuthenticated" class="navlinks">
      <router-link to="/login" class="btn btn-primary">Iniciar Sesión</router-link>
      <router-link to="/register" class="btn btn-secondary">Registrarse</router-link>
      <!-- Si prefieres borde en lugar de relleno:
      <router-link to="/register" class="btn btn-outline">Registrarse</router-link>
      -->
    </div>

    <!-- Autenticado -->
    <div v-else class="navlinks">
      <router-link to="/cars" class="navlink" active-class="active">Autos</router-link>
      <router-link to="/reservations" class="navlink" active-class="active">Reservas</router-link>
      <router-link to="/payments" class="navlink" active-class="active">Pagos</router-link>

      <div class="user">
        <div class="avatar">{{ (userInfo?.firstname?.[0] || 'U').toUpperCase() }}</div>
        <span class="name">
          {{ userInfo?.firstname }} {{ userInfo?.lastname }}
        </span>
        <button class="btn btn-danger" type="button" @click="handleLogout">Cerrar Sesión</button>
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
