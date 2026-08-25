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

/**
 * Service responsible for creating and retrieving shopping lists.
 */
@Service
public class ShoppingListService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;

    public ShoppingListService(RecipeIngredientRepository recipeIngredientRepository, ShoppingListRepository shoppingListRepository, ShoppingListMapper shoppingListMapper) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
    }

    /**
     * Builds a shopping list from the selected recipes.
     * Ingredients shared by multiple recipes are combined and their
     * quantities are summed.
     *
     * @param recipeIds the IDs of the recipes to include
     * @return the created shopping list
     */
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

    /**
     * Retrieves a shopping list by its ID.
     *
     * @param shoppingListId the shopping list ID
     * @return the requested shopping list
     * @throws EntityNotFoundException if the shopping list does not exist
     */
    @Transactional
    public ShoppingListResponse retrieveShoppingList(Long shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping List not found with shoppingListId: " + shoppingListId));
        return shoppingListMapper.toShoppingListResponse(shoppingList);
    }

    /**
     * Retrieves all shopping lists.
     *
     * @return a list of all shopping lists
     */
    @Transactional
    public List<ShoppingListResponse> getAllShoppingLists() {
        return shoppingListRepository.findAll().stream()
                .map(shoppingListMapper::toShoppingListResponse)
                .toList();
    }
}
