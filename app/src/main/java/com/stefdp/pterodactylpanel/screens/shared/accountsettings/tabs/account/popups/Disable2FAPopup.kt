package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountSettingsAccountTabUiState
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountSettingsAccountTabViewModel

@Composable
fun Disable2FaPopup(
    activity: FragmentActivity,
    context: Context,
    state: AccountSettingsAccountTabUiState,
    viewModel: AccountSettingsAccountTabViewModel,
) {
    Popup(
        showPopup = state.showDisable2FAPopup,
        onDismissRequest = {
            viewModel.hideDisable2FAPopup()
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Disable Two-Step Verification",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Disabling two-step verification will make your account less secure"
            )

            TextInput(
                label = "Password",
                value = state.twoFactorAuthenticationPassword,
                onValueChange = {
                    viewModel.set2FAPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.hideDisable2FAPopup()
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        viewModel.disable2FA(
                            context = context,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = "2FA disabled successfully",
                                        color = MaterialTheme.colorScheme.onSurface
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
                            },
                        )
                    },
                    buttonType = ButtonType.ERROR,
                    enabled =
                        state.twoFactorAuthenticationPassword.text.trim().isNotBlank() &&
                        !state.isLoading
                ) {
                    Text("Disable")
                }
            }
        }
    }
}