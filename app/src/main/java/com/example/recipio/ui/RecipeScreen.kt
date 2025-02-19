package com.example.recipio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe

@Composable
fun RecipeScreen(
    recipe: Recipe,
    modifier: Modifier
){
    Column(modifier = modifier) {
        Row {
            Text(text=recipe.image)
            Column {
                //bouton modifier
                //bouton favoris
            }
        }
        Text(text = recipe.name)
        Text(text=recipe.time.toString()+" min")
        Text(text=recipe.description)
        Text(text=recipe.tags.toString())
        Text(text=recipe.steps.toString())
        Text(text="pour "+recipe.numberOfPeople.toString()+" personnes")
        Text(text=recipe.ingredients.toString())
        Text(text=recipe.notes)
    }
}

@Preview
@Composable
fun RecipeScreenPreview(){
    val recipe = Recipe("image",
        false,
        "Nom",
        "description",
        listOf("tag1","tag2"),
        listOf("etape1","etape2"),
        listOf(Ingredient("ing1",30,"g")),
        4,
        30,
        "notes")
    RecipeScreen(recipe,modifier = Modifier)
}