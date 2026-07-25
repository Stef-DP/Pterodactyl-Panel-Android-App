package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.screens.client.server.ServerTab

data class ServerSubuser(
    val `object`: String = "server_subuser",
    val attributes: Attributes
) {
    data class Attributes(
        val uuid: String,
        val username: String,
        val email: String,
        val image: String,
        @SerializedName("2fa_enabled") val twoFactorAuthenticationEnabled: Boolean,
        @SerializedName("created_at") val createdAt: String,
        val permissions: List<Permissions>,
    )

    enum class Permissions(
        val value: String,
        val uiName: String,
        val uiDescription: String
    ) {
        @SerializedName("control.console")
        CONTROL_CONSOLE(
            value = "control.console",
            uiName = "Console",
            uiDescription = "Allows a user to send commands to the server instance via the console"
        ),

        @SerializedName("control.start")
        CONTROL_START(
            value = "control.start",
            uiName = "Start",
            uiDescription = "Allows a user to start the server if it is stopped"
        ),

        @SerializedName("control.stop")
        CONTROL_STOP(
            value = "control.stop",
            uiName = "Stop",
            uiDescription = "Allows a user to stop a server if it is running"
        ),

        @SerializedName("control.restart")
        CONTROL_RESTART(
            value = "control.restart",
            uiName = "Restart",
            uiDescription = "Allows a user to perform a server restart. This allows them to start the server if it is offline, but not put the server in a completely stopped state"
        ),

        @SerializedName("user.create")
        USER_CREATE(
            value = "user.create",
            uiName = "Create",
            uiDescription = "Allows a user to create new subusers for the server"
        ),

        @SerializedName("user.read")
        USER_READ(
            value = "user.read",
            uiName = "Read",
            uiDescription = "Allows the user to view subusers and their permissions for the server"
        ),

        @SerializedName("user.update")
        USER_UPDATE(
            value = "user.update",
            uiName = "Update",
            uiDescription = "Allows a user to modify other subusers"
        ),

        @SerializedName("user.delete")
        USER_DELETE(
            value = "user.delete",
            uiName = "Delete",
            uiDescription = "Allows a user to delete a subuser from the server"
        ),

        @SerializedName("file.create")
        FILE_CREATE(
            value = "file.create",
            uiName = "Create",
            uiDescription = "Allows a user to create additional files and folders via the Panel or direct upload"
        ),

        @SerializedName("file.read")
        FILE_READ(
            value = "file.read",
            uiName = "Read",
            uiDescription = "Allows a user to view the contents of a directory, but not view the contents of or download files"
        ),
//// TODO
        @SerializedName("file.read-content")
        FILE_READ_CONTENT(
            value = "file.read-content",
            uiName = "Read Content",
            uiDescription = "Allows a user to view the contents of a given file. This will also allow the user to download files"
        ),

        @SerializedName("file.update")
        FILE_UPDATE(
            value = "file.update",
            uiName = "Update",
            uiDescription = "Allows a user to update the contents of an existing file or directory"
        ),

        @SerializedName("file.delete")
        FILE_DELETE(
            value = "file.delete",
            uiName = "Delete",
            uiDescription = "Allows a user to delete files or directories"
        ),

        @SerializedName("file.archive")
        FILE_ARCHIVE(
            value = "file.archive",
            uiName = "Archive",
            uiDescription = "Allows a user to archive the contents of a directory as well as decompress existing archives on the system"
        ),

        @SerializedName("file.sftp")
        FILE_SFTP(
            value = "file.sftp",
            uiName = "SFTP",
            uiDescription = "Allows a user to connect to SFTP and manage server files using the other assigned file permissions"
        ),

        @SerializedName("backup.create")
        BACKUP_CREATE(
            value = "backup.create",
            uiName = "Create",
            uiDescription = "Allows a user to create new backups for this server"
        ),

        @SerializedName("backup.read")
        BACKUP_READ(
            value = "backup.read",
            uiName = "Read",
            uiDescription = "Allows a user to view all backups that exist for this server"
        ),

        @SerializedName("backup.delete")
        BACKUP_DELETE(
            value = "backup.delete",
            uiName = "Delete",
            uiDescription = "Allows a user to remove backups from the system"
        ),

        @SerializedName("backup.download")
        BACKUP_DOWNLOAD(
            value = "backup.download",
            uiName = "Download",
            uiDescription = "Allows a user to download a backup for the server. Danger: this allows a user to access all files for the server in the backup"
        ),

        @SerializedName("backup.restore")
        BACKUP_RESTORE(
            value = "backup.restore",
            uiName = "Restore",
            uiDescription = "Allows a user to restore a backup for the server. Danger: this allows the user to delete all of the server files in the process"
        ),

        @SerializedName("allocation.read")
        ALLOCATION_READ(
            value = "allocation.read",
            uiName = "Read",
            uiDescription = "Allows a user to view all allocations currently assigned to this server. Users with any level of access to this server can always view the primary allocation"
        ),

        @SerializedName("allocation.create")
        ALLOCATION_CREATE(
            value = "allocation.create",
            uiName = "Create",
            uiDescription = "Allows a user to assign additional allocations to the server"
        ),

        @SerializedName("allocation.update")
        ALLOCATION_UPDATE(
            value = "allocation.update",
            uiName = "Update",
            uiDescription = "Allows a user to change the primary server allocation and attach notes to each allocation"
        ),

        @SerializedName("allocation.delete")
        ALLOCATION_DELETE(
            value = "allocation.delete",
            uiName = "Delete",
            uiDescription = "Allows a user to delete an allocation from the server"
        ),

        @SerializedName("startup.read")
        STARTUP_READ(
            value = "startup.read",
            uiName = "Read",
            uiDescription = "Allows a user to view the startup variables for a server"
        ),

        @SerializedName("startup.update")
        STARTUP_UPDATE(
            value = "startup.update",
            uiName = "Update",
            uiDescription = "Allows a user to modify the startup variables for the server"
        ),

        @SerializedName("startup.docker-image")
        STARTUP_DOCKER_IMAGE(
            value = "startup.docker-image",
            uiName = "Docker Image",
            uiDescription = "Allows a user to modify the Docker image used when running the server"
        ),

        @SerializedName("database.create")
        DATABASE_CREATE(
            value = "database.create",
            uiName = "Create",
            uiDescription = "Allows a user to create a new database for this server"
        ),

        @SerializedName("database.read")
        DATABASE_READ(
            value = "database.read",
            uiName = "Read",
            uiDescription = "Allows a user to view the database associated with this server"
        ),

        @SerializedName("database.update")
        DATABASE_UPDATE(
            value = "database.update",
            uiName = "Update",
            uiDescription = "Allows a user to rotate the password on a database instance. If the user does not have the view_password permission they will not see the updated password"
        ),

        @SerializedName("database.delete")
        DATABASE_DELETE(
            value = "database.delete",
            uiName = "Delete",
            uiDescription = "Allows a user to remove a database instance from this server"
        ),

        @SerializedName("database.view_password")
        DATABASE_VIEW_PASSWORD(
            value = "database.view_password",
            uiName = "View Password",
            uiDescription = "Allows a user to view the password associated with a database instance for this server"
        ),

        @SerializedName("schedule.create")
        SCHEDULE_CREATE(
            value = "schedule.create",
            uiName = "Create",
            uiDescription = "Allows a user to create new schedules for this server"
        ),

        @SerializedName("schedule.read")
        SCHEDULE_READ(
            value = "schedule.read",
            uiName = "Read",
            uiDescription = "Allows a user to view schedules and the tasks associated with them for this server"
        ),

        @SerializedName("schedule.update")
        SCHEDULE_UPDATE(
            value = "schedule.update",
            uiName = "Update",
            uiDescription = "Allows a user to update schedules and schedule tasks for this server"
        ),

        @SerializedName("schedule.delete")
        SCHEDULE_DELETE(
            value = "schedule.delete",
            uiName = "Delete",
            uiDescription = "Allows a user to delete schedules for this server"
        ),

        @SerializedName("settings.rename")
        SETTINGS_RENAME(
            value = "settings.rename",
            uiName = "Rename",
            uiDescription = "Allows a user to rename this server and change the description of it"
        ),

        @SerializedName("settings.reinstall")
        SETTINGS_REINSTALL(
            value = "settings.reinstall",
            uiName = "Reinstall",
            uiDescription = "Allows a user to trigger a reinstall of this server"
        ),

        @SerializedName("activity.read")
        ACTIVITY_READ(
            value = "activity.read",
            uiName = "Read",
            uiDescription = "Allows a user to view the activity logs for the server"
        ),

        @SerializedName("websocket.connect")
        WEBSOCKET_CONNECT(
            value = "websocket.connect",
            uiName = "",
            uiDescription = ""
        );

        override fun toString(): String = value

        companion object {
            fun fromTab(tab: ServerTab): List<Permissions>? {
                return when (tab) {
                    ServerTab.FILES -> listOf(FILE_READ)
                    ServerTab.DATABASES -> listOf(DATABASE_READ)
                    ServerTab.SCHEDULES -> listOf(SCHEDULE_READ)
                    ServerTab.USERS -> listOf(USER_READ)
                    ServerTab.BACKUPS -> listOf(BACKUP_READ)
                    ServerTab.NETWORK -> listOf(ALLOCATION_READ)
                    ServerTab.STARTUP -> listOf(STARTUP_READ)
                    ServerTab.SETTINGS -> listOf(SETTINGS_RENAME, SETTINGS_REINSTALL)
                    ServerTab.ACTIVITY -> listOf(ACTIVITY_READ)
                    else -> null
                }
            }
        }
    }
}
