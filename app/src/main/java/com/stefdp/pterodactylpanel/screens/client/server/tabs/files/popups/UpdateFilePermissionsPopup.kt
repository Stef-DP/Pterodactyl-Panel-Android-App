package com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.ClientServerFilesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.ClientServerFilesTabViewModel
import com.stefdp.pterodactylpanel.utils.PermissionModeRegex

@Composable
fun UpdateFilePermissionsPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerFilesTabUiState,
    viewModel: ClientServerFilesTabViewModel,
) {
    Popup(
        showPopup = state.showUpdatePermissionsPopup,
        onDismissRequest = {
            viewModel.hideUpdatePermissionsPopup()
        },
        scrollable = true
    ) {
        Text(
            text = "Update Permissions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )

        TextInput(
            value = state.newPermissions,
            label = "Permissions Mode",
            onValueChange = { newValue ->
                if (PermissionModeRegex.matches(newValue.text) && newValue.text.length <= 4) {
                    viewModel.setNewPermissions(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            placeholder = "New Permissions",
            modifier = Modifier
                .fillMaxWidth(),
            enabled = !state.isLoading
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideUpdatePermissionsPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.updateFilePermissions(
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
                                    text = "Permissions updated successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled = state.newPermissions.text.length in 3..4 && !state.isLoading
            ) {
                Text("Update")
            }
        }
    }
}