package com.astrom.dinnerihno.image;

import com.astrom.dinnerihno.recipe.Recipe;
import com.astrom.dinnerihno.recipe.RecipeRepository;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final RecipeRepository recipeRepository;
    private final Cloudinary cloudinary;

    public Image uploadImage(MultipartFile file, Long recipeId) throws IOException {
        System.out.println("Uploading image, recipeId = " + recipeId);
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
        System.out.println("Upload success, result: " + uploadResult);

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + recipeId));
        System.out.println("Found recipe: " + recipe.getId());

        Image image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setUrl(url);
        image.setPublicId(publicId);
        image.setRecipe(recipe);

        Image saved = imageRepository.save(image);
        System.out.println("Image saved with id: " + saved.getId());
        return saved;
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + id));
    }

    public void deleteImageForRecipe(Long recipeId, String username) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recept hittades inte"));

        if (!recipe.getCreatedBy().getUsername().equals(username)) {
            throw new SecurityException("Du kan bara ta bort bilder från dina egna recept.");
        }

        Image image = recipe.getImage();
        if (image == null) {
            throw new IllegalArgumentException("Ingen bild kopplad till detta recept.");
        }

        // Ta bort från Cloudinary
        try {
            cloudinary.uploader().destroy(image.getPublicId(), Map.of());
        } catch (Exception e) {
            throw new IOException("Misslyckades ta bort från Cloudinary: " + e.getMessage());
        }

        // Nollställ relation
        recipe.setImage(null);
        recipeRepository.save(recipe);

        // Ta bort från databasen
        imageRepository.delete(image);
    }

}
