<template>
  <Navbar v-if="!isLoginPage" />
  <router-view />
</template>

<script setup>
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'

const route = useRoute()
const isLoginPage = route.path === '/login'
</script>
