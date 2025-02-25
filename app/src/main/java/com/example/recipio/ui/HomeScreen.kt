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

@Composable
fun HomeScreen(navController: NavHostController) {
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

            // Sections
            RecipeSection(title = "Récents")
            RecipeSection(title = "Favoris")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { /* TODO: Action ajouter */ },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE58E30))
                ) {
                    Text(text = "Ajouter", color = Color.White)
                }
            }


            RecipeImageScrollSection(title = "Toutes les recettes")
            Spacer(modifier = Modifier.height(10.dp))
        }

        BottomNavigationBar(navController)
    }
}

@Composable
fun RecipeImageItem(imageRes: Int) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp)  )
            .background(colorResource(id = R.color.green))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Recette",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun RecipeSection(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = "Voir plus",
                modifier = Modifier.size(24.dp)
            )
        }
        LazyRow(modifier = Modifier.padding(start = 16.dp)) {
            items(List(3) { "Recette" }) {
                RecipeItem()
            }
        }
    }
}

@Composable
fun RecipeImageScrollSection(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = "Voir plus",
                modifier = Modifier.size(24.dp)
            )
        }
        LazyRow(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            items(List(10) { R.drawable.default_dish_image }) { imageRes ->
                RecipeImageItem(imageRes)
            }
        }
    }
}

@Composable
fun RecipeItem() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp)  )
            .background(colorResource(id = R.color.green))
    ) {
        Image(
            painter = painterResource(id = R.drawable.default_dish_image),
            contentDescription = "Recette",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB6D08F))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf("Entrée", "Plats", "Desserts", "Autres").forEach {
            Text(
                text = it,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(RecipeApp.Search.name)
                }
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_display_settings_24),
            contentDescription = "settings",
            modifier = Modifier.size(30.dp)
        )
    }
}