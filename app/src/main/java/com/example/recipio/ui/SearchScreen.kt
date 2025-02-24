package com.example.recipio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipio.data.Recipe

@Composable
fun SearchScreen(
    recipes : List<Recipe>,
    onValueChanged : (String) -> Unit
){
    Column {
        SearchBar(onValueChanged)
        for (recipe in recipes){
            Row{
                Text(recipe.name)
                Text(recipe.description)
            }
        }
    }
}

@Composable
fun SearchBar(
    onValueChanged : (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onValueChanged(it) },
        label = { Text("Tapez ici") }
    )
}
