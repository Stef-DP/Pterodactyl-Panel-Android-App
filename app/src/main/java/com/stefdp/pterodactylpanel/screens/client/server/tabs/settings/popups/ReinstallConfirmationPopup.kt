package com.stefdp.pterodactylpanel.screens.client.server.tabs.settings.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.settings.ClientServerSettingsTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.settings.ClientServerSettingsTabViewModel

@Composable
fun ReinstallConfirmationPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerSettingsTabUiState,
    viewModel: ClientServerSettingsTabViewModel,
    updateServer: () -> Unit
) {
    Popup(
        showPopup = state.showReinstallConfirmation,
        onDismissRequest = {
            viewModel.hideReinstallConfirmation()
        },
    ) {
        Text(
            text = "Confirm Server Reinstallation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Your server will be stopped and some files may be deleted or modified during this process, are you sure you wish to continue?"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideReinstallConfirmation()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.reinstall(
                        context = context,
                        updateServer = updateServer,
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
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Started reinstalling server",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                Text("Yes, reinstall server")
            }
        }
    }
}