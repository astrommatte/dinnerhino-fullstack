package com.astrom.dinnerihno.shoppinglistitem;

import com.astrom.dinnerihno.user.User;
import lombok.Data;

@Data
public class ShoppingListItemCreateDTO {
    private String ingredientName;
    private String quantity;
}
