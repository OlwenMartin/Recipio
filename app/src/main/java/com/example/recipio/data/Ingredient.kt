package com.example.recipio.data

data class Ingredient(
    var name: String,
    var amount: Int,
    var unit: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "quantity" to amount,
            "unit" to unit
        )
    }
}
