package com.astrom.dinnerihno.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeCreateDTO dto, Principal principal) {
        RecipeDTO created = recipeService.createRecipe(dto, principal.getName());
        return ResponseEntity.ok(created);
    }


    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/my")
    public ResponseEntity<List<RecipeDTO>> getUserRecipes(Principal principal) {
        return ResponseEntity.ok(recipeService.getUserRecipes(principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id,
                                                  @RequestBody RecipeCreateDTO dto,
                                                  Principal principal) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id,
                                             Principal principal) {
        recipeService.deleteRecipe(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}


