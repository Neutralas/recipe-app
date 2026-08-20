package com.example.recipe_app.controller;

import com.example.recipe_app.dto.CreateRecipeRequest;
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
import static org.mockito.Mockito.when;
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
        CreateRecipeRequest request = new CreateRecipeRequest(
                RECIPE_NAME, 4, List.of(), INSTRUCTIONS, CREATED_BY
        );

        RecipeResponse response = new RecipeResponse(
                1L, RECIPE_NAME, 4, List.of(), INSTRUCTIONS, CREATED_BY
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
        RecipeIngredientResponse riResponse = RecipeIngredientResponse.builder()
                .id(1L)
                .name(INGREDIENT_NAME)
                .quantity(BigDecimal.ONE)
                .unit(Unit.TABLESPOON)
                .build();

        RecipeResponse response = new RecipeResponse(
                1L, RECIPE_NAME, 4, List.of(riResponse), INSTRUCTIONS, CREATED_BY
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
        RecipeIngredientResponse riResponse = RecipeIngredientResponse.builder()
                .id(1L)
                .name(INGREDIENT_NAME)
                .quantity(BigDecimal.ONE)
                .unit(Unit.TABLESPOON)
                .build();

        RecipeResponse response = new RecipeResponse(
                1L, RECIPE_NAME, 4, List.of(riResponse), INSTRUCTIONS, CREATED_BY
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
}