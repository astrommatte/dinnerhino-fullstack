package com.astrom.dinnerihno.recipe;

import com.astrom.dinnerihno.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByCreatedBy(User user);
    List<Recipe> findByCreatedBy(User user);
}
