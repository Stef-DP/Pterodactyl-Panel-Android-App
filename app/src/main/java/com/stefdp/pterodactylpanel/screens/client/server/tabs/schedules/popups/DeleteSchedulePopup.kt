package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups

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
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabViewModel

@Composable
fun DeleteSchedulePopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerSchedulesTabUiState,
    viewModel: ClientServerSchedulesTabViewModel,
) {
    Popup(
        showPopup = state.scheduleToDelete != null,
        onDismissRequest = {
            viewModel.setScheduleToDelete(null)
        },
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Delete Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "All tasks will be removed and any running processes will be terminated"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setScheduleToDelete(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.deleteSchedule(
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
                                    text = "Schedule deleted successfully",
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