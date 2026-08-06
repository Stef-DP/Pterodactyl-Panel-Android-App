package com.stefdp.pterodactylpanel.screens.login

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.IS_DEBUG
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.screens.ClientServersScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen

@Composable
fun LoginScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: LoginViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser is User && currentDestination?.route == LoginScreen::class.qualifiedName) {
        navController.navigate(ClientServersScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                )
                .padding(16.dp),
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                AnimatedVisibility(
                    visible = state.isInsecureUrl
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Warning: You are using an unencrypted connection. Your password and data may be visible to others on your network. It is recommended to use HTTPS.",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(
                                modifier = Modifier.size(8.dp)
                            )

                            Switch(
                                checked = state.hasAcknowledgedInsecureUrlWarning,
                                onCheckedChange = {
                                    viewModel.setHasAcknowledgedInsecureUrlWarning(it)
                                },
                                label = "I understand the risks",
                                description = "I acknowledge that using an unencrypted connection may expose my password and data to others on the network, and I accept these risks."
                            )
                        }

                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.serverUrl,
                    onValueChange = {
                        viewModel.setServerUrl(it)
                    },
                    placeholder = "https://example.com",
                    label = "Panel URL",
                    enabled = !state.isLoading,
                    required = true
                )

                TextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.clientApiKey,
                    onValueChange = {
                        viewModel.setClientApiKey(it)
                    },
                    isPassword = true,
                    placeholder = "ptlc_xxxx",
                    label = "Client API Key",
                    enabled = !state.isLoading
                )

                if (IS_DEBUG) {
                    TextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.applicationApiKey,
                        onValueChange = {
                            viewModel.setApplicationApiKey(it)
                        },
                        isPassword = true,
                        placeholder = "ptla_xxxx",
                        label = "Application API Key",
                        description = "Currently unavailable - Planned for a future release",
                        enabled = false // TODO: enable when admin side is done
//                    enabled = !state.isLoading
                    )
                }

                val updateLoggedUser = LocalUpdateLoggedUser.current

                Button(
                    onClick = {
                        viewModel.onLogin(
                            context = context,
                            onSuccess = { hasClientApiKey, hasApplicationApiKey ->
                                if (currentDestination?.route?.startsWith(LoginScreen::class.qualifiedName ?: "") == true) {
                                    if (hasClientApiKey) {
                                        navController.navigate(ClientServersScreen) {
                                            popUpTo(navController.graph.id) { inclusive = true }
                                        }
                                    } else {
                                        // TODO: navigate to home for application API key
//                                        navController.navigate(HomeScreen) {
//                                            popUpTo(navController.graph.id) { inclusive = true }
//                                        }
                                    }
                                }
                            },
                            onError = { error ->
                                Logger.debug("LoginScreen", "Login error: $error")

                                Notification.show(
                                    activity = activity,
                                    duration = 6000L
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            updateLoggedUser = updateLoggedUser,
                        )
                    },
                    enabled = !state.isLoading && (
                            if (state.isInsecureUrl) state.hasAcknowledgedInsecureUrlWarning else true
                        ) && (
                            state.clientApiKey.text.isNotBlank() || state.applicationApiKey.text.isNotBlank()
                        ),
                    modifier = Modifier.padding(
                        top = 4.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = LocalContentColor.current,
                            )

                            Spacer(
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = LocalContentColor.current,
                        )
                    }
                }
            }
        }
    }
}