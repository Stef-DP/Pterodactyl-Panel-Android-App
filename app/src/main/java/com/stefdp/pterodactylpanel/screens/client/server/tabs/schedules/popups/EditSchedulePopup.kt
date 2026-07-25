package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.components.ScheduleCronCheatsheet
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun EditSchedulePopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerSchedulesTabUiState,
    viewModel: ClientServerSchedulesTabViewModel,
) {
    Popup(
        showPopup = state.scheduleToEdit != null,
        onDismissRequest = {
            viewModel.setScheduleToEdit(null)
        },
        scrollable = false
    ) {
        Text(
            text = "Edit Schedule",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScrollWithScrollbar(
                        scrollState = scrollState
                    )
            ) {
                TextInput(
                    label = "Schedule Name",
                    value = state.editScheduleName,
                    description = "A human readable identifier for this schedule",
                    onValueChange = {
                        viewModel.setEditScheduleName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Minute",
                    value = state.editScheduleCronMinute,
                    onValueChange = {
                        viewModel.setEditScheduleCronMinute(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading                )

                TextInput(
                    label = "Hour",
                    value = state.editScheduleCronHour,
                    onValueChange = {
                        viewModel.setEditScheduleCronHour(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Day of Month",
                    value = state.editScheduleCronDayOfMonth,
                    onValueChange = {
                        viewModel.setEditScheduleCronDayOfMonth(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Month",
                    value = state.editScheduleCronMonth,
                    onValueChange = {
                        viewModel.setEditScheduleCronMonth(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Day of Week",
                    value = state.editScheduleCronDayOfWeek,
                    onValueChange = {
                        viewModel.setEditScheduleCronDayOfWeek(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                Switch(
                    label = "Show Cheatsheet",
                    description = "Show the cron cheatsheet for some examples",
                    checked = state.editScheduleShowCheatsheet,
                    onCheckedChange = {
                        viewModel.setEditScheduleShowCheatsheet(it)
                    }
                )

                AnimatedVisibility(
                    visible = state.editScheduleShowCheatsheet
                ) {
                    ScheduleCronCheatsheet()
                }

                Switch(
                    label = "Only When Server is Online",
                    description = "Only execute this schedule when the server is in a running state",
                    checked = state.editScheduleOnlyWhenOnline,
                    onCheckedChange = {
                        viewModel.setEditScheduleOnlyWhenOnline(it)
                    },
                    enabled = !state.isLoading
                )

                Switch(
                    label = "Schedule Enabled",
                    description = "This schedule will be executed automatically if enabled",
                    checked = state.editScheduleEnabled,
                    onCheckedChange = {
                        viewModel.setEditScheduleEnabled(it)
                    },
                    enabled = !state.isLoading
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.setScheduleToEdit(null)
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        viewModel.updateSchedule(
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
                                        text = "Schedule updated successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.PRIMARY,
                    enabled =
                        state.editScheduleName.text.trim().isNotBlank() &&
                                state.editScheduleCronMinute.text.trim().isNotBlank() &&
                                state.editScheduleCronHour.text.trim().isNotBlank() &&
                                state.editScheduleCronDayOfMonth.text.trim().isNotBlank() &&
                                state.editScheduleCronMonth.text.trim().isNotBlank() &&
                                state.editScheduleCronDayOfWeek.text.trim().isNotBlank() &&
                                !state.isLoading
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}