package com.stefdp.pterodactylpanel.screens.client.server.components

import android.R.attr.contentDescription
import android.R.attr.fontWeight
import android.R.attr.label
import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.MoreActionsButton
import com.stefdp.pterodactylpanel.components.MoreActionsMenuItem
import com.stefdp.pterodactylpanel.network.client.models.ServerBackup
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.ui.theme.Yellow
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun BackupDisplay(
    backup: ServerBackup,
    onDownload: () -> Unit,
    onRestore: () -> Unit,
    onLock: () -> Unit,
    onUnlock: () -> Unit,
    onDelete: () -> Unit,
    hasDeletePermission: Boolean,
    hasRestorePermission: Boolean,
    hasDownloadPermission: Boolean,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = if (backup.attributes.isLocked) {
                        painterResource(R.drawable.lock)
                    } else {
                        painterResource(R.drawable.inventory_2_fill)
                    },
                    tint = if (backup.attributes.isLocked) {
                        Yellow
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    contentDescription = "Backup Icon",
                )

                Column {
                    Text(
                        text = backup.attributes.name,
                        fontWeight = FontWeight.Bold
                    )

                    if (backup.attributes.checksum != null) {
                        Text(
                            text = backup.attributes.checksum,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            ),
                        )
                    }
                }
            }

            MoreActionsButton(
                items = listOfNotNull(
                    if (hasDownloadPermission) {
                        MoreActionsMenuItem(
                            label = "Download",
                            icon = painterResource(R.drawable.download),
                            iconDescription = "Download Backup",
                            onClick = onDownload
                        )
                    } else null,
                    if (hasRestorePermission) {
                        MoreActionsMenuItem(
                            label = "Restore",
                            icon = painterResource(R.drawable.history),
                            iconDescription = "Restore Backup",
                            onClick = onRestore
                        )
                    } else null,
                    if (hasDeletePermission) {
                        if (backup.attributes.isLocked) {
                            MoreActionsMenuItem(
                                label = "Unlock",
                                icon = painterResource(R.drawable.lock_open),
                                iconDescription = "Unlock Backup",
                                onClick = onUnlock
                            )
                        } else {
                            MoreActionsMenuItem(
                                label = "Lock",
                                icon = painterResource(R.drawable.lock),
                                iconDescription = "Lock Backup",
                                onClick = onLock
                            )
                        }
                    } else null,
                    if (hasDeletePermission && !backup.attributes.isLocked) {
                        MoreActionsMenuItem(
                            label = "Delete",
                            icon = painterResource(R.drawable.delete),
                            iconColor = MaterialTheme.colorScheme.error,
                            labelColor = MaterialTheme.colorScheme.error,
                            iconDescription = "Delete Backup",
                            onClick = onDelete
                        )
                    } else null
                ),
                enabled = enabled
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append("Size:")
                }

                val size = HumanReadable.fileSize(
                    backup.attributes.bytes,
                    decimals = 2
                )

                append(" $size")
            }
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append("Created:")
                }

                val date = HumanReadable.timeAgo(
                    Instant.parse(backup.attributes.createdAt),
                )

                append(" about $date")
            }
        )
    }
}

val backupPreview = ServerBackup(
    attributes = ServerBackup.Attributes(
        uuid = "backup-uuid",
        name = "Backup Name",
        ignoredFiles = listOf("file1.txt", "file2.txt"),
        checksum = "sha1:a7ffd47a88b30f5aa35ec040cdcad28f77001942",
        bytes = 1024L,
        createdAt = "2024-06-01T12:00:00Z",
        completedAt = "2024-06-01T12:30:00Z",
        isSuccessful = true,
        isLocked = false,
    )
)

@Preview
@Composable
fun BackupDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    BackupDisplay(
                        backup = backupPreview,
                        hasDeletePermission = true,
                        hasRestorePermission = true,
                        hasDownloadPermission = true,
                        onDownload = {},
                        onRestore = {},
                        onLock = {},
                        onUnlock = {},
                        onDelete = {},
                        enabled = true
                    )
                }
            }
        }
    }
}