package com.example.recipio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recipio.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.LaunchedEffect
import com.example.recipio.RecipeApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(1500)

        // Vérifier si l'utilisateur est déjà connecté
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // L'utilisateur est déjà connecté, aller directement à l'écran d'accueil
            navController.navigate(RecipeApp.Home.name) {
                popUpTo(RecipeApp.Splash.name) { inclusive = true }
            }
        } else {
            // L'utilisateur n'est pas connecté, aller à l'écran de connexion
            navController.navigate(RecipeApp.Login.name) {
                popUpTo(RecipeApp.Splash.name) { inclusive = true }
            }
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        WindowCompat.setDecorFitsSystemWindows(
            (view.context as android.app.Activity).window,
            false
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .systemBarsPadding()
            .fillMaxWidth()
            .safeContentPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = stringResource(R.string.bg),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.app_logo),
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_circle_down_24),
                contentDescription = stringResource(R.string.down_arrow),
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        navController.navigate(RecipeApp.Login.name)
                    }
            )
        }
    }
}