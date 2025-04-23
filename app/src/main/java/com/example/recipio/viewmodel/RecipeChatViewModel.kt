package com.example.recipio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.data.ChatMessage
import com.example.recipio.data.ChatUiState
import com.example.recipio.services.MistralService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import com.example.recipio.data.MistralMessage

class RecipeChatViewModel(
    private val recipeViewModel: RecipeViewModel? = null
) : ViewModel() {

    private val _chatState = MutableStateFlow(ChatUiState())
    val chatState: StateFlow<ChatUiState> = _chatState.asStateFlow()

    private val mistralService = MistralService()

    // Historique des messages pour Mistral (sans le message système)
    private val messageHistory = mutableListOf<MistralMessage>()

    // Pour envoyer un message utilisateur et obtenir une réponse
    fun sendMessage(text: String) {
        // Ajouter le message de l'utilisateur à l'UI
        val userMessage = ChatMessage(text = text, isFromUser = true)
        _chatState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                isLoading = true
            )
        }

        // Ajouter le message à l'historique pour Mistral
        messageHistory.add(MistralMessage("user", text))

        viewModelScope.launch {
            try {
                // Obtenir les recettes de l'utilisateur
                val recipes = recipeViewModel?.uiState?.value?.recipes ?: emptyList()

                // Obtenir la réponse de Mistral
                val aiResponse = mistralService.getChatResponse(
                    userMessage = text,
                    messageHistory = messageHistory,
                    recipes = recipes
                )

                // Ajouter la réponse à l'historique Mistral
                messageHistory.add(MistralMessage("assistant", aiResponse))

                // Limiter l'historique à 10 messages pour éviter de consommer trop de tokens
                if (messageHistory.size > 10) {
                    val excessMessages = messageHistory.size - 10
                    repeat(excessMessages) {
                        messageHistory.removeAt(0)
                    }
                }

                // Mettre à jour l'UI avec la réponse
                val botMessage = ChatMessage(text = aiResponse, isFromUser = false)
                _chatState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + botMessage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("RecipeChatViewModel", "Error getting response from Mistral", e)

                // Gérer l'erreur
                val errorMessage = ChatMessage(
                    text = _chatState.value.errorMessage,
                    isFromUser = false
                )

                _chatState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + errorMessage,
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}