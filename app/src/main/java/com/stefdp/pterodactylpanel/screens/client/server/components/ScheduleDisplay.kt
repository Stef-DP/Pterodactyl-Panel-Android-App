package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.network.client.models.ServerSchedule
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.ui.theme.SecondaryDark

@Composable
fun ScheduleDisplay(
    schedule: ServerSchedule,
    onShowScheduleDetails: (scheduleId: Long) -> Unit,
) {
    val scheduleName = schedule.attributes.name

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_month),
                    contentDescription = "Schedule Icon"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = scheduleName
                )
            }

            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .background(if (schedule.attributes.isActive) Green else MaterialTheme.colorScheme.background)
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                ) {
                    Text(
                        text = if (schedule.attributes.isActive) "Active" else "Inactive",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                IconButton(
                    icon = painterResource(R.drawable.visibility),
                    iconContentDescription = "Open Schedule Details",
                    onClick = {
                        onShowScheduleDetails(schedule.attributes.id)
                    },
                    border = true
                )
            }
        }
    }
}

val schedule = ServerSchedule(
    `object` = "schedule",
    attributes = ServerSchedule.Attributes(
        id = 1L,
        name = "Test Schedule",
        cron = ServerSchedule.Attributes.Cron(
            minute = "0",
            hour = "0",
            dayOfMonth = "*",
            month = "*",
            dayOfWeek = "*"
        ),
        isActive = false,
        isProcessing = false,
        lastRunAt = null,
        nextRunAt = "2024-07-01T00:00:00Z",
        createdAt = "2024-05-01T00:00:00Z",
        updatedAt = "2024-06-01T00:00:00Z",
        relationships = ServerSchedule.Attributes.Relationships(
            tasks = ServerSchedule.Attributes.Relationships.Tasks(
                `object` = "list",
                data = listOf(
                    ServerScheduleTask(
                        `object` = "schedule_task",
                        attributes = ServerScheduleTask.Attributes(
                            id = 1L,
                            sequenceId = 1L,
                            action = "command",
                            payload = "echo 'Hello, World!'",
                            timeOffset = 0L,
                            isQueued = false,
                            createdAt = "2024-05-01T00:00:00Z",
                            updatedAt = "2024-06-01T00:00:00Z"
                        )
                    )
                )
            )
        )
    )
)

@Preview
@Composable
fun ScheduleDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    ScheduleDisplay(
                        schedule = schedule,
                        onShowScheduleDetails = {}
                    )
                }
            }
        }
    }
}