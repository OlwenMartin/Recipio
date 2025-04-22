package com.example.recipio

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.recipio.services.NotificationService
import com.example.recipio.viewmodel.SettingsViewModel


class MainActivity : ComponentActivity() {
    private lateinit var settingsViewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        // Créer le canal de notification
        NotificationService.createNotificationChannel(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState(initial = false)

            RecipioTheme(darkTheme = isDarkMode)  {
                RecipeApp()
            }
        }
    }

    @Composable
    fun RecipioTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
        // Définir deux schémas de couleurs : un pour le mode clair et un pour le mode sombre
        val lightColors = lightColorScheme(
            primary = Color(0xFF9bc268),
            onPrimary = Color.White,
            secondary = Color(0xFF88B04B),
            onSecondary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color(0xFFF9F9F9),
            onSurface = Color.Black
        )

        val darkColors = darkColorScheme(
            primary = Color(0xFF9bc268),
            onPrimary = Color.White,
            secondary = Color(0xFF88B04B),
            onSecondary = Color.White,
            background = Color(0xFF121212),
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White
        )

        // Appliquer le schéma de couleurs approprié selon le mode
        MaterialTheme(
            colorScheme = if (darkTheme) darkColors else lightColors,
            shapes = MaterialTheme.shapes.copy(
                extraSmall = MaterialTheme.shapes.medium
            ),
            content = content
        )
    }

    private fun seedDatabase(){
        val recipes = listOf(
            Recipe(
                name = "Pâtes Carbonara",
                description = "Un classique italien revisité sans crème.",
                tags = listOf("Italien", "Rapide"),
                steps = listOf(
                    "Faire cuire les pâtes.",
                    "Faire revenir les lardons.",
                    "Mélanger avec les œufs et le parmesan."
                ),
                ingredients = listOf(
                    Ingredient("Pâtes", 250.0, "g"),
                    Ingredient("Lardons", 100.0, "g"),
                    Ingredient("Œufs", 2.0, "unités"),
                    Ingredient("Parmesan", 50.0, "g")
                ),
                numberOfPeople = 2,
                time = 20,
                notes = "Servir avec du poivre noir fraîchement moulu."
            ),
            Recipe(
                name = "Salade César",
                description = "Une salade fraîche et croquante.",
                tags = listOf("Salade", "Healthy"),
                steps = listOf(
                    "Préparer la sauce César.",
                    "Griller les morceaux de poulet.",
                    "Assembler laitue, croûtons et parmesan."
                ),
                ingredients = listOf(
                    Ingredient("Laitue", 1.0, "unité"),
                    Ingredient("Poulet", 150.0, "g"),
                    Ingredient("Parmesan", 30.0, "g"),
                    Ingredient("Croûtons", 50.0, "g")
                ),
                numberOfPeople = 2,
                time = 15,
                notes = "Ajouter des anchois pour plus de saveur."
            ),
            Recipe(
                name = "Ratatouille",
                description = "Un plat provençal coloré et savoureux.",
                tags = listOf("Végétarien", "Healthy"),
                steps = listOf(
                    "Couper les légumes en dés.",
                    "Faire revenir l'oignon et l'ail.",
                    "Ajouter les légumes et laisser mijoter."
                ),
                ingredients = listOf(
                    Ingredient("Courgettes", 2.0, "unités"),
                    Ingredient("Aubergines", 1.0, "unité"),
                    Ingredient("Poivrons", 2.0, "unités"),
                    Ingredient("Tomates", 4.0, "unités")
                ),
                numberOfPeople = 4,
                time = 40,
                notes = "Servir chaud ou froid."
            ),
            Recipe(
                name = "Omelette aux champignons",
                description = "Une omelette savoureuse et rapide à préparer.",
                tags = listOf("Rapide", "Végétarien"),
                steps = listOf(
                    "Battre les œufs.",
                    "Faire revenir les champignons.",
                    "Cuire l'omelette à la poêle."
                ),
                ingredients = listOf(
                    Ingredient("Œufs", 3.0, "unités"),
                    Ingredient("Champignons", 100.0, "g"),
                    Ingredient("Beurre", 10.0, "g"),
                    Ingredient("Sel", 1.0, "pincée")
                ),
                numberOfPeople = 1,
                time = 10,
                notes = "Servir avec une salade verte."
            ),
            Recipe(
                name = "Poulet au curry",
                description = "Un plat épicé et savoureux.",
                tags = listOf("Asiatique", "Épicé"),
                steps = listOf(
                    "Faire revenir les oignons.",
                    "Ajouter le poulet et les épices.",
                    "Laisser mijoter avec du lait de coco."
                ),
                ingredients = listOf(
                    Ingredient("Poulet", 200.0, "g"),
                    Ingredient("Oignon", 1.0, "unité"),
                    Ingredient("Lait de coco", 200.0, "ml"),
                    Ingredient("Curry", 2.0, "c.à.c")
                ),
                numberOfPeople = 2,
                time = 30,
                notes = "Servir avec du riz basmati."
            ),
            Recipe(
                name = "Tiramisu",
                description = "Un dessert italien crémeux et savoureux.",
                tags = listOf("Dessert", "Italien"),
                steps = listOf(
                    "Tremper les biscuits dans le café.",
                    "Monter la crème mascarpone.",
                    "Assembler les couches et laisser reposer."
                ),
                ingredients = listOf(
                    Ingredient("Biscuits à la cuillère", 12.0, "unités"),
                    Ingredient("Mascarpone", 250.0, "g"),
                    Ingredient("Café", 200.0, "ml"),
                    Ingredient("Cacao", 2.0, "c.à.c")
                ),
                numberOfPeople = 4,
                time = 180,
                notes = "Laisser reposer au frais pour plus de saveur."
            ),
            Recipe(
                name = "Risotto aux champignons",
                description = "Un plat crémeux et gourmand.",
                tags = listOf("Italien", "Végétarien"),
                steps = listOf(
                    "Faire revenir l’oignon.",
                    "Ajouter le riz et le bouillon progressivement.",
                    "Incorporer les champignons et le parmesan."
                ),
                ingredients = listOf(
                    Ingredient("Riz arborio", 200.0, "g"),
                    Ingredient("Champignons", 150.0, "g"),
                    Ingredient("Bouillon de légumes", 500.0, "ml"),
                    Ingredient("Parmesan", 50.0, "g")
                ),
                numberOfPeople = 2,
                time = 40,
                notes = "Remuer constamment pour une texture parfaite."
            )
        )
        val db = Firebase.firestore

        for(recipe in recipes) {
            db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        ContextCompat.getString(this@MainActivity.baseContext, R.string.app_name),
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w(
                        ContextCompat.getString(this@MainActivity.baseContext, R.string.app_name),
                        "Error adding document",
                        e
                    )
                }
        }
    }

    private fun addUser(){
        val db = Firebase.firestore
        val user = hashMapOf(
            "first" to "Francis",
            "last" to "Lavoie",
            "born" to 1815
        )
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContextCompat.getString(this@MainActivity.baseContext, R.string.app_name), "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContextCompat.getString(this@MainActivity.baseContext,R.string.app_name), "Error adding document", e)
            }
    }

    private fun logUsers(){
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(getString(this@MainActivity.baseContext, R.string.app_name), "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(getString(this@MainActivity.baseContext, R.string.app_name), "Error getting documents.", exception)
            }
    }
}
