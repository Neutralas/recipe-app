package com.example.recipe_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    ShoppingList shoppingList;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Recipe> recipes;

    @ManyToOne(fetch = FetchType.LAZY)
    Ingredient ingredient;

    BigDecimal quantity;

    Boolean isChecked = false;
}
