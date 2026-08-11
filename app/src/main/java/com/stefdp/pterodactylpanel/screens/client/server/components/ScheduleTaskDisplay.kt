package com.stefdp.pterodactylpanel.screens.client.server.components

import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.ui.theme.Yellow

@Composable
fun ScheduleTaskDisplay(
    task: ServerScheduleTask,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = when (task.attributes.action) {
                    ServerScheduleTask.Attributes.Action.COMMAND -> "Send Command"
                    ServerScheduleTask.Attributes.Action.POWER -> "Send Power Action"
                    ServerScheduleTask.Attributes.Action.BACKUP -> "Create Backup"
                }.uppercase(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    icon = painterResource(R.drawable.edit),
                    iconContentDescription = "Edit Task",
                    onClick = onEdit
                )

                IconButton(
                    icon = painterResource(R.drawable.delete),
                    iconContentDescription = "Delete Task",
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = onDelete
                )
            }
        }

        if (task.attributes.action == ServerScheduleTask.Attributes.Action.BACKUP) {
            Text(
                text = "Ignoring files & folders:",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
        }

        CodeText(
            text = task.attributes.payload
        )

        if (task.attributes.continueOnFailure) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .background(Yellow)
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_circle_down),
                    contentDescription = "Continues on Failure Icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Continues on Failure",
                )
            }
        }

        if (task.attributes.timeOffset > 0L) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                    .background(MaterialTheme.colorScheme.outline)
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.timer),
                    contentDescription = "Delay Icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${task.attributes.timeOffset}s later",
                )
            }
        }
    }
}

val task = ServerScheduleTask(
    attributes = ServerScheduleTask.Attributes(
        id = 1L,
        sequenceId = 1L,
        action = ServerScheduleTask.Attributes.Action.POWER,
        payload = "echo 'Hello World'",
        timeOffset = 5L,
        isQueued = false,
        createdAt = "2024-06-01T00:00:00Z",
        updatedAt = null,
        continueOnFailure = true
    )
)

@Preview
@Composable
fun ScheduleTaskDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.outline
            ) {
                Column {
                    ScheduleTaskDisplay(
                        task = task,
                        onEdit = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}