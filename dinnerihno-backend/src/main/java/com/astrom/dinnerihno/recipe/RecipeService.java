package com.astrom.dinnerihno.recipe;

import com.astrom.dinnerihno.mapper.DtoMapper;
import com.astrom.dinnerihno.user.User;
import com.astrom.dinnerihno.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    public RecipeDTO createRecipe(RecipeCreateDTO dto, String email) {
        // hämta användaren från DB baserat på inloggad email
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // konvertera till entity
        Recipe recipe = dtoMapper.toRecipeEntity(dto, createdBy);

        // spara
        Recipe saved = recipeRepository.save(recipe);

        // konvertera tillbaka till DTO
        return dtoMapper.toRecipeDto(saved);
    }

    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(dtoMapper::toRecipeDto)
                .toList();
    }

    public List<RecipeDTO> getUserRecipes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recipeRepository.findByCreatedBy(user).stream()
                .map(dtoMapper::toRecipeDto)
                .toList();
    }

    public RecipeDTO updateRecipe(Long recipeId, RecipeCreateDTO dto, String email) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!recipe.getCreatedBy().getEmail().equals(email)) {
            throw new RuntimeException("You can only update your own recipes");
        }

        recipe.setName(dto.getName());
        recipe.setDescription(dto.getDescription());
        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(
                dto.getIngredients().stream()
                        .map(i -> dtoMapper.toIngredientEntity(i, recipe))
                        .toList()
        );

        Recipe saved = recipeRepository.save(recipe);
        return dtoMapper.toRecipeDto(saved);
    }

    public void deleteRecipe(Long recipeId, String email) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!recipe.getCreatedBy().getEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own recipes");
        }

        recipeRepository.delete(recipe);
    }
}

