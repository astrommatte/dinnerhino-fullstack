<template>
      <Accordion multiple class="accordion-card">
        <AccordionTab
        v-for="recipe in sortedRecipeList"
        :key="recipe.id"
        >
        <template #header>
            <div>
                <div>{{ recipe.name }}</div>
                <small>
                    <h5>
                        Skriven av: {{ recipe.createdByUsername || 'Okänd' }}<br>
                    </h5>
                    <h5>
                        Användar Id: {{ recipe.createdById || 'Okänd' }}<br>
                    </h5>
                </small>
            </div>
        </template>

        <!-- Innehåll -->
        <p class="mt-2">{{ recipe.description }}</p>
              <h4>Ingredienser:</h4>
        <ul>
            <li
            v-for="(ingredient, index) in recipe.ingredients"
            :key="index"
            >
            {{ ingredient.name }} - {{ ingredient.quantity }}
            </li>
        </ul>

        </AccordionTab>
    </Accordion>
</template>

<script setup>
import { computed } from 'vue'
import { useRecipeStore } from '@/stores/useRecipeStore'
import Accordion from 'primevue/accordion'
import AccordionTab from 'primevue/accordiontab'

const recipeStore = useRecipeStore()

const sortedRecipeList = computed(() => {
  return [...recipeStore.recipes].sort((a, b) =>
    a.name.localeCompare(b.name, 'sv')
  )
})

</script>

<style>
.accordion-card{
    margin-top: 100px;
    padding: 10px;
}
</style>