package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Checkbox
import com.stefdp.pterodactylpanel.components.MoreActionsButton
import com.stefdp.pterodactylpanel.components.MoreActionsMenuItem
import com.stefdp.pterodactylpanel.network.client.models.ServerFile
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import nl.jacobras.humanreadable.HumanReadable

private val zipMimetypes = listOf(
    "application/zip",
    "application/x-zip-compressed",
    "application/x-zip",
    "application/octet-stream",
    "application/x-tar",
    "application/x-gzip",
    "application/gzip",
    "application/x-gtar",
)

@Composable
fun FileDisplay(
    file: ServerFile,
    isSelected: Boolean,
    hasReadContentPermission: Boolean,
    hasDeletePermission: Boolean,
    hasArchivePermission: Boolean,
    hasUpdatePermission: Boolean,
    hasCreatePermission: Boolean,
    onSelectionToggle: () -> Unit,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onMove: () -> Unit,
    onChangePermissions: () -> Unit,
    onCopy: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .clickable(
                enabled = hasReadContentPermission,
                onClick = onClick
            )
            .padding(8.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onToggle = onSelectionToggle,
        )

        val icon = if (file.attributes.isFile) {
            if (file.attributes.mimetype in zipMimetypes) {
                painterResource(R.drawable.folder_zip_fill)
            } else {
                painterResource(R.drawable.description_fill)
            }
        } else {
            painterResource(R.drawable.folder_fill)
        }

        val iconDescription = if (file.attributes.isFile) {
            if (file.attributes.mimetype in zipMimetypes) {
                "Compressed File"
            } else {
                "File"
            }
        } else {
            "Folder"
        }

        Icon(
            painter = icon,
            contentDescription = iconDescription
        )

        Text(
            text = file.attributes.name,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (file.attributes.isFile) {
                Text(
                    text = HumanReadable.fileSize(file.attributes.size),
                )
            }

            MoreActionsButton(
                items = listOfNotNull(
                    if (hasUpdatePermission) {
                        MoreActionsMenuItem(
                            label = "Rename",
                            icon = painterResource(R.drawable.edit),
                            iconDescription = "Rename",
                            onClick = onRename
                        )
                    } else null,
                    if (hasUpdatePermission) {
                        MoreActionsMenuItem(
                            label = "Move",
                            icon = painterResource(R.drawable.arrow_top_left),
                            iconDescription = "Move",
                            onClick = onMove
                        )
                    } else null,
                    if (hasUpdatePermission) {
                        MoreActionsMenuItem(
                            label = "Permissions",
                            icon = painterResource(R.drawable.key),
                            iconDescription = "Change Permissions",
                            onClick = onChangePermissions
                        )
                    } else null,
                    if (file.attributes.isFile && hasCreatePermission) {
                        MoreActionsMenuItem(
                            label = "Copy",
                            icon = painterResource(R.drawable.content_copy),
                            iconDescription = "Copy",
                            onClick = onCopy
                        )
                    } else null,
                    if (hasArchivePermission) {
                        if (file.attributes.isFile && file.attributes.mimetype in zipMimetypes) {
                            MoreActionsMenuItem(
                                label = "Unarchive",
                                icon = painterResource(R.drawable.unarchive),
                                iconDescription = "Unarchive",
                                onClick = onUnarchive
                            )
                        } else {
                            MoreActionsMenuItem(
                                label = "Archive",
                                icon = painterResource(R.drawable.folder_zip_fill),
                                iconDescription = "Archive",
                                onClick = onArchive
                            )
                        }
                    } else null,
                    if (file.attributes.isFile && hasReadContentPermission) {
                        MoreActionsMenuItem(
                            label = "Download",
                            icon = painterResource(R.drawable.download),
                            iconDescription = "Download",
                            onClick = onDownload
                        )
                    } else null,
                    if (hasDeletePermission) {
                        MoreActionsMenuItem(
                            label = "Delete",
                            icon = painterResource(R.drawable.delete),
                            iconDescription = "Delete",
                            onClick = onDelete,
                            labelColor = MaterialTheme.colorScheme.error,
                            iconColor = MaterialTheme.colorScheme.error
                        )
                    } else null
                )
            )
        }
    }
}

