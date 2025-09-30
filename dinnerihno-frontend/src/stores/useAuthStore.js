import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: null, // t.ex. { username: 'admin', roles: ['ROLE_USER', 'ROLE_ADMIN'] }
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.roles.includes('ROLE_ADMIN'),
  },
  actions: {
    async login(username, password) {
      const res = await axios.post('/api/auth/login', { username, password })
      this.token = res.data.token
      localStorage.setItem('token', this.token)

      await this.fetchUserProfile()
    },

    async fetchUserProfile() {
      const res = await axios.get('/api/users/me', {
        headers: { Authorization: `Bearer ${this.token}` }
      })
      this.user = res.data
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
    }
  }
})
