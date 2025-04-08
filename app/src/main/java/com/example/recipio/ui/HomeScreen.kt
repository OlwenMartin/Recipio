import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recipio.R
import com.example.recipio.RecipeApp
import com.example.recipio.data.Recipe
import com.example.recipio.viewmodel.RecipeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipio.data.RecipeUiState

@Composable
fun HomeScreen(navController: NavHostController, uiState: RecipeUiState, viewModel: RecipeViewModel = viewModel()) {

    LaunchedEffect(uiState.recipes.size) {
        if (uiState.recipes.isEmpty()) {
            viewModel.getRecipes()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_accueil),
                        contentDescription = "Logo",
                        modifier = Modifier.size(130.dp)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Text(
                        text = "Recipio",
                        fontSize = 33.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate(RecipeApp.Search.name)
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val sections = listOf(
                "Récents" to uiState.recipes.takeLast(8).reversed(),
                "Favoris" to uiState.recipes.filter { it.isFavorite },
                "Toutes les recettes" to uiState.recipes
            )

            sections.forEach { (title, recipes) ->
                RecipeSection(title = title, navController = navController, recipes = recipes)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        navController.navigate(RecipeApp.Add.name)
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE58E30))
                ) {
                    Text(text = "Ajouter", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}
/*
@Composable
fun RecipeImageItem(imageRes: Int, navController: NavHostController) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(id = R.color.green))
            .clickable { navController.navigate(RecipeApp.Recipe.name) }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Recette",
            modifier = Modifier.fillMaxSize()
        )
    }
}*/

@Composable
fun RecipeSection(title: String, navController: NavHostController, recipes : List<Recipe>) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    when (title) {
                        "Favoris" -> navController.navigate("favorite_recipes")
                        "Récents" -> navController.navigate("recent_recipes")
                        "Toutes les recettes" -> navController.navigate("all_recipes")
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = "Voir plus",
                modifier = Modifier.size(24.dp)
            )
        }
        LazyRow(modifier = Modifier.padding(start = 16.dp)) {
            items(recipes) { recipe ->
                RecipeItem(recipe, navController)
            }
        }
    }
}

@Composable
fun RecipeItem(
    recipe: Recipe,
    navController: NavHostController,
    onRecipeClick: () -> Unit = {
        navController.navigate("recipe_detail/${recipe.id}")
    }
) {    Box(
    modifier = Modifier
        .padding(8.dp)
        .size(120.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.Gray)
        .clickable {
            //navController.navigate("recipe_detail/${recipe.id}")
            navController.navigate(RecipeApp.Recipe.name)
        }
) {
    Image(
        painter = painterResource(id = R.drawable.default_dish_image),
        contentDescription = recipe.name,
        modifier = Modifier.fillMaxSize()
    )
}
}
