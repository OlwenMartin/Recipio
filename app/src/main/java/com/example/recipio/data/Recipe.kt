package com.example.recipio.data

data class Recipe(
    var image: Int = 0,
    var isFavorite: Boolean = false,
    var category: String = "",
    var name: String = "",
    var description: String = "",
    var tags: List<String> = listOf(),
    var steps: List<String> = listOf(),
    var ingredients: List<Ingredient> = listOf(),
    var numberOfPeople: Int = 4,
    var time: Int? = 0,
    var notes: String = "",
    var id: String = ""
)
