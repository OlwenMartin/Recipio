package com.example.recipio.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.example.recipio.data.RecipeField
import com.example.recipio.data.RecipeUiState
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RecipeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState
    var recipes : List<Recipe> = emptyList()

    fun getRecipes() {
        viewModelScope.launch {
            try {
                val fetchedRecipes = fetchUserRecipes()
                val recentRecipesList = fetchRecentRecipes()

                Log.d("Recipio", "Fetched ${fetchedRecipes.size} recipes")

                recipes = fetchedRecipes

                _uiState.update { currentState ->
                    currentState.copy(
                        recipes = fetchedRecipes,
                        filteredRecipes = fetchedRecipes,
                        recentRecipes = recentRecipesList
                    )
                }
            } catch (e: Exception) {
                Log.e("Recipio", "Error fetching recipes", e)
            }
        }
    }

    fun getRecipe(recipeId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            try {
                val document = db.collection("recipes").document(recipeId).get().await()
                if (document.exists()) {
                    val recipe = Recipe(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        tags = document.get("tags") as? List<String> ?: listOf(),
                        steps = document.get("steps") as? List<String> ?: listOf(),
                        ingredients = (document.get("ingredients") as? List<Map<String, Any>>)?.map { map ->
                            Ingredient(
                                name = map["name"] as? String ?: "",
                                amount = (map["quantity"] as? Number)?.toDouble() ?: 0.0,
                                unit = map["unit"] as? String ?: ""
                            )
                        } ?: listOf(),
                        numberOfPeople = document.getLong("numberOfPeople")?.toInt() ?: 4,
                        time = document.getLong("time")?.toInt() ?: 30,
                        notes = document.getString("notes") ?: "",
                        imageUrl = document.getString("image_url") ?: "",
                        isFavorite = document.getBoolean("isFavorite") ?: false,
                        category = document.getString("category") ?: ""
                    )

                    Log.d("Recipio", "Fetched recipe: ${recipe.id}, name: ${recipe.name}")
                    _uiState.update { currentState ->
                        currentState.copy(selectedRecipe = recipe)
                    }
                } else {
                    Log.w("Recipio", "Recipe document not found: $recipeId")
                    _uiState.update { currentState ->
                        currentState.copy(selectedRecipe = Recipe())
                    }
                }
            } catch (e: Exception) {
                Log.e("Recipio", "Error fetching recipe by ID", e)
                _uiState.update { currentState ->
                    currentState.copy(selectedRecipe = Recipe())
                }
            }
        }
    }

    fun selectRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            currentState.copy(selectedRecipe = recipe)
        }
    }

    private suspend fun fetchRecentRecipes(): List<Recipe> {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.w("Recipio", "Utilisateur non authentifié.")
            return emptyList()
        }

        return try {
            Log.d("Recipio", "Fetching recent recipes for user: ${user.uid}")

            val result = db.collection("recipes")
                .whereEqualTo("userId", user.uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(8)
                .get()
                .await()

            Log.d("Recipio", "Found ${result.documents.size} recent recipes")

            result.documents.map { document ->
                Recipe(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    tags = document.get("tags") as? List<String> ?: listOf(),
                    steps = document.get("steps") as? List<String> ?: listOf(),
                    ingredients = listOf(Ingredient("ing1", 30.0, "g")),
                    numberOfPeople = document.getLong("numberOfPeople")?.toInt() ?: 4,
                    time = document.getLong("time")?.toInt() ?: 30,
                    notes = document.getString("notes") ?: "",
                    imageUrl = document.getString("image_url") ?: "",
                    isFavorite = document.getBoolean("isFavorite") ?: false,
                    category = document.getString("category")?: "",
                    createdAt = document.getTimestamp("createdAt")?.toDate()
                )
            }
        } catch (exception: Exception) {
            Log.w("Recipio", "Erreur lors de la récupération des recettes récentes.", exception)
            exception.printStackTrace()
            emptyList()
        }
    }

    private suspend fun fetchUserRecipes(): List<Recipe> {

        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("Recipio", "Current user: ${user?.uid ?: "null"}")


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

            Log.d("Recipio", "Query constructed, executing...")
            Log.d("Recipio", "Found ${result.documents.size} recipes")

            result.documents.map { document ->
                Recipe(
                    id = document.id, //on ajoute l'id du document comme id de la recette
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    tags = document.get("tags") as? List<String> ?: listOf(),
                    steps = document.get("steps") as? List<String> ?: listOf(),
                    //ingredients = listOf(Ingredient("ing1", 30.0, "g")),
                    ingredients = (document.get("ingredients") as? List<Map<String, Any>>)?.map { map ->
                        Ingredient(
                            name = map["name"] as? String ?: "",
                            amount = (map["quantity"] as? Number)?.toDouble() ?: 0.0,
                            unit = map["unit"] as? String ?: ""
                        )
                    } ?: listOf(),
                    numberOfPeople = document.getLong("numberOfPeople")?.toInt() ?: 4,
                    time = document.getLong("time")?.toInt() ?: 30,
                    notes = document.getString("notes") ?: "",
                    imageUrl = document.getString("image_url") ?: "",
                    isFavorite = document.getBoolean("isFavorite") ?: false,
                    category =  document.getString("category")?: "",
                    createdAt = document.getTimestamp("createdAt")?.toDate()
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

    }

    fun addRecipeToUser(recipe: Recipe, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore

        if (user == null) {
            Log.w("Recipio", "Utilisateur non authentifié.")
            return
        }
        viewModelScope.launch {
            try {
                if (recipe.imageUri != Uri.EMPTY) {
                    val imageUrl = uploadImage(recipe.imageUri).await()
                    recipe.imageUrl = imageUrl
                }

                val newRecipeData = recipe.toMap().toMutableMap()

                // Ajout du timestamp
                newRecipeData["createdAt"] = FieldValue.serverTimestamp()

                // Ajout dans Firestore avec le timestamp
                val recipeRef = db.collection("recipes").add(newRecipeData).await()
                val newRecipeId = recipeRef.id

                // Ajout de la référence de la recette dans le document de l'utilisateur
                val userRef = db.collection("users").document(user.uid)

                userRef.update("recipes", FieldValue.arrayUnion(newRecipeId))
                    .await()

                Log.d("Recipio", "Recette ajoutée avec succès à l'utilisateur")

                val newRecipe = recipe.copy(id = newRecipeId)
                _uiState.update { currentState ->
                    val updatedRecipes = currentState.recipes + newRecipe
                    val updatedRecentRecipes = listOf(newRecipe) + currentState.recentRecipes.take(7)

                    currentState.copy(
                        recipes = updatedRecipes,
                        filteredRecipes = updatedRecipes,
                        recentRecipes = updatedRecentRecipes
                    )
                }

                onSuccess(newRecipeId)
            } catch (e: Exception) {
                Log.e("Recipio", "Erreur lors de l'ajout de la recette", e)
                onError(e)
            }
        }
    }

    fun deleteRecipe(recipeId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.w("Recipio", "Utilisateur non authentifié.")
            onError(Exception("Utilisateur non authentifié"))
            return
        }

        if (recipeId.isBlank()) {
            Log.w("Recipio", "ID de la recette manquant.")
            onError(Exception("ID de la recette manquant"))
            return
        }

        viewModelScope.launch {
            try {
                //  Supprimer la recette de Firestore
                db.collection("recipes").document(recipeId)
                    .delete()
                    .await()

                // Supprimer la référence de la recette du document utilisateur
                val userRef = db.collection("users").document(user.uid)
                userRef.update("recipes", FieldValue.arrayRemove(recipeId))
                    .await()

                // Mettre à jour l'état local (supprimer la recette des listes)
                _uiState.update { currentState ->
                    val updatedRecipes = currentState.recipes.filter { it.id != recipeId }
                    val updatedFilteredRecipes = currentState.filteredRecipes.filter { it.id != recipeId }
                    val updatedRecentRecipes = currentState.recentRecipes.filter { it.id != recipeId }

                    currentState.copy(
                        recipes = updatedRecipes,
                        filteredRecipes = updatedFilteredRecipes,
                        recentRecipes = updatedRecentRecipes
                    )
                }

                Log.d("Recipio", "Recette supprimée avec succès: $recipeId")
                onSuccess()
            } catch (e: Exception) {
                Log.e("Recipio", "Erreur lors de la suppression de la recette", e)
                onError(e)
            }
        }
    }
    fun filterRecipes(key : String, value : String){
        var filteredList = emptyList<Recipe> ()
        when(key){
            RecipeField.Name.toString() -> filteredList = recipes.filter { it.name.startsWith(value) }
            RecipeField.Category.toString() -> filteredList = recipes.filter { it.category == value }
        }
        _uiState.update { currentState ->
            currentState.copy(filteredRecipes = filteredList)
        }
    }

    fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.w("Recipio", "Utilisateur non authentifié.")
            onError(Exception("Utilisateur non authentifié"))
            return
        }

        val recipeId = recipe.id
        if (recipeId.isBlank()) {
            Log.w("Recipio", "ID de la recette manquant.")
            onError(Exception("ID de la recette manquant"))
            return
        }

        db.collection("recipes").document(recipeId)
            .set(recipe.toMap(), SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Recipio", "Recette mise à jour avec succès")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("Recipio", "Erreur lors de la mise à jour", exception)
                onError(exception)
            }
            .addOnSuccessListener {
                Log.d("Recipio", "Recette mise à jour avec succès: ${recipe.id}, name: ${recipe.name}")
                onSuccess()
            }
    }
    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                val recipe = _uiState.value.selectedRecipe
                val updatedFavorite = !recipe.isFavorite

                // Mettre à jour uniquement le champ isFavorite dans Firebase
                db.collection("recipes")
                    .document(recipeId)
                    .update("isFavorite", updatedFavorite)
                    .await()

                // Mettre à jour l'état local
                _uiState.update { currentState ->
                    val updatedRecipe = currentState.selectedRecipe.copy(isFavorite = updatedFavorite)
                    currentState.copy(selectedRecipe = updatedRecipe)
                }

                Log.d("Recipio", "Statut favori mis à jour avec succès: $recipeId, favori: $updatedFavorite")

                // Rafraîchir la liste des recettes
                getRecipes()
            } catch (e: Exception) {
                Log.e("Recipio", "Erreur lors de la mise à jour du statut favori", e)
            }
        }
    }

}
