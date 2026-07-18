package com.stefdp.pterodactylpanel.screens.client.server.tabs

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Checkbox
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerUiState
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerViewModel
import com.stefdp.pterodactylpanel.screens.client.server.components.File
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun FilesTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerViewModel,
    state: ClientServerUiState
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.filesPath) {
        listState.animateScrollToItem(0)

        viewModel.updateFiles(
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
            }
        )
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // TODO
                },
                enabled = !state.isLoading,
                buttonType = ButtonType.SECONDARY,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Create Directory",
                    color = if (state.isLoading) {
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSecondary
                    },
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    // TODO
                },
                enabled = !state.isLoading,
                buttonType = ButtonType.PRIMARY,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Upload",
                    color = if (state.isLoading) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            }

            Button(
                onClick = {
                    // TODO
                },
                enabled = !state.isLoading,
                buttonType = ButtonType.PRIMARY,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "New File",
                    color = if (state.isLoading) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp
            )
        ) {
            val areAllFilesSelected by remember(
                state.files,
                state.selectedFiles
            ) {
                mutableStateOf(state.files.isNotEmpty() && state.files.all { it.attributes.name in state.selectedFiles })
            }

            Checkbox(
                checked = areAllFilesSelected,
                onToggle = {
                    if (areAllFilesSelected) {
                        viewModel.deselectAllFiles()
                    } else {
                        viewModel.selectAllFiles()
                    }
                },
                enabled = state.files.isNotEmpty()
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "/",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                state.filesPath.forEachIndexed { index, directory ->
                    val isEnabled = index != state.filesPath.lastIndex && index != 0

                    Text(
                        text = directory,
                        color = if (isEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        },
                        modifier = Modifier.clickable(
                            enabled = isEnabled,
                            onClick = {
                                viewModel.navigateToDirectory(index)
                            }
                        )
                    )

                    Text(
                        text = "/",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp
                )
                .weight(1f)
                .verticalLazyScrollbar(listState),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.isLoading) {
                items(15) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmerable(
                                enabled = true,
                                height = 50.dp
                            )
                    ) {}
                }
            } else if (state.files.isEmpty()) {
                item {
                    Text(
                        text = "This directory seems to be empty",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(state.files.size) { index ->
                    val file = state.files[index]

                    File(
                        file = file,
                        isSelected = file.attributes.name in state.selectedFiles,
                        onSelectionToggle = {
                            viewModel.toggleFileSelection(file.attributes.name)
                        },
                        onClick = {
                            if (file.attributes.isFile) {
                                // TOOD
                            } else {
                                viewModel.addDirectoryToPath(file.attributes.name)
                            }
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = state.selectedFiles.isNotEmpty()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // TODO
                    },
                    enabled = !state.isLoading,
                    buttonType = ButtonType.PRIMARY,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Move",
                        color = if (state.isLoading) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        },
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        // TODO
                    },
                    enabled = !state.isLoading,
                    buttonType = ButtonType.PRIMARY,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Archive",
                        color = if (state.isLoading) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                }

                Button(
                    onClick = {
                        // TODO
                    },
                    enabled = !state.isLoading,
                    buttonType = ButtonType.ERROR,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Delete",
                        color = if (state.isLoading) {
                            MaterialTheme.colorScheme.onError.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onError
                        }
                    )
                }
            }
        }
    }
}