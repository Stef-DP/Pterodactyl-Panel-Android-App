package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.ScheduleDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.CreateNewSchedulePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.CreateScheduleTaskPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.DeleteSchedulePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.DeleteScheduleTaskPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.EditSchedulePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.EditScheduleTaskPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.ScheduleDetailsPopup
import com.stefdp.pterodactylpanel.utils.hasPermission
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun SchedulesTab(
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

    CreateNewSchedulePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    ScheduleDetailsPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
        hasDeletePermission = hasPermission(
            isServerOwner = state.isServerOwner,
            userPermissions = state.userPermissions,
            requiredPermission = ServerSubuser.Permissions.SCHEDULE_DELETE
        ),
        hasUpdatePermission = hasPermission(
            isServerOwner = state.isServerOwner,
            userPermissions = state.userPermissions,
            requiredPermission = ServerSubuser.Permissions.SCHEDULE_UPDATE
        )
    )

    DeleteSchedulePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    EditSchedulePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    CreateScheduleTaskPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    EditScheduleTaskPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    DeleteScheduleTaskPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    if (
        hasPermission(
            isServerOwner = state.isServerOwner,
            userPermissions = state.userPermissions,
            requiredPermission = ServerSubuser.Permissions.SCHEDULE_CREATE
        )
    ) {
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
    }

    if (state.schedules.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "There are no schedules configured for this server",
                textAlign = TextAlign.Center
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
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (state.isLoading) {
            items(10) {
                Box(
                   modifier = Modifier
                       .fillMaxWidth()
                       .shimmerable(
                           enabled = true,
                           height = 60.dp
                       )
                ) {}
            }

            return@LazyColumn
        }

        items(state.schedules.size) { index ->
            val schedule = state.schedules[index]

            ScheduleDisplay(
                schedule = schedule,
                onShowScheduleDetails = {
                    viewModel.setScheduleToDisplayDetails(schedule.attributes.id)
                }
            )
        }
    }
}