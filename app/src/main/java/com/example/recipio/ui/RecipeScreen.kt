package com.example.recipio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipio.R
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe

@Composable
fun RecipeScreen(
    recipe: Recipe,
    modifier: Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nom de l'application avec icône
            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    painter = painterResource(id = Logo app),
//                    contentDescription = "App Icon",
//                    tint = Color(0xFF88B04B), // Vert clair
//                    modifier = Modifier.size(32.dp)
//                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "name",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B) // Vert clair
                )
            }

            // Icônes à droite (Modifier et Favori)
            Row {
                IconButton(onClick = { /* Action modifier */ }) {
                    Icon(Icons.Default.Create, contentDescription = "Modify")
                }
                IconButton(onClick = { /* Action favori */ }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Modify") // *****A ajouter une condition pour si favorit ou pas
                }
            }
        }

        // Image centrale
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = recipe.image),
                contentDescription = "Dish Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Nom
        OutlinedTextField(
            value = recipe.name,
            onValueChange = { recipe.name = it },
            label = { Text("Nom") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        OutlinedTextField(
            value = recipe.description,
            onValueChange = { recipe.description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tags
        Column {
            Text("Tags:")
            Row {
                recipe.tags.forEach { tag ->
                    Chip(text = tag)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Number of people
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pour : ")
            IconButton(onClick = { if (recipe.numberOfPeople > 1) recipe.numberOfPeople-- }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Diminuer")
            }
            Text(text = recipe.numberOfPeople.toString())
            IconButton(onClick = { recipe.numberOfPeople++ }) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Augmenter")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ingrédients
        Column {
            Text("Ingrédients:")
            recipe.ingredients.forEachIndexed { index, ing ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = ing.name + " " + ing.amount + " " + ing.unit,
                        onValueChange = { recipe.ingredients = recipe.ingredients.toMutableList() },
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Étapes
        Column {
            Text("Étapes:")
            recipe.steps.forEachIndexed { index, step ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = step,
                        onValueChange = { recipe.steps = recipe.steps.toMutableList().apply { set(index, it) } },
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notes supplémentaires
        OutlinedTextField(
            value = recipe.notes,
            onValueChange = { recipe.notes = it },
            label = { Text("Notes supplémentaires") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Chip(text: String) {
    Row(
        modifier = Modifier
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.padding(end = 4.dp))
    }
}

@PreviewScreenSizes
@Preview
@Composable
fun RecipeScreenPreview(){
    val recipe = Recipe(R.drawable.exemple_image,
        false,
        "Muffin",
        "c'est des muffins quoi",
        listOf("tag1","tag2"),
        listOf("tu fais la pate","tu met au four"),
        listOf(Ingredient("ing1",30,"g")),
        4,
        30,
        "notes en plus")
    RecipeScreen(recipe,modifier = Modifier)
}