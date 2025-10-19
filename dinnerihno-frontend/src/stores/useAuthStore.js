import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import router from '@/router'
import { useToaster } from '@/stores/useToastStore.js'

export const useAuthStore = defineStore('auth', () => {
  const auth = ref(localStorage.getItem('auth') || null)
  const user = ref(localStorage.getItem('currentUser') ? JSON.parse(localStorage.getItem('currentUser')) : null)
  const { showErrorToast, showSuccessToast } = useToaster()
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

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

  const encodeBase64Utf8 = (str) => {
    const bytes = new TextEncoder().encode(str)   // konverterar till UTF-8 bytes
    let binary = ''
    bytes.forEach((b) => binary += String.fromCharCode(b))
    return btoa(binary)
  }
  
  const login = async (values) => {
    try {
      const authHeader = 'Basic ' + encodeBase64Utf8(`${values.username}:${values.password}`)
  
      const meRes = await axios.get(`${apiUrl}/api/auth/me`, {
        headers: { Authorization: authHeader }
      })
  
      user.value = meRes.data
  
      localStorage.setItem('auth', authHeader)
      localStorage.setItem('currentUser', JSON.stringify(meRes.data))
  
      window.dispatchEvent(new CustomEvent('auth-change', { detail: meRes.data }))
      showSuccessToast('Du är nu inloggad!')
      router.push('/recipes')
    } catch (err) {
      showErrorToast('Inloggning misslyckad')
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

