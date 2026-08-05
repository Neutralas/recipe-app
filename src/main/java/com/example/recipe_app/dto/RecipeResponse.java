package com.example.recipe_app.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RecipeResponse(
        Long id,
        String name,
        int portions,
        List<RecipeIngredientResponse> ingredients,
        String instructions,
        String createdBy
) {}
