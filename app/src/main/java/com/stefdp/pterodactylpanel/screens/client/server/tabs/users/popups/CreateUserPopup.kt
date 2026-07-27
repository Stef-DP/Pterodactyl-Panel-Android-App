package com.stefdp.pterodactylpanel.screens.client.server.tabs.users.popups

import android.R.attr.enabled
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.components.SubuserPermissions
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.ClientServerUsersTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.ClientServerUsersTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun CreateUserPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerUsersTabUiState,
    viewModel: ClientServerUsersTabViewModel,
) {
    Popup(
        showPopup = state.showCreateNewUserPopup,
        onDismissRequest = {
            viewModel.hideCreateNewUserPopup()
        },
        scrollable = false
    ){
        Text(
            text = "Create New Subuser",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScrollWithScrollbar(
                    scrollState = scrollState
                )
        ) {
            TextInput(
                value = state.newUserEmail,
                onValueChange = {
                    viewModel.setNewUserEmail(it)
                },
                label = "User Email",
                description = "Enter the email address of the user you wish to invite as a subuser for this server",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            SubuserPermissions(
                permissions = state.newSubuserPermissions,
                updatePermissions = { permission ->
                    viewModel.setNewSubuserPermissions(permission)
                },
                enabled = !state.isLoading,
                allowedPermissions = state.userPermissions
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
                    viewModel.hideCreateNewUserPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createUser(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Subuser created successfully",
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
                enabled =
                    state.newUserEmail.text.isNotBlank() &&
                            state.newSubuserPermissions.values.any { it } &&
                            !state.isLoading
            ) {
                Text(
                    text = "Invite User"
                )
            }
        }
    }
}