package com.astrom.dinnerihno.shoppinglistitem;

import com.astrom.dinnerihno.recipe.Recipe;
import com.astrom.dinnerihno.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ingredientName;
    private String quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

}
