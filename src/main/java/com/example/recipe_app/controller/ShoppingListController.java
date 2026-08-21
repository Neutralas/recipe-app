package com.example.recipe_app.controller;

import com.example.recipe_app.dto.BuildShoppingListRequest;
import com.example.recipe_app.dto.ShoppingListResponse;
import com.example.recipe_app.service.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResponse addShoppingList(@RequestBody BuildShoppingListRequest buildShoppingListRequest) {
        return shoppingListService.buildShoppingList(buildShoppingListRequest.recipeIds());
    }

    @GetMapping("/{id}")
    public ShoppingListResponse getShoppingList(@PathVariable Long id) {
        return shoppingListService.retrieveShoppingList(id);
    }

    @GetMapping
    public List<ShoppingListResponse> getShoppingLists() {
        return shoppingListService.getAllShoppingLists();
    }
}
