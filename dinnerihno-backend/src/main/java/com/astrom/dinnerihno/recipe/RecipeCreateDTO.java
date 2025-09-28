package com.astrom.dinnerihno.recipe;

import com.astrom.dinnerihno.ingredient.Ingredient;
import com.astrom.dinnerihno.ingredient.IngredientCreateDTO;
import com.astrom.dinnerihno.user.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeCreateDTO {
    private String name;
    private String description;
    private int servings;
    private List<IngredientCreateDTO> ingredients = new ArrayList<>();
}
