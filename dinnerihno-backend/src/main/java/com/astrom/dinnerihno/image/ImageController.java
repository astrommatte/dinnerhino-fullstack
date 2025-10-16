package com.astrom.dinnerihno.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("recipeId") Long recipeId) {
        try {
            Image savedImage = imageService.uploadImage(file, recipeId);
            return ResponseEntity.ok(savedImage);
        } catch (Exception e) {
            e.printStackTrace();  // Logga full stacktrace i konsolen
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Uppladdning misslyckades: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable Long id) {
        Image image = imageService.getImageById(id);
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/recipe/{recipeId}")
    public ResponseEntity<?> deleteImageFromRecipe(@PathVariable Long recipeId, Principal principal) throws IOException {

            imageService.deleteImageForRecipe(recipeId, principal.getName());
            return ResponseEntity.ok("Bilden har tagits bort från receptet.");
    }
}
