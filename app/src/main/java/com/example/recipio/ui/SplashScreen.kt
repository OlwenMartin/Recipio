package com.example.recipio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recipio.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.WindowInsets

@Composable
fun SplashScreen(navController: NavController) {

    // Ensure edge-to-edge
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
            .background(Color(0xFF9bc268))
            .systemBarsPadding()
            .fillMaxWidth()
            .safeContentPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier.fillMaxSize() .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "Recipio",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_circle_down_24),
                contentDescription = "Flèche vers le bas",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        navController.navigate("Login")
                    }
            )
        }
    }
}
