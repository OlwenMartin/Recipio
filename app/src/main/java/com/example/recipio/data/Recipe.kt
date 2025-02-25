package com.example.recipio.data

import android.media.Image
import com.example.recipio.R

data class Recipe(
    var image : Int = 0,
    var isFavorite : Boolean = false,
    var name : String = "",
    var description : String = "",
    var tags : List<String> = listOf(),
    var steps : List<String> = listOf(),
    var ingredients : List<Ingredient> = listOf(),
    var numberOfPeople : Int = 4,
    var time : Int = 0,
    var notes : String = ""
)
