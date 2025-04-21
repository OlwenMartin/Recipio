package com.example.recipio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.data.ChatMessage
import com.example.recipio.data.ChatUiState
import com.example.recipio.data.Recipe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeChatViewModel(
    private val recipeViewModel: RecipeViewModel? = null
) : ViewModel() {

    private val _chatState = MutableStateFlow(ChatUiState())
    val chatState: StateFlow<ChatUiState> = _chatState.asStateFlow()

    // Liste de mots-clés liés aux recettes pour filtrer les questions
    private val recipeKeywords = listOf(
        "recette", "cuisine", "ingrédient", "plat", "repas", "cuisson",
        "préparation", "dessert", "entrée", "végétarien", "vegan", "gastronomie",
        "cuire", "frire", "bouillir", "couper", "épice", "légume", "viande",
        "poisson", "fruit", "pâtisserie", "pain", "gâteau", "tarte",
        // À améliorer
        "brocoli", "brocolli", "carotte", "tomate", "oignon", "ail", "pomme", "pomme de terre",
        "sel", "poivre", "sucre", "farine", "oeuf", "œuf", "lait", "beurre", "huile",
        "poulet", "boeuf", "bœuf", "fromage", "thon", "saumon"
    )

    // Fonction pour envoyer un message utilisateur et obtenir une réponse
    fun sendMessage(text: String) {
        // Ajouter le message de l'utilisateur
        val userMessage = ChatMessage(text = text, isFromUser = true)
        _chatState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                isLoading = true
            )
        }

        // Vérifier si la question concerne les recettes
        viewModelScope.launch {
            // Simuler un délai de traitement
            delay(500)

            if (isRecipeRelated(text)) {
                // Générer une réponse liée aux recettes
                val response = generateRecipeResponse(text)
                val botMessage = ChatMessage(text = response, isFromUser = false)

                _chatState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + botMessage,
                        isLoading = false
                    )
                }
            } else {
                // Répondre que la question n'est pas liée aux recettes
                val botMessage = ChatMessage(
                    text = "Je suis désolé, mais je peux uniquement répondre aux questions concernant les recettes et la cuisine. Pourriez-vous reformuler votre question ?",
                    isFromUser = false
                )

                _chatState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + botMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    // Vérifier si la question est liée aux recettes
    private fun isRecipeRelated(text: String): Boolean {
        val lowerText = text.lowercase()
        return recipeKeywords.any { keyword -> lowerText.contains(keyword) }
    }

    // Générer une réponse basée sur la question de l'utilisateur
    private fun generateRecipeResponse(text: String): String {
        val lowerText = text.lowercase()

        // Rechercher des recettes dans les données disponibles
        val recipes = recipeViewModel?.uiState?.value?.recipes ?: emptyList()

        // Pattern pour détecter les questions sur les ingrédients d'une recette
        val ingredientQuestionPattern = Regex("(?:quel(?:s|le)?|quoi) (?:sont|est) les? ingrédients? (?:pour|de) (?:ma|mon|la|le) (.+)\\??")
        val matchResult = ingredientQuestionPattern.find(lowerText)

        // Si c'est une question sur les ingrédients d'une recette
        if (matchResult != null) {
            val recipeName = matchResult.groupValues[1].trim() // Extraire le nom de la recette

            // Chercher la recette par nom
            val recipe = recipes.find { it.name.lowercase().contains(recipeName) }
            if (recipe != null) {
                return listIngredientsForRecipe(recipe)
            } else {
                return "Je ne trouve pas de recette nommée '$recipeName' dans ma base de données."
            }
        }

        // Extraire les noms de recettes mentionnés dans la question
        val mentionedRecipeName = extractRecipeName(lowerText, recipes)

        // Extraire les ingrédients potentiels mentionnés dans la question
        val mentionedIngredients = extractPotentialIngredients(text)

        return when {
            // Questions sur les recettes disponibles
            lowerText.contains("quelles recettes") || lowerText.contains("liste des recettes") -> {
                if (recipes.isEmpty()) {
                    "Je n'ai pas encore de recettes dans ma base de données. Vous pouvez en ajouter pour commencer."
                } else {
                    "Voici les recettes disponibles : ${recipes.joinToString(", ") { it.name }}"
                }
            }

            // Vérification si un ingrédient est présent dans une recette spécifique
            mentionedRecipeName != null && mentionedIngredients.isNotEmpty() -> {
                val recipe = recipes.find { it.name.equals(mentionedRecipeName, ignoreCase = true) }
                if (recipe != null) {
                    checkIngredientsInRecipe(recipe, mentionedIngredients)
                } else {
                    "Je ne trouve pas de recette nommée '$mentionedRecipeName' dans ma base de données."
                }
            }

            // Questions sur les ingrédients d'une recette spécifique
            mentionedRecipeName != null && lowerText.contains("ingrédient") -> {
                val recipe = recipes.find { it.name.equals(mentionedRecipeName, ignoreCase = true) }
                if (recipe != null) {
                    listIngredientsForRecipe(recipe)
                } else {
                    "Je ne trouve pas de recette nommée '$mentionedRecipeName' dans ma base de données."
                }
            }

            // Questions sur les ingrédients spécifiques - chercher des recettes avec cet ingrédient
            mentionedIngredients.isNotEmpty() && (
                    lowerText.contains("trouve") || lowerText.contains("cherche") ||
                            lowerText.contains("avec") || lowerText.contains("contient") ||
                            lowerText.contains("utilise")
                    ) -> {
                findRecipesWithIngredients(mentionedIngredients, recipes)
            }

            // Questions sur le temps de préparation
            lowerText.contains("temps") && (lowerText.contains("préparation") || lowerText.contains("cuisson")) -> {
                if (mentionedRecipeName != null) {
                    val recipe = recipes.find { it.name.equals(mentionedRecipeName, ignoreCase = true) }
                    if (recipe != null) {
                        "Le temps de préparation pour ${recipe.name} est de ${recipe.time} minutes."
                    } else {
                        "Je ne trouve pas de recette nommée '$mentionedRecipeName' dans ma base de données."
                    }
                } else {
                    "Le temps de préparation varie selon la recette. Pourriez-vous préciser quelle recette vous intéresse ?"
                }
            }

            // Questions générales sur la cuisine
            lowerText.contains("conseil") || lowerText.contains("astuce") -> {
                "Un bon conseil pour la cuisine : toujours goûter vos plats pendant la préparation pour ajuster l'assaisonnement si nécessaire."
            }

            // Recherche de recette spécifique
            mentionedRecipeName != null -> {
                val recipe = recipes.find { it.name.equals(mentionedRecipeName, ignoreCase = true) }
                if (recipe != null) {
                    "La recette de ${recipe.name} : ${recipe.description}"
                } else {
                    "Je ne trouve pas de recette nommée '$mentionedRecipeName' dans ma base de données."
                }
            }

            // Si rien ne correspond spécifiquement
            else -> {
                // Dernière tentative : vérifier si c'est une recherche par ingrédient
                if (mentionedIngredients.isNotEmpty()) {
                    findRecipesWithIngredients(mentionedIngredients, recipes)
                } else {
                    "Je n'ai pas trouvé d'information spécifique sur cette question culinaire. Pourriez-vous être plus précis ou demander autre chose concernant les recettes ?"
                }
            }
        }
    }

    // Extraire le nom de la recette mentionnée dans la question
    private fun extractRecipeName(text: String, recipes: List<Recipe>): String? {
        // Chercher si un nom de recette existante est mentionné dans le texte
        return recipes.find { recipe ->
            text.contains(recipe.name.lowercase())
        }?.name
    }

    // Extraire les ingrédients potentiels de la question
    private fun extractPotentialIngredients(query: String): List<String> {
        // Liste de mots communs à ignorer
        val commonWords = setOf(
            "recette", "avec", "ingrédient", "trouve", "cherche", "moi", "une", "un", "des", "les",
            "qui", "contient", "contenant", "utilise", "utilisant", "à", "a", "de", "du", "la", "le",
            "pour", "et", "ou", "je", "tu", "il", "nous", "vous", "ils", "ce", "cette", "ces",
            "est-ce", "que", "quoi", "comment", "combien", "pourquoi", "dans", "sur", "sous",
            "j'ai", "j'avais", "j'utilise", "j'aimerais", "j'aime", "j'adorerais", "j'adore"
        )

        // Phrases communes à traiter spécialement
        val phrases = listOf("j'ai des", "j'ai du", "j'ai de la", "j'ai d'", "j'ai")

        // Prétraiter la requête pour enlever les phrases communes
        var processedQuery = query.lowercase()
        for (phrase in phrases) {
            if (processedQuery.contains(phrase)) {
                processedQuery = processedQuery.replace(phrase, "")
            }
        }

        // Diviser la requête prétraitée en mots
        val words = processedQuery.trim().split(Regex("[\\s,;:.!?]+"))

        // Filtrer les mots communs pour trouver des ingrédients potentiels
        return words.filter { word ->
            word.length > 2 && !commonWords.contains(word)
        }
    }

    // Vérifier si des ingrédients spécifiques sont présents dans une recette
    private fun checkIngredientsInRecipe(recipe: Recipe, ingredients: List<String>): String {
        val foundIngredients = mutableListOf<String>()
        val notFoundIngredients = mutableListOf<String>()

        for (ingredient in ingredients) {
            val found = recipe.ingredients.any {
                it.name.lowercase().contains(ingredient.lowercase())
            }

            if (found) {
                foundIngredients.add(ingredient)
            } else {
                notFoundIngredients.add(ingredient)
            }
        }

        return when {
            foundIngredients.isEmpty() -> {
                "Non, la recette ${recipe.name} ne contient pas ${ingredients.joinToString(", ")}."
            }
            notFoundIngredients.isEmpty() -> {
                "Oui, la recette ${recipe.name} contient bien ${foundIngredients.joinToString(", ")}."
            }
            else -> {
                "La recette ${recipe.name} contient ${foundIngredients.joinToString(", ")}, mais pas ${notFoundIngredients.joinToString(", ")}."
            }
        }
    }

    // Lister les ingrédients d'une recette
    private fun listIngredientsForRecipe(recipe: Recipe): String {
        return if (recipe.ingredients.isEmpty()) {
            "La recette ${recipe.name} n'a pas d'ingrédients listés."
        } else {
            "Voici les ingrédients pour ${recipe.name} : ${recipe.ingredients.joinToString(", ") {
                "${it.name} (${it.amount} ${it.unit})"
            }}"
        }
    }

    // Trouver des recettes contenant certains ingrédients
    private fun findRecipesWithIngredients(ingredients: List<String>, recipes: List<Recipe>): String {
        if (ingredients.isEmpty()) {
            return "Veuillez spécifier un ingrédient pour que je puisse trouver des recettes."
        }

        val matchedRecipes = mutableListOf<Recipe>()

        for (ingredient in ingredients) {
            val matches = recipes.filter { recipe ->
                recipe.ingredients.any { it.name.lowercase().contains(ingredient.lowercase()) }
            }

            // Ajouter seulement les nouvelles correspondances
            matches.forEach { recipe ->
                if (!matchedRecipes.contains(recipe)) {
                    matchedRecipes.add(recipe)
                }
            }
        }

        return if (matchedRecipes.isNotEmpty()) {
            val ingredientsList = ingredients.joinToString(", ")
            "J'ai trouvé ${matchedRecipes.size} recette(s) avec ${ingredientsList} : " +
                    matchedRecipes.joinToString(", ") { it.name }
        } else {
            "Je n'ai pas trouvé de recettes contenant ${ingredients.joinToString(", ")}."
        }
    }
}