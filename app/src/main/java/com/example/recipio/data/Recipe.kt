package com.example.recipio.data

import com.google.firebase.auth.FirebaseAuth

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
    var time: Int = 0,
    var notes: String = "",
    var id: String = ""
) {

    fun toMap(): Map<String, Any> {
        val user = FirebaseAuth.getInstance().currentUser
        return mapOf(
            "name" to name,
            "description" to description,
            "tags" to tags,
            "steps" to steps,
            "ingredients" to ingredients.map { it.toMap() },
            "numberOfPeople" to numberOfPeople,
            "time" to time,
            "notes" to notes,
            "isFavorite" to isFavorite,
            "userId" to (user?.uid ?: "") // Add the userId
        )
    }
}

