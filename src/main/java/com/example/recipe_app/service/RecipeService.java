package com.example.recipe_app.service;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.entity.Ingredient;
import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.entity.RecipeIngredient;
import com.example.recipe_app.mapper.RecipeMapper;
import com.example.recipe_app.repository.IngredientRepository;
import com.example.recipe_app.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository, RecipeMapper recipeMapper, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.ingredientRepository = ingredientRepository;
    }

    public RecipeResponse createRecipe(CreateRecipeRequest createRecipeRequest) {

        Recipe recipe = new Recipe();
        recipe.setName(createRecipeRequest.name());
        recipe.setPortions(createRecipeRequest.portions());
        recipe.setInstructions(createRecipeRequest.instructions());
        recipe.setCreatedBy(createRecipeRequest.createdBy());
        recipe.setCreatedAt(LocalDateTime.now());

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        createRecipeRequest.ingredients().forEach(ingredientDto -> {
            Ingredient ingredient = ingredientRepository.findByName(ingredientDto.name())
                    .orElseGet(() -> ingredientRepository.save(Ingredient.builder()
                            .name(ingredientDto.name())
                            .unit(ingredientDto.unit())
                            .build()));

            recipeIngredients.add(RecipeIngredient.builder()
                    .ingredient(ingredient)
                    .recipe(recipe)
                    .quantity(ingredientDto.quantity())
                    .build());
        });

        recipe.setRecipeIngredients(recipeIngredients);
        recipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(recipe);
    }

    public RecipeResponse getRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + id));
        return recipeMapper.toRecipeResponse(recipe);
    }
}
