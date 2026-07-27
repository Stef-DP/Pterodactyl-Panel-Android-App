package com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups

import android.R.attr.checked
import android.R.attr.fontWeight
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.ClientServerBackupsTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.ClientServerBackupsTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun CreateBackupPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerBackupsTabUiState,
    viewModel: ClientServerBackupsTabViewModel,
) {
    Popup(
        showPopup = state.showCreateBackupPopup,
        onDismissRequest = {
            viewModel.hideCreateBackupPopup()
        },
        scrollable = false
    ) {
        Text(
            text = "Create Server Backup",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScrollWithScrollbar(scrollState)
        ) {
            TextInput(
                value = state.newBackupName,
                onValueChange = {
                    viewModel.setNewBackupName(it)
                },
                label = "Backup Name",
                description = "If provided, the name that should be used to reference this backup",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newBackupIgnoredFiles,
                onValueChange = {
                    viewModel.setNewBackupIgnoredFiles(it)
                },
                label = "Ignored Files",
                description = "Enter the files or folders to ignore while generating this backup. Leave blank to use the contents of the .pteroignore file in the root of the server directory if present. Wildcard matching of files and folders is supported in addition to negating a rule by prefixing the path with an exclamation point",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                enabled = !state.isLoading
            )

            Switch(
                checked = state.newBackupLocked,
                onCheckedChange = {
                    viewModel.setNewBackupLocked(it)
                },
                label = "Locked",
                description = "Prevents this backup from being deleted until explicitly unlocked",
                enabled = !state.isLoading
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideCreateBackupPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createBackup(
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
                                    text = "Backup started successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled = !state.isLoading
            ) {
                Text("Start Backup")
            }
        }
    }
}