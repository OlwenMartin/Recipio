package com.example.recipio.data

// État UI pour le chatbot
data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Bonjour ! Je suis votre assistant de recettes. Comment puis-je vous aider aujourd'hui ?",
            isFromUser = false
        )
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)