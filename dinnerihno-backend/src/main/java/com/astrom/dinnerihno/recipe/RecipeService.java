package com.astrom.dinnerihno.recipe;

import com.astrom.dinnerihno.image.Image;
import com.astrom.dinnerihno.image.ImageRepository;
import com.astrom.dinnerihno.mapper.DtoMapper;
import com.astrom.dinnerihno.user.User;
import com.astrom.dinnerihno.user.UserRepository;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final DtoMapper dtoMapper;
    private final Cloudinary cloudinary;

    public RecipeDTO createRecipe(RecipeCreateDTO dto, String username) {
        // hämta användaren från DB baserat på inloggad email
        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

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

    public List<RecipeDTO> getUserRecipes(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recipeRepository.findByCreatedBy(user).stream()
                .map(dtoMapper::toRecipeDto)
                .toList();
    }

    public RecipeDTO updateRecipe(Long recipeId, RecipeCreateDTO dto, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!recipe.getCreatedBy().getUsername().equals(username)) {
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

    public void deleteRecipe(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!recipe.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own recipes");
        }

        // Radera bild från Cloudinary om det finns en
        Image image = recipe.getImage();
        if (image != null) {
            try {
                cloudinary.uploader().destroy(image.getPublicId(), Map.of());
            } catch (IOException e) {
                System.err.println("Kunde inte ta bort bild från Cloudinary: " + image.getPublicId());
            }
            // Om du vill radera bilden från DB separat:
            imageRepository.delete(image);
        }

        recipeRepository.delete(recipe);
    }
}

