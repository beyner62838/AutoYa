import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import CarsView from '../views/CarsView.vue'
import ReservationsView from '../views/ReservationsView.vue'
import PaymentsView from '../views/PaymentsView.vue'
import RatingSection from '../components/RatingSection.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/cars', component: CarsView },
  { path: '/reservations', component: ReservationsView },
  { path: '/payments', component: PaymentsView },
  { path: '/rating', component: RatingSection }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
