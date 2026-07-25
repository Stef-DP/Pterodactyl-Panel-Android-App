package com.stefdp.pterodactylpanel.screens.client.server.tabs.users.popups

import android.R.attr.enabled
import android.content.Context
import androidx.compose.foundation.layout.Column
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
            viewModel.hideCreateScheduleTaskPopup()
        },
        scrollable = false
    ){
        Text(
            text = "Create New Subuser",
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
            TextInput(
                value = state.newUserEmail,
                onValueChange = {
                    viewModel.setNewUserEmail(it)
                },
                label = "User Email",
                description = "Enter the email address of the user you wish to invite as a subuser for this server",
                modifier = Modifier.fillMaxWidth()
            )

            SubuserPermissions(
                permissions = state.newSubuserPermissions,
                updatePermissions = { permission ->
                    viewModel.setNewSubuserPermissions(permission)
                },
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            buttonType = ButtonType.PRIMARY,
        ) {
            Text(
                text = "Invite User"
            )
        }
    }
}