package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.ScheduleDisplay
import com.stefdp.pterodactylpanel.utils.lazyScrollbar
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun SchedulesTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerSchedulesTabViewModel = viewModel(),
    server: GetServerResponse?
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(server) {
        viewModel.init(server)

        viewModel.updateSchedules(
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
            onSuccess = {}
        )
    }

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
            modifier = Modifier.padding(12.dp)
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
                )

                TextInput(
                    label = "Minute",
                    value = state.newScheduleCronMinute,
                    onValueChange = {
                        viewModel.setNewScheduleCronMinute(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                TextInput(
                    label = "Hour",
                    value = state.newScheduleCronHour,
                    onValueChange = {
                        viewModel.setNewScheduleCronHour(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                TextInput(
                    label = "Day of Month",
                    value = state.newScheduleCronDayOfMonth,
                    onValueChange = {
                        viewModel.setNewScheduleCronDayOfMonth(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                TextInput(
                    label = "Month",
                    value = state.newScheduleCronMonth,
                    onValueChange = {
                        viewModel.setNewScheduleCronMonth(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                TextInput(
                    label = "Day of Week",
                    value = state.newScheduleCronDayOfWeek,
                    onValueChange = {
                        viewModel.setNewScheduleCronDayOfWeek(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "*/5 * * * *",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Every 5 minutes"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "0 */1 * * *",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Every hour"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "0 8-12 * * *",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Hour range"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "0 0 * * *",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Once a day"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "0 0 * * MON",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Every Monday"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "*",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Any value"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = ",",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Value list separator"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "-",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Range values"
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                            ) {
                                Text(
                                    text = "/",
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Step values"
                            )
                        }
                    }
                }

                Switch(
                    label = "Only When Server is Online",
                    description = "Only execute this schedule when the server is in a running state",
                    checked = state.newScheduleOnlyWhenOnline,
                    onCheckedChange = {
                        viewModel.setNewScheduleOnlyWhenOnline(it)
                    }
                )

                Switch(
                    label = "Schedule Enabled",
                    description = "This schedule will be executed automatically if enabled",
                    checked = state.newScheduleEnabled,
                    onCheckedChange = {
                        viewModel.setNewScheduleEnabled(it)
                    }
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

    Popup(
        showPopup = state.scheduleToDisplayDetails != null,
        onDismissRequest = {
            viewModel.setScheduleToDisplayDetails(null)
        }
    ) { }

    Button(
        onClick = {
            viewModel.showCreateSchedulePopup()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 12.dp,
                start = 12.dp,
                end = 12.dp
            )
    ) {
        Text(
            text = "Create Schedule"
        )
    }

    if (state.schedules.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "There are no schedules configured for this server"
            )
        }

        return
    }

    val lazyColumnListState = rememberLazyListState()

    LazyColumn(
        state = lazyColumnListState,
        modifier = Modifier
            .fillMaxSize()
            .verticalLazyScrollbar(
                listState = lazyColumnListState
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.isLoading) {
            items(10) {
                Box(
                   modifier = Modifier
                       .fillMaxWidth()
                       .shimmerable(
                           enabled = true,
                           height = 50.dp
                       )
                ) {}
            }

            return@LazyColumn
        }

        items(state.schedules.size) { index ->
            val schedule = state.schedules[index]

            ScheduleDisplay(
                schedule = schedule,
                onShowScheduleDetails = { scheduleId ->
                    viewModel.setScheduleToDisplayDetails(scheduleId)
                }
            )
        }
    }
}