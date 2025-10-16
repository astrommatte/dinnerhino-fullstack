<template>
  <div class="form-wrapper">
    <div class="card">
      <h2 class="title-text">Skapa nytt recept</h2>

      <div class="p-fluid">
        <!-- Receptinfo -->
        <div class="field">
          <label for="name"></label>
          <InputText v-model="recipe.name" id="name" placeholder="Namn" />
        </div>

        <div class="field textarea-wrapper">
          <FloatLabel variant="on">
            <label for="description"></label>
            <Textarea
              id="description"
              v-model="recipe.description"
              rows="8"
              maxlength="1000"
              autoResize
              placeholder="Beskrivning.."
            />
          </FloatLabel>

          <span class="char-counter-inside">
            {{ recipe.description.length }}/1000
          </span>
        </div>


        <div class="field">
          <label for="servings"></label>
          <InputNumber v-model="recipe.servings" id="servings" placeholder="Antal portioner" />
        </div>

        <!-- Ingredienser -->
        <h3 class="title-text">Ingredienser</h3>
        <div
          v-if="showIngredientForm"
          v-for="(ingredient, index) in recipe.ingredients"
          :key="index"
          class="p-inputgroup mb-2"
        >
          <InputText v-model="ingredient.name" placeholder="Namn" />
          <InputText v-model="ingredient.quantity" placeholder="Mängd" />
          <Button icon="pi pi-times-circle" severity="danger" @click="removeIngredient(index)" />
        </div>
        <Button label="Lägg till ingrediens" class="mt-2" @click="addIngredient" />

        <FileUpload
          mode="basic"
          name="file"
          choose-label="Välj bild"
          :auto="false"
          :custom-upload="true"
          accept="image/*"
          @select="onImageSelect"
        />

        <div v-if="recipe.image?.url" class="recipe-image-container">
          <img :src="recipe.image.url" alt="Receptbild" class="image-preview" />
          <Button icon="pi pi-times" severity="danger" @click="removeImage" class="remove-image-btn" />
        </div>

        <!-- Skapa-knapp -->
        <Button label="Spara recept" class="mt-4" @click="submitRecipe" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import axios from 'axios'
import { useToaster } from '@/stores/useToastStore'
import { hideLoading, showLoading } from '@/stores/useLoadingStore'
import FloatLabel from 'primevue/floatlabel'
import { useConfirmationStore } from '@/stores/useConfirmationStore'
import FileUpload from 'primevue/fileupload';

const confirmationStore = useConfirmationStore()
const { showSuccessToast, showErrorToast, showInfoToast } = useToaster()
const showIngredientForm = ref(false)
const selectedFile = ref(null);

const props = defineProps({
  existingRecipe: {
    type: Object,
    default: null
  }
})

const resetForm = () => {
  recipe.value = {
    name: '',
    description: '',
    servings: 1,
    ingredients: [],
    image: null
  }
}

const emit = defineEmits(['saved', 'imageDeleted'])

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const recipe = ref({
  name: '',
  description: '',
  servings: 1,
  ingredients: [
    { name: '', quantity: '' }  // quantity som string, utan unit
  ],
  image: null
})

watch(() => props.existingRecipe, (newVal) => {
  if (newVal) {
    recipe.value = JSON.parse(JSON.stringify(newVal)) // Deep copy
  } else {
    resetForm()
  }
}, { immediate: true })

const addIngredient = () => {
  recipe.value.ingredients.push({ name: '', quantity: '' })
  showIngredientForm.value = true
}

const removeIngredient = (index) => {
  recipe.value.ingredients.splice(index, 1)
}

function onImageSelect(event) {
  const file = event.files?.[0];
  if (file) {
    selectedFile.value = file;
  }
}

async function removeImage () {
  confirmationStore.confirm2(async () => {
    try {
      showLoading();
      showInfoToast('Tar bort bild...');
      
      const headers = { Authorization: localStorage.getItem('auth') || '' };
      await axios.delete(`${apiUrl}/api/images/recipe/${props.existingRecipe.id}`, { headers });

      showSuccessToast('Bild borttagen!');
      emit('imageDeleted', props.existingRecipe.id);
    } catch (error) {
      showErrorToast('Misslyckades att ta bort bild');
    } finally {
      hideLoading();
    }
  });
}

const submitRecipe = async () => {
  try {
    showLoading();
    showInfoToast('Laddar upp nytt recept..');
    const headers = { Authorization: localStorage.getItem('auth') };
    let savedRecipeId = null;

    if (props.existingRecipe && props.existingRecipe.id) {
      // Uppdatera
      const res = await axios.put(
        `${apiUrl}/api/recipes/${props.existingRecipe.id}`,
        recipe.value,
        { headers }
      );
      savedRecipeId = res.data.id;
      
    } else {
      // Skapa nytt
      const res = await axios.post(
        `${apiUrl}/api/recipes`,
        recipe.value,
        { headers }
      );
      savedRecipeId = res.data.id;
      
    }

    // Ladda upp bild om en fil valts
    if (selectedFile.value) {

      // 2. Bygg formData
      const formData = new FormData();
      formData.append("file", selectedFile.value);
      formData.append("recipeId", savedRecipeId); // För att backend ska koppla bilden till recept

      // 3. Skicka till din egen server (inte direkt till Cloudinary)
      await axios.post(`${apiUrl}/api/images/upload`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: localStorage.getItem('auth') || '',
        }
      });

      selectedFile.value = null;
    }
    showSuccessToast('Recept skapat!');
    emit('saved'); // för att uppdatera receptlistan
    resetForm();
  } catch (error) {
    showErrorToast('Fel vid recept-sparande eller bilduppladdning.');
  } finally {
    hideLoading();
  }
};
</script>

<style scoped>
    .image-preview {
    max-width: 150px;
    max-height: 100px;
    border-radius: 4px;
    object-fit: cover;
  }

  .remove-image-btn {
    background-color: transparent !important;
    border: none !important;
    box-shadow: none !important;
    padding: 0 !important;
    color: var(--surface-500);
    position: absolute;
    border-radius: 50%;
    cursor: pointer;
    font-size: 5px;
  }

  .textarea-wrapper {
    position: relative;
  }

  .char-counter-inside {
    position: absolute;
    bottom: 8px;
    left: 175px;
    font-size: 12px;
    color: var(--text-secondary, #888);
    background: var(--surface-card);
    padding: 0 4px;
    border-radius: 4px;
    pointer-events: none; /* så att den inte stör skrivandet */
  }

  .form-wrapper {
    padding: 1rem;
    box-sizing: border-box;
  }

  .card {
    width: 100%;
  }

  .field label {
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  h2, h3 {
    margin-bottom: 1rem;
  }

  .p-inputgroup{
    margin-bottom: 1rem;
  }

  .p-inputgroup button{
    width: 15px;
    height: 10px;
    margin: 5px;
  }
</style>
