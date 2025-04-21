package com.example.recipio.ui


import android.content.ContentValues
import androidx.compose.material3.AlertDialog
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recipio.R
import com.example.recipio.RecipeApp
import com.example.recipio.data.Ingredient
import com.example.recipio.data.Recipe
import com.example.recipio.viewmodel.RecipeViewModel
import java.io.File
import android.Manifest
import android.media.MediaScannerConnection
import android.content.Context

@Composable
fun ModifyScreen(
    recipe: Recipe,
    isNew: Boolean = false,
    onRecipeChange: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: RecipeViewModel = viewModel()

) {
    val focusManager = LocalFocusManager.current
    var copy by remember { mutableStateOf(recipe) }
    val context = LocalContext.current
    val recette_sup= stringResource(R.string.recipe_deleted)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
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
                    text = stringResource(R.string.new_recipe),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B)
                )
            }
            else{
                Text(
                    text = stringResource(R.string.modify_recipe),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF88B04B)
                )
                IconButton(onClick = {
                    viewModel.deleteRecipe(
                        recipeId = copy.id,
                        onSuccess = {
                            Toast.makeText(context, recette_sup, Toast.LENGTH_SHORT).show()

                            // Rafraîchir la liste des recettes
                            viewModel.getRecipes()

                            // Naviguer vers la page d'accueil
                            navController.navigate(RecipeApp.Home.name) {
                                popUpTo(RecipeApp.Modify.name) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            Toast.makeText(
                                context,
                                "Erreur lors de la suppression: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
            if(!isNew) {
                // Modification
                IconButton(onClick = {
                    viewModel.updateRecipe(copy,
                        onSuccess = {
                            Toast.makeText(context, context.getString(R.string.recipe_saved), Toast.LENGTH_SHORT)
                                .show()

                            // Reload all recipes
                            viewModel.getRecipes()

                            // Force update the selected recipe
                            viewModel.selectRecipe(copy)

                            navController.navigate("${RecipeApp.Recipe.name}/${copy.id}") {
                                popUpTo(RecipeApp.Modify.name) { inclusive = true }
                            }
                        },
                        onError = { e ->
                            Toast.makeText(context, context.getString(R.string.delete_error, e.message), Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                })
                {
                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
                }
            }
            else {
                //Ajout
                IconButton(onClick = {
                    viewModel.addRecipeToUser(copy,
                        onSuccess = {
                            Toast.makeText(context,
                                context.getString(R.string.add_recipe_success), Toast.LENGTH_SHORT).show()

                            viewModel.getRecipes()
                            // Naviguer vers la page d'accueil
                            navController.navigate(RecipeApp.Home.name) {
                                popUpTo(RecipeApp.Add.name) { inclusive = true }
                            }
                        },
                        onError = {error ->
                            Toast.makeText(context,
                                context.getString(R.string.add_recipe_error), Toast.LENGTH_SHORT).show()
                        }
                    )
                })
                {
                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.add_recipe))
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
                var imageUri by remember { mutableStateOf<Uri?>(null) }
                var photoUri by remember { mutableStateOf<Uri?>(null) }
                var showDialog by remember { mutableStateOf(false) }

                fun uriToFile(uri: Uri, context: Context): File? {
                    return try {
                        val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                                val path = it.getString(columnIndex)
                                File(path)
                            } else null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                // Ajouter l'image à la galerie (MediaStore)
                fun addImageToGallery(uri: Uri, context: Context) {
                    val file = uriToFile(uri, context)
                    file?.let {
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(it.absolutePath),
                            arrayOf("image/jpeg"),
                            null
                        )
                    }
                }

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

                // Launcher pour prendre une photo avec la caméra
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture()
                ) { success ->
                    if (success) {
                        // Ajouter la photo à la galerie
                        photoUri?.let {
                            // Mettre à jour imageUri avec la photo prise
                            imageUri = it
                            copy = copy.copy(imageUri = it)

                            // Ajouter la photo au MediaStore (Galerie)
                            addImageToGallery(it, context)
                        }
                    }
                }

                // Fonction pour créer un fichier temporaire et en extraire l'Uri
                fun createImageUri(): Uri {
                    // Utilisation du dossier Pictures dans le stockage externe
                    val file = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "photo_${System.currentTimeMillis()}.jpg"
                    )

                    // Obtenir l'URI du fichier en utilisant FileProvider
                    return FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider", // Assure-toi que cela correspond à ce qui est dans le manifeste
                        file
                    )
                }

                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        val uri = createImageUri()
                        photoUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        Toast.makeText(context, "Permission caméra refusée", Toast.LENGTH_SHORT).show()
                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDialog = true }, // Ouvre le menu au clic
                    contentAlignment = Alignment.Center
                ) {
                    if (copy.imageUri != Uri.EMPTY) {
                        AsyncImage(
                            model = copy.imageUri,
                            contentDescription = stringResource(R.string.loaded_image),
                            modifier = Modifier.size(200.dp)
                        )
                    }
                    else if(copy.imageUrl != "") {
                        AsyncImage(
                            model = copy.imageUrl,
                            contentDescription = "Image chargée depuis une URL",
                            modifier = Modifier.size(200.dp)
                        )
                    }else {
                        Text(stringResource(R.string.add_image))
                    }

                    // Menu de choix
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text("Choisir une option")
                            },
                            text = {
                                Text("Sélectionne une source d'image")
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    launcher.launch("image/*")
                                }) {
                                    Text("Galerie")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }) {
                                    Text("Caméra")
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Choix catégorie (entrées, plats, dessert,...)
                // État local pour gérer l'ouverture du menu déroulant
                var expanded by remember { mutableStateOf(false) }

                // Liste des catégories disponibles
                val categories = listOf("Entrée", "Plat", "Dessert", "Autre")

                // UI : sélection de la catégorie
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.category))

                    Text(
                        text = copy.category,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.choose_category))
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
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = copy.description,
                    onValueChange = { copy = copy.copy(description = it) },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                Column {
                    var newTag by remember { mutableStateOf("") }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.tags), modifier = Modifier.padding(end = 8.dp))
                            OutlinedTextField(
                                value = newTag,
                                onValueChange = { newTag = it },
                                label = { stringResource(R.string.new_tag) },
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
                                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_tag))
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
                    Text(stringResource(R.string.time))
                    OutlinedTextField(
                        value = copy.time.toString(),
                        onValueChange = {
                            var time = 0
                            try {
                                time = it.toInt()
                            }
                            catch ( e : NumberFormatException){
                                time = 0
                            }
                            copy = copy.copy(time = time)

                                        },
                        label = { Text(stringResource(R.string.time_in_minutes)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    Text(stringResource(R.string.minutes))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre de personnes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.for_people))
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
                    Text(stringResource(R.string.people))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ingrédients
                Column {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.ingredients))
                        IconButton(onClick = {
                            val updatedIngredients = copy.ingredients + Ingredient("", 0.0, "")
                            copy = copy.copy(ingredients = updatedIngredients)
                            onRecipeChange(copy)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_ingredient))
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
                                label = { Text(stringResource(R.string.ingredient))  },
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
                                label = { Text(stringResource(R.string.quantity))  },
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
                                label = { Text(stringResource(R.string.unit))  },
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
                                    contentDescription = stringResource(R.string.delete_ingredient),
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
                        Text(stringResource(R.string.steps))
                        IconButton(onClick = {
                            val updatedSteps = copy.steps + ""
                            copy = copy.copy(steps = updatedSteps)
                            onRecipeChange(copy)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_step))
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
                                label = { Text(stringResource(R.string.step, index + 1)) },
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
                                    contentDescription = stringResource(R.string.delete_step),
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