package com.example.recipe_app.dto;

import com.example.recipe_app.entity.Unit;

import java.math.BigDecimal;

public record ShoppingListItemResponse(
        Long id,
        String name,
        BigDecimal quantity,
        Unit unit,
        Boolean isChecked
) {}
