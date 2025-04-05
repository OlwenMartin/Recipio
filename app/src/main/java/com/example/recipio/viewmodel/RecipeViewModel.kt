package com.example.recipio.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.R
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    fun getRecipes() {
        viewModelScope.launch {
            val fetchedRecipes = fetchUserRecipes()
            _recipes.value = fetchedRecipes
        }
    }

    private suspend fun fetchUserRecipes(): List<Recipe> {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.w("Recipio", "Utilisateur non authentifié.")
            return emptyList()
        }

        return try {
            Log.d("Recipio", "Fetching recipes for user: ${user.uid}")

            val result = db.collection("recipes")
                .whereEqualTo("userId", user.uid)
                .get()
                .await()

            Log.d("Recipio", "Found ${result.documents.size} recipes")

            result.documents.map { document ->
                Recipe(
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    tags = document.get("tags") as? List<String> ?: listOf(),
                    steps = document.get("steps") as? List<String> ?: listOf(),
                    ingredients = listOf(Ingredient("ing1", 30.0, "g")),
                    numberOfPeople = document.getLong("numberOfPeople")?.toInt() ?: 4,
                    time = document.getLong("time")?.toInt() ?: 30,
                    notes = document.getString("notes") ?: "",
                    image = R.drawable.default_dish_image, // juste pour tester: default image
                    isFavorite = document.getBoolean("isFavorite") ?: false
                )
            }
        } catch (exception: Exception) {
            Log.w("Recipio", "Erreur lors de la récupération des recettes.", exception)
            exception.printStackTrace()
            emptyList()
        }
    }

    fun addRandomRecipe() {
        val randomRecipe = Recipe(
            name = "Recette aléatoire",
            description = "Une recette ajoutée au hasard",
            image = R.drawable.default_dish_image
        )

        addRecipe(randomRecipe)
    }

    fun addRecipe(recipe: Recipe) {
        val updatedList = _recipes.value.toMutableList()
        updatedList.add(recipe)
        _recipes.value = updatedList
    }

    fun addRecipeToUser(recipe: Recipe) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore

        if (user == null) {
            Log.w("Recipio", "Utilisateur non authentifié.")
            return
        }

        viewModelScope.launch {
            try {
                val recipeWithUserId = recipe.copy()

                // Ajout de la recette à la collection "recipes"
                val recipeRef = db.collection("recipes").add(recipe.toMap()).await()

                // Ajout de la référence de la recette dans le document de l'utilisateur
                val userRef = db.collection("users").document(user.uid)
                userRef.update("recipes", FieldValue.arrayUnion(recipeRef.id))
                    .await()

                Log.d("Recipio", "Recette ajoutée avec succès à l'utilisateur")

                getRecipes()
            } catch (e: Exception) {
                Log.e("Recipio", "Erreur lors de l'ajout de la recette", e)
            }
        }
    }



}
