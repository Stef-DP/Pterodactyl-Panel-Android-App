package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
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
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.CreateNewSchedulePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.CreateScheduleTaskPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.DeleteSchedulePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.DeleteScheduleTaskPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.EditSchedulePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.EditScheduleTaskPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.popups.ScheduleDetailsPopup
import com.stefdp.pterodactylpanel.ui.theme.Green
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
        viewModel = viewModel
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                onShowScheduleDetails = { scheduleId ->
                    viewModel.setScheduleToDisplayDetails(scheduleId)
                }
            )
        }
    }
}