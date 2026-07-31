package com.stefdp.pterodactylpanel.screens.client.server.tabs.network.popups

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
import com.stefdp.pterodactylpanel.screens.client.server.tabs.network.ClientServerNetworkTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.network.ClientServerNetworkTabViewModel

@Composable
fun DeleteAllocationPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerNetworkTabUiState,
    viewModel: ClientServerNetworkTabViewModel,
) {
    val allocation = state.allocations.find { it.attributes.id == state.allocationToDelete }

    if (allocation == null) return

    Popup(
        showPopup = state.allocationToDelete != null,
        onDismissRequest = {
            viewModel.setAllocationToDelete(null)
        },
    ) {
        Text(
            text = "Remove Allocation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "This allocation will be immediately removed from your server"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setAllocationToDelete(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.deleteAllocation(
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
                                    text = "Allocation deleted successfully",
                                )
                            }
                        }
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