package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.account.popups.Disable2FaPopup
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.account.popups.Enable2FaPopup
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.account.popups.RecoveryCodes2FAPopup
import com.stefdp.pterodactylpanel.utils.EmailRegex
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun AccountTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientAccountSettingsAccountTabViewModel = viewModel(),
    refreshIndex: Int
) {
    val localLoggedUser = LocalLoggedUser.current

    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(localLoggedUser?.attributes?.id, refreshIndex) {
        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad && state.currentEmail.text.isNotBlank()) return@LaunchedEffect

        lastRefreshIndex = refreshIndex
        viewModel.init(
            context = context,
            user = localLoggedUser,
            onError = { error ->
                Notification.show(
                    activity = activity,
                    duration = 3000L
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }

    Enable2FaPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
    )

    RecoveryCodes2FAPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
    )

    Disable2FaPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScrollWithScrollbar(
                scrollState = scrollState
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Container(
            title = {
                Text(
                    text = "Update Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                label = "Current Password",
                value = state.currentPassword,
                onValueChange =  {
                    viewModel.setCurrentPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                isPassword = true
            )

            TextInput(
                label = "New Password",
                description = "Your new password should be at least 8 characters in length and unique to this service",
                value = state.newPassword,
                onValueChange =  {
                    viewModel.setNewPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                isPassword = true
            )

            TextInput(
                label = "Confirm New Password",
                value = state.newPasswordConfirmation,
                onValueChange =  {
                    viewModel.setNewPasswordConfirmation(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                isPassword = true
            )

            Button(
                onClick = {
                    viewModel.updatePassword(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Password updated successfully"
                                )
                            }
                        },
                        onError = { error ->
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    !state.isLoading &&
                    state.currentPassword.text.trim().isNotBlank() &&
                    state.newPassword.text.trim().isNotBlank() &&
                    state.newPassword.text.trim().length >= 8 &&
                    state.newPassword.text.trim() == state.newPasswordConfirmation.text.trim()
            ) {
                Text(
                    text = "Update Password"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "Update Email Address",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                label = "Email",
                value = state.newEmail,
                onValueChange = {
                    viewModel.setNewEmail(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                label = "Confirm Password",
                value = state.emailPasswordConfirmation,
                onValueChange = {
                    viewModel.setEmailPasswordConfirmation(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                isPassword = true
            )

            Button(
                onClick = {
                    viewModel.updateEmail(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Your primary email has been updated"
                                )
                            }
                        },
                        onError = { error ->
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    !state.isLoading &&
                    state.currentEmail.text.trim() != state.newEmail.text.trim() &&
                    EmailRegex.matches(state.newEmail.text.trim()) &&
                    state.emailPasswordConfirmation.text.trim().isNotBlank()
            ) {
                Text(
                    text = "Update Email"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "Two-Step Verification",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Text(
                text = if (state.is2FAEnabled) {
                    "Two-step verification is currently enabled on your account"
                } else {
                    "You do not currently have two-step verification enabled on your account. Click the button below to begin configuring it"
                }
            )

            Button(
                onClick = {
                    if (state.is2FAEnabled) {
                        viewModel.showDisable2FAPopup()
                    } else {
                        viewModel.showEnable2FAPopup(
                            context = context,
                            onError = { error ->
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                buttonType = if (state.is2FAEnabled) ButtonType.ERROR else ButtonType.PRIMARY
            ) {
                Text(
                    text = (if (state.is2FAEnabled) "Disable" else "Enable") + " Two-Step"
                )
            }
        }
    }
}