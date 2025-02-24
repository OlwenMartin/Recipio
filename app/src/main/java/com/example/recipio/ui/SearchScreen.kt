package com.example.recipio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Label
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.recipio.R
import com.example.recipio.data.Recipe

@Composable
fun SearchScreen(
    recipes : List<Recipe>
){
    Column {
        for (recipe in recipes){
            Row{
                Text(recipe.name)
                Text(recipe.description)
            }
        }
    }
}