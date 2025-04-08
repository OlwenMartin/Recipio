package com.example.recipio

import HomeScreen
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.currentBackStackEntryAsState

enum class RecipeApp(@StringRes val title: Int){
    Start(title = R.string.app_name),
    Search(title=R.string.search),
    Home(title=R.string.home),
    Recipe(title=R.string.recipe),
    Add(title=R.string.add),
    Modify(title=R.string.modify),
    Login(title=R.string.login),
    Signup(title=R.string.signup),
    Splash(title=R.string.splash)
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
            if (currentRoute != RecipeApp.Home.name && currentRoute != RecipeApp.Login.name && currentRoute != RecipeApp.Signup.name && currentRoute != RecipeApp.Splash.name) {
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
            val excludedRoutes = listOf(RecipeApp.Login.name, RecipeApp.Signup.name, RecipeApp.Splash.name)
            if (currentRoute !in excludedRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NavHost(
                navController = navController,
                startDestination = "Splash",
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(route = RecipeApp.Login.name) {
                    LoginScreen(
                        onLoginSuccess = {
                            viewModel.getRecipes() // Charge les recettes immédiatement après connexion
                            navController.navigate(RecipeApp.Home.name) {
                                popUpTo(RecipeApp.Login.name) { inclusive = true }
                            }
                        },
                        navController = navController
                    )
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
                    SearchScreen(
                        recipes = uiState.filteredRecipes,
                        onValueChanged = { filter -> viewModel.filterRecipes(filter) })
                }
                composable(route = "${RecipeApp.Recipe.name}/{recipeId}") { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId")
                    LaunchedEffect(recipeId) {
                        if (recipeId != null) {
                            viewModel.getRecipe(recipeId)
                        }
                    }

                    RecipeScreen(
                        recipe = uiState.selectedRecipe,
                        onRecipeChange = {updatedRecipe ->
                            viewModel.updateRecipe(updatedRecipe)},
                        onModifyClicked = { navController.navigate(RecipeApp.Modify.name) },
                        modifier = Modifier
                            .padding(top = 25.dp, start = 5.dp)
                            .fillMaxWidth(),
                        onFavoriteToggle = { recipeId ->
                            viewModel.toggleFavorite(recipeId)
                        },
                    )
                }
                composable(route = RecipeApp.Add.name) {
                    ModifyScreen(
                        recipe = Recipe(),
                        isNew = true,
                        onRecipeChange = {},
                        onSave = { recipe -> viewModel.addRecipeToUser(recipe) },
                        navController = navController,
                        modifier = Modifier
                            .padding(top = 25.dp, start = 5.dp)
                            .fillMaxWidth()
                    )
                }
                composable(route = RecipeApp.Modify.name) {
                    ModifyScreen(
                        recipe = uiState.selectedRecipe,
                        onRecipeChange = {},
                        onSave = { recipe -> viewModel.addRecipeToUser(recipe) },
                        navController = navController,
                        modifier = Modifier
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
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xFFB6D08F),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Catégories de recettes avec la même structure
            listOf("Entrée", "Plats", "Desserts", "Autres").forEach { category ->
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { navController.navigate(RecipeApp.Search.name) }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            // Icône paramètres
            Icon(
                painter = painterResource(id = R.drawable.baseline_display_settings_24),
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(2.dp)
            )

            // Icône accueil
            Icon(
                painter = painterResource(id = R.drawable.baseline_home_24),
                contentDescription = "Home",
                tint = if (currentRoute == RecipeApp.Home.name) Color(0xFF436118) else Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { navController.navigate(RecipeApp.Home.name) }
                    .padding(2.dp)
            )
        }
    }

}
