package com.example.recipio.data

data class RecipeUiState (
    var recipes : List<Recipe> = emptyList(),
    var selectedRecipe : Recipe = Recipe(),
    var filteredRecipes : List<Recipe> = emptyList()
)
