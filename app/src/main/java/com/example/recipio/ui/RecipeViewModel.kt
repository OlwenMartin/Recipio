package com.example.recipio.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.R
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.example.recipio.data.RecipeUiState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecipeViewModel() : ViewModel(){
    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState
    private var recipes : List<Recipe> = emptyList()

    suspend fun fetchRecipes(): List<Recipe> {
        val db = Firebase.firestore
        return try {
            val result = db.collection("recipes").get().await()
            result.documents.map { document ->
                Recipe(
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    tags = document.get("tags") as? List<String> ?: listOf(),
                    steps = document.get("steps+") as? List<String> ?: listOf(),
                    ingredients = listOf(Ingredient("ing1",30.0,"g")),
                    numberOfPeople = document.getLong("numberOfPeople")?.toInt() ?: 0,
                    time = document.getLong("time")?.toInt() ?: 0,
                    notes = document.getString("notes").toString(),
                    image = R.drawable.exemple_image,
                    isFavorite = document.getBoolean("favorite") == true,//pour la valeur par défaut
                    id = document.id
                )
            }
        } catch (exception: Exception) {
            Log.w("Recipio", "Error getting documents.", exception)
            emptyList()
        }
    }

    fun filterRecipes(filter : String){
        val filteredList = recipes.filter { it.name.startsWith(filter) }
        _uiState.update { currentState ->
            currentState.copy(filteredRecipes = filteredList)
        }
    }

    fun getRecipes() {
        viewModelScope.launch {
            recipes  = fetchRecipes()

            _uiState.update { currentState ->
                currentState.copy(recipes = recipes, filteredRecipes = recipes)
            }
        }
    }

    fun addRecipe(recipe: Recipe){
        val db = Firebase.firestore

        db.collection("recipes")
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "RECIPIO",
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                Log.w(
                    "RECIPIO",
                    "Error adding document",
                    e
                )
            }
    }
}
