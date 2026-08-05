package com.example.recipe_app.dto;

import com.example.recipe_app.entity.Unit;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RecipeIngredientResponse(
        Long id,
        String name,
        BigDecimal quantity,
        Unit unit
) {}
