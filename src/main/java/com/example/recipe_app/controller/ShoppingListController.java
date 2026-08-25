package com.example.recipe_app.controller;

import com.example.recipe_app.dto.BuildShoppingListRequest;
import com.example.recipe_app.dto.ShoppingListResponse;
import com.example.recipe_app.service.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing shopping lists.
 */
@RestController
@RequestMapping("/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    /**
     * Creates a shopping list from the selected recipes.
     *
     * @param buildShoppingListRequest the recipe IDs to include
     * @return the created shopping list
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResponse addShoppingList(@RequestBody BuildShoppingListRequest buildShoppingListRequest) {
        return shoppingListService.buildShoppingList(buildShoppingListRequest.recipeIds());
    }

    /**
     * Retrieves a shopping list by its ID.
     *
     * @param id the shopping list ID
     * @return the requested shopping list
     */
    @GetMapping("/{id}")
    public ShoppingListResponse getShoppingList(@PathVariable Long id) {
        return shoppingListService.retrieveShoppingList(id);
    }

    /**
     * Retrieves all shopping lists.
     *
     * @return a list of all shopping lists
     */
    @GetMapping
    public List<ShoppingListResponse> getShoppingLists() {
        return shoppingListService.getAllShoppingLists();
    }
}
