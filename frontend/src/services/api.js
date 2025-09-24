import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE || 'http://localhost:8080/autoya'

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// attach token if present in localStorage
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  } else {
    delete config.headers.Authorization
  }
  return config
})

// Optional: helper to update token in localStorage
export function setApiToken(token) {
  localStorage.setItem('token', token)
}

// Ejemplo de uso con Axios:
api.get('/api/cars')

export default api
