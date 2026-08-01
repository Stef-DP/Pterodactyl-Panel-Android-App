package com.stefdp.pterodactylpanel.screens.client.server.tabs.users

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.SubuserDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.popups.CreateUserPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.popups.DeleteUserPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.users.popups.EditUserPopup
import com.stefdp.pterodactylpanel.utils.hasPermission
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun UsersTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerUsersTabViewModel = viewModel(),
    server: GetServerResponse?,
) {
    LaunchedEffect(server) {
        viewModel.init(server)

        viewModel.updateUsers(
            context = context,
            onSuccess = {},
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

    val state by viewModel.state.collectAsState()

    CreateUserPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    EditUserPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    DeleteUserPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    if (
        hasPermission(
            isServerOwner = state.isServerOwner,
            userPermissions = state.userPermissions,
            requiredPermission = ServerSubuser.Permissions.USER_CREATE
        )
    ) {
        Button(
            onClick = {
                viewModel.showCreateNewUserPopup()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                ),
            buttonType = ButtonType.PRIMARY
        ) {
            Text(
                text = "New User"
            )
        }
    }

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .verticalLazyScrollbar(
                listState = listState,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.isLoading) {
            items(5) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerable(
                            enabled = true,
                            height = 150.dp
                        )
                )
            }
        } else if (state.subusers.isEmpty()) {
            item {
                Text(
                    text = "It looks like you don't have any subusers",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(state.subusers.size) { index ->
                val subuser = state.subusers[index]

                SubuserDisplay(
                    subuser = subuser,
                    onEdit = {
                        viewModel.setUserToEdit(subuser)
                    },
                    onDelete = {
                        viewModel.setUserToDelete(subuser)
                    },
                    hasDeletePermission = hasPermission(
                        isServerOwner = state.isServerOwner,
                        userPermissions = state.userPermissions,
                        requiredPermission = ServerSubuser.Permissions.USER_DELETE
                    ),
                    hasUpdatePermission = hasPermission(
                        isServerOwner = state.isServerOwner,
                        userPermissions = state.userPermissions,
                        requiredPermission = ServerSubuser.Permissions.USER_UPDATE
                    )
                )
            }
        }
    }
}