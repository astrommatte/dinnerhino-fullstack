package com.astrom.dinnerihno.shoppinglistitem;

import lombok.Data;

@Data
public class ShoppingListItemDTO {

    private Long id;
    private String ingredientName;
    private String quantity;
    private String recipeName;
    private int recipeServings;
}
