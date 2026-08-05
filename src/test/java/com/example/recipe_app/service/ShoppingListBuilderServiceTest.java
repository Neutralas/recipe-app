package com.example.recipe_app.service;

import com.example.recipe_app.entity.Ingredient;
import com.example.recipe_app.entity.RecipeIngredient;
import com.example.recipe_app.entity.ShoppingList;
import com.example.recipe_app.repository.RecipeIngredientRepository;
import com.example.recipe_app.repository.ShoppingListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListBuilderServiceTest {

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;
    @Mock
    private ShoppingListRepository shoppingListRepository;

    @InjectMocks
    private ShoppingListBuilderService testee;

    @Test
    void buildShoppingList_correctlyAddsDuplicateIngredientQuantities() {
        List<Long> recipeIds = List.of(1L, 2L, 3L);
        List<RecipeIngredient> recipeIngredients = List.of(
                RecipeIngredient.builder()
                        .ingredient(Ingredient.builder()
                                .id(1L).build())
                        .quantity(BigDecimal.ONE)
                        .build(),
                RecipeIngredient.builder()
                        .ingredient(Ingredient.builder()
                                .id(2L).build())
                        .quantity(BigDecimal.TWO)
                        .build(),
                RecipeIngredient.builder()
                        .ingredient(Ingredient.builder()
                                .id(1L).build())
                        .quantity(BigDecimal.TEN)
                        .build()
        );

        when(recipeIngredientRepository.findByRecipeIdIn(recipeIds)).thenReturn(recipeIngredients);

        ShoppingList generatedShoppingList = testee.buildShoppingList(recipeIds);

        BigDecimal ingredient1Quantity = generatedShoppingList.getItems().stream()
                .filter(shoppingListItem -> shoppingListItem.getIngredient().getId().equals(1L))
                .findFirst()
                .orElseThrow()
                .getQuantity();

        BigDecimal ingredient2Quantity = generatedShoppingList.getItems().stream()
                .filter(shoppingListItem -> shoppingListItem.getIngredient().getId().equals(2L))
                .findFirst()
                .orElseThrow()
                .getQuantity();

        assertNotNull(generatedShoppingList);
        assertEquals(BigDecimal.valueOf(11), ingredient1Quantity);
        assertEquals(BigDecimal.valueOf(2), ingredient2Quantity);
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    //empty input
    @Test
    void buildShoppingList_emptyList() {
        ShoppingList generatedShoppingList = testee.buildShoppingList(List.of());

        assertEquals(List.of(), generatedShoppingList.getItems());
        verify(recipeIngredientRepository, never()).findByRecipeIdIn(any());
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    //no duplicates
    @Test
    void buildShoppingList_noDuplicateIngredients() {
        List<Long> recipeIds = List.of(1L, 2L, 3L);
        List<RecipeIngredient> recipeIngredients = List.of(
                RecipeIngredient.builder()
                        .ingredient(Ingredient.builder()
                                .id(1L).build())
                        .quantity(BigDecimal.ONE)
                        .build(),
                RecipeIngredient.builder()
                        .ingredient(Ingredient.builder()
                                .id(2L).build())
                        .quantity(BigDecimal.TWO)
                        .build(),
                RecipeIngredient.builder()
                        .ingredient(Ingredient.builder()
                                .id(3L).build())
                        .quantity(BigDecimal.TEN)
                        .build()
        );

        when(recipeIngredientRepository.findByRecipeIdIn(recipeIds)).thenReturn(recipeIngredients);

        ShoppingList generatedShoppingList = testee.buildShoppingList(recipeIds);

        BigDecimal ingredient1Quantity = generatedShoppingList.getItems().stream()
                .filter(shoppingListItem -> shoppingListItem.getIngredient().getId().equals(1L))
                .findFirst()
                .orElseThrow()
                .getQuantity();

        BigDecimal ingredient2Quantity = generatedShoppingList.getItems().stream()
                .filter(shoppingListItem -> shoppingListItem.getIngredient().getId().equals(2L))
                .findFirst()
                .orElseThrow()
                .getQuantity();

        BigDecimal ingredient3Quantity = generatedShoppingList.getItems().stream()
                .filter(shoppingListItem -> shoppingListItem.getIngredient().getId().equals(3L))
                .findFirst()
                .orElseThrow()
                .getQuantity();

        assertNotNull(generatedShoppingList);
        assertEquals(BigDecimal.valueOf(1), ingredient1Quantity);
        assertEquals(BigDecimal.valueOf(2), ingredient2Quantity);
        assertEquals(BigDecimal.valueOf(10), ingredient3Quantity);
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }
}
