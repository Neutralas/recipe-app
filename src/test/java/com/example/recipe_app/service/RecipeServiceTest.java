package com.example.recipe_app.service;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeIngredientRequest;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.entity.Ingredient;
import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.entity.RecipeIngredient;
import com.example.recipe_app.mapper.RecipeMapper;
import com.example.recipe_app.repository.IngredientRepository;
import com.example.recipe_app.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.recipe_app.entity.Unit.GRAM;
import static com.example.recipe_app.entity.Unit.ML;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        assertEquals(GRAM, recipeIngredient1.getIngredient().getUnit());
        assertEquals(BigDecimal.ONE, recipeIngredient1.getQuantity());
        assertEquals(result, recipeIngredient1.getRecipe());
        assertEquals(GARLIC, recipeIngredient2.getIngredient().getName());
        assertEquals(ML, recipeIngredient2.getIngredient().getUnit());
        assertEquals(BigDecimal.TWO, recipeIngredient2.getQuantity());
        assertEquals(result, recipeIngredient2.getRecipe());
    }

    private CreateRecipeRequest getCreateRecipeRequest() {
        RecipeIngredientRequest ingredient1dto = new RecipeIngredientRequest(
                ONION, BigDecimal.ONE, GRAM
        );

        RecipeIngredientRequest ingredient2dto = new RecipeIngredientRequest(
                GARLIC, BigDecimal.TWO, ML
        );

        return new CreateRecipeRequest(
                RECIPE_NAME,
                2,
                List.of(ingredient1dto, ingredient2dto),
                INSTRUCTIONS,
                CREATED_BY
        );
    }

    private CreateRecipeRequest getCreateRecipeRequestEmptyIngredients() {
        return new CreateRecipeRequest(
                RECIPE_NAME,
                2,
                List.of(),
                INSTRUCTIONS,
                CREATED_BY
        );
    }

    // create recipe existing ingredients
    @Test
    void createRecipeWithExistingIngredients() {

        CreateRecipeRequest createRecipeRequest = getCreateRecipeRequest();
        Ingredient ingredient1 = new Ingredient(0L, ONION, GRAM);
        Ingredient ingredient2 = new Ingredient(1L, GARLIC, ML);

        when(ingredientRepository.findByName(ONION)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.findByName(GARLIC)).thenReturn(Optional.of(ingredient2));

        Recipe result = testee.createRecipe(createRecipeRequest);

        assertEquals(RECIPE_NAME, result.getName());
        assertEquals(2, result.getPortions());
        assertEquals(INSTRUCTIONS, result.getInstructions());
        assertEquals(CREATED_BY, result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
        verify(ingredientRepository, never()).save(any(Ingredient.class));

        RecipeIngredient recipeIngredient1 = result.getRecipeIngredients().stream()
                .filter(ri -> ONION.equals(ri.getIngredient().getName()))
                .findFirst()
                .orElseThrow();
        RecipeIngredient recipeIngredient2 = result.getRecipeIngredients().stream()
                .filter(ri -> GARLIC.equals(ri.getIngredient().getName()))
                .findFirst()
                .orElseThrow();

        assertEquals(ONION, recipeIngredient1.getIngredient().getName());
        assertEquals(GRAM, recipeIngredient1.getIngredient().getUnit());
        assertEquals(BigDecimal.ONE, recipeIngredient1.getQuantity());
        assertEquals(result, recipeIngredient1.getRecipe());
        assertEquals(GARLIC, recipeIngredient2.getIngredient().getName());
        assertEquals(ML, recipeIngredient2.getIngredient().getUnit());
        assertEquals(BigDecimal.TWO, recipeIngredient2.getQuantity());
        assertEquals(result, recipeIngredient2.getRecipe());
    }

    // create recipe empty list of ingredients passed in
    @Test
    void createRecipeWithNoIngredients() {
        CreateRecipeRequest createRecipeRequest = getCreateRecipeRequestEmptyIngredients();

        Recipe result = testee.createRecipe(createRecipeRequest);

        assertEquals(RECIPE_NAME, result.getName());
        assertEquals(2, result.getPortions());
        assertEquals(INSTRUCTIONS, result.getInstructions());
        assertEquals(CREATED_BY, result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
        assertEquals(List.of(), result.getRecipeIngredients());
    }

    // get recipe when recipe exists
    @Test
    void getRecipeWhenRecipeExists() {
        Recipe foundRecipe = Recipe.builder()
                .id(0L)
                .name(RECIPE_NAME)
                .portions(2)
                .recipeIngredients(List.of())
                .instructions(INSTRUCTIONS)
                .createdBy(CREATED_BY)
                .createdAt(LocalDateTime.now())
                .build();

        RecipeResponse mappedRecipeResponse = RecipeResponse.builder()
                .id(0L)
                .name(RECIPE_NAME)
                .portions(2)
                .ingredients(List.of())
                .instructions(INSTRUCTIONS)
                .createdBy(CREATED_BY)
                .build();

        when(recipeRepository.findById(0L)).thenReturn(Optional.of(foundRecipe));
        when(recipeMapper.toRecipeResponse(foundRecipe)).thenReturn(mappedRecipeResponse);

        RecipeResponse result = testee.getRecipe(0L);

        assertEquals(0L, result.id());
        assertEquals(RECIPE_NAME, result.name());
        assertEquals(2, result.portions());
        assertEquals(List.of(), result.ingredients());
        assertEquals(INSTRUCTIONS, result.instructions());
        assertEquals(CREATED_BY, result.createdBy());
    }

    // get recipe when recipe doesn't exist
    @Test
    void getRecipeWhenRecipeDoesNotExist() {
        when(recipeRepository.findById(0L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> testee.getRecipe(0L));

        String expectedMessage = "Recipe not found with id: 0";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}