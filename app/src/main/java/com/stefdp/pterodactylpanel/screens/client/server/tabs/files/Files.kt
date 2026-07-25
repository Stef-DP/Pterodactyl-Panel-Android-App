package com.stefdp.pterodactylpanel.screens.client.server.tabs.files

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Checkbox
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.UploadFile
import com.stefdp.pterodactylpanel.screens.client.server.components.File
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups.CreateNewDirectoryPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups.DeleteFilesPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups.MoveRenameFilesPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups.UpdateFilePermissionsPopup
import com.stefdp.pterodactylpanel.ui.theme.HighlightLanguage
import com.stefdp.pterodactylpanel.utils.PermissionModeRegex
import com.stefdp.pterodactylpanel.utils.getFileInfo
import com.stefdp.pterodactylpanel.utils.linuxPermissionToInt
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import java.nio.file.Paths

@Composable
fun FilesTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerFilesTabViewModel = viewModel(),
    server: GetServerResponse?,
    directory: String?
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(server) {
        viewModel.init(
            context = context,
            server = server,
            directory = directory,
        )
    }

    if (state.fileToEdit != null || state.createNewFile) {
        FileEditTab(
            navController = navController,
            context = context,
            activity = activity,
            viewModel = viewModel,
            state = state
        )

        return
    }

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

    CreateNewDirectoryPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    MoveRenameFilesPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    DeleteFilesPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    UpdateFilePermissionsPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column {
        if (state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_CREATE)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.showCreateNewDirectoryPopup()
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

                val filePicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenMultipleDocuments()
                ) { uris ->
                    if (uris.isNotEmpty()) {
                        val files = uris.mapNotNull { uri ->
                            getFileInfo(context, uri)?.let { (name, _, mimeType) ->
                                UploadFile(
                                    uri = uri,
                                    name = name,
                                    mimeType = mimeType
                                )
                            }
                        }

                        viewModel.uploadFiles(
                            context = context,
                            files = files,
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
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = "Files uploaded successfully",
                                    )
                                }
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        filePicker.launch(arrayOf("*/*"))
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
                        viewModel.setCreateNewFile(true)
                        viewModel.setSelectedLanguage(HighlightLanguage.PLAIN_TEXT)
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

                    val directoryPicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.OpenDocumentTree()
                    ) { uri: Uri? ->
                        uri?.let {
                            context.contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )

                            viewModel.setSelectedUri(context, it)

                            viewModel.performDownload(
                                context = context,
                                file = file,
                                uri = uri,
                                sendNotification = { content ->
                                    Notification.show(
                                        activity = activity,
                                        content = content
                                    )
                                }
                            )
                        }
                    }

                    File(
                        file = file,
                        isSelected = file.attributes.name in state.selectedFiles,
                        hasDeletePermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_DELETE),
                        hasUpdatePermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_UPDATE),
                        hasCreatePermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_CREATE),
                        hasArchivePermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_ARCHIVE),
                        hasReadContentPermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_READ_CONTENT),
                        onSelectionToggle = {
                            viewModel.toggleFileSelection(file.attributes.name)
                        },
                        onClick = {
                            if (file.attributes.isFile) {
                                viewModel.setFileToEdit(
                                    context = context,
                                    file = file,
                                    onError = {
                                        Notification.show(
                                            activity = activity,
                                            duration = 3000L
                                        ) {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                )
                            } else {
                                viewModel.addDirectoryToPath(file.attributes.name)
                            }
                        },
                        onRename = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)
                            viewModel.setNewDirectoryName(TextFieldValue(file.attributes.name))

                            viewModel.showMoveFilesPopup(true)
                        },
                        onMove = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)

                            viewModel.showMoveFilesPopup()
                        },
                        onChangePermissions = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)

                            viewModel.setNewPermissions(TextFieldValue(linuxPermissionToInt(file.attributes.mode)))

                            viewModel.showUpdatePermissionsPopup()
                        },
                        onCopy = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)

                            viewModel.copyFile(
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
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = "File copied successfully",
                                        )
                                    }
                                }
                            )
                        },
                        onArchive = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)

                            viewModel.archiveFiles(
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
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = "Files archived successfully",
                                        )
                                    }
                                }
                            )
                        },
                        onUnarchive = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)

                            viewModel.unarchiveFile(
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
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = "File unarchived successfully",
                                        )
                                    }
                                }
                            )
                        },
                        onDownload = {
                            if (state.selectedUri == null) {
                                directoryPicker.launch(null)

                                return@File
                            }

                            viewModel.performDownload(
                                context = context,
                                file = file,
                                uri = state.selectedUri!!,
                                sendNotification = { content ->
                                    Notification.show(
                                        activity = activity,
                                        content = content
                                    )
                                }
                            )
                        },
                        onDelete = {
                            viewModel.deselectAllFiles()
                            viewModel.toggleFileSelection(file.attributes.name)

                            viewModel.showDeleteFilesPopup()
                        }
                    )
                }
            }
        }

        val bottomUiPermissions = listOf(
            ServerSubuser.Permissions.FILE_DELETE,
            ServerSubuser.Permissions.FILE_UPDATE,
            ServerSubuser.Permissions.FILE_ARCHIVE
        )

        if (state.isServerOwner || state.userPermissions.any { it in bottomUiPermissions }) {
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
                    if (state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_UPDATE)) {
                        Button(
                            onClick = {
                                viewModel.showMoveFilesPopup()
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
                    }

                    if (state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_ARCHIVE)) {
                        Button(
                            onClick = {
                                viewModel.archiveFiles(
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
                                    onSuccess = {
                                        Notification.show(
                                            activity = activity,
                                            duration = 3000L
                                        ) {
                                            Text(
                                                text = "Files archived successfully",
                                            )
                                        }
                                    }
                                )
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
                    }

                    if (state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.FILE_DELETE)) {
                        Button(
                            onClick = {
                                viewModel.showDeleteFilesPopup()
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
    }
}