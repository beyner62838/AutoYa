<template>
  <div class="login-bg">
    <div class="overlay"></div>
    <div class="login-content">
      <div class="welcome">
        <h1>Bienvenido de nuevo</h1>
        <p>Ingresa tus datos para acceder a AutoYa.</p>
        <div class="social">
          <a href="#"><i class="fab fa-facebook-f"></i></a>
          <a href="#"><i class="fab fa-twitter"></i></a>
          <a href="#"><i class="fab fa-instagram"></i></a>
        </div>
      </div>
      <form class="login-form" @submit.prevent="handleLogin" :class="{ 'loading': isLoading }">
        <h2>Iniciar sesión</h2>
        <input v-model="email" type="email" placeholder="Correo electrónico" required />
        <input v-model="password" type="password" placeholder="Contraseña" required />
        <div class="remember">
          <label>
            <input type="checkbox" v-model="remember" />
            Recuérdame
          </label>
        </div>
        <button type="submit" :disabled="isLoading">
          <span v-if="!isLoading">Entrar</span>
          <span v-else class="loader"></span>
        </button>
        <a class="forgot" href="#">¿Olvidaste tu contraseña?</a>
        <p v-if="error" class="error">{{ error }}</p>
        <div class="terms">
          Al iniciar sesión aceptas nuestros <a href="#">Términos</a> y <a href="#">Privacidad</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      remember: false,
      isLoading: false,
      error: '',
      isAuthenticated: !!localStorage.getItem('token')
    };
  },
  methods: {
    async handleLogin() {
      this.isLoading = true;
      this.error = '';
      try {
        const resp = await api.post('/auth/login', { email: this.email, password: this.password })
        const token = resp.data.token
        localStorage.setItem('token', token)
        this.$emit('auth-changed', { isAuthenticated: true })
        // fetch user info and navigate
        const userResp = await api.get('/auth/hello')
        this.$emit('auth-changed', { isAuthenticated: true, userInfo: userResp.data })
        this.$router.push('/cars')
        this.$emit('show-alert', 'success', 'Inicio de sesión exitoso')
      } catch (err) {
        this.error = 'Credenciales incorrectas'
        this.$emit('show-alert', 'error', 'Error al iniciar sesión')
      } finally {
        this.isLoading = false
      }
    }
  }
};
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

.login-bg {
  position: relative;
  min-height: 100vh;
  background: url('https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80') center/cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
}
.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #6366f1cc 0%, #f8fafc99 100%);
  z-index: 1;
}
.login-content {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 3rem;
  background: rgba(255,255,255,0.85);
  border-radius: 1.2rem;
  box-shadow: 0 8px 32px rgba(60, 60, 120, 0.08);
  padding: 2.5rem 3rem;
  max-width: 700px;
  width: 100%;
  align-items: center;
}
.welcome {
  flex: 1;
  color: #6366f1;
  padding-right: 2rem;
}
.welcome h1 {
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}
.welcome p {
  color: #4b5563;
  margin-bottom: 1.2rem;
}
.social a {
  color: #6366f1;
  margin-right: 1rem;
  font-size: 1.3rem;
  transition: color 0.2s;
}
.social a:hover {
  color: #312e81;
}
.login-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.1rem;
  background: transparent;
  padding: 0;
}
.login-form h2 {
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #6366f1;
  text-align: left;
}
.login-form input[type="email"],
.login-form input[type="password"] {
  border: none;
  border-bottom: 2px solid #e5e7eb;
  padding: 0.8rem;
  font-size: 1rem;
  outline: none;
  background: transparent;
  transition: border-color 0.2s;
}
.login-form input:focus {
  border-color: #6366f1;
}
.remember {
  font-size: 0.95rem;
  color: #4b5563;
  display: flex;
  align-items: center;
}
.login-form button {
  background: #6366f1;
  color: #fff;
  border: none;
  padding: 0.9rem;
  border-radius: 0.7rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
  overflow: hidden;
  margin-top: 0.5rem;
}
.login-form button:disabled {
  background: #a5b4fc;
  cursor: not-allowed;
}
.loader {
  display: inline-block;
  width: 22px;
  height: 22px;
  border: 3px solid #fff;
  border-top: 3px solid #6366f1;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.forgot {
  color: #6366f1;
  font-size: 0.95rem;
  text-align: left;
  margin-top: 0.2rem;
  text-decoration: underline;
  cursor: pointer;
}
.error {
  color: #ef4444;
  font-size: 0.95rem;
  text-align: left;
}
.terms {
  font-size: 0.85rem;
  color: #6b7280;
  margin-top: 0.7rem;
}
.terms a {
  color: #6366f1;
  text-decoration: underline;
}
@media (max-width: 800px) {
  .login-content {
    flex-direction: column;
    gap: 1.5rem;
    padding: 2rem 1.2rem;
    max-width: 95vw;
  }
  .welcome {
    padding-right: 0;
    text-align: center;
  }
}
</style>
