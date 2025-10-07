import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MyPageView from '@/views/MyPageView.vue'
import RecipeView from '../views/RecipeView.vue'
import MyRecipesView from '@/views/MyRecipesView.vue'
import AdminView from '../views/AdminView.vue'
import ShoppingListView from '../views/ShoppingListView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/mypage', component: MyPageView },
  { path: '/recipes', component: RecipeView },
  { path: '/myrecipes', component: MyRecipesView },
  { path: '/admin', component: AdminView },
  { path: '/shoppinglist', component: ShoppingListView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
