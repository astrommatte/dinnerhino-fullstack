import axios from 'axios'
import { setupCache } from 'axios-cache-adapter'

// Skapa cache med TTL på 15 minuter
const cache = setupCache({
  maxAge: 15 * 60 * 1000,
  // store: localStorage // om du vill spara cache mellan sessions
})

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  adapter: cache.adapter // Viktigt: använd cache-adaptern här!
})

// Auth header interceptor
instance.interceptors.request.use(config => {
  const auth = localStorage.getItem('auth')
  if (auth) {
    config.headers['Authorization'] = auth
  }
  return config
})

export default instance