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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recipio.R
import com.example.recipio.viewmodel.RecipeViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: RecipeViewModel = viewModel(),
    navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    val connexion_reussies = stringResource(R.string.connexion_reussie)
    val connexion_echouee = stringResource(R.string.echec_connexion)


    fun loginUser(email: String, password: String) {
        loading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    viewModel.getRecipes()
                    Toast.makeText(context, connexion_reussies, Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                    navController.navigate("Home")
                } else {
                    Toast.makeText(context, "${connexion_echouee}: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
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
            contentDescription = stringResource(R.string.bg),
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
                text = stringResource(R.string.app_name),
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
                            text = stringResource(R.string.seconnecter),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(70.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.courriel)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_email_24),
                                    contentDescription = stringResource(R.string.email_icon)
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
                            label = { Text(stringResource(R.string.mot_de_passe)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_adb_24),
                                    contentDescription = stringResource(R.string.password_icon)
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
                            Text(text = if (loading) stringResource(R.string.connexion) else stringResource(R.string.seconnecter), fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row {
                            Text(
                                text = stringResource(R.string.no_account),
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.black)
                            )
                            Text(
                                text = stringResource(R.string.sinscrire),
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