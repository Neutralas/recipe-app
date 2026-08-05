package com.example.recipe_app.dto;

import java.util.List;

public record CreateRecipeRequest(
        String name,
        int portions,
        List<RecipeIngredientRequest> ingredients,
        String instructions,
        String createdBy
) {}