@Preview
@Composable
fun FileDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    FileDisplay(
                        file = testFile,
                        isSelected = true,
                        onSelectionToggle = {},
                        onClick = {},
                        onRename = {},
                        onMove = {},
                        onChangePermissions = {},
                        onCopy = {},
                        onArchive = {},
                        onUnarchive = {},
                        onDownload = {},
                        onDelete = {},
                        hasDeletePermission = true,
                        hasReadContentPermission = true,
                        hasArchivePermission = true,
                        hasUpdatePermission = true,
                        hasCreatePermission = true
                    )

                    FileDisplay(
                        file = testFile,
                        isSelected = false,
                        onSelectionToggle = {},
                        onClick = {},
                        onRename = {},
                        onMove = {},
                        onChangePermissions = {},
                        onCopy = {},
                        onArchive = {},
                        onUnarchive = {},
                        onDownload = {},
                        onDelete = {},
                        hasDeletePermission = true,
                        hasReadContentPermission = true,
                        hasArchivePermission = true,
                        hasUpdatePermission = true,
                        hasCreatePermission = true
                    )

                    FileDisplay(
                        file = testFolder,
                        isSelected = true,
                        onSelectionToggle = {},
                        onClick = {},
                        onRename = {},
                        onMove = {},
                        onChangePermissions = {},
                        onCopy = {},
                        onArchive = {},
                        onUnarchive = {},
                        onDownload = {},
                        onDelete = {},
                        hasDeletePermission = true,
                        hasReadContentPermission = true,
                        hasArchivePermission = true,
                        hasUpdatePermission = true,
                        hasCreatePermission = true
                    )

                    FileDisplay(
                        file = testZipFile,
                        isSelected = false,
                        onSelectionToggle = {},
                        onClick = {},
                        onRename = {},
                        onMove = {},
                        onChangePermissions = {},
                        onCopy = {},
                        onArchive = {},
                        onUnarchive = {},
                        onDownload = {},
                        onDelete = {},
                        hasDeletePermission = true,
                        hasReadContentPermission = true,
                        hasArchivePermission = true,
                        hasUpdatePermission = true,
                        hasCreatePermission = true
                    )

                    FileDisplay(
                        file = testFile,
                        isSelected = false,
                        onSelectionToggle = {},
                        onClick = {},
                        onRename = {},
                        onMove = {},
                        onChangePermissions = {},
                        onCopy = {},
                        onArchive = {},
                        onUnarchive = {},
                        onDownload = {},
                        onDelete = {},
                        hasDeletePermission = true,
                        hasReadContentPermission = true,
                        hasArchivePermission = true,
                        hasUpdatePermission = true,
                        hasCreatePermission = true
                    )
                }
            }
        }
    }
}

val testFile = ServerFile(
    attributes = ServerFile.Attributes(
        name = "test-file.txt",
        mode = "rw-r--r--",
        size = 1024L,
        isFile = true,
        isSymlink = false,
        isEditable = true,
        mimetype = "text/plain",
        createdAt = "2024-06-01T12:00:00Z",
        modifiedAt = "2024-06-01T12:00:00Z",
    )
)

val testZipFile = ServerFile(
    attributes = ServerFile.Attributes(
        name = "test-file.zip",
        mode = "rw-r--r--",
        size = 1024L,
        isFile = true,
        isSymlink = false,
        isEditable = true,
        mimetype = "application/zip",
        createdAt = "2024-06-01T12:00:00Z",
        modifiedAt = "2024-06-01T12:00:00Z",
    )
)

val testFolder = ServerFile(
    attributes = ServerFile.Attributes(
        name = "test-dir",
        mode = "rw-r--r--",
        size = 0L,
        isFile = false,
        isSymlink = false,
        isEditable = true,
        mimetype = "directory",
        createdAt = "2024-06-01T12:00:00Z",
        modifiedAt = "2024-06-01T12:00:00Z",
    )
)