package com.example.recipio.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recipio.R
import com.example.recipio.RecipeApp
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.example.recipio.viewmodel.RecipeViewModel

@Composable
fun ModifyScreen(
    recipe: Recipe,
    isNew: Boolean = false,
    onRecipeChange: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
    onSave: (Recipe) -> Unit,
    navController: NavHostController,
    viewModel: RecipeViewModel = viewModel()

) {
    var copy by remember { mutableStateOf(recipe) }
    val context = LocalContext.current

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
            IconButton(onClick = {
                viewModel.updateRecipe(copy,
                    onSuccess = {
                        Toast.makeText(context, "Recette sauvegardée", Toast.LENGTH_SHORT).show()

                        // Reload all recipes
                        viewModel.getRecipes()

                        // Force update the selected recipe
                        viewModel.selectRecipe(copy)

                        navController.navigate("${RecipeApp.Recipe.name}/${copy.id}") {
                            popUpTo(RecipeApp.Modify.name) { inclusive = true }
                        }
                    },
                    onError = { e ->
                        Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            })
            {
                Icon(Icons.Default.Check, contentDescription = "Sauvegarder")
            }
        }

        var imageUri by remember { mutableStateOf<Uri?>(null) }
        var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

        // Launcher pour ouvrir le sélecteur d'images
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri

            // Charger l'image en bitmap si une URI est sélectionnée
            uri?.let {
                copy = copy.copy(imageUri = uri)
            }
            }
        // Image modifiable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if(copy.imageUri != Uri.EMPTY) {
                AsyncImage(
                    model = copy.imageUri,
                    contentDescription = "Image chargée depuis une URI",
                    modifier = Modifier.size(200.dp)
                )
            }
            else{
                Text(stringResource(R.string.add_image))
            }
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
                // État local pour gérer l'ouverture du menu déroulant
                var expanded by remember { mutableStateOf(false) }

                // Liste des catégories disponibles
                val categories = listOf("Entrée", "Plat principal", "Dessert", "Autre")

                // UI : sélection de la catégorie
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Catégorie : ")

                    Text(
                        text = copy.category,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Choisir une catégorie")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    copy = copy.copy(category = category)
                                    onRecipeChange(copy)
                                    expanded = false
                                }
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                // Nom
                OutlinedTextField(
                    value = copy.name,
                    onValueChange = { copy = copy.copy(name = it) },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = copy.description,
                    onValueChange = { copy = copy.copy(description = it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                Column {
                    var newTag by remember { mutableStateOf("") }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tags:", modifier = Modifier.padding(end = 8.dp))
                            OutlinedTextField(
                                value = newTag,
                                onValueChange = { newTag = it },
                                label = { Text("Nouveau tag") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                            )
                            IconButton(
                                onClick = {
                                    if (newTag.isNotBlank()) {
                                        val updatedTags = copy.tags + newTag.trim()
                                        copy = copy.copy(tags = updatedTags)
                                        onRecipeChange(copy)
                                        newTag = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ajouter un tag")
                            }
                        }

                        // Affichage des tags avec les chips
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            copy.tags.forEachIndexed { index, tag ->
                                Chip(
                                    text = tag,
                                    onRemove = {
                                        val updatedTags = copy.tags.toMutableList().apply { removeAt(index) }
                                        copy = copy.copy(tags = updatedTags)
                                        onRecipeChange(copy)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Temps : ")
                    OutlinedTextField(
                        value = copy.time.toString(),
                        onValueChange = { copy = copy.copy(time = it.toInt()) },
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
                    var number:Int by remember { mutableStateOf(4) }
                    IconButton(onClick = {
                        if (number > 1) {
                            number--
                            copy = copy.copy(numberOfPeople =  number)
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Diminuer")
                    }
                    Text(text = copy.numberOfPeople.toString())
                    IconButton(onClick = {
                        number++
                        copy = copy.copy(numberOfPeople =  number)
                    }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Augmenter")
                    }
                    Text(" personnes")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ingrédients
                Column {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ingrédients:")
                        IconButton(onClick = {
                            val updatedIngredients = copy.ingredients + Ingredient("", 0.0, "")
                            copy = copy.copy(ingredients = updatedIngredients)
                            onRecipeChange(copy)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Ajouter un ingrédient")
                        }
                    }

                    copy.ingredients.forEachIndexed { index, ing ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = ing.name,
                                onValueChange = { newName ->
                                    val updated = copy.ingredients.toMutableList()
                                    updated[index] = updated[index].copy(name = newName)
                                    copy = copy.copy(ingredients = updated)
                                    onRecipeChange(copy)
                                },
                                label = { Text("Ingredient") },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(1f)
                            )
                            OutlinedTextField(
                                value = ing.amount.toString(),
                                onValueChange = { newAmountStr ->
                                    val newAmount = newAmountStr.toDoubleOrNull()
                                    if (newAmount != null) {
                                        val updated = copy.ingredients.toMutableList()
                                        updated[index] = updated[index].copy(amount = newAmount)
                                        copy = copy.copy(ingredients = updated)
                                        onRecipeChange(copy)
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text("Quantity") },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(80.dp)
                            )
                            OutlinedTextField(
                                value = ing.unit,
                                onValueChange = { newUnit ->
                                    val updated = copy.ingredients.toMutableList()
                                    updated[index] = updated[index].copy(unit = newUnit)
                                    copy = copy.copy(ingredients = updated)
                                    onRecipeChange(copy)
                                },
                                label = { Text("Unit") },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(80.dp)
                            )
                            IconButton(
                                onClick = {
                                    val updated = copy.ingredients.toMutableList()
                                    updated.removeAt(index)
                                    copy = copy.copy(ingredients = updated)
                                    onRecipeChange(copy)
                                },
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer l'ingrédient",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Étapes
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Étapes:")
                        IconButton(onClick = {
                            val updatedSteps = copy.steps + ""
                            copy = copy.copy(steps = updatedSteps)
                            onRecipeChange(copy)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Ajouter une étape")
                        }
                    }

                    copy.steps.forEachIndexed { index, etape ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = etape,
                                onValueChange = { newStep ->
                                    val updated = copy.steps.toMutableList()
                                    updated[index] = newStep
                                    copy = copy.copy(steps = updated)
                                    onRecipeChange(copy)
                                },
                                label = { Text("Étape ${index + 1}") },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    val updated = copy.steps.toMutableList()
                                    updated.removeAt(index)
                                    copy = copy.copy(steps = updated)
                                    onRecipeChange(copy)
                                },
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer l'étape",
                                    tint = Color.Red
                                )
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notes supplémentaires
                OutlinedTextField(
                    value = copy.notes,
                    onValueChange = { copy = copy.copy(notes = it) },
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
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.padding(end = 4.dp))
        IconButton(onClick = { onRemove() }) {
            Icon(Icons.Default.Close, contentDescription = "Supprimer", Modifier.size(16.dp))
        }
    }
}
/*
@PreviewScreenSizes
@Preview
@Composable
fun ModifyScreenPreview(){
    val recipe = Recipe(
        Uri.EMPTY,
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
    ModifyScreen(recipe, onRecipeChange = {}, onSave = {}, modifier = Modifier)
}
*/