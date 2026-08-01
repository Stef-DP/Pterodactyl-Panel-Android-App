package com.stefdp.pterodactylpanel.screens.client.server.tabs.backups

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.BackupDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups.CreateBackupPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups.DeleteBackupPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups.RestoreBackupPopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.backups.popups.UnlockBackupPopup
import com.stefdp.pterodactylpanel.utils.hasPermission
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun BackupsTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerBackupsTabViewModel = viewModel(),
    server: GetServerResponse?
) {
    LaunchedEffect(server) {
        viewModel.init(
            context = context,
            server = server,
        )

        viewModel.updateBackups(
            context = context,
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

    val state by viewModel.state.collectAsState()

    if (server?.attributes?.featureLimits?.backups == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Backups cannot be created for this server because the backup limit is set to 0",
                textAlign = TextAlign.Center
            )
        }

        return
    }

    RestoreBackupPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    UnlockBackupPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    DeleteBackupPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    CreateBackupPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val allocatedBackups = state.backups.size
            val backupLimit = server?.attributes?.featureLimits?.backups ?: 0

            Text(
                text = "$allocatedBackups of $backupLimit backups have been created for this server",
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            if (
                hasPermission(
                    isServerOwner = state.isServerOwner,
                    userPermissions = state.userPermissions,
                    requiredPermission = ServerSubuser.Permissions.BACKUP_CREATE
                )
            ) {
                Button(
                    onClick = {
                        viewModel.showCreateBackupPopup()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = allocatedBackups < backupLimit && !state.isLoading
                ) {
                    Text(
                        text = "Create Backup"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (state.backups.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "It looks like there are no backups currently stored for this server",
                    textAlign = TextAlign.Center
                )
            }

            return@Column
        }

        val lazyColumnListState = rememberLazyListState()

        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier
                .fillMaxSize()
                .verticalLazyScrollbar(
                    listState = lazyColumnListState
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.isLoading) {
                items(10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmerable(
                                enabled = true,
                                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                                height = 60.dp
                            )
                    )
                }

                return@LazyColumn
            }

            items(state.backups.size) { index ->
                val backup = state.backups[index]

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
                            backup = backup,
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

                BackupDisplay(
                    backup = backup,
                    onDownload = {
                        if (state.selectedUri == null) {
                            directoryPicker.launch(null)

                            return@BackupDisplay
                        }

                        viewModel.performDownload(
                            context = context,
                            backup = backup,
                            uri = state.selectedUri!!,
                            sendNotification = { content ->
                                Notification.show(
                                    activity = activity,
                                    content = content
                                )
                            }
                        )
                    },
                    onRestore = {
                        viewModel.setBackupToRestore(backup.attributes.uuid)
                    },
                    onLock = {
                        viewModel.toggleBackupLock(
                            context = context,
                            backup = backup,
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
                            onSuccess = { action ->
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = "Backup $action successfully",
                                    )
                                }
                            }
                        )
                    },
                    onUnlock = {
                        viewModel.setBackupToUnlock(backup.attributes.uuid)
                    },
                    onDelete = {
                        viewModel.setBackupToDelete(backup.attributes.uuid)
                    },
                    hasDeletePermission = hasPermission(
                        isServerOwner = state.isServerOwner,
                        userPermissions = state.userPermissions,
                        requiredPermission = ServerSubuser.Permissions.BACKUP_DELETE
                    ),
                    hasRestorePermission = hasPermission(
                        isServerOwner = state.isServerOwner,
                        userPermissions = state.userPermissions,
                        requiredPermission = ServerSubuser.Permissions.BACKUP_RESTORE
                    ),
                    hasDownloadPermission = hasPermission(
                        isServerOwner = state.isServerOwner,
                        userPermissions = state.userPermissions,
                        requiredPermission = ServerSubuser.Permissions.BACKUP_DOWNLOAD
                    ),
                )
            }
        }
    }
}