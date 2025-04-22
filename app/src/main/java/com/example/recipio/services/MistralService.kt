package com.example.recipio.services

import android.util.Log
import com.example.recipio.api.MistralApi
import com.example.recipio.data.MistralMessage
import com.example.recipio.data.MistralRequest
import com.example.recipio.data.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MistralService {
    private val TAG = "MistralService"

    private val apiKey = "wxPMOHGgF6bGsrGws6tIzYeRQNp9a9F0"

    private val model = "mistral-small-latest"

    // Client HTTP
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Configuration de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mistral.ai/") // URL de base de l'API Mistral
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Service API
    private val mistralApi: MistralApi by lazy {
        retrofit.create(MistralApi::class.java)
    }

    //Pour obtenir une réponse du chatbot
    suspend fun getChatResponse(
        userMessage: String,
        messageHistory: List<MistralMessage>,
        recipes: List<Recipe>
    ): String = withContext(Dispatchers.IO) {
        try {
            // Formatage des données de recettes
            val recipesData = formatRecipes(recipes)

            // Construction du prompt système
            val systemPrompt = """
            Tu es un assistant culinaire expert qui connaît uniquement les recettes de l'utilisateur.
            Tu dois répondre UNIQUEMENT aux questions relatives à la cuisine, aux recettes et aux ingrédients.
            Si l'utilisateur pose une question sur un autre sujet, indique poliment que tu ne peux parler que de cuisine.
            
            Voici les recettes que l'utilisateur a enregistrées :
            $recipesData
            
            Quand l'utilisateur demande une recette spécifique, donne-lui tous les détails.
            Si l'utilisateur demande des suggestions de recettes, base-toi uniquement sur cette liste.
            Sois précis, convivial et n'invente pas de recettes qui ne sont pas dans cette liste.
            """.trimIndent()

            // Préparation des messages pour la requête
            val systemMessage = MistralMessage("system", systemPrompt)
            val updatedHistory = mutableListOf(systemMessage)
            updatedHistory.addAll(messageHistory)
            updatedHistory.add(MistralMessage("user", userMessage))

            // Préparation de la requête
            val request = MistralRequest(
                model = model,
                messages = updatedHistory,
                max_tokens = 500,
                temperature = 0.7
            )

            // Envoi de la requête
            val response = mistralApi.createChatCompletion(
                "Bearer $apiKey",
                request
            )

            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content ?:
                "Désolé, je n'ai pas pu générer une réponse."
            } else {
                Log.e(TAG, "Error: ${response.code()} - ${response.errorBody()?.string()}")
                "Une erreur s'est produite lors de la communication avec le service. Veuillez réessayer plus tard."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Mistral API", e)
            "Désolé, une erreur inattendue s'est produite. Veuillez réessayer plus tard."
        }
    }

    private fun formatRecipes(recipes: List<Recipe>): String {
        return recipes.joinToString("\n\n") { recipe ->
            """
            Recette: ${recipe.name}
            Description: ${recipe.description}
            Catégorie: ${recipe.category}
            Temps de préparation: ${recipe.time} minutes
            Pour ${recipe.numberOfPeople} personnes
            Ingrédients: ${recipe.ingredients.joinToString(", ") {
                "${it.amount} ${it.unit} de ${it.name}"
            }}
            Étapes: ${recipe.steps.joinToString(" ")}
            """.trimIndent()
        }
    }
}