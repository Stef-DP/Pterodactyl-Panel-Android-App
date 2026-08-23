package com.stefdp.pterodactylpanel.screens.application.server.tabs.databases.popups

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
import com.stefdp.pterodactylpanel.screens.application.server.tabs.databases.ApplicationServerDatabasesTabUiState
import com.stefdp.pterodactylpanel.screens.application.server.tabs.databases.ApplicationServerDatabasesTabViewModel

@Composable
fun DeleteDatabasePopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationServerDatabasesTabUiState,
    viewModel: ApplicationServerDatabasesTabViewModel,
    reload: (
        isRefresh: Boolean,
        onReloadFinish: () -> Unit,
        increaseRefreshIndex: Boolean,
        onError: (String) -> Unit
    ) -> Unit
) {
    val database = state.databases.find { it.attributes.id == state.databaseToDelete }

    if (database == null) return

    Popup(
        showPopup = state.databaseToDelete != null,
        onDismissRequest = {
            viewModel.setDatabaseToDelete(null)
        },
    ) {
        Text(
            text = "Delete Database",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Are you sure that you want to delete this database? There is no going back, all data will immediately be removed"
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

            Button(
                onClick = {
                    viewModel.deleteDatabase(
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
                                    text = "Server database deleted successfully",
                                )
                            }
                        },
                        reload = reload
                    )
                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                Text("Delete")
            }
        }
    }
}