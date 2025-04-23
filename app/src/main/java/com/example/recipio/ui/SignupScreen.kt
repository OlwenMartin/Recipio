package com.example.recipio.ui

import android.widget.Toast
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
import com.example.recipio.RecipeApp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = stringResource(R.string.background_image_desc),
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
                            text = stringResource(R.string.signup),
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
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.width(300.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(20.dp))

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
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.width(300.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(R.string.confirm_password)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_adb_24),
                                    contentDescription = stringResource(R.string.confirm_password_icon_desc)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.width(300.dp),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (password == confirmPassword && email.isNotBlank()) {
                                    loading = true
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            loading = false
                                            if (task.isSuccessful) {
                                                val userId = auth.currentUser?.uid
                                                if (userId != null) {
                                                    val userRef = Firebase.firestore.collection("users").document(userId)
                                                    val userData = hashMapOf(
                                                        "email" to email,
                                                        "recipes" to listOf<String>() // Liste vide de recettes
                                                    )

                                                    userRef.set(userData)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(context,
                                                                context.getString(R.string.signup_success),
                                                                Toast.LENGTH_SHORT).show()
                                                            navController.navigate(RecipeApp.Login.name) {
                                                                popUpTo(RecipeApp.Signup.name) { inclusive = true }
                                                            }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(context,
                                                                context.getString(R.string.firestore_error, e.message),
                                                                Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                            } else {
                                                val errorMessage = task.exception?.message ?:
                                                context.getString(R.string.general_error)
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(context,
                                        context.getString(R.string.passwords_mismatch),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }) {
                            Text(
                                text = stringResource(
                                    if (loading) R.string.signing_up else R.string.signup_button
                                ),
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row {
                            Text(
                                text = stringResource(R.string.already_have_account),
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.black)
                            )
                            Text(
                                text = stringResource(R.string.login_now),
                                fontSize = 18.sp,
                                color = colorResource(id = R.color.orange),
                                modifier = Modifier.clickable {
                                    navController.navigate(RecipeApp.Login.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}