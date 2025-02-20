package com.example.recipio.data

import android.media.Image
import com.example.recipio.R

data class Recipe(
    var image : Int = R.drawable.exemple_image,
    var isFavorite : Boolean = false,
    var name : String = "Nom",
    var description : String = "Description",
    var tags : List<String> = listOf("Vegan", "Vegetarian"),
    var steps : List<String> = listOf("etape 1","etape 2"),
    var ingredients : List<Ingredient> = listOf(Ingredient("ing1",30,"g"),
        Ingredient("ing2",30,"g")
    ),
    var numberOfPeople : Int = 4,
    var time : Int = 0,
    var notes : String = "Notes supplémentaires"
)
