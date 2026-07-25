package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
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
fun CreateNewSchedulePopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerSchedulesTabUiState,
    viewModel: ClientServerSchedulesTabViewModel,
) {
    Popup(
        showPopup = state.showCreateSchedulePopup,
        onDismissRequest = {
            viewModel.hideCreateSchedulePopup()
        },
        scrollable = false
    ) {
        Text(
            text = "Create New Schedule",
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
                    value = state.newScheduleName,
                    description = "A human readable identifier for this schedule",
                    onValueChange = {
                        viewModel.setNewScheduleName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Minute",
                    value = state.newScheduleCronMinute,
                    onValueChange = {
                        viewModel.setNewScheduleCronMinute(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Hour",
                    value = state.newScheduleCronHour,
                    onValueChange = {
                        viewModel.setNewScheduleCronHour(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Day of Month",
                    value = state.newScheduleCronDayOfMonth,
                    onValueChange = {
                        viewModel.setNewScheduleCronDayOfMonth(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Month",
                    value = state.newScheduleCronMonth,
                    onValueChange = {
                        viewModel.setNewScheduleCronMonth(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Day of Week",
                    value = state.newScheduleCronDayOfWeek,
                    onValueChange = {
                        viewModel.setNewScheduleCronDayOfWeek(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                Switch(
                    label = "Show Cheatsheet",
                    description = "Show the cron cheatsheet for some examples",
                    checked = state.newScheduleShowCheatsheet,
                    onCheckedChange = {
                        viewModel.setNewScheduleShowCheatsheet(it)
                    }
                )

                AnimatedVisibility(
                    visible = state.newScheduleShowCheatsheet
                ) {
                    ScheduleCronCheatsheet()
                }

                Switch(
                    label = "Only When Server is Online",
                    description = "Only execute this schedule when the server is in a running state",
                    checked = state.newScheduleOnlyWhenOnline,
                    onCheckedChange = {
                        viewModel.setNewScheduleOnlyWhenOnline(it)
                    },
                    enabled = !state.isLoading
                )

                Switch(
                    label = "Schedule Enabled",
                    description = "This schedule will be executed automatically if enabled",
                    checked = state.newScheduleEnabled,
                    onCheckedChange = {
                        viewModel.setNewScheduleEnabled(it)
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
                        viewModel.hideCreateSchedulePopup()
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        viewModel.createSchedule(
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
                                        text = "Schedule created successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.PRIMARY,
                    enabled =
                        state.newScheduleName.text.trim().isNotBlank() &&
                                state.newScheduleCronMinute.text.trim().isNotBlank() &&
                                state.newScheduleCronHour.text.trim().isNotBlank() &&
                                state.newScheduleCronDayOfMonth.text.trim().isNotBlank() &&
                                state.newScheduleCronMonth.text.trim().isNotBlank() &&
                                state.newScheduleCronDayOfWeek.text.trim().isNotBlank() &&
                                !state.isLoading
                ) {
                    Text("Create Schedule")
                }
            }
        }
    }
}