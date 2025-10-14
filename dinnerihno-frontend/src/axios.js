import axios from 'axios'

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