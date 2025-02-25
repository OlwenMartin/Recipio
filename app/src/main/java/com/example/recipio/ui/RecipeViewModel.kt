package com.example.recipio.ui

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
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
import kotlinx.coroutines.flow.asStateFlow
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
                    tags = listOf("Vegan", "Vegetarian"),
                    steps = listOf("etape 1","etape 2"),
                    ingredients = listOf(Ingredient("ing1",30,"g")),
                    numberOfPeople = 4,
                    time = 30,
                    notes = "Notes supplémentaires",
                    image = R.drawable.exemple_image,
                    isFavorite = false
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
}
