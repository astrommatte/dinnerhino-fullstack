package com.astrom.dinnerihno.shoppinglistitem;

import com.astrom.dinnerihno.user.User;
import com.astrom.dinnerihno.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/shopping-list")
@RequiredArgsConstructor
public class ShoppingListItemController {
    private final ShoppingListItemService shoppingListService;
    private final UserRepository userRepository;

    @PostMapping("/add/{recipeId}")
    public void addRecipeToShoppingList(@PathVariable Long recipeId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        shoppingListService.addRecipeToShoppingList(recipeId, currentUser);
    }

    @GetMapping
    public List<ShoppingListItemDTO> getShoppingList(Principal principal) {
        return shoppingListService.getShoppingListDto(principal.getName());
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId, Principal principal) {
        shoppingListService.removeShoppingListItem(itemId, principal.getName());
    }

    @DeleteMapping("/clear")
    public void clearShoppingList(Principal principal) {
        shoppingListService.clearShoppingList(principal.getName());
    }


    @PutMapping("/{itemId}")
    public ShoppingListItemDTO updateItem(@PathVariable Long itemId,
                                          @RequestBody ShoppingListItemCreateDTO dto,
                                          Principal principal) {
        return shoppingListService.updateItem(itemId, dto, principal.getName());
    }
}

