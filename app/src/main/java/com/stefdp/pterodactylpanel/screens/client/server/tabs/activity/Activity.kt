package com.stefdp.pterodactylpanel.screens.client.server.tabs.activity

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.ActivityDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.activity.popups.MetadataPopup
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun ActivityTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerActivityTabViewModel = viewModel(),
    server: GetServerResponse?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    fun reload() {
        viewModel.updateActivity(
            context = context,
            page = state.page,
            onSuccess = {},
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
            }
        )
    }

    LaunchedEffect(server?.attributes?.identifier, refreshIndex, state.page) {
        viewModel.init(server)

        reload()
    }

    MetadataPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val lazyColumnListState = rememberLazyListState()

        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier
                .verticalLazyScrollbar(
                    listState = lazyColumnListState,
                )
                .weight(1f)
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.activity.isEmpty()) {
                items(10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmerable(
                                enabled = true,
                                height = 100.dp
                            )
                    )
                }

                return@LazyColumn
            }

            items(state.activity.size) { index ->
                val activity = state.activity[index]

                ActivityDisplay(
                    activity = activity,
                    onOpenMetadata = {
                        viewModel.setLogToShowMetadata(activity.attributes.id)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Pager(
            currentPage = state.page,
            totalPages = state.pagination?.total ?: 1,
            enabled = state.activity.isNotEmpty(),
            onFirstPageClick = {
                viewModel.setPage(1)
            },
            onPreviousPageClick = {
                viewModel.setPage(state.page - 1)
            },
            onCustomPageInput = { page ->
                viewModel.setPage(page)
            },
            onNextPageClick = {
                viewModel.setPage(state.page + 1)
            },
            onLastPageClick = {
                viewModel.setPage(state.pagination?.total ?: 1)
            }
        )
    }
}