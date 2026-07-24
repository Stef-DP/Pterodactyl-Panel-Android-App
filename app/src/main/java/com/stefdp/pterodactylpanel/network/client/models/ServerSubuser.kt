package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

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
        val permissions: List<String>,
    )

    enum class Permissions(val value: String) {
        @SerializedName("control.console")
        CONTROL_CONSOLE("control.console"),

        @SerializedName("control.start")
        CONTROL_START("control.start"),

        @SerializedName("control.stop")
        CONTROL_STOP("control.stop"),

        @SerializedName("control.restart")
        CONTROL_RESTART("control.restart"),

        @SerializedName("user.create")
        USER_CREATE("user.create"),

        @SerializedName("user.read")
        USER_READ("user.read"),

        @SerializedName("user.update")
        USER_UPDATE("user.update"),

        @SerializedName("user.delete")
        USER_DELETE("user.delete"),

        @SerializedName("file.create")
        FILE_CREATE("file.create"),

        @SerializedName("file.read")
        FILE_READ("file.read"),

        @SerializedName("file.read-content")
        FILE_READ_CONTENT("file.read-content"),

        @SerializedName("file.update")
        FILE_UPDATE("file.update"),

        @SerializedName("file.delete")
        FILE_DELETE("file.delete"),

        @SerializedName("file.archive")
        FILE_ARCHIVE("file.archive"),

        @SerializedName("file.sftp")
        FILE_SFTP("file.sftp"),

        @SerializedName("backup.create")
        BACKUP_CREATE("backup.create"),

        @SerializedName("backup.read")
        BACKUP_READ("backup.read"),

        @SerializedName("backup.delete")
        BACKUP_DELETE("backup.delete"),

        @SerializedName("backup.download")
        BACKUP_DOWNLOAD("backup.download"),

        @SerializedName("backup.restore")
        BACKUP_RESTORE("backup.restore"),

        @SerializedName("allocation.read")
        ALLOCATION_READ("allocation.read"),

        @SerializedName("allocation.create")
        ALLOCATION_CREATE("allocation.create"),

        @SerializedName("allocation.update")
        ALLOCATION_UPDATE("allocation.update"),

        @SerializedName("allocation.delete")
        ALLOCATION_DELETE("allocation.delete"),

        @SerializedName("startup.read")
        STARTUP_READ("startup.read"),

        @SerializedName("startup.update")
        STARTUP_UPDATE("startup.update"),

        @SerializedName("startup.docker-image")
        STARTUP_DOCKER_IMAGE("startup.docker-image"),

        @SerializedName("database.create")
        DATABASE_CREATE("database.create"),

        @SerializedName("database.read")
        DATABASE_READ("database.read"),

        @SerializedName("database.update")
        DATABASE_UPDATE("database.update"),

        @SerializedName("database.delete")
        DATABASE_DELETE("database.delete"),

        @SerializedName("database.view_password")
        DATABASE_VIEW_PASSWORD("database.view_password"),

        @SerializedName("schedule.create")
        SCHEDULE_CREATE("schedule.create"),

        @SerializedName("schedule.read")
        SCHEDULE_READ("schedule.read"),

        @SerializedName("schedule.update")
        SCHEDULE_UPDATE("schedule.update"),

        @SerializedName("schedule.delete")
        SCHEDULE_DELETE("schedule.delete"),

        @SerializedName("settings.rename")
        SETTINGS_RENAME("settings.rename"),

        @SerializedName("settings.reinstall")
        SETTINGS_REINSTALL("settings.reinstall"),

        @SerializedName("activity.read")
        ACTIVITY_READ("activity.read"),

        @SerializedName("websocket.connect")
        WEBSOCKET_CONNECT("websocket.connect");

        override fun toString(): String = value
    }
}
