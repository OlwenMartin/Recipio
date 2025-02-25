package com.example.recipio

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipio.ui.HomeScreen
import com.example.recipio.ui.RecipeViewModel
import com.example.recipio.ui.SearchScreen
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipio.data.Recipe
import com.example.recipio.ui.ModifyScreen
import com.example.recipio.ui.RecipeScreen
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

enum class RecipeApp(@StringRes val title: Int){
    Start(title = R.string.app_name),
    Search(title=R.string.search),
    Home(title=R.string.home),
    Recipe(title=R.string.recipe),
    Add(title=R.string.add),
    Modify(title=R.string.modify)
}

@Composable
fun RecipeApp(
    viewModel : RecipeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 30.dp)
            ){
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Recipe Image",
                    modifier = Modifier
                        .size(180.dp, 60.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF9bc268)) {
                Button(
                    onClick = {navController.navigate(RecipeApp.Home.name)}
                ) {
                    Text(stringResource(R.string.home), fontSize = 20.sp)
                }
                Button(
                    onClick = {
                        viewModel.getRecipes()
                        navController.navigate(RecipeApp.Search.name)
                    }
                ) {
                    Text(stringResource(R.string.search), fontSize = 20.sp)
                }
                Button(
                    onClick = {navController.navigate(RecipeApp.Recipe.name)}
                ) {
                    Text(stringResource(R.string.recipe), fontSize = 20.sp)
                }
                Button(
                    onClick = {navController.navigate(RecipeApp.Add.name)}
                ) {
                    Text(stringResource(R.string.add))
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
                    SearchScreen(recipes = uiState.filteredRecipes, onValueChanged = {filter -> viewModel.filterRecipes(filter)})
                }
                composable(route = RecipeApp.Recipe.name) {
                    RecipeScreen(recipe = uiState.selectedRecipe,
                        onRecipeChange = {},
                        onModifyClicked = {navController.navigate(RecipeApp.Modify.name)},
                        modifier=Modifier
                            .padding(top = 25.dp, start = 5.dp)
                            .fillMaxWidth())
                }
                composable(route = RecipeApp.Add.name) {
                    ModifyScreen(recipe = Recipe(),onRecipeChange = {},modifier=Modifier
                        .padding(top = 25.dp, start = 5.dp)
                        .fillMaxWidth())
                }
                composable(route = RecipeApp.Modify.name) {
                    ModifyScreen(recipe = uiState.selectedRecipe, onRecipeChange = {}, modifier = Modifier
                        .padding(top = 25.dp, start = 5.dp)
                        .fillMaxWidth()
                    )
                }
            }
        }
    }

}