package com.example.recipio

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipio.ui.HomeScreen
import com.example.recipio.ui.RecipeViewModel
import com.example.recipio.ui.SearchScreen
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipio.ui.RecipeScreen

enum class RecipeApp(@StringRes val title: Int){
    Start(title = R.string.app_name),
    Search(title=R.string.search),
    Home(title=R.string.home),
    Recipe(title=R.string.recipe)
}

@Composable
fun RecipeApp(
    viewModel : RecipeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {navController.navigate(RecipeApp.Home.name)}
                ) {
                    Text(stringResource(R.string.home))
                }
                Button(
                    onClick = {
                        viewModel.getRecipes()
                        navController.navigate(RecipeApp.Search.name)
                    }
                ) {
                    Text(stringResource(R.string.search))
                }
                Button(
                    onClick = {navController.navigate(RecipeApp.Recipe.name)}
                ) {
                    Text(stringResource(R.string.recipe))
                }

            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = 25.dp, start = 5.dp)
                .fillMaxWidth()
        ) {
            NavHost(
                navController = navController,
                startDestination = RecipeApp.Home.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = RecipeApp.Home.name) {
                    HomeScreen()
                }
                composable(route = RecipeApp.Search.name) {
                    SearchScreen(recipes = uiState.recipes)
                }
                composable(route = RecipeApp.Recipe.name) {
                    RecipeScreen(recipe = uiState.selectedRecipe,modifier=Modifier
                        .padding(top = 25.dp, start = 5.dp)
                        .fillMaxWidth())
                }
            }
    }
    }

}