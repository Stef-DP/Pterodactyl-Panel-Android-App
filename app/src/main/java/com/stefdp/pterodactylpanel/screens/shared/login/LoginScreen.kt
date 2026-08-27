package com.stefdp.pterodactylpanel.screens.shared.login

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.PromptPopup
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.screens.ClientServersScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.utils.hasNotificationsPermission

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

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasPermission = hasNotificationsPermission(context)

                viewModel.setShowNotificationsPopup(!hasPermission)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.closeNotificationsPopup()
        }

        val notificationText = if (isGranted)
            "Notifications Permission Granted"
        else
            "Notifications Permission Denied, please go to the app settings and allow it from there"

        Notification.show(
            activity = activity,
        ) {
            Text(notificationText)
        }
    }

    PromptPopup(
        showPopup = state.showNotificationsPopup,
        onDismissRequest = {
            viewModel.closeNotificationsPopup()
        },
        title = "Notifications Permission",
        description = "The notifications permission is required to properly use background uploads and downloads.\n" +
                "Without this permission, the app will still upload files but it has a higher chance of being killed by the Android system while in the background.",
        successText = "Grant Permission",
        successButtonType = ButtonType.PRIMARY,
        onSuccess = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Notification.show(
                    activity = activity,
                ) {
                    Text("Notifications Permission is automatically granted on this version of Android")
                }
            }
        },
        cancelText = "Not Now",
        cancelButtonType = ButtonType.SECONDARY,
        onCancel = {
            viewModel.closeNotificationsPopup()
        }
    )

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
                    required = true,
                    placeholder = "ptlc_xxxxxxxxx",
                    label = "Client API Key",
                    enabled = !state.isLoading
                )

                val updateLoggedUser = LocalUpdateLoggedUser.current

                Button(
                    onClick = {
                        viewModel.onLogin(
                            context = context,
                            onSuccess = {
                                navController.navigate(ClientServersScreen) {
                                    popUpTo(navController.graph.id) { inclusive = true }
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
                    enabled =
                        !state.isLoading &&
                        (if (state.isInsecureUrl) state.hasAcknowledgedInsecureUrlWarning else true)
                        && state.clientApiKey.text.isNotBlank(),
                    modifier = Modifier.padding(
                        top = 4.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AnimatedVisibility(
                            visible = state.isLoading
                        ) {
                            Row {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = LocalContentColor.current,
                                )

                                Spacer(
                                    modifier = Modifier.size(16.dp)
                                )
                            }
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