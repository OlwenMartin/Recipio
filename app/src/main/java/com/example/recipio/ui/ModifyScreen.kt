package com.example.recipio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipio.R
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe

@Composable
fun ModifyScreen(
    recipe: Recipe,
    isNew: Boolean = false,
    onRecipeChange: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
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
            if(isNew) {
                Text(
                    text = "Nouvelle recette",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B)
                )
            }
            else{
                Text(
                    text = "Modifier la recette",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B)
                )
                IconButton(onClick = { /* Action supprimer */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                }
            }
            IconButton(onClick = { /* Action sauvegarde */ }) {
                Icon(Icons.Default.Check, contentDescription = "Sauvegarder")
            }
        }

        // Image modifiable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Ajouter un sélecteur d'image */ },
            contentAlignment = Alignment.Center
        ) {
            Image(
                if(recipe.image != 0) {
                    painterResource(id = recipe.image)

                } else {
                    painterResource(id = R.drawable.default_dish_image)
                },
                contentDescription = "Dish Image",
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Partie scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            item {
                //Choix catégorie (entrées, plats, dessert,...)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Catégorie : ")
                    Text("Entrée")
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Entrée") },
                            onClick = { /* Changement du Texte */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Plat principal") },
                            onClick = { /* Changement du Texte */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Dessert") },
                            onClick = { /* Changement du Texte */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Autre") },
                            onClick = { /* Changement du Texte */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nom
                var name by remember { mutableStateOf(recipe.name) }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                var description by remember { mutableStateOf(recipe.description) }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                Column {
                    Text("Tags:")
                    Row {
                        recipe.tags.forEach { tag ->
                            Chip(text = tag, onRemove = { /* TODO: Supprimer le tag */ })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Temps : ")
                    var time by remember { mutableStateOf(recipe.time.toString()) }
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time in minutes") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    Text("min")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre de personnes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pour : ")
                    var numberOfPeople by remember { mutableStateOf(recipe.numberOfPeople.toString()) }
                    var number:Int by remember { mutableStateOf(recipe.numberOfPeople) }
                    IconButton(onClick = {
                        if (number > 1) {
                            number--
                            numberOfPeople = number.toString()
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Diminuer")
                    }
                    Text(text = numberOfPeople)
                    IconButton(onClick = {
                        number++
                        numberOfPeople = number.toString()
                    }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Augmenter")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ingrédients
                Column {
                    Text("Ingrédients:")
                    recipe.ingredients.forEachIndexed { index, ing ->
                        var ingredient by remember { mutableStateOf(ing.name) }
                        var amount by remember { mutableStateOf(ing.amount.toString()) }
                        var unit by remember { mutableStateOf(ing.unit) }
                        OutlinedTextField(
                            value = ingredient,
                            onValueChange = { ingredient = it },
                            label = { Text("Ingredient") },
                            modifier = Modifier
                                 .padding(4.dp)
                                 .width(IntrinsicSize.Min)
                            )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Quantity") },
                            modifier = Modifier
                                .padding(4.dp)
                                .width(IntrinsicSize.Min)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            modifier = Modifier
                                .padding(4.dp)
                                .width(IntrinsicSize.Min)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Étapes
                Column {
                    Text("Étapes:")
                    recipe.steps.forEachIndexed { index, etape ->
                        var step by remember { mutableStateOf(etape) }
                        OutlinedTextField(
                            value = step,
                            onValueChange = { step = it },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notes supplémentaires
                var notes by remember { mutableStateOf(recipe.notes) }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes supplémentaires") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun Chip(text: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
            .clickable { onRemove() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.padding(end = 4.dp))
        Icon(Icons.Default.Close, contentDescription = "Supprimer", Modifier.size(16.dp))
    }
}

@PreviewScreenSizes
@Preview
@Composable
fun ModifyScreenPreview(){
    val recipe = Recipe(
        R.drawable.exemple_image,
        true,
        "Muffin",
        "c'est des muffins quoi",
        listOf("tag1","tag2"),
        listOf("tu fais la pate","tu met au four"),
        listOf(Ingredient("ing1",30,"g")),
        4,
        30,
        "notes en plus")
    ModifyScreen(recipe, onRecipeChange = {}, modifier = Modifier)
}
