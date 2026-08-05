package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.activity

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.ActivityDisplay
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.activity.popups.MetadataPopup
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun ActivityTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientAccountSettingsActivityTabViewModel = viewModel(),
    refreshIndex: Int
) {
    val localLoggedUser = LocalLoggedUser.current

    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    fun reload() {
        coroutineScope.launch {
            scrollState.animateScrollTo(0)

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
    }

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    var lastPage by rememberSaveable {
        mutableLongStateOf(1L)
    }

    LaunchedEffect(localLoggedUser?.attributes?.id, refreshIndex) {
        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad && state.activity.isNotEmpty()) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        reload()
    }

    LaunchedEffect(state.page) {
        if (state.page != lastPage) {
            lastPage = state.page

            reload()
        }
    }

    MetadataPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .verticalScrollWithScrollbar(
                    scrollState = scrollState
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
                repeat(10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmerable(
                                enabled = true,
                                height = 100.dp
                            )
                    )
                }

                return@Column
            }

            for (activity in state.activity) {
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
            totalPages = state.pagination?.totalPages ?: 1,
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
                viewModel.setPage(state.pagination?.totalPages ?: 1)
            }
        )
    }
}