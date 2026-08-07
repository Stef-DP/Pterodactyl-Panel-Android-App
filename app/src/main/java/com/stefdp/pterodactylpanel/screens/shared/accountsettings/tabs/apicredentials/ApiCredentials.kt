package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.apicredentials

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.components.ApiKeyDisplay
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.apicredentials.popups.DeleteApiKeyPopup
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun ApiCredentialsTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: AccountSettingsApiCredentialsTabViewModel = viewModel(),
    refreshIndex: Int
) {
    val localLoggedUser = LocalLoggedUser.current

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(localLoggedUser?.attributes?.id, refreshIndex) {
        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad && state.apiKeys.isNotEmpty() && state.currentApikey != null) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.updateApiKeys(
            context = context,
            onSuccess = {
                coroutineScope.launch {
                    if (state.apiKeys.isNotEmpty()) listState.animateScrollToItem(0)
                }
            },
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

    DeleteApiKeyPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(
                scrollState = scrollState
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Container(
            title = {
                Text(
                    text = "Create API Key",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                label = "Description",
                description = "A description of this API key",
                value = state.newApiKeyDescription,
                onValueChange = {
                    viewModel.setNewApiKeyDescription(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                label = "Allowed IPs",
                description = "Leave blank to allow any IP address to use this API key, otherwise provide each IP address on a new line",
                value = state.newApikeyAllowedIps,
                onValueChange = {
                    viewModel.setNewApiKeyAllowedIps(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                singleLine = false,
                enabled = !state.isLoading
            )

            Button(
                onClick = {
                    viewModel.createApiKey(
                        context = context,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "API key created successfully",
                                )
                            }
                        },
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
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    state.newApiKeyDescription.text.trim().isNotBlank() &&
                    !state.isLoading
            ) {
                Text(
                    text = "Create"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "API Keys",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = 350.dp
                    )
                    .verticalLazyScrollbar(
                        listState = listState
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
                                    height = 130.dp
                                )
                        )
                    }
                }

                if (state.apiKeys.isEmpty()) {
                    item {
                        Text(
                            text = "No API keys exist for this account",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                items(state.apiKeys.size) { index ->
                    val apiKey = state.apiKeys[index]

                    val isCurrentApiKey = state.currentApikey?.startsWith(apiKey.attributes.identifier)

                    ApiKeyDisplay(
                        apiKey = apiKey,
                        onDelete = {
                            viewModel.setApiKeyToDelete(apiKey.attributes.identifier)
                        },
                        enabled = !state.isLoading && isCurrentApiKey == false
                    )
                }
            }
        }
    }
}