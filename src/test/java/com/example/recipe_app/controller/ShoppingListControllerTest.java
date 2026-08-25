package com.example.recipe_app.controller;

import com.example.recipe_app.dto.*;
import com.example.recipe_app.service.ShoppingListService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingListController.class)
class ShoppingListControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ShoppingListService shoppingListService;

    @Test
    void addShoppingList_returnsCreated() throws Exception {
        List<Long> recipeIds = List.of(1L, 2L, 3L);
        BuildShoppingListRequest request = new BuildShoppingListRequest(recipeIds);

        ShoppingListResponse shoppingListResponse = new ShoppingListResponse(
                1L, List.of()
        );

        when(shoppingListService.buildShoppingList(recipeIds)).thenReturn(shoppingListResponse);

        mockMvc.perform(post("/shopping-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients", hasSize(0)));
    }

    @Test
    void getShoppingList_returnsOk() throws Exception {
        ShoppingListResponse shoppingListResponse = new ShoppingListResponse(
                1L, List.of()
        );

        when(shoppingListService.retrieveShoppingList(1L)).thenReturn(shoppingListResponse);

        mockMvc.perform(get("/shopping-lists/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(shoppingListResponse)));
    }

    @Test
    void getShoppingList_returnsNotFound() throws Exception {
        when(shoppingListService.retrieveShoppingList(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/shopping-lists/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShoppingLists_returnsOk() throws Exception {
        ShoppingListResponse shoppingListResponse = new ShoppingListResponse(
                1L, List.of()
        );

        when(shoppingListService.getAllShoppingLists()).thenReturn(List.of(shoppingListResponse));

        mockMvc.perform(get("/shopping-lists"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(shoppingListResponse))));
    }

    @Test
    void getShoppingLists_returnsEmptyList() throws Exception {
        when(shoppingListService.getAllShoppingLists()).thenReturn(List.of());

        mockMvc.perform(get("/shopping-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addShoppingListWithEmptyRecipeIds_ReturnsBadRequest() throws Exception {
        BuildShoppingListRequest request = new BuildShoppingListRequest(List.of());

        mockMvc.perform(post("/shopping-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.recipeIds").value("At least one recipe ID is required"));

        verify(shoppingListService, never()).buildShoppingList(any());
    }
}