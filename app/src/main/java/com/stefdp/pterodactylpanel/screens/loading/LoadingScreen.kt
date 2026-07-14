package com.stefdp.pterodactylpanel.screens.loading

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.screens.ClientServersScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.ui.theme.Yellow

@Composable
fun LoadingScreen(
    navController: NavHostController,
    activity: FragmentActivity,
    context: Context,
    viewModel: LoadingViewModel = viewModel()
) {
    val updateLoggedUser = LocalUpdateLoggedUser.current

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (state.isLogging) return@LaunchedEffect

        viewModel.startLoading(
            context = context,
            onError = { error ->
                if (error != null) {
                    Notification.show(
                        activity = activity
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onSuccess = { switchToBiometric, notificationContent ->
                if (notificationContent != null) {
                    Notification.show(
                        activity = activity,
                        duration = 3000L
                    ) {
                        Text(
                            text = notificationContent,
                            color = Yellow
                        )
                    }
                }

                navController.navigate(ClientServersScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            updateLoggedUser = updateLoggedUser
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        CircularProgressIndicator()
    }
}