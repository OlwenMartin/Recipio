package com.example.recipio.data

data class RecipeUiState (
    val recipes : List<Recipe> = emptyList(),
    val filteredRecipes : List<Recipe> = emptyList()
)
