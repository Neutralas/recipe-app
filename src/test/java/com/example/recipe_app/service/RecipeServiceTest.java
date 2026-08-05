package com.example.recipe_app.service;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeIngredientRequest;
import com.example.recipe_app.entity.Ingredient;
import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.entity.RecipeIngredient;
import com.example.recipe_app.entity.Unit;
import com.example.recipe_app.mapper.RecipeMapper;
import com.example.recipe_app.repository.IngredientRepository;
import com.example.recipe_app.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    public static final String RECIPE_NAME = "recipe-name";
    public static final String INSTRUCTIONS = "instructions";
    public static final String CREATED_BY = "createdBy";
    public static final String ONION = "onion";
    public static final String GARLIC = "garlic";
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeService testee;

    // create recipe new ingredients
    @Test
    void createRecipeWithNewIngredients() {

        CreateRecipeRequest createRecipeRequest = getCreateRecipeRequest();

        when(ingredientRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = testee.createRecipe(createRecipeRequest);

        assertEquals(RECIPE_NAME, result.getName());
        assertEquals(2, result.getPortions());
        assertEquals(INSTRUCTIONS, result.getInstructions());
        assertEquals(CREATED_BY, result.getCreatedBy());
        assertNotNull(result.getCreatedAt());

        RecipeIngredient recipeIngredient1 = result.getRecipeIngredients().stream()
                .filter(ri -> ONION.equals(ri.getIngredient().getName()))
                .findFirst()
                .orElseThrow();
        RecipeIngredient recipeIngredient2 = result.getRecipeIngredients().stream()
                .filter(ri -> GARLIC.equals(ri.getIngredient().getName()))
                .findFirst()
                .orElseThrow();

        assertEquals(ONION, recipeIngredient1.getIngredient().getName());
        assertEquals(Unit.GRAM, recipeIngredient1.getIngredient().getUnit());
        assertEquals(BigDecimal.ONE, recipeIngredient1.getQuantity());
        assertEquals(GARLIC, recipeIngredient2.getIngredient().getName());
        assertEquals(Unit.ML, recipeIngredient2.getIngredient().getUnit());
        assertEquals(BigDecimal.TWO, recipeIngredient2.getQuantity());
    }

    private CreateRecipeRequest getCreateRecipeRequest() {
        RecipeIngredientRequest ingredient1dto = new RecipeIngredientRequest(
                ONION, BigDecimal.ONE, Unit.GRAM
        );

        RecipeIngredientRequest ingredient2dto = new RecipeIngredientRequest(
                GARLIC, BigDecimal.TWO, Unit.ML
        );

        return new CreateRecipeRequest(
                RECIPE_NAME,
                2,
                List.of(ingredient1dto, ingredient2dto),
                INSTRUCTIONS,
                CREATED_BY
        );
    }

    // create recipe existing ingredients

    // create recipe empty list of ingredients passed in


    // get recipe when recipe exists

    // get recipe when recipe doesn't exist

}