package com.example.recipe_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateRecipeRequest(

        @NotBlank(message = "Recipe name must not be blank")
        String name,

        @NotNull(message = "Portions must not be null")
        @Positive(message = "Portions must be greater than 0")
        int portions,

        @NotNull(message = "Ingredients must not be null")
        List<@Valid RecipeIngredientRequest> ingredients,

        @NotBlank(message = "Instructions must not be blank")
        String instructions,

        @NotBlank(message = "Created by must not be blank")
        String createdBy
) {}
