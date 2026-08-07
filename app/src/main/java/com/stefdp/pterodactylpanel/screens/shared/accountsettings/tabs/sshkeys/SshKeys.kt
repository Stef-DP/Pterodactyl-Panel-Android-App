package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.sshkeys

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
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.components.SshKeyDisplay
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.sshkeys.popups.DeleteSshKeyPopup
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun SshKeysTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: AccountSettingsSshKeysTabViewModel = viewModel(),
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

        if (!isExplicitRefresh && !isFirstLoad && state.sshKeys.isNotEmpty()) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.updateSshKeys(
            context = context,
            onSuccess = {
                coroutineScope.launch {
                    if (state.sshKeys.isNotEmpty()) listState.animateScrollToItem(0)
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

    DeleteSshKeyPopup(
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
                    text = "Add SSH Key",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                label = "SSH Key Name",
                value = state.newSshKeyName,
                onValueChange = {
                    viewModel.setNewSshKeyName(it)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                label = "Public Key",
                value = state.newSshKeyPublicKey,
                onValueChange = {
                    viewModel.setNewSshKeyPublicKey(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                singleLine = false,
                enabled = !state.isLoading
            )

            Button(
                onClick = {
                    viewModel.createSshKey(
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
                    state.newSshKeyName.text.trim().isNotBlank() &&
                    state.newSshKeyPublicKey.text.trim().isNotBlank() &&
                    !state.isLoading
            ) {
                Text(
                    text = "Save"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "SSH Keys",
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
                                    height = 140.dp
                                )
                        )
                    }
                }

                if (state.sshKeys.isEmpty()) {
                    item {
                        Text(
                            text = "No SSH Keys exist for this account",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                items(state.sshKeys.size) { index ->
                    val sshKey = state.sshKeys[index]

                    SshKeyDisplay(
                        sshKey = sshKey,
                        onDelete = {
                            viewModel.setSshKeyToDelete(sshKey.attributes.fingerprint)
                        },
                        enabled = !state.isLoading
                    )
                }
            }
        }
    }
}