package com.example.recipe_app.dto;

import com.example.recipe_app.entity.Unit;

import java.math.BigDecimal;

public record RecipeIngredientRequest(
        String name,
        BigDecimal quantity,
        Unit unit
) {}
