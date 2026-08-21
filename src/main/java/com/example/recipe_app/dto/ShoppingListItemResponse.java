package com.example.recipe_app.dto;

import com.example.recipe_app.entity.Unit;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShoppingListItemResponse(
        Long ingredientId,
        String name,
        BigDecimal quantity,
        Unit unit,
        Boolean isChecked
) {}
