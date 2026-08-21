package com.example.recipe_app.service;

import com.example.recipe_app.dto.ShoppingListResponse;
import com.example.recipe_app.entity.RecipeIngredient;
import com.example.recipe_app.entity.ShoppingList;
import com.example.recipe_app.entity.ShoppingListItem;
import com.example.recipe_app.mapper.ShoppingListMapper;
import com.example.recipe_app.repository.RecipeIngredientRepository;
import com.example.recipe_app.repository.ShoppingListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingListService {

    // create a shopping list
    // shopping list consists of shopping list items
    // each shopping list item is basically an ingredient, just with summed up quantities

    // i am given recipe ids, I can extract what recipe ingredients make them up
    // from recipe ingredient, I can get the ingredient and its quantity
    // I loop through all ingredients with the same name and add their quantities
        // and save them as a shopping list item
    // add all shopping list items to a single shopping list

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;

    public ShoppingListService(RecipeIngredientRepository recipeIngredientRepository, ShoppingListRepository shoppingListRepository, ShoppingListMapper shoppingListMapper) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
    }

    public ShoppingListResponse buildShoppingList(List<Long> recipeIds) {

        ShoppingList shoppingList = new ShoppingList();

        if (!recipeIds.isEmpty()) {
            List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findByRecipeIdIn(recipeIds);

            Map<Long, ShoppingListItem> itemsByIngredientId = new HashMap<>();

            recipeIngredients.forEach(recipeIngredient -> {
                Long ingredientId = recipeIngredient.getIngredient().getId();
                ShoppingListItem existingItem = itemsByIngredientId.get(ingredientId);

                if (existingItem == null) {
                    ShoppingListItem shoppingListItem = new ShoppingListItem();
                    shoppingListItem.setIngredient(recipeIngredient.getIngredient());
                    shoppingListItem.setQuantity(recipeIngredient.getQuantity());
                    shoppingListItem.setShoppingList(shoppingList);
                    itemsByIngredientId.put(ingredientId, shoppingListItem);
                } else {
                    existingItem.setQuantity(existingItem.getQuantity().add(recipeIngredient.getQuantity()));
                }
            });

            shoppingList.getItems().addAll(itemsByIngredientId.values());
        }

        shoppingListRepository.save(shoppingList);
        return shoppingListMapper.toShoppingListResponse(shoppingList);
    }

    @Transactional
    public ShoppingListResponse retrieveShoppingList(Long shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping List not found with shoppingListId: " + shoppingListId));
        return shoppingListMapper.toShoppingListResponse(shoppingList);
    }

    @Transactional
    public List<ShoppingListResponse> getAllShoppingLists() {
        return shoppingListRepository.findAll().stream()
                .map(shoppingListMapper::toShoppingListResponse)
                .toList();
    }
}
