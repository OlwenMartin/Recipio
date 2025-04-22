package com.example.recipio

import com.example.recipio.ui.HomeScreen
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.recipio.data.RecipeField
import com.example.recipio.ui.HomeScreen
import com.example.recipio.ui.SettingsScreen

enum class RecipeApp(@StringRes val title: Int){
    Start(title = R.string.app_name),
    Search(title=R.string.search),
    Home(title=R.string.home),
    Recipe(title=R.string.recipe),
    Add(title=R.string.add),
    Modify(title=R.string.modify),
    Login(title=R.string.login),
    Signup(title=R.string.signup),
    Splash(title=R.string.splash),
    Settings(title=R.string.settings)
}

@Composable
fun RecipeApp(
    viewModel : RecipeViewModel = RecipeViewModel(),
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
                            viewModel.getRecipes()
                            navController.navigate(RecipeApp.Home.name) {
                                popUpTo(RecipeApp.Login.name) { inclusive = true }
                            }
                        },
                        navController = navController
                    )
                }

                composable(route = RecipeApp.Settings.name) {
                    SettingsScreen(navController)
                }

                composable(route = "Splash") {
                    SplashScreen(navController)
                }

                composable(route = RecipeApp.Signup.name) {
                    SignupScreen(navController)
                }

                composable(route = RecipeApp.Home.name) {
                    HomeScreen(navController, uiState, viewModel)
                }
                composable(route = "${RecipeApp.Search.name}/{key}/{value}") { backStackEntry ->
                    val key = backStackEntry.arguments?.getString("key") ?: "Name"
                    val value = backStackEntry.arguments?.getString("value") ?: ""

                    viewModel.filterRecipes(key, value)
                    SearchScreen(
                        recipes = uiState.filteredRecipes,
                        navigate = {location -> navController.navigate(location)}
                    )
                }

                composable(route = RecipeApp.Search.name) {
                    viewModel.filterRecipes("Name", "")
                    SearchScreen(
                        recipes = uiState.filteredRecipes,
                        navigate = {location -> navController.navigate(location)})
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
                        //onSave = { recipe -> viewModel.addRecipeToUser(recipe) },
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
                        //onSave = { recipe -> viewModel.addRecipeToUser(recipe) },
                        navController = navController,
                        modifier = Modifier
                            .padding(top = 25.dp, start = 5.dp)
                            .fillMaxWidth()
                    )
                }

                composable(route = "recent_recipes") {
                    RecentRecipesScreen(navController, viewModel)
                }
                composable(route = "favorite_recipes") {
                    FavoriteRecipesScreen(navController, viewModel)
                }
                composable(route = "all_recipes") {
                    AllRecipesScreen(navController, viewModel)
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Catégories de recettes
            val categories = listOf("Entrée", "Plat", "Dessert", "Autre")

            categories.forEach { category ->
                val isSelected = currentRoute.toString() == "${RecipeApp.Search.name}/{key}/{value}" &&
                        currentBackStackEntry?.arguments?.getString("key") == RecipeField.Category.toString() &&
                        currentBackStackEntry?.arguments?.getString("value") == category

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            navController.navigate("${RecipeApp.Search.name}/${RecipeField.Category}/${category}")
                        }
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Indicateur de sélection
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(3.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }

            // Séparateur vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )

            // Icônes de navigation
            val navItems = listOf(
                Triple(R.drawable.baseline_display_settings_24, "Settings", RecipeApp.Settings.name),
                Triple(R.drawable.baseline_home_24, "Home", RecipeApp.Home.name),
                Triple(R.drawable.search_icon, "Search", RecipeApp.Search.name)
            )

            navItems.forEach { (iconRes, description, route) ->
                val isSelected = currentRoute == route

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate(route) }
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = description,
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(26.dp)
                            .padding(bottom = 4.dp)
                    )

                    // Indicateur de sélection
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(3.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}