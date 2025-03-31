package com.example.recipio.data

import com.example.recipio.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class RecipeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())

    val recipes: StateFlow<List<Recipe>> = _recipes

    suspend fun fetchRecipes() {
        try {
            val snapshot = db.collection("recipes").get().await()
            val recipeList = snapshot.documents.map { doc ->
                Recipe(
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    image = R.drawable.default_dish_image,
                    isFavorite = doc.getBoolean("isFavorite") ?: false,
                    tags = doc.get("tags") as? List<String> ?: listOf(),
                    steps = doc.get("steps") as? List<String> ?: listOf(),
                    ingredients = listOf(),
                    numberOfPeople = doc.getLong("numberOfPeople")?.toInt() ?: 4,
                    time = doc.getLong("time")?.toInt() ?: 0,
                    notes = doc.getString("notes") ?: ""
                )
            }
            _recipes.value = recipeList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
