package com.astrom.dinnerihno.recipe;

import com.astrom.dinnerihno.ingredient.Ingredient;
import com.astrom.dinnerihno.ingredient.IngredientDTO;
import com.astrom.dinnerihno.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeDTO {

    private Long id;
    private String name;
    private String description;
    private int servings;
    private Long createdById;
    private String createdByEmail;
    private List<IngredientDTO> ingredients = new ArrayList<>();
}
