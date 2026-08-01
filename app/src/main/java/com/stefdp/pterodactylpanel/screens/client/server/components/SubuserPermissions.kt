package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Checkbox
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.LabeledCheckbox
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.toAnnotatedString

@Composable
fun SubuserPermissions(
    permissions: Map<ServerSubuser.Permissions, Boolean>,
    updatePermissions: (Map<ServerSubuser.Permissions, Boolean>) -> Unit,
    allowedPermissions: List<ServerSubuser.Permissions>,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubuserPermissionContainer(
            title = "Control",
            description = "Permissions that control a user's ability to control the power state of a server, or send commands",
            permissionGroup = listOf(
                ServerSubuser.Permissions.CONTROL_CONSOLE,
                ServerSubuser.Permissions.CONTROL_START,
                ServerSubuser.Permissions.CONTROL_STOP,
                ServerSubuser.Permissions.CONTROL_RESTART,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "User",
            description = "Permissions that allow a user to manage other subusers on a server. They will never be able to edit their own account, or assign permissions they do not have themselves",
            permissionGroup = listOf(
                ServerSubuser.Permissions.USER_CREATE,
                ServerSubuser.Permissions.USER_READ,
                ServerSubuser.Permissions.USER_UPDATE,
                ServerSubuser.Permissions.USER_DELETE,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "File",
            description = "Permissions that control a user's ability to modify the filesystem for this server",
            permissionGroup = listOf(
                ServerSubuser.Permissions.FILE_CREATE,
                ServerSubuser.Permissions.FILE_READ,
                ServerSubuser.Permissions.FILE_READ_CONTENT,
                ServerSubuser.Permissions.FILE_UPDATE,
                ServerSubuser.Permissions.FILE_DELETE,
                ServerSubuser.Permissions.FILE_ARCHIVE,
                ServerSubuser.Permissions.FILE_SFTP,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Backup",
            description = "Permissions that control a user's ability to generate and manage server backups",
            permissionGroup = listOf(
                ServerSubuser.Permissions.BACKUP_CREATE,
                ServerSubuser.Permissions.BACKUP_READ,
                ServerSubuser.Permissions.BACKUP_DELETE,
                ServerSubuser.Permissions.BACKUP_DOWNLOAD,
                ServerSubuser.Permissions.BACKUP_RESTORE,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Allocation",
            description = "Permissions that control a user's ability to modify the port allocations for this server",
            permissionGroup = listOf(
                ServerSubuser.Permissions.ALLOCATION_READ,
                ServerSubuser.Permissions.ALLOCATION_CREATE,
                ServerSubuser.Permissions.ALLOCATION_UPDATE,
                ServerSubuser.Permissions.ALLOCATION_DELETE,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Startup",
            description = "Permissions that control a user's ability to view this server's startup parameters",
            permissionGroup = listOf(
                ServerSubuser.Permissions.STARTUP_READ,
                ServerSubuser.Permissions.STARTUP_UPDATE,
                ServerSubuser.Permissions.STARTUP_DOCKER_IMAGE
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Database",
            description = "Permissions that control a user's access to the database management for this server",
            permissionGroup = listOf(
                ServerSubuser.Permissions.DATABASE_CREATE,
                ServerSubuser.Permissions.DATABASE_READ,
                ServerSubuser.Permissions.DATABASE_UPDATE,
                ServerSubuser.Permissions.DATABASE_DELETE,
                ServerSubuser.Permissions.DATABASE_VIEW_PASSWORD
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Schedule",
            description = "Permissions that control a user's access to the schedule management for this server",
            permissionGroup = listOf(
                ServerSubuser.Permissions.SCHEDULE_CREATE,
                ServerSubuser.Permissions.SCHEDULE_READ,
                ServerSubuser.Permissions.SCHEDULE_UPDATE,
                ServerSubuser.Permissions.SCHEDULE_DELETE,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Settings",
            description = "Permissions that control a user's access to the settings for this server",
            permissionGroup = listOf(
                ServerSubuser.Permissions.SETTINGS_RENAME,
                ServerSubuser.Permissions.SETTINGS_REINSTALL,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )

        SubuserPermissionContainer(
            title = "Activity",
            description = "Permissions that control a user's access to the server activity logs",
            permissionGroup = listOf(
                ServerSubuser.Permissions.ACTIVITY_READ,
            ),
            permissions = permissions,
            updatePermissions = updatePermissions,
            allowedPermissions = allowedPermissions,
            enabled = enabled
        )
    }
}

@Composable
fun SubuserPermissionContainer(
    title: String,
    description: String,
    permissionGroup: List<ServerSubuser.Permissions>,
    permissions: Map<ServerSubuser.Permissions, Boolean>,
    updatePermissions: (Map<ServerSubuser.Permissions, Boolean>) -> Unit,
    allowedPermissions: List<ServerSubuser.Permissions>,
    enabled: Boolean = true
) {
    Container(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Checkbox(
                checked = permissionGroup.all { permissions[it] == true },
                onToggle = {
                    val newPermissions = permissions.toMutableMap()

                    val currentState = permissionGroup.all { permissions[it] == true }

                    permissionGroup.forEach { permission ->
                        if (permission in allowedPermissions) {
                            newPermissions[permission] = !currentState
                        }
                    }

                    updatePermissions(newPermissions)
                },
                enabled = enabled && permissionGroup.any { it in allowedPermissions }
            )
        }
    ) {
        Text(
            text = description.toAnnotatedString()
        )

        permissionGroup.forEach { permission ->
            LabeledCheckbox(
                label = permission.uiName,
                description = permission.uiDescription,
                checked = permissions[permission] ?: false,
                onToggle = {
                    val newPermissions = permissions.toMutableMap()

                    newPermissions[permission] = !newPermissions[permission]!!

                    updatePermissions(newPermissions)
                },
                enabled = enabled && permission in allowedPermissions
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.outline
            ) {
                SubuserPermissionContainer(
                    title = "Control",
                    description = "Permissions that control a user's ability to control the power state of a server, or send commands",
                    permissionGroup = listOf(
                        ServerSubuser.Permissions.CONTROL_CONSOLE,
                        ServerSubuser.Permissions.CONTROL_START,
                        ServerSubuser.Permissions.CONTROL_STOP,
                        ServerSubuser.Permissions.CONTROL_RESTART,
                    ),
                    permissions = mapOf(
                        ServerSubuser.Permissions.CONTROL_CONSOLE to true,
                        ServerSubuser.Permissions.CONTROL_START to false,
                        ServerSubuser.Permissions.CONTROL_STOP to true,
                        ServerSubuser.Permissions.CONTROL_RESTART to false,
                    ),
                    allowedPermissions = listOf(
                        ServerSubuser.Permissions.CONTROL_CONSOLE,
                        ServerSubuser.Permissions.CONTROL_START,
                        ServerSubuser.Permissions.CONTROL_STOP,
                        ServerSubuser.Permissions.CONTROL_RESTART,
                    ),
                    updatePermissions = {
                    }
                )
            }
        }
    }
}