package com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.popups

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.ApplicationNodeAllocationsTabUiState
import com.stefdp.pterodactylpanel.screens.application.node.tabs.allocations.ApplicationNodeAllocationsTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun BulkDeleteAllocationsPopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationNodeAllocationsTabUiState,
    viewModel: ApplicationNodeAllocationsTabViewModel,
) {
    Popup(
        showPopup = state.showBulkDeletePopup,
        onDismissRequest = {
            viewModel.hideBulkDeletePopup()
        },
        scrollable = false
    ) {
        Text(
            text = "Delete Allocations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 12.dp
            )
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
            Text(
                text = "Are you sure you want to delete the following allocations:"
            )

            Text(
                text = AnnotatedString.fromHtml("""
                <ul>
                    ${state.selectedAllocations.joinToString("") { allocationId ->
                        val allocation = state.allocations.find { it.attributes.id == allocationId }
                    
                        if (allocation != null) {
                            "<li>${allocation.attributes.ip}:${allocation.attributes.port}</li>"
                        } else {
                            "<li>Allocation ID: $allocationId</li>"
                        }
                    }}
                </ul>
            """.trimIndent())
            )
        }

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
                    viewModel.hideBulkDeletePopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.bulkDeleteAllocations(
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
                                    text = "Allocations deleted successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                AnimatedVisibility(
                    visible = state.isLoading
                ) {
                    Row {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                    }
                }

                Text("Delete")
            }
        }
    }
}