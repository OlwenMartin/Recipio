package com.example.recipio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.recipio.R
import com.example.recipio.RecipeApp
import com.example.recipio.viewmodel.SettingsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val auth = Firebase.auth
    var showConfirmDialog by remember { mutableStateOf(false) }
    val user = auth.currentUser

    val isDarkMode by settingsViewModel.isDarkMode.collectAsState(initial = false)
    val areNotificationsEnabled by settingsViewModel.areNotificationsEnabled.collectAsState(initial = true)

    val scope = rememberCoroutineScope()

    // Configurer la couleur de fond en fonction du thème
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF9F9F9)
    val titleColor = Color(0xFF88B04B) // Cette couleur reste la même en mode sombre/clair

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Informations de l'utilisateur
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.user_info),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(
                        R.string.email_display,
                        user?.email ?: stringResource(R.string.email_not_available)
                    ),
                    fontSize = 16.sp,
                    color = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section paramètres de l'application
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notifications
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.notifications),
                        fontSize = 16.sp,
                        color = textColor
                    )

                    Switch(
                        checked = areNotificationsEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                settingsViewModel.setNotificationsEnabled(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFE58E30),
                            checkedTrackColor = Color(0xFFFFDDB7)
                        )
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (isDarkMode) Color(0xFF3A3A3A) else Color(0xFFE0E0E0)
                )

                // Mode sombre
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.dark_mode),
                        fontSize = 16.sp,
                        color = textColor
                    )

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                settingsViewModel.setDarkMode(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFE58E30),
                            checkedTrackColor = Color(0xFFFFDDB7)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bouton de déconnexion
        Button(
            onClick = { showConfirmDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE58E30)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp)
        ) {
            Text(
                text = stringResource(R.string.logout),
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }

    // Popup de confirmation de déconnexion
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(R.string.confirm_logout)) },
            text = { Text(stringResource(R.string.logout_confirmation_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        showConfirmDialog = false
                        // Rediriger vers l'écran de connexion
                        navController.navigate(RecipeApp.Login.name) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE58E30)
                    )
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF88B04B)
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}