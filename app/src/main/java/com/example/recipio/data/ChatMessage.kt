package com.example.recipio.data

// Classe pour représenter un message dans le chat
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)