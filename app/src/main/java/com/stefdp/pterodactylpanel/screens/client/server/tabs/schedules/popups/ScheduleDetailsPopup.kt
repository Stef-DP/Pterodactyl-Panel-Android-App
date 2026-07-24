package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups

import android.R.attr.enabled
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.client.server.components.ScheduleTaskDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabViewModel
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.utils.formatDate
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun ScheduleDetailsPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerSchedulesTabUiState,
    viewModel: ClientServerSchedulesTabViewModel,
) {
    Popup(
        showPopup =
            state.scheduleToDisplayDetails != null &&
            !state.showCreateSchedulePopup &&
            state.scheduleToDelete == null &&
            state.scheduleToEdit == null &&
            state.scheduleTaskToEdit == null &&
            state.scheduleTaskToDelete == null,
        onDismissRequest = {
            viewModel.setScheduleToDisplayDetails(null)
        },
        scrollable = false
    ) {
        val schedule = state.schedules.find { it.attributes.id == state.scheduleToDisplayDetails }?.attributes

        if (schedule == null) return@Popup

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = schedule.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        end = 12.dp
                    )
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .background(if (schedule.isActive) Green else MaterialTheme.colorScheme.error)
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
            ) {
                Text(
                    text = if (schedule.isActive) "Active" else "Inactive",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScrollWithScrollbar(
                    scrollState = scrollState
                )
                .weight(1f, fill = false)
        ) {
            @Composable
            fun CronStats(
                label: String,
                value: String
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

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
                            text = value,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            @Composable
            fun Divider() {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            val lastRun = if (schedule.lastRunAt != null) {
                formatDate(schedule.lastRunAt)
            } else {
                "N/A"
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Last run at: ")
                    }

                    append(lastRun)
                }
            )

            val nextRun = formatDate(schedule.nextRunAt)

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Next run at: ")
                    }

                    append(nextRun)
                }
            )

            Divider()

            CronStats(
                label = "Minute",
                value = schedule.cron.minute
            )

            CronStats(
                label = "Hour",
                value = schedule.cron.hour
            )

            CronStats(
                label = "Day of Month",
                value = schedule.cron.dayOfMonth
            )

            CronStats(
                label = "Month",
                value = schedule.cron.month
            )

            CronStats(
                label = "Day of Week",
                value = schedule.cron.dayOfWeek
            )

            Divider()

            val tasks = schedule.relationships.tasks.data

            val lazyColumnListState = rememberLazyListState()

            LazyColumn(
                state = lazyColumnListState,
                modifier = Modifier
                    .heightIn(
                        max = 250.dp
                    )
                    .verticalLazyScrollbar(
                        listState = lazyColumnListState
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks.size) { index ->
                    val task = tasks[index]

                    ScheduleTaskDisplay(
                        task = task,
                        onEdit = {
                            viewModel.setScheduleTaskToEdit(task.attributes)
                        },
                        onDelete = {
                            viewModel.setScheduleTaskToDelete(task.attributes.id)
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setScheduleToDelete(schedule.id)
                    },
                    buttonType = ButtonType.ERROR,
                    enabled = !state.isLoading
                ) {
                    Text("Delete")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setScheduleToEdit(schedule)
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Edit")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (schedule.relationships.tasks.data.isNotEmpty()) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.executeSchedule(
                                context = context,
                                onError = { error ->
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = error,
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                },
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = "Schedule executed successfully",
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            )
                        },
                        buttonType = ButtonType.PRIMARY,
                        enabled = !state.isLoading,
                    ) {
                        Text(
                            text = "Run Now"
                        )
                    }
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.showCreateScheduleTaskPopup()
                    },
                    buttonType = ButtonType.PRIMARY,
                    enabled = !state.isLoading
                ) {
                    Text("New Task")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {



        }
    }
}