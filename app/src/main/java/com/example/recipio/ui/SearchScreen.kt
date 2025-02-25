package com.example.recipio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipio.R
import com.example.recipio.data.Recipe

@Composable
fun SearchScreen(
    recipes : List<Recipe>,
    onValueChanged : (String) -> Unit
){
    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()){
        SearchBar(onValueChanged)
        for (recipe in recipes){
            Row{
                Text(recipe.name)
                Text(recipe.description)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onValueChanged : (String) -> Unit,
    modifier : Modifier = Modifier
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
            onValueChanged(it) },
        label = { Text(stringResource(R.string.search_text)) }
    )
}
