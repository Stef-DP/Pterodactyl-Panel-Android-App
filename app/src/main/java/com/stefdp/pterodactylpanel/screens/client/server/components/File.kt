package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.stefdp.pterodactylpanel.network.client.models.ServerFile
import com.stefdp.pterodactylpanel.network.client.models.ServerFileAttributes
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
fun File(
    file: ServerFile,
    isSelected: Boolean,
    onSelectionToggle: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .clickable(
                enabled = true,
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
            modifier = Modifier.weight(1f)
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
                items = emptyList()
            )
        }
    }
}

@Preview
@Composable
fun FilePreview() {
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
                    File(
                        file = testFile,
                        isSelected = true,
                        onSelectionToggle = {},
                        onClick = {}
                    )

                    File(
                        file = testFile,
                        isSelected = false,
                        onSelectionToggle = {},
                        onClick = {}
                    )

                    File(
                        file = testFolder,
                        isSelected = true,
                        onSelectionToggle = {},
                        onClick = {}
                    )

                    File(
                        file = testZipFile,
                        isSelected = false,
                        onSelectionToggle = {},
                        onClick = {}
                    )

                    File(
                        file = testFile,
                        isSelected = false,
                        onSelectionToggle = {},
                        onClick = {}
                    )
                }
            }
        }
    }
}

val testFile = ServerFile(
    attributes = ServerFileAttributes(
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
    attributes = ServerFileAttributes(
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
    attributes = ServerFileAttributes(
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