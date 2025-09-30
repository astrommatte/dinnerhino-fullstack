<template>
  <header>
    <div class="wrapper">
      <nav>
        <RouterLink to="/recipes">Recept</RouterLink>
        <RouterLink to="/myrecipes">Mina Recept</RouterLink>
        <RouterLink to="/shoppinglist">Handlingslista</RouterLink>
        <RouterLink to="/admin">Admin</RouterLink>
        <RouterLink to="/login">Logga ut</RouterLink>
      </nav>
    </div>
  </header>
</template>

<script lang="js">
    import { RouterLink } from 'vue-router'
</script>
