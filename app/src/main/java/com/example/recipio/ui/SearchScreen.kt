package com.example.recipio.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.recipio.R
import com.example.recipio.RecipeApp
import com.example.recipio.viewmodel.RecipeViewModel

@Composable
fun SearchScreen(
    navigate: (String) -> Unit,
    key: String,
    value: String,
    viewModel: RecipeViewModel = viewModel()
) {
    var loadingInitiated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!loadingInitiated) {
            loadingInitiated = true
            viewModel.getRecipes()
            viewModel.filterRecipes(key, value)
        }
    }

    val currentState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }

    var filteredList = currentState.filteredRecipes

    if (key == "Category" && value.isNotEmpty()) {
        filteredList = filteredList.filter { it.category.equals(value, ignoreCase = true) }
    }

    if (searchText.isNotEmpty()) {
        filteredList = filteredList.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            onValueChanged = { searchText = it },
            modifier = Modifier.padding(bottom = 10.dp)
        )

        if (currentState.recipes.isEmpty() && filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF9bc268))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 30.dp)
            ) {
                if (filteredList.isEmpty()) {
                    Text(stringResource(R.string.no_recipe_found))
                }
                for (recipe in filteredList) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .clickable {
                                navigate("${RecipeApp.Recipe.name}/${recipe.id}")
                            }
                    ) {
                        if (recipe.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = recipe.imageUrl,
                                contentDescription = "Image chargée depuis une URI",
                                modifier = Modifier.size(120.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.default_dish_image),
                                contentDescription = recipe.name,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = recipe.name, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFF9bc268),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedLabelColor = Color.White
        ),
        shape = RoundedCornerShape(25.dp),
        onValueChange = {
            text = it
            onValueChanged(it)
        },
        label = { Text(stringResource(R.string.search_text)) }
    )
}