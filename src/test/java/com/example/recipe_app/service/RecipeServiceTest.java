package com.example.recipe_app.service;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeIngredientRequest;
import com.example.recipe_app.dto.RecipeIngredientResponse;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.entity.Ingredient;
import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.entity.RecipeIngredient;
import com.example.recipe_app.mapper.RecipeMapper;
import com.example.recipe_app.repository.IngredientRepository;
import com.example.recipe_app.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private IngredientRepository ingredientRepository;

    private final RecipeMapper recipeMapper = new RecipeMapper();
    private RecipeService testee;

    @BeforeEach
    void setUp() {
        testee = new RecipeService(recipeRepository, recipeMapper, ingredientRepository);
    }

    // create recipe new ingredients
    @Test
    void createRecipeWithNewIngredients() {

        CreateRecipeRequest createRecipeRequest = getCreateRecipeRequest();

        when(ingredientRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecipeResponse result = testee.createRecipe(createRecipeRequest);

        // check RecipeResponse fields:
        assertEquals(RECIPE_NAME, result.name());
        assertEquals(2, result.portions());
        assertEquals(INSTRUCTIONS, result.instructions());
        assertEquals(CREATED_BY, result.createdBy());

        RecipeIngredientResponse recipeIngredient1 = result.ingredients().stream()
                .filter(ri -> ONION.equals(ri.name()))
                .findFirst().orElseThrow();
        RecipeIngredientResponse recipeIngredient2 = result.ingredients().stream()
                .filter(ri -> GARLIC.equals(ri.name()))
                .findFirst().orElseThrow();

        assertEquals(ONION, recipeIngredient1.name());
        assertEquals(GRAM, recipeIngredient1.unit());
        assertEquals(BigDecimal.ONE, recipeIngredient1.quantity());
        assertEquals(GARLIC, recipeIngredient2.name());
        assertEquals(ML, recipeIngredient2.unit());
        assertEquals(BigDecimal.TWO, recipeIngredient2.quantity());

        // to be able to check Recipe fields as it is not returned by the method call any more
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();

        assertNotNull(savedRecipe.getCreatedAt());

        RecipeIngredient savedIngredient1 = savedRecipe.getRecipeIngredients().stream()
                .filter(ri -> ONION.equals(ri.getIngredient().getName()))
                .findFirst().orElseThrow();
        RecipeIngredient savedIngredient2 = savedRecipe.getRecipeIngredients().stream()
                .filter(ri -> GARLIC.equals(ri.getIngredient().getName()))
                .findFirst().orElseThrow();

        assertEquals(savedRecipe, savedIngredient1.getRecipe());
        assertEquals(savedRecipe, savedIngredient2.getRecipe());
    }

    // create recipe existing ingredients
    @Test
    void createRecipeWithExistingIngredients() {

        CreateRecipeRequest createRecipeRequest = getCreateRecipeRequest();
        Ingredient ingredient1 = new Ingredient(0L, ONION, GRAM);
        Ingredient ingredient2 = new Ingredient(1L, GARLIC, ML);

        when(ingredientRepository.findByName(ONION)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.findByName(GARLIC)).thenReturn(Optional.of(ingredient2));

        RecipeResponse result = testee.createRecipe(createRecipeRequest);

        // check RecipeResponse fields:
        assertEquals(RECIPE_NAME, result.name());
        assertEquals(2, result.portions());
        assertEquals(INSTRUCTIONS, result.instructions());
        assertEquals(CREATED_BY, result.createdBy());
        verify(ingredientRepository, never()).save(any(Ingredient.class));

        RecipeIngredientResponse recipeIngredient1 = result.ingredients().stream()
                .filter(ri -> ONION.equals(ri.name()))
                .findFirst().orElseThrow();
        RecipeIngredientResponse recipeIngredient2 = result.ingredients().stream()
                .filter(ri -> GARLIC.equals(ri.name()))
                .findFirst().orElseThrow();

        assertEquals(ONION, recipeIngredient1.name());
        assertEquals(GRAM, recipeIngredient1.unit());
        assertEquals(BigDecimal.ONE, recipeIngredient1.quantity());
        assertEquals(GARLIC, recipeIngredient2.name());
        assertEquals(ML, recipeIngredient2.unit());
        assertEquals(BigDecimal.TWO, recipeIngredient2.quantity());

        // to be able to check Recipe fields as it is not returned by the method call any more
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();

        assertNotNull(savedRecipe.getCreatedAt());

        RecipeIngredient savedIngredient1 = savedRecipe.getRecipeIngredients().stream()
                .filter(ri -> ONION.equals(ri.getIngredient().getName()))
                .findFirst().orElseThrow();
        RecipeIngredient savedIngredient2 = savedRecipe.getRecipeIngredients().stream()
                .filter(ri -> GARLIC.equals(ri.getIngredient().getName()))
                .findFirst().orElseThrow();

        assertEquals(savedRecipe, savedIngredient1.getRecipe());
        assertEquals(savedRecipe, savedIngredient2.getRecipe());
    }

    // create recipe empty list of ingredients passed in
    @Test
    void createRecipeWithNoIngredients() {
        CreateRecipeRequest createRecipeRequest = getCreateRecipeRequestEmptyIngredients();

        RecipeResponse result = testee.createRecipe(createRecipeRequest);

        assertEquals(RECIPE_NAME, result.name());
        assertEquals(2, result.portions());
        assertEquals(INSTRUCTIONS, result.instructions());
        assertEquals(CREATED_BY, result.createdBy());
        assertEquals(List.of(), result.ingredients());

        // to be able to check Recipe fields as it is not returned by the method call any more
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();

        assertNotNull(savedRecipe.getCreatedAt());
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

        when(recipeRepository.findById(0L)).thenReturn(Optional.of(foundRecipe));

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

    // get recipes when recipes exist
    @Test
    void getRecipesWhenRecipesExist() {
        Recipe foundRecipe = Recipe.builder()
                .id(0L)
                .name(RECIPE_NAME)
                .portions(2)
                .recipeIngredients(List.of())
                .instructions(INSTRUCTIONS)
                .createdBy(CREATED_BY)
                .createdAt(LocalDateTime.now())
                .build();

        when(recipeRepository.findAll()).thenReturn(List.of(foundRecipe));

        List<RecipeResponse> result = testee.getAllRecipes();

        assertEquals(0L, result.getFirst().id());
        assertEquals(RECIPE_NAME, result.getFirst().name());
        assertEquals(2, result.getFirst().portions());
        assertEquals(List.of(), result.getFirst().ingredients());
        assertEquals(INSTRUCTIONS, result.getFirst().instructions());
        assertEquals(CREATED_BY, result.getFirst().createdBy());
    }

    // get recipes when no recipes exist
    @Test
    void getRecipesWhenNoRecipesExist() {
        when(recipeRepository.findAll()).thenReturn(List.of());

        List<RecipeResponse> result = testee.getAllRecipes();

        assertTrue(result.isEmpty());
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
}