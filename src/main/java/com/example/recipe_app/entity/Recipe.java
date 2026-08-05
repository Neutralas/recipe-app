package com.example.recipe_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Recipe {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    int portions;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    List<RecipeIngredient> recipeIngredients;

    String instructions;

    String createdBy;

    LocalDateTime createdAt;
}
