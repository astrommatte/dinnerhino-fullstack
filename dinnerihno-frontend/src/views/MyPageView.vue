<template>
  <h2 class="title-text">Mina sidor</h2>
  <div class="card">
    <MyPageCard/>
  </div>
</template>

<script setup>
    import MyPageCard from '@/components/MyPageCard.vue'
</script>

<style scoped>
  .card {
      display: flex;
      justify-content: center;
      max-width: auto;
      margin: 1rem;
      padding: 2rem;
      /* border-radius: 8px;
      box-shadow: 0 0 10px #ccc; */
  }
</style>
