package com.example.recipe_app.service;

import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.mapper.RecipeMapper;
import com.example.recipe_app.repository.IngredientRepository;
import com.example.recipe_app.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

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
        Recipe recipe = new Recipe();
    }

    // create recipe existing ingredients

    // create recipe empty list of ingredients passed in


    // get recipe when recipe exists

    // get recipe when recipe doesn't exist

}