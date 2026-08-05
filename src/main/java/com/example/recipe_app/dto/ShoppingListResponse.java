package com.example.recipe_app.dto;

import java.util.List;

public record ShoppingListResponse(
        Long id,
        List<ShoppingListItemResponse> ingredients
) {
}
