package com.example.recipe_app.mapper;

import com.example.recipe_app.dto.ShoppingListItemResponse;
import com.example.recipe_app.dto.ShoppingListResponse;
import com.example.recipe_app.entity.ShoppingList;
import com.example.recipe_app.entity.ShoppingListItem;
import org.springframework.stereotype.Component;

@Component
public class ShoppingListMapper {

    public ShoppingListResponse toShoppingListResponse(ShoppingList shoppingList) {
        return ShoppingListResponse.builder()
                .id(shoppingList.getId())
                .ingredients(shoppingList.getItems().stream()
                        .map(this::toShoppingListItemResponse)
                        .toList())
                .build();
    }

    private ShoppingListItemResponse toShoppingListItemResponse(ShoppingListItem shoppingListItem) {
        return ShoppingListItemResponse.builder()
                .ingredientId(shoppingListItem.getIngredient().getId())
                .name(shoppingListItem.getIngredient().getName())
                .quantity(shoppingListItem.getQuantity())
                .unit(shoppingListItem.getIngredient().getUnit())
                .isChecked(shoppingListItem.getIsChecked())
                .build();
    }
}
