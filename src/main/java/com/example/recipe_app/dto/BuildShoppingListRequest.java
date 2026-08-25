package com.example.recipe_app.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BuildShoppingListRequest(

        @NotEmpty(message = "At least one recipe ID is required")
        List<Long> recipeIds
) {}
