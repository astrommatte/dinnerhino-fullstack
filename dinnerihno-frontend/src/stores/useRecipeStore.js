// stores/useRecipeStore.js
import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import jsPDF from 'jspdf';
import { useShoppingListStore } from './useShoppingListStore';

export const useRecipeStore = defineStore('recipe', () => {
  const recipes = ref([])
  const currentIndex = ref(0)
  const loopEvent = ref(0)

  const likedRecipes = ref(JSON.parse(localStorage.getItem('likedRecipes') || '[]'))

  watch(likedRecipes, (newVal) => {
    localStorage.setItem('likedRecipes', JSON.stringify(newVal))
  }, { deep: true })

  const setRecipes = (newRecipes) => {
    recipes.value = shuffle(newRecipes)
    currentIndex.value = 0
  }

  const likeCurrentRecipe = () => {
    likedRecipes.value.push(recipes.value[currentIndex.value])
    nextRecipe()
  }

  const skipCurrentRecipe = () => {
    nextRecipe()
  }

  const nextRecipe = () => {
    if (currentIndex.value < recipes.value.length - 1) {
      currentIndex.value++
    } else {
      startOver()
    }
  }

  function reset() {
    currentIndex.value = 0
    likedRecipes.value = []
    localStorage.removeItem('likedRecipes')
    recipes.value = shuffle(recipes.value)
    currentIndex.value = 0
    loopEvent.value = 0
  }

  function startOver() {
    recipes.value = shuffle(recipes.value)
    currentIndex.value = 0
    loopEvent.value++
  }

  function shuffle(array) {
    return [...array].sort(() => Math.random() - 0.5)
  }

    function downloadAllRecipesPDF(recipes) {
    const doc = new jsPDF()
    const shoppingListStore = useShoppingListStore()

    let yOffset = 20
    const pageHeight = 280
    const marginX = 10
    const lineHeight = 7

    doc.setFontSize(18)
    doc.text('Mina recept', marginX, yOffset)
    yOffset += lineHeight + 5

    doc.setFontSize(14)

    recipes.forEach((recipe, recipeIndex) => {
      if (recipeIndex !== 0) {
        yOffset += 10 // mellanrum mellan recept
      }

      if (yOffset > pageHeight - 30) {
        doc.addPage()
        yOffset = 20
      }

      doc.text(recipe.name || 'Unnamed recipe', marginX, yOffset)
      yOffset += lineHeight + 2

      doc.setFontSize(12)
      doc.text('Ingredienser:', marginX, yOffset)
      yOffset += lineHeight

      const filteredIngredients = (recipe.ingredients || []).filter((ingredient) => {
        return !shoppingListStore.isMarkedAsAtHome(ingredient.name)
      })

      if (filteredIngredients.length === 0) {
        doc.text('Inga ingredienser att visa (alla markerade)', marginX + 5, yOffset)
        yOffset += lineHeight
      } else {
        filteredIngredients.forEach((ingredient) => {
          const text = `• ${ingredient.name} – ${ingredient.quantity}`
          const splitText = doc.splitTextToSize(text, 180)
          doc.text(splitText, marginX + 5, yOffset)
          yOffset += lineHeight * splitText.length

          if (yOffset > pageHeight - 20) {
            doc.addPage()
            yOffset = 20
          }
        })
      }

      yOffset += 5
      doc.text('Beskrivning:', marginX, yOffset)
      yOffset += lineHeight

      const splitDescription = doc.splitTextToSize(recipe.description || 'Ingen beskrivning angiven', 180)
      doc.text(splitDescription, marginX, yOffset)
      yOffset += lineHeight * splitDescription.length

      doc.setFontSize(14)
    })

    doc.save('Mina_recept.pdf')
  }

  function downloadSinglePDF(recipe) {
    const doc = new jsPDF()
    let yOffset = 20
    const pageHeight = 280
    const marginX = 10
    const lineHeight = 7

    const shoppingListStore = useShoppingListStore()

    doc.setFontSize(16)
    doc.text(recipe.name || 'Unnamed recipe', marginX, yOffset)
    yOffset += lineHeight + 3

    doc.setFontSize(12)
    doc.text('Ingredienser:', marginX, yOffset)
    yOffset += lineHeight

    if (recipe.ingredients && recipe.ingredients.length > 0) {
      const filteredIngredients = recipe.ingredients.filter((ingredient) => {
        return !shoppingListStore.isMarkedAsAtHome(ingredient.name)
      })

      if (filteredIngredients.length === 0) {
        doc.text('Inga ingredienser att visa (alla är markerade)', marginX + 5, yOffset)
        yOffset += lineHeight
      } else {
        filteredIngredients.forEach((ingredient) => {
          const text = `• ${ingredient.name} – ${ingredient.quantity}`
          const splitText = doc.splitTextToSize(text, 180)
          doc.text(splitText, marginX + 5, yOffset)
          yOffset += lineHeight * splitText.length

          if (yOffset > pageHeight) {
            doc.addPage()
            yOffset = 20
          }
        })
      }
    } else {
      doc.text('Inga ingredienser angivna', marginX + 5, yOffset)
      yOffset += lineHeight
    }

    yOffset += 5

    doc.text('Beskrivning:', marginX, yOffset)
    yOffset += lineHeight

    const splitDescription = doc.splitTextToSize(recipe.description || 'Ingen beskrivning angiven', 180)
    doc.text(splitDescription, marginX, yOffset)

    const safeName = (recipe.name || 'Recept').replace(/[^a-z0-9_\-åäö ]/gi, '_')
    doc.save(`${safeName}.pdf`)
  }

  return {
    recipes,
    currentIndex,
    likedRecipes,
    loopEvent,
    setRecipes,
    likeCurrentRecipe,
    skipCurrentRecipe,
    reset,
    startOver,
    downloadAllRecipesPDF,
    downloadSinglePDF,
  }
})
