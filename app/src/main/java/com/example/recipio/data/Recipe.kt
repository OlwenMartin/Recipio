package com.example.recipio.data

import android.media.Image

data class Recipe(
    val image : String = "Image",
    val isFavorite : Boolean = false,
    val name : String = "Nom",
    val description : String = "Description",
    val tags : List<String> = listOf(),
    val steps : List<String> = listOf(),
    val ingredients : List<Ingredient> = listOf(),
    val numberOfPeople : Int = 4,
    val time : Int = 0,
    val notes : String = "Notes supplémentaires"
)
