package com.example.recipe_app.controller;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse addRecipe(@RequestBody CreateRecipeRequest createRecipeRequest) {
        return recipeService.createRecipe(createRecipeRequest);
    }

    @GetMapping
    public List<RecipeResponse> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public RecipeResponse getRecipe(@PathVariable Long id) {
        return recipeService.getRecipe(id);
    }
}
