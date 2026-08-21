package com.example.recipe_app.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ShoppingListResponse(
        Long id,
        List<ShoppingListItemResponse> ingredients
) {
}
