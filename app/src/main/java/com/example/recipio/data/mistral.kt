package com.example.recipio.data

data class MistralRequest(
    val model: String,
    val messages: List<MistralMessage>,
    val max_tokens: Int = 500,
    val temperature: Double = 0.7
)

data class MistralMessage(
    val role: String,
    val content: String
)

data class MistralResponse(
    val id: String,
    val choices: List<MistralChoice>,
    val usage: MistralUsage
)

data class MistralChoice(
    val index: Int,
    val message: MistralMessage,
    val finish_reason: String
)

data class MistralUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)