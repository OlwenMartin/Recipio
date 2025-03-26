package com.example.recipio.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recipio.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    fun loginUser(email: String, password: String) {
        loading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    Toast.makeText(context, "Connexion réussie!", Toast.LENGTH_SHORT).show()
                    navController.navigate("Home")
                } else {
                    Toast.makeText(context, "Échec de connexion: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Recipio",
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "Se connecter",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(70.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Courriel") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_email_24),
                                    contentDescription = "email Icon"
                                )
                            },
                            modifier = Modifier.width(300.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(40.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Mot de passe") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_adb_24),
                                    contentDescription = "Password Icon"
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.width(300.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { loginUser(email, password) },
                            enabled = !loading,
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.orange)
                            ),
                            border = BorderStroke(1.dp, colorResource(id = R.color.orange_dark))
                        ) {
                            Text(text = if (loading) "Connexion..." else "Se connecter", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row {
                            Text(
                                text = "Pas de compte? ",
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.black)
                            )
                            Text(
                                text = "Inscris-toi !",
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.orange),
                                modifier = Modifier.clickable {
                                    navController.navigate("Signup")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}