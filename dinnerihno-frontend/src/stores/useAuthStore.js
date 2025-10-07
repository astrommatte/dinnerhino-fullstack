import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import router from '@/router'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'


export const useAuthStore = defineStore('auth', () => {
  const auth = ref(localStorage.getItem('auth') || null)
  const user = ref(localStorage.getItem('currentUser') ? JSON.parse(localStorage.getItem('currentUser')) : null)

  const isLoggedIn = computed(() => !!auth.value)
  const isAdmin = computed(() => user.value?.roles?.includes('ROLE_ADMIN'))

  const setUser = (userData) => {
    user.value = userData
  }
  
  const setAuth = (authHeader, userData) => {
    auth.value = authHeader
    user.value = userData

    localStorage.setItem('auth', authHeader)
    localStorage.setItem('currentUser', JSON.stringify(userData))
  }

  const logout = () => {
    auth.value = null
    user.value = null
    localStorage.removeItem('auth')
    localStorage.removeItem('currentUser')
  }

  const login = async (values) => {
  try {
    const authHeader = 'Basic ' + btoa(`${values.email}:${values.password}`)

    const meRes = await axios.get(`${apiUrl}/api/auth/me`, {
      headers: { Authorization: authHeader }
    })

    user.value = meRes.data

    // Om vi kommer hit = OK, spara auth och navigera
    localStorage.setItem('auth', authHeader)
    localStorage.setItem('currentUser', JSON.stringify(meRes.data))

    window.dispatchEvent(new CustomEvent('auth-change', { detail: meRes.data }))

    router.push('/recipes')  // Navigera först efter lyckad login

  } catch (err) {
    // Hantera fel, t.ex. visa felmeddelande
    console.error('Inloggning misslyckades', err)
    alert('Felaktigt användarnamn eller lösenord')
  }
}

  return {
    auth,
    user,
    isLoggedIn,
    isAdmin,
    setAuth,
    logout,
    login,
    setUser,
  }
})

