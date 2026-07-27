package com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups

import android.R.attr.checked
import android.R.attr.fontWeight
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
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.ClientServerBackupsTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.ClientServerBackupsTabViewModel

@Composable
fun RestoreBackupPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerBackupsTabUiState,
    viewModel: ClientServerBackupsTabViewModel,
) {
    val backup = state.backups.find { it.attributes.uuid == state.backupToRestore }

    if (backup == null) return

    Popup(
        showPopup = state.backupToRestore != null,
        onDismissRequest = {
            viewModel.setBackupToRestore(null)
        },
    ) {
        Text(
            text = "Restore \"${backup.attributes.name}\"",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Your server will be stopped. You will not be able to control the power state, access the file manager, or create additional backups until completed"
        )

        Switch(
            label = "Delete all files before restoring backup",
            checked = state.restoreDeleteAllFiles,
            onCheckedChange = {
                viewModel.setRestoreDeleteAllFiles(it)
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setBackupToRestore(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.restoreBackup(
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
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Backup restored successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                Text("Restore")
            }
        }
    }
}