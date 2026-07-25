package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups

import android.R.attr.enabled
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabViewModel
import com.stefdp.pterodactylpanel.utils.NumberRegex
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun CreateScheduleTaskPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerSchedulesTabUiState,
    viewModel: ClientServerSchedulesTabViewModel,
) {
    Popup(
        showPopup = state.showCreateScheduleTaskPopup,
        onDismissRequest = {
            viewModel.hideCreateScheduleTaskPopup()
        },
        scrollable = false
    ) {
        Text(
            text = "Create Task",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScrollWithScrollbar(
                    scrollState = scrollState,
                )
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Select(
                options = ServerScheduleTask.Attributes.Action.entries.map {
                    SelectOption(
                        id = it.value,
                        label = { enabled ->
                            Text(
                                text = it.uiName,
                                color = if (enabled) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                }
                            )
                        }
                    )
                },
                onSelectionChange = {
                    if (it.isEmpty()) return@Select

                    val value = ServerScheduleTask.Attributes.Action.fromValue(it.first()) ?: return@Select

                    viewModel.setNewScheduleTaskSelectedAction(value)
                },
                selectedIds = setOf(state.newScheduleTaskSelectedAction.toString()),
                enabled = !state.isLoading
            )

            TextInput(
                label = "Time Offset (In Seconds)",
                value = state.newScheduleTaskTimeOffset,
                onValueChange = {
                    if (!NumberRegex.matches(it.text)) return@TextInput

                    viewModel.setNewScheduleTaskTimeOffset(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                description = "The amount of time to wait after the previous task executes before running this one. If this is the first task on a schedule this will not be applied",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            when (state.newScheduleTaskSelectedAction) {
                ServerScheduleTask.Attributes.Action.COMMAND -> {
                    TextInput(
                        label = "Payload",
                        value = state.newScheduleTaskPayload,
                        onValueChange = {
                            viewModel.setNewScheduleTaskPayload(it)
                        },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        enabled = !state.isLoading
                    )
                }

                ServerScheduleTask.Attributes.Action.POWER -> {
                    Select(
                        options = ServerPowerSignal.entries.map { powerSignal ->
                            SelectOption(
                                id = powerSignal.value,
                                label = { enabled ->
                                    Text(
                                        text = powerSignal.uiName,
                                        color = if (enabled) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        }
                                    )
                                }
                            )
                        },
                        selectedIds = setOf(state.newScheduleTaskPayload.text),
                        onSelectionChange = {
                            if (it.isEmpty()) return@Select

                            Logger.debug("Select", "Selected Power Signal: ${it.first()}")

                            viewModel.setNewScheduleTaskPayload(TextFieldValue(it.first()))
                        },
                        enabled = !state.isLoading
                    )
                }

                ServerScheduleTask.Attributes.Action.BACKUP -> {
                    TextInput(
                        label = "Ignored Files",
                        value = state.newScheduleTaskPayload,
                        onValueChange = {
                            viewModel.setNewScheduleTaskPayload(it)
                        },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        description = "Optional. Include the files and folders to be excluded in this backup. By default, the contents of your .pteroignore file will be used. If you have reached your backup limit, the oldest backup will be rotated",
                        enabled = !state.isLoading,
                    )
                }
            }

            Switch(
                label = "Continue on Failure",
                checked = state.newScheduleTaskContinueOnFailure,
                onCheckedChange = {
                    viewModel.setNewScheduleTaskContinueOnFailure(it)
                },
                description = "Future tasks will be run when this task fails",
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
                    viewModel.hideCreateScheduleTaskPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createNewScheduleTask(
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
                                    text = "Schedule task created successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled =
                    (
                        state.newScheduleTaskSelectedAction == ServerScheduleTask.Attributes.Action.BACKUP ||
                        state.newScheduleTaskPayload.text.isNotBlank()
                    ) &&
                    state.newScheduleTaskTimeOffset.text.isNotBlank() &&
                    !state.isLoading
            ) {
                Text("Create Task")
            }
        }
    }
}