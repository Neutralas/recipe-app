package com.example.recipe_app.dto;

import java.util.List;

public record BuildShoppingListRequest(
        List<Long> recipeIds
) {}
