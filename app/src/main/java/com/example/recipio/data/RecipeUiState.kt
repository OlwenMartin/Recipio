package com.example.recipio.data

data class RecipeUiState (
    val recipes : List<Recipe> = emptyList(),
    var selectedRecipe : Recipe = Recipe(),
    val filteredRecipes : List<Recipe> = emptyList()
)
