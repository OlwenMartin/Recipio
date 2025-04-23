package com.example.recipio.api

import com.example.recipio.data.MistralRequest
import com.example.recipio.data.MistralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MistralApi {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: MistralRequest
    ): Response<MistralResponse>
}