package com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.ClientServerDatabasesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.ClientServerDatabasesTabViewModel

@Composable
fun CreateDatabasePopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerDatabasesTabUiState,
    viewModel: ClientServerDatabasesTabViewModel,
) {
    Popup(
        showPopup = state.showCreateDatabasePopup,
        onDismissRequest = {
            viewModel.hideCreateDatabasePopup()
        },
        scrollable = true
    ) {
        Text(
            text = "Create New Database",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextInput(
                label = "Database Name",
                value = state.newDatabaseName,
                onValueChange = {
                    viewModel.setNewDatabaseName(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextInput(
                label = "Connections From",
                value = state.newDatabaseAllowedIp,
                onValueChange = {
                    viewModel.setNewDatabaseAllowedIp(it)
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.hideCreateDatabasePopup()
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        viewModel.createDatabase(
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
                                        text = "Database created successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.PRIMARY,
                    enabled = state.newDatabaseName.text.trim().length >= 3 && !state.isLoading
                ) {
                    Text("Create Database")
                }
            }
        }
    }
}