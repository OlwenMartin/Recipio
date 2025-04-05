package com.example.recipio.data

data class RecipeUiState (
    var recipes : List<Recipe> = emptyList(),
    var selectedRecipe : Recipe = Recipe(
        category = "Entrée",
        name="Ceci est test pour voir si le transfers entre consultation et modifier marche",
        description = "ceci est une description",
        ingredients = listOf(Ingredient("test",100,"g"),
            Ingredient("test2",50,"oz")),
        steps = listOf("test","test2"),
        tags = listOf("test","test2"),
        notes = "test",
    ),
    var filteredRecipes : List<Recipe> = emptyList()
)
