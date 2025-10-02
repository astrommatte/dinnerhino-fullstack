package com.astrom.dinnerihno.mapper;

import com.astrom.dinnerihno.ingredient.Ingredient;
import com.astrom.dinnerihno.ingredient.IngredientCreateDTO;
import com.astrom.dinnerihno.ingredient.IngredientDTO;
import com.astrom.dinnerihno.recipe.Recipe;
import com.astrom.dinnerihno.recipe.RecipeCreateDTO;
import com.astrom.dinnerihno.recipe.RecipeDTO;
import com.astrom.dinnerihno.shoppinglistitem.ShoppingListItem;
import com.astrom.dinnerihno.shoppinglistitem.ShoppingListItemCreateDTO;
import com.astrom.dinnerihno.shoppinglistitem.ShoppingListItemDTO;
import com.astrom.dinnerihno.user.CreateUserDTO;
import com.astrom.dinnerihno.user.User;
import com.astrom.dinnerihno.user.UserDTO;
import com.astrom.dinnerihno.user.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoMapper {

    public UserDTO toUserDto(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole() != null ? UserRole.valueOf(user.getRole().name()) : null);
        return dto;
    }

    public IngredientDTO toIngredientDto(Ingredient ingredient) {
        if (ingredient == null) return null;

        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantity(ingredient.getQuantity());
        return dto;
    }

    public Ingredient toIngredientEntity(IngredientCreateDTO dto, Recipe recipe) {
        if (dto == null) return null;

        Ingredient ingredient = new Ingredient();
        ingredient.setName(dto.getName());
        ingredient.setQuantity(dto.getQuantity());
        ingredient.setRecipe(recipe); // koppla ihop med receptet
        return ingredient;
    }

    public RecipeDTO toRecipeDto(Recipe recipe) {
        if (recipe == null) return null;

        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setServings(recipe.getServings());
        dto.setCreatedById(recipe.getCreatedBy().getId());
        dto.setCreatedByEmail(recipe.getCreatedBy().getEmail());

        dto.setIngredients(recipe.getIngredients().stream()
                .map(this::toIngredientDto)
                .toList());

        return dto;
    }

    public Recipe toRecipeEntity(RecipeCreateDTO dto, User createdBy) {
        if (dto == null) return null;

        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());
        recipe.setDescription(dto.getDescription());
        recipe.setServings(dto.getServings());
        recipe.setCreatedBy(createdBy);

        List<Ingredient> ingredients = dto.getIngredients().stream()
                .map(ingredientDto -> toIngredientEntity(ingredientDto, recipe))
                .toList();

        recipe.setIngredients(ingredients);

        return recipe;
    }

    // ---------- SHOPPING LIST ----------
    public ShoppingListItemDTO toShoppingListItemDto(ShoppingListItem item) {
        if (item == null) return null;

        ShoppingListItemDTO dto = new ShoppingListItemDTO();
        dto.setId(item.getId());
        dto.setIngredientName(item.getIngredientName());
        dto.setQuantity(item.getQuantity());

        if (item.getRecipe() != null) {
            dto.setRecipeName(item.getRecipe().getName());
            dto.setRecipeServings(item.getRecipe().getServings());
        }
        return dto;
    }

    public ShoppingListItem toShoppingListItemEntity(ShoppingListItemCreateDTO dto, User user) {
        if (dto == null) return null;

        ShoppingListItem item = new ShoppingListItem();
        item.setIngredientName(dto.getIngredientName());
        item.setQuantity(dto.getQuantity());
        item.setUser(user);
        return item;
    }
}

