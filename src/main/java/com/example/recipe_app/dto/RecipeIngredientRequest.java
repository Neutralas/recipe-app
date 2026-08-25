package com.example.recipe_app.dto;

import com.example.recipe_app.entity.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecipeIngredientRequest(

        @NotBlank(message = "Ingredient name must not be blank")
        String name,

        @NotNull(message = "Quantity must not be null")
        @Positive(message = "Quantity must be greater than 0")
        BigDecimal quantity,

        @NotNull(message = "Unit must not be null")
        Unit unit
) {}
