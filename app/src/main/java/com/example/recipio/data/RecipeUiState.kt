package com.example.recipio.data

data class RecipeUiState (
    var recipes : List<Recipe> = emptyList(),
    var selectedRecipe : Recipe = Recipe(name="Ceci est test pour voir si le transfers entre consultation et modifier marche"),
    var filteredRecipes : List<Recipe> = emptyList()
)
