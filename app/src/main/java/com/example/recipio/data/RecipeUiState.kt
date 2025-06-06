package com.example.recipio.data

data class RecipeUiState (
    var recipes : List<Recipe> = emptyList(),
    var selectedRecipe : Recipe = Recipe(),
    var filteredRecipes : List<Recipe> = emptyList(),
    val recentRecipes: List<Recipe> = emptyList(),

    //j'ai ajouté
    val isLoading: Boolean = false,
    val error: String? = null
)
