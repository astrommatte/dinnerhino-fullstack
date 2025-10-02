package com.astrom.dinnerihno.shoppinglistitem;

import com.astrom.dinnerihno.ingredient.Ingredient;
import com.astrom.dinnerihno.mapper.DtoMapper;
import com.astrom.dinnerihno.recipe.Recipe;
import com.astrom.dinnerihno.recipe.RecipeRepository;
import com.astrom.dinnerihno.user.User;
import com.astrom.dinnerihno.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListItemService {
    private final ShoppingListItemRepository shoppingListRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    public void addRecipeToShoppingList(Long recipeId, User currentUser) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));


        for (Ingredient ingredient : recipe.getIngredients()) {
            // Skapa DTO först
            ShoppingListItemCreateDTO itemDto = new ShoppingListItemCreateDTO();
            itemDto.setIngredientName(ingredient.getName());
            itemDto.setQuantity(ingredient.getQuantity());

            // Använd mapper för att skapa entity
            ShoppingListItem item = dtoMapper.toShoppingListItemEntity(itemDto, currentUser);
            item.setRecipe(recipe);
            shoppingListRepository.save(item);
        }
    }

    public List<ShoppingListItemDTO> getShoppingListDto(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return shoppingListRepository.findByUser(user).stream()
                .map(dtoMapper::toShoppingListItemDto)
                .toList();
    }

    public void removeShoppingListItem(Long itemId, String email) {
        ShoppingListItem item = shoppingListRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own items");
        }

        shoppingListRepository.delete(item);
    }

    public ShoppingListItemDTO updateItem(Long itemId, ShoppingListItemCreateDTO dto, String email) {
        ShoppingListItem item = shoppingListRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only update your own items");
        }

        item.setIngredientName(dto.getIngredientName());
        item.setQuantity(dto.getQuantity());

        shoppingListRepository.save(item);

        return dtoMapper.toShoppingListItemDto(item);
    }

    public void clearShoppingList(String email) {
        List<ShoppingListItem> items = shoppingListRepository.findByUserEmail(email);
        shoppingListRepository.deleteAll(items);
    }
}

