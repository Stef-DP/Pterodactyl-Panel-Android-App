package com.stefdp.pterodactylpanel.screens.shared.biometricauth

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.screens.ClientServersScreen
import com.stefdp.pterodactylpanel.utils.createBiometricPrompt
import com.stefdp.pterodactylpanel.utils.createPromptInfo
import com.stefdp.pterodactylpanel.utils.promptBiometricAuthentication

@Composable
fun BiometricAuthScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues
) {
    fun promptBiometrics() {
        val biometricPrompt = createBiometricPrompt(
            activity = activity,
            onSuccess = {
                navController.navigate(ClientServersScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onError = { _, _ ->
                Notification.show(
                    activity = activity,
                    duration = 3000L
                ) {
                    Text(
                        text = "Biometric Authentication Failed",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        val biometricPromptInfo = createPromptInfo()

        promptBiometricAuthentication(
            activity = activity,
            prompt = biometricPrompt,
            promptInfo = biometricPromptInfo,
        )
    }

    LaunchedEffect(Unit) {
        promptBiometrics()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.lock),
            contentDescription = "Pterodactyl Panel Locked",
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Pterodactyl Panel Locked",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.weight(0.7f)
        )

        TextButton(
            onClick = ::promptBiometrics
        ) {
            Text(
                text = "Unlock",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}