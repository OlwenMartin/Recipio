package com.example.recipio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.recipio.R
import com.example.recipio.viewmodel.RecipeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavoriteRecipesScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.recipes.isEmpty()) viewModel.getRecipes()
    }

    val favoriteRecipes = uiState.recipes.filter { it.isFavorite }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            Text(
                text = stringResource(R.string.favorite_recipes_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (favoriteRecipes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_favorite_recipes),
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(favoriteRecipes) { recipe ->
                        RecipeItem(
                            recipe = recipe,
                            navController = navController
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.back_button))
            }
        }
    }
}