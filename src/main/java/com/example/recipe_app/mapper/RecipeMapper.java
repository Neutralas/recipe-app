package com.example.recipe_app.mapper;

import com.example.recipe_app.dto.RecipeIngredientResponse;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.entity.RecipeIngredient;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {

    public RecipeResponse toRecipeResponse(Recipe recipe) {
        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .portions(recipe.getPortions())
                .ingredients(recipe.getRecipeIngredients().stream()
                        .map(this::toRecipeIngredientResponse)
                        .toList())
                .instructions(recipe.getInstructions())
                .createdBy(recipe.getCreatedBy())
                .build();
    }

    public RecipeIngredientResponse toRecipeIngredientResponse(RecipeIngredient recipeIngredient) {
        return RecipeIngredientResponse.builder()
                .id(recipeIngredient.getIngredient().getId())
                .name(recipeIngredient.getIngredient().getName())
                .quantity(recipeIngredient.getQuantity())
                .unit(recipeIngredient.getIngredient().getUnit())
                .build();
    }
}
