package com.example.recipio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipio.R
import com.example.recipio.data.RecipeUiState
import com.example.recipio.viewmodel.RecipeChatViewModel
import com.example.recipio.viewmodel.RecipeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.recipio.RecipeApp
import com.example.recipio.data.Recipe
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun HomeScreen(navController: NavHostController, uiState: RecipeUiState, viewModel: RecipeViewModel = viewModel()) {
    var showChatDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getRecipes()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            contentDescription = stringResource(R.string.app_logo),
                            modifier = Modifier.size(130.dp)
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 33.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                navController.navigate(RecipeApp.Search.name)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val sections = listOf(
                    stringResource(R.string.recent_recipes) to uiState.recentRecipes,
                    stringResource(R.string.favorites) to uiState.recipes.filter { it.isFavorite },
                    stringResource(R.string.all_recipes) to uiState.recipes
                )

                sections.forEach { (title, recipes) ->
                    RecipeSection(title = title, navController = navController, recipes = recipes, viewModel)
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
                        Text(text = stringResource(R.string.add), color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Bouton de chat
        FloatingActionButton(
            onClick = { showChatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFE58E30)
        ) {
            Icon(
                Icons.Filled.Message,
                contentDescription = "Assistant recettes",
                tint = Color.White
            )
        }
    }

    // Boîte de dialogue du chatbot
    if (showChatDialog) {
        RecipeChatDialog(
            onDismiss = { showChatDialog = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun RecipeSection(title: String, navController: NavHostController, recipes : List<Recipe>, viewModel: RecipeViewModel) {
    val recent = stringResource(R.string.recent_recipes)
    val favorites = stringResource(R.string.favorites)
    val all = stringResource(R.string.all_recipes)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    when (title) {
                        favorites -> navController.navigate("favorite_recipes")
                        recent -> navController.navigate("recent_recipes")
                        all -> navController.navigate("all_recipes")
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = stringResource(R.string.voir_plus),
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
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .clickable {
                navController.navigate("${RecipeApp.Recipe.name}/${recipe.id}")
            }
    ) {
        if(recipe.imageUrl != "") {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = stringResource(R.string.description_uri),
                modifier = Modifier.size(200.dp)
            )
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.default_dish_image),
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun RecipeChatDialog(
    onDismiss: () -> Unit,
    viewModel: RecipeViewModel,
    chatViewModel: RecipeChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = RecipeChatViewModelFactory(viewModel)
    )
) {
    val chatState by chatViewModel.chatState.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Faire défiler automatiquement jusqu'au dernier message
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assistant de recettes") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                // Afficher les messages du chat
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState
                ) {
                    items(chatState.messages) { message ->
                        ChatMessage(
                            text = message.text,
                            isFromUser = message.isFromUser
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Afficher l'indicateur de chargement
                    if (chatState.isLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFFE58E30),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Réflexion en cours...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Zone de saisie du message
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Posez une question sur les recettes...") },
                        singleLine = true,
                        enabled = !chatState.isLoading
                    )

                    IconButton(
                        onClick = {
                            if (userInput.isNotEmpty() && !chatState.isLoading) {
                                chatViewModel.sendMessage(userInput)
                                userInput = ""
                            }
                        },
                        enabled = !chatState.isLoading && userInput.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Envoyer",
                            tint = if (!chatState.isLoading && userInput.isNotEmpty())
                                Color(0xFFE58E30) else Color.Gray
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE58E30))
            ) {
                Text("Fermer")
            }
        }
    )
}

@Composable
fun ChatMessage(
    text: String,
    isFromUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFromUser)
                    Color(0xFFE58E30)
                else
                    Color(0xFFF1F1F1)
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = if (isFromUser)
                    Color.White
                else
                    Color.Black
            )
        }
    }
}


//  pour créer le RecipeChatViewModel avec une instance de RecipeViewModel
class RecipeChatViewModelFactory(private val recipeViewModel: RecipeViewModel) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeChatViewModel::class.java)) {
            return RecipeChatViewModel(recipeViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}