package com.example.recipe_app.controller;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing recipes.
 */
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Creates a new recipe.
     *
     * @param createRecipeRequest the recipe data
     * @return the created recipe
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse addRecipe(@Valid @RequestBody CreateRecipeRequest createRecipeRequest) {
        return recipeService.createRecipe(createRecipeRequest);
    }

    /**
     * Retrieves all recipes.
     *
     * @return a list of all recipes
     */
    @GetMapping
    public List<RecipeResponse> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    /**
     * Retrieves a recipe by its ID.
     *
     * @param id the recipe ID
     * @return the requested recipe
     */
    @GetMapping("/{id}")
    public RecipeResponse getRecipe(@PathVariable Long id) {
        return recipeService.getRecipe(id);
    }
}
