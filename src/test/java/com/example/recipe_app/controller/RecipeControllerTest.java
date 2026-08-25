package com.example.recipe_app.controller;

import com.example.recipe_app.dto.CreateRecipeRequest;
import com.example.recipe_app.dto.RecipeIngredientRequest;
import com.example.recipe_app.dto.RecipeIngredientResponse;
import com.example.recipe_app.dto.RecipeResponse;
import com.example.recipe_app.entity.Unit;
import com.example.recipe_app.service.RecipeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    public static final String RECIPE_NAME = "recipe-name";
    public static final String INSTRUCTIONS = "instructions";
    public static final String CREATED_BY = "created-by";
    public static final String INGREDIENT_NAME = "ingredient-name";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RecipeService recipeService;

    @Test
    void addRecipe_returnsCreated() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(
                RECIPE_NAME, 4, getFilledRecipeIngredientRequest(), INSTRUCTIONS, CREATED_BY
        );

        RecipeResponse response = new RecipeResponse(
                1L, RECIPE_NAME, 4, List.of(getRecipeIngredientResponse()), INSTRUCTIONS, CREATED_BY
        );

        when(recipeService.createRecipe(any(CreateRecipeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(RECIPE_NAME));
    }

    @Test
    void getRecipe_returnsOk() throws Exception {
        RecipeResponse response = new RecipeResponse(
                1L, RECIPE_NAME, 4, List.of(getRecipeIngredientResponse()), INSTRUCTIONS, CREATED_BY
        );

        when(recipeService.getRecipe(1L)).thenReturn(response);

        mockMvc.perform(get("/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getRecipe_returnsNotFound() throws Exception {
        when(recipeService.getRecipe(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/recipes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRecipes_returnsOk() throws Exception {
        RecipeResponse response = new RecipeResponse(
                1L, RECIPE_NAME, 4, List.of(getRecipeIngredientResponse()), INSTRUCTIONS, CREATED_BY
        );

        when(recipeService.getAllRecipes()).thenReturn(List.of(response));

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    void getAllRecipes_returnsNoRecipes() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(List.of());

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // method 1
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().json("[]")); // method 2 (no need for both)
    }

    @Test
    void createRecipeWithBlankNameReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest("",
                2,
                getFilledRecipeIngredientRequest(),
                INSTRUCTIONS,
                CREATED_BY);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Recipe name must not be blank"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithNegativePortionsReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(RECIPE_NAME,
                -1,
                getFilledRecipeIngredientRequest(),
                INSTRUCTIONS,
                CREATED_BY);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.portions").value("Portions must be greater than 0"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithBlankInstructionsReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(RECIPE_NAME,
                2,
                getFilledRecipeIngredientRequest(),
                " ",
                CREATED_BY);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.instructions").value("Instructions must not be blank"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithBlankCreatedByReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(RECIPE_NAME,
                2,
                getFilledRecipeIngredientRequest(),
                INSTRUCTIONS,
                " ");

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.createdBy").value("Created by must not be blank"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithBlankIngredientNameReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(RECIPE_NAME,
                2,
                getRecipeIngredientRequest(
                        " ",
                        BigDecimal.ONE,
                        Unit.TABLESPOON
                ),
                INSTRUCTIONS,
                CREATED_BY);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['ingredients[0].name']")
                        .value("Ingredient name must not be blank"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithNegativeIngredientQuantityReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(RECIPE_NAME,
                2,
                getRecipeIngredientRequest(
                        INGREDIENT_NAME,
                        BigDecimal.valueOf(-1),
                        Unit.TABLESPOON
                ),
                INSTRUCTIONS,
                CREATED_BY);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['ingredients[0].quantity']")
                        .value("Quantity must be greater than 0"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithNullUnitReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(RECIPE_NAME,
                2,
                getRecipeIngredientRequest(
                        INGREDIENT_NAME,
                        BigDecimal.ONE,
                        null
                ),
                INSTRUCTIONS,
                CREATED_BY);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['ingredients[0].unit']")
                        .value("Unit must not be null"));

        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipeWithEverythingEmptyReturnsBadRequest() throws Exception {
        CreateRecipeRequest request = getCreateRecipeRequest(" ",
                -1,
                getRecipeIngredientRequest(
                        " ",
                        BigDecimal.valueOf(-1),
                        null
                ),
                " ",
                " ");

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Recipe name must not be blank"))
                .andExpect(jsonPath("$.portions").value("Portions must be greater than 0"))
                .andExpect(jsonPath("$.instructions").value("Instructions must not be blank"))
                .andExpect(jsonPath("$.createdBy").value("Created by must not be blank"))
                .andExpect(jsonPath("$.['ingredients[0].name']")
                        .value("Ingredient name must not be blank"))
                .andExpect(jsonPath("$.['ingredients[0].quantity']")
                        .value("Quantity must be greater than 0"))
                .andExpect(jsonPath("$.['ingredients[0].unit']")
                        .value("Unit must not be null"));

        verify(recipeService, never()).createRecipe(any());
    }

    private CreateRecipeRequest getCreateRecipeRequest(String name,
                                                       int portions,
                                                       RecipeIngredientRequest recipeIngredientRequest,
                                                       String instructions,
                                                       String createdBy) {
        return new CreateRecipeRequest(
                name,
                portions,
                List.of(recipeIngredientRequest),
                instructions,
                createdBy
        );
    }

    private RecipeIngredientRequest getRecipeIngredientRequest(String name, BigDecimal quantity, Unit unit) {
        return new RecipeIngredientRequest(
                name,
                quantity,
                unit
        );
    }

    private RecipeIngredientRequest getFilledRecipeIngredientRequest() {
        return new RecipeIngredientRequest(
                INGREDIENT_NAME,
                BigDecimal.ONE,
                Unit.TABLESPOON
        );
    }

    private static RecipeIngredientResponse getRecipeIngredientResponse() {
        return RecipeIngredientResponse.builder()
                .id(1L)
                .name(INGREDIENT_NAME)
                .quantity(BigDecimal.ONE)
                .unit(Unit.TABLESPOON)
                .build();
    }
}