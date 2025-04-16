package com.example.recipio.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth

data class Recipe(
    var imageUri: Uri = Uri.EMPTY,
    var imageUrl: String = "",
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
    var id: String = "",
    var createdAt: java.util.Date? = null,
    ) {

    fun toMap(): Map<String, Any> {
        val user = FirebaseAuth.getInstance().currentUser
        return mapOf(
            "image_url" to imageUrl,
            "name" to name,
            "description" to description,
            "tags" to tags,
            "steps" to steps,
            "ingredients" to ingredients.map { it.toMap() },
            "numberOfPeople" to numberOfPeople,
            "time" to time,
            "notes" to notes,
            "isFavorite" to isFavorite,
            "category" to category,
            "userId" to (user?.uid ?: "")
        )
    }
}

