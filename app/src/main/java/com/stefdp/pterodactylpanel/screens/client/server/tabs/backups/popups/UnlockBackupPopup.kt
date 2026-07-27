package com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups

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
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.ClientServerBackupsTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.ClientServerBackupsTabViewModel

@Composable
fun UnlockBackupPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerBackupsTabUiState,
    viewModel: ClientServerBackupsTabViewModel,
) {
    val backup = state.backups.find { it.attributes.uuid == state.backupToUnlock }

    if (backup == null) return

    Popup(
        showPopup = state.backupToUnlock != null,
        onDismissRequest = {
            viewModel.setBackupToUnlock(null)
        },
    ) {
        Text(
            text = "Unlock \"${backup.attributes.name}\"",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "This backup will no longer be protected from automated or accidental deletions"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setBackupToUnlock(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.toggleBackupLock(
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
                        },
                        onSuccess = { action ->
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Backup $action successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                Text("Okay")
            }
        }
    }
}