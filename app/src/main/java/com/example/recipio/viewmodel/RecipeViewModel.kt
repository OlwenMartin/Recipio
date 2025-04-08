package com.example.recipio.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.R
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.example.recipio.data.RecipeUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState
    var recipes : List<Recipe> = emptyList()

    fun getRecipes() {
        viewModelScope.launch {
            recipes  = fetchUserRecipes()

            _uiState.update { currentState ->
                currentState.copy(recipes = recipes, filteredRecipes = recipes)
            }
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
                    imageUri = Uri.EMPTY, // juste pour tester: default image
                    isFavorite = document.getBoolean("isFavorite") ?: false
                )
            }
        } catch (exception: Exception) {
            Log.w("Recipio", "Erreur lors de la récupération des recettes.", exception)
            exception.printStackTrace()
            emptyList()
        }
    }

    fun uploadImage(imageUri : Uri) : Task<String> {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var value = ""
        val fileRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        return fileRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }
                fileRef.downloadUrl
            }.continueWith { it.result.toString() }

        /*if (imageUri != null) {
            val fileRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("RECIPIO", "Image uploaded. Download URL: $uri")
                        value = uri.toString()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RECIPIO", "Upload failed", e)
                }
        } else {
            Log.e("RECIPIO", "No image selected")
        }
        return value*/
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
                if (recipe.imageUri != null) {
                    val imageUrl = uploadImage(recipe.imageUri).await()
                    recipe.imageUrl = imageUrl
                }

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

    fun filterRecipes(filter : String){
        val filteredList = recipes.filter { it.name.startsWith(filter) }
        _uiState.update { currentState ->
            currentState.copy(filteredRecipes = filteredList)
        }
    }
}
