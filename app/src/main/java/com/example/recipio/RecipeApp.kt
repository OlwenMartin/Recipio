package com.example.recipio

import HomeScreen
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
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
import com.example.recipio.viewmodel.RecipeViewModel
import com.example.recipio.ui.SearchScreen
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipio.data.Recipe
import com.example.recipio.ui.AllRecipesScreen
import com.example.recipio.ui.FavoriteRecipesScreen
import com.example.recipio.ui.LoginScreen
import com.example.recipio.ui.ModifyScreen
import com.example.recipio.ui.RecentRecipesScreen
import com.example.recipio.ui.RecipeScreen
import com.example.recipio.ui.SignupScreen
import com.example.recipio.ui.SplashScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


enum class RecipeApp(@StringRes val title: Int){
    Start(title = R.string.app_name),
    Search(title=R.string.search),
    Home(title=R.string.home),
    Recipe(title=R.string.recipe),
    Add(title=R.string.add),
    Modify(title=R.string.modify),
    Login(title=R.string.login),
    Signup(title=R.string.signup)
}

@Composable
fun RecipeApp(
    viewModel : RecipeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute = backStackEntry.destination.route
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != RecipeApp.Home.name && currentRoute != "Splash" && currentRoute != "Signup" && currentRoute != "Login") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_accueil),
                        contentDescription = "Recipe Image",
                        modifier = Modifier
                            .size(100.dp, 110.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
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
                    Text(stringResource(R.string.add), fontSize = 20.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                //je décommente pcq empêche l’arrière-plan de couvrir toute la largeur de l’écran
                //.padding(top = 25.dp, start = 5.dp)
                .fillMaxWidth()
        ) {
            NavHost(
                navController = navController,
                //startDestination = RecipeApp.Home.name,
                startDestination = "Splash",
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(route = RecipeApp.Login.name) {
                    LoginScreen(navController)
                }

                composable(route = "Splash") {
                    SplashScreen(navController)
                }

                composable(route = RecipeApp.Signup.name) {
                    SignupScreen(navController)
                }

                composable(route = RecipeApp.Home.name) {
                    HomeScreen(navController, uiState)
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
                    ModifyScreen(
                        recipe = Recipe(),
                        isNew = true, onRecipeChange = {}, onSave = {recipe-> viewModel.addRecipeToUser(recipe)},
                        modifier =Modifier
                        .padding(top = 25.dp, start = 5.dp)
                        .fillMaxWidth())
                }
                composable(route = RecipeApp.Modify.name) {
                    ModifyScreen(
                        recipe = uiState.selectedRecipe, onRecipeChange = {}, onSave = {recipe-> viewModel.addRecipeToUser(recipe)}, modifier = Modifier
                        .padding(top = 25.dp, start = 5.dp)
                        .fillMaxWidth()
                    )
                }
                composable(route = "recent_recipes") {
                    RecentRecipesScreen(navController)
                }
                composable(route = "favorite_recipes") {
                    FavoriteRecipesScreen(navController)
                }
                composable(route = "all_recipes") {
                    AllRecipesScreen(navController)
                }
            }
        }
    }

}