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
import androidx.compose.ui.res.stringResource
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
            // Icônes Modifier et Favori
            var favorite by remember { mutableStateOf(recipe.isFavorite) }
            Row {
                IconButton(onClick = { onModifyClicked(recipe) }) {
                    Icon(Icons.Default.Create,
                        contentDescription = stringResource(R.string.modify_button))
                }
                IconButton(onClick = {
                    if (recipe.id.isNotEmpty()) {
                        onFavoriteToggle(recipe.id)
                    }
                }) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_button),
                        tint = if (recipe.isFavorite) Color.Red else Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = recipe.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B)
                )
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
                            contentDescription = stringResource(R.string.loaded_image),
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }

                //Categorie
                Text(stringResource(R.string.recipe_category, recipe.category))

                Spacer(modifier = Modifier.height(8.dp))

                // Nom
                OutlinedTextField(
                    value = recipe.name,
                    onValueChange = { onRecipeChange(recipe.copy(name = it)) },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = recipe.description,
                    onValueChange = { onRecipeChange(recipe.copy(description = it)) },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                Column {
                    Text(stringResource(R.string.tags))
                    Row {
                        recipe.tags.forEach { tag -> Chip(text = tag) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.time))
                    OutlinedTextField(
                        value = recipe.time.toString(),
                        onValueChange = { onRecipeChange(recipe.copy(description = it)) },
                        label = { Text(stringResource(R.string.time_in_minutes)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(stringResource(R.string.minutes))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre de personnes
                var number by remember { mutableStateOf(recipe.numberOfPeople) }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.for_people))
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
                    Text(stringResource(R.string.people))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ingrédients avec quantités ajustées
                Column {
                    Text(stringResource(R.string.ingredients))

                    // Ratio de modification des quantités selon le nombre de personnes
                    val ratio = number.toFloat() / recipe.numberOfPeople.toFloat()

                    recipe.ingredients.forEachIndexed { index, ing ->
                        // Calcul de la quantité ajustée selon le ratio
                        val adjustedAmount = ing.amount * ratio

                        // Affichage dynamique des ingrédients sans modification possible par l'utilisateur
                        OutlinedTextField(
                            value = "${ing.name} ${String.format("%.2f", adjustedAmount)} ${ing.unit}",
                            onValueChange = {}, // Aucune modification autorisée
                            label = { Text(stringResource(R.string.ingredient)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Étapes
                Column {
                    Text(stringResource(R.string.steps))
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
                    label = { Text(stringResource(R.string.additional_notes)) },
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
    val previewCategory = stringResource(R.string.category_starter)
    val previewSteps = listOf(
        stringResource(R.string.preview_step1),
        stringResource(R.string.preview_step2)
    )

    val recipe = Recipe(
        imageUri = Uri.EMPTY,
        imageUrl = "",
        isFavorite = true,
        category = previewCategory,
        name = stringResource(R.string.preview_recipe_name),
        description = stringResource(R.string.preview_recipe_description),
        tags = listOf(
            stringResource(R.string.preview_tag1),
            stringResource(R.string.preview_tag2)
        ),
        steps = previewSteps,
        ingredients = listOf(
            Ingredient(
                stringResource(R.string.preview_ingredient),
                30.0,
                stringResource(R.string.preview_unit)
            )
        ),
        numberOfPeople = 4,
        time = 30,
        notes = stringResource(R.string.preview_notes)
    )

    RecipeScreen(recipe, onRecipeChange = {}, onModifyClicked = {},modifier = Modifier)
}