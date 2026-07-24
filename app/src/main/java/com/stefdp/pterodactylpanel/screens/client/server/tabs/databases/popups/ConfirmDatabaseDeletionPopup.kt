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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
fun ConfirmDatabaseDeletionPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerDatabasesTabUiState,
    viewModel: ClientServerDatabasesTabViewModel,
) {
    Popup(
        showPopup = state.databaseToDelete != null,
        onDismissRequest = {
            viewModel.setDatabaseToDelete(null)
        },
        scrollable = true
    ) {
        val database = state.databases.find { it.attributes.id == state.databaseToDelete }?.attributes ?: return@Popup

        val databaseName = database.name

        Text(
            text = "Confirm database deletion",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )

        Column(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val warningString = buildAnnotatedString {
                append("Deleting a database is a permanent action, it cannot be undone. This will permanently delete the ")

                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(databaseName)
                }

                append(" database and remove all associated data")
            }

            Text(
                text = warningString
            )

            TextInput(
                label = "Confirm Database Name",
                value = state.confirmDatabaseNameValue,
                onValueChange = {
                    viewModel.setConfirmDeleteDatabaseNameValue(it)
                },
                modifier = Modifier.fillMaxWidth(),
                description = "Enter the database name to confirm deletion"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.setDatabaseToDelete(null)
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Cancel")
                }

                val validNames = listOf(
                    databaseName,
                    databaseName.split("_").drop(1).joinToString("_")
                )

                Button(
                    onClick = {
                        viewModel.deleteDatabase(
                            context = context,
                            databaseId = database.id,
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
                                        text = "Database Deleted successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.ERROR,
                    enabled = state.confirmDatabaseNameValue.text in validNames && !state.isLoading
                ) {
                    Text("Delete Database")
                }
            }
        }
    }
}