package com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.popups

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
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.ApplicationNodeAllocationsTabUiState
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.ApplicationNodeAllocationsTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun CreateAllocationsPopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationNodeAllocationsTabUiState,
    viewModel: ApplicationNodeAllocationsTabViewModel,
) {
    Popup(
        showPopup = state.showCreateAllocationsPopup,
        onDismissRequest = {
            viewModel.hideCreateAllocationsPopup()
        },
        scrollable = false
    ) {
        Text(
            text = "Assign New Allocations",
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
                value = state.newAllocationsIp,
                onValueChange = {
                    viewModel.setNewAllocationsIp(it)
                },
                label = "IP Address",
                description = "Enter an IP address to assign ports to here",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                suggestions = state.allocations.map { it.attributes.ip }.distinct()
            )

            TextInput(
                value = state.newAllocationsIpAlias,
                onValueChange = {
                    viewModel.setNewAllocationsIpAlias(it)
                },
                label = "IP Alias",
                description = "If you would like to assign a default alias to these allocations enter it here",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            )

            TextInput(
                value = state.newAllocationsPorts,
                onValueChange = {
                    viewModel.setNewAllocationsPorts(it)
                },
                label = "Ports",
                description = "Enter individual ports or port ranges here separated by commas or spaces",
                modifier = Modifier.fillMaxWidth(),
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
                    viewModel.hideCreateAllocationsPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createAllocations(
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
                                    text = "Allocations assigned successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled =
                    !state.isLoading &&
                    state.newAllocationsIp.text.isNotBlank() &&
                    state.newAllocationsPorts.text.isNotBlank()
            ) {
                Text("Submit")
            }
        }
    }
}