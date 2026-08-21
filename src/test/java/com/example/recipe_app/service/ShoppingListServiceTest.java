package com.example.recipe_app.service;

import com.example.recipe_app.dto.ShoppingListItemResponse;
import com.example.recipe_app.dto.ShoppingListResponse;
import com.example.recipe_app.entity.*;
import com.example.recipe_app.mapper.ShoppingListMapper;
import com.example.recipe_app.repository.RecipeIngredientRepository;
import com.example.recipe_app.repository.ShoppingListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.recipe_app.entity.Unit.GRAM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    public static final String ONION = "onion";
    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;
    @Mock
    private ShoppingListRepository shoppingListRepository;

    private final ShoppingListMapper shoppingListMapper = new ShoppingListMapper();
    private ShoppingListService testee;

    @BeforeEach
    void setUp() {
        testee = new ShoppingListService(recipeIngredientRepository, shoppingListRepository, shoppingListMapper);
    }

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

        ShoppingListResponse generatedShoppingList = testee.buildShoppingList(recipeIds);

        BigDecimal ingredient1Quantity = generatedShoppingList.ingredients().stream()
                .filter(shoppingListItem -> shoppingListItem.ingredientId().equals(1L))
                .findFirst()
                .orElseThrow()
                .quantity();

        BigDecimal ingredient2Quantity = generatedShoppingList.ingredients().stream()
                .filter(shoppingListItem -> shoppingListItem.ingredientId().equals(2L))
                .findFirst()
                .orElseThrow()
                .quantity();

        assertNotNull(generatedShoppingList);
        assertEquals(BigDecimal.valueOf(11), ingredient1Quantity);
        assertEquals(BigDecimal.valueOf(2), ingredient2Quantity);
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    //empty input
    @Test
    void buildShoppingList_emptyList() {
        ShoppingListResponse generatedShoppingList = testee.buildShoppingList(List.of());

        assertEquals(List.of(), generatedShoppingList.ingredients());
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

        ShoppingListResponse generatedShoppingList = testee.buildShoppingList(recipeIds);

        BigDecimal ingredient1Quantity = generatedShoppingList.ingredients().stream()
                .filter(shoppingListItem -> shoppingListItem.ingredientId().equals(1L))
                .findFirst()
                .orElseThrow()
                .quantity();

        BigDecimal ingredient2Quantity = generatedShoppingList.ingredients().stream()
                .filter(shoppingListItem -> shoppingListItem.ingredientId().equals(2L))
                .findFirst()
                .orElseThrow()
                .quantity();

        BigDecimal ingredient3Quantity = generatedShoppingList.ingredients().stream()
                .filter(shoppingListItem -> shoppingListItem.ingredientId().equals(3L))
                .findFirst()
                .orElseThrow()
                .quantity();

        assertNotNull(generatedShoppingList);
        assertEquals(BigDecimal.valueOf(1), ingredient1Quantity);
        assertEquals(BigDecimal.valueOf(2), ingredient2Quantity);
        assertEquals(BigDecimal.valueOf(10), ingredient3Quantity);
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    @Test
    void getShoppingList_success() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setQuantity(BigDecimal.ONE);
        shoppingListItem.setIngredient(Ingredient.builder().id(1L).name(ONION).unit(GRAM).build());
        shoppingListItem.setIsChecked(false);

        shoppingList.setId(1L);
        shoppingList.setItems(List.of(shoppingListItem));

        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(shoppingList));

        ShoppingListResponse result = testee.retrieveShoppingList(1L);
        ShoppingListItemResponse resultItem = result.ingredients().getFirst();

        assertEquals(1L, result.id());
        assertEquals(1L, resultItem.ingredientId());
        assertEquals(ONION, resultItem.name());
        assertEquals(BigDecimal.ONE, resultItem.quantity());
        assertEquals(GRAM, resultItem.unit());
        assertEquals(false, resultItem.isChecked());
    }

    @Test
    void getShoppingList_notFound() {
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> testee.retrieveShoppingList(1L));

        String expectedMessage = "Shopping List not found with shoppingListId: 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getShoppingLists_shoppingListsExist() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setQuantity(BigDecimal.ONE);
        shoppingListItem.setIngredient(Ingredient.builder().id(1L).name(ONION).unit(GRAM).build());
        shoppingListItem.setIsChecked(false);

        shoppingList.setId(1L);
        shoppingList.setItems(List.of(shoppingListItem));

        when(shoppingListRepository.findAll()).thenReturn(List.of(shoppingList));

        List<ShoppingListResponse> result = testee.getAllShoppingLists();
        ShoppingListResponse response = result.getFirst();
        ShoppingListItemResponse responseItem = response.ingredients().getFirst();

        assertEquals(1L, response.id());
        assertEquals(1L, responseItem.ingredientId());
        assertEquals(ONION, responseItem.name());
        assertEquals(BigDecimal.ONE, responseItem.quantity());
        assertEquals(GRAM, responseItem.unit());
        assertEquals(false, responseItem.isChecked());
    }

    @Test
    void getShoppingLists_noShoppingListsExist() {
        when(shoppingListRepository.findAll()).thenReturn(List.of());

        List<ShoppingListResponse> result = testee.getAllShoppingLists();

        assertTrue(result.isEmpty());
    }
}
