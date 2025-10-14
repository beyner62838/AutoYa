<template>
  <div class="register-bg">
    <div class="register-container">
      <form class="register-form" @submit.prevent="register">
        <h2>Registrarse</h2>
        <div class="fields">
          <div>
            <label>Nombre</label>
            <input v-model="registerForm.firstname" required />
          </div>
          <div>
            <label>Apellido</label>
            <input v-model="registerForm.lastname" required />
          </div>
        </div>
        <div class="fields">
          <div>
            <label>Teléfono</label>
            <input v-model="registerForm.phone" required />
          </div>
          <div>
            <label>Email</label>
            <input type="email" v-model="registerForm.email" required />
          </div>
        </div>
        <div class="fields">
          <div>
            <label>Contraseña</label>
            <input type="password" v-model="registerForm.password" required />
          </div>
          <div>
            <label>Rol</label>
            <select v-model="registerForm.role">
              <option value="CLIENT">Cliente</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>
        </div>
        <button class="primary" type="submit" :disabled="loading">
          <span v-if="loading" class="loader"></span>
          <span v-else>Registrarse</span>
        </button>
      </form>
    </div>
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

<style scoped>
.register-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #6366f1 0%, #a5b4fc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 0;
}
.register-container {
  background: #fff;
  border-radius: 1.5rem;
  box-shadow: 0 12px 40px 0 #6366f133;
  padding: 2.5rem 2rem;
  min-width: 340px;
  max-width: 420px;
  width: 100%;
  animation: fadeInUp 0.7s;
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}
.register-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}
.register-form h2 {
  color: #6366f1;
  font-weight: 700;
  text-align: center;
  margin-bottom: 0.5rem;
}
.fields {
  display: flex;
  gap: 1.2rem;
}
.fields > div {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
.register-form label {
  font-weight: 500;
  color: #312e81;
}
.register-form input,
.register-form select {
  border: none;
  border-bottom: 2px solid #e5e7eb;
  padding: 0.8rem;
  font-size: 1rem;
  outline: none;
  background: transparent;
  transition: border-color 0.2s;
  border-radius: 0.3rem 0.3rem 0 0;
}
.register-form input:focus,
.register-form select:focus {
  border-color: #6366f1;
}
.primary {
  background: linear-gradient(90deg, #6366f1 60%, #a5b4fc 100%);
  color: #fff;
  border: none;
  padding: 0.9rem;
  border-radius: 0.7rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
  overflow: hidden;
  font-weight: 600;
}
.primary:disabled {
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
@media (max-width: 500px) {
  .register-container {
    padding: 1.2rem 0.5rem;
    border-radius: 1rem;
    min-width: unset;
    max-width: 98vw;
  }
  .fields {
    flex-direction: column;
    gap: 0.7rem;
  }
}
</style>
