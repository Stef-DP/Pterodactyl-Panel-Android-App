package com.stefdp.pterodactylpanel.screens.client.server.tabs.users.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.stefdp.pterodactylpanel.screens.client.server.components.SubuserPermissions
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.ClientServerUsersTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.ClientServerUsersTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun EditUserPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerUsersTabUiState,
    viewModel: ClientServerUsersTabViewModel,
) {
    val userName = state.subusers.find { it.attributes.uuid == state.userToEdit }?.attributes?.email ?: "unknown@unknown.com"

    Popup(
        showPopup = state.userToEdit != null,
        onDismissRequest = {
            viewModel.setUserToEdit(null)
        },
        scrollable = false
    ){
        Text(
            text = "Modify Permissions for $userName",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(8.dp)
                .verticalScrollWithScrollbar(
                    scrollState = scrollState
                )
        ) {
            SubuserPermissions(
                permissions = state.newSubuserPermissions,
                updatePermissions = { permission ->
                    viewModel.setNewSubuserPermissions(permission)
                },
                enabled = !state.isLoading,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setUserToEdit(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.updateUser(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Subuser updated successfully",
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
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled = !state.isLoading
            ) {
                Text(
                    text = "Save"
                )
            }
        }
    }
}