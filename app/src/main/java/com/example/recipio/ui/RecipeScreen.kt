package com.example.recipio.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.recipio.R
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.example.recipio.viewmodel.RecipeViewModel

@Composable
fun RecipeScreen(
    recipe: Recipe,
    onRecipeChange: (Recipe) -> Unit,
    onModifyClicked: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeViewModel = viewModel(),
    onFavoriteToggle: (String) -> Unit = {},

) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = recipe.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B) // Vert clair
                )
            }

            // Icônes Modifier et Favori
            var favorite by remember { mutableStateOf(recipe.isFavorite) }
            Row {
                IconButton(onClick = { onModifyClicked(recipe) }) {
                    Icon(Icons.Default.Create, contentDescription = "Modify")
                }
                IconButton(onClick = {
                    if (recipe.id.isNotEmpty()) {
                        onFavoriteToggle(recipe.id)
                    }
                }) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favori",
                        tint = if (recipe.isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }



        // Partie scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            item {
                // Image
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    if(recipe.imageUrl != "") {
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = "Image chargée depuis une URI",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }

                //Categorie
                Text(text = "Categorie : " + recipe.category)

                Spacer(modifier = Modifier.height(8.dp))

                // Nom
                OutlinedTextField(
                    value = recipe.name,
                    onValueChange = { onRecipeChange(recipe.copy(name = it)) },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = recipe.description,
                    onValueChange = { onRecipeChange(recipe.copy(description = it)) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                Column {
                    Text("Tags:")
                    Row {
                        recipe.tags.forEach { tag -> Chip(text = tag) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Temps : ")
                    OutlinedTextField(
                        value = recipe.time.toString(),
                        onValueChange = { onRecipeChange(recipe.copy(description = it)) },
                        label = { Text("Time in minutes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("min")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre de personnes
                var number by remember { mutableStateOf(recipe.numberOfPeople) }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pour : ")
                    IconButton(onClick = {
                        if (number > 1) {
                            number -= 1
                            onRecipeChange(recipe.copy(numberOfPeople = number)) // Met à jour le nombre de personnes
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Diminuer")
                    }
                    Text(number.toString())
                    IconButton(onClick = {
                        number += 1
                        onRecipeChange(recipe.copy(numberOfPeople = number)) // Met à jour le nombre de personnes
                    }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Augmenter")
                    }
                    Text(" personnes")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ingrédients avec quantités ajustées
                Column {
                    Text("Ingrédients :")

                    // Ratio de modification des quantités selon le nombre de personnes
                    val ratio = number.toFloat() / recipe.numberOfPeople.toFloat()

                    recipe.ingredients.forEachIndexed { index, ing ->
                        // Calcul de la quantité ajustée selon le ratio
                        val adjustedAmount = ing.amount * ratio

                        // Affichage dynamique des ingrédients sans modification possible par l'utilisateur
                        OutlinedTextField(
                            value = "${ing.name} ${String.format("%.2f", adjustedAmount)} ${ing.unit}",
                            onValueChange = {}, // Aucune modification autorisée
                            label = { Text("Ingrédient") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Étapes
                Column {
                    Text("Étapes:")
                    recipe.steps.forEachIndexed { index, step ->
                        OutlinedTextField(
                            value = step,
                            onValueChange = { newValue ->
                                val updatedSteps = recipe.steps.toMutableList().apply { set(index, newValue) }
                                onRecipeChange(recipe.copy(steps = updatedSteps))
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notes supplémentaires
                OutlinedTextField(
                    value = recipe.notes,
                    onValueChange = { onRecipeChange(recipe.copy(notes = it)) },
                    label = { Text("Notes supplémentaires") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
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
    val recipe = Recipe(Uri.EMPTY,
        "",
        true,
        "Entrée",
        "Muffin",
        "c'est des muffins quoi",
        listOf("tag1","tag2"),
        listOf("tu fais la pate","tu met au four"),
        listOf(Ingredient("ing1",30.0,"g")),
        4,
        30,
        "notes en plus")
    RecipeScreen(recipe, onRecipeChange = {}, onModifyClicked = {},modifier = Modifier)
}