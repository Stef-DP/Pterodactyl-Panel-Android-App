package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ActivityLog(
    val `object`: String = "activity_log",
    val attributes: Attributes
) {
    data class Attributes(
        val id: String,
        val batch: String? = null,
        val event: Event,
        @SerializedName("is_api") val isApi: Boolean,
        val ip: String,
        val description: String? = null,
        val properties: Map<String, Any?> = emptyMap(),
        @SerializedName("has_additional_metadata") val hasAdditionalMetadata: Boolean,
        val timestamp: String,
        val relationships: Relationships? = null
    ) {
        enum class Event(
            val value: String,
            val description: String,
            val hasPluralVariation: Boolean = false,
            val descriptionPlural: String = ""
        ) {
            @SerializedName("auth:fail")
            AUTH_FAIL(
                value = "auth:fail",
                description = "Failed authentication attempt"
            ),

            @SerializedName("auth:success")
            AUTH_SUCCESS(
                value = "auth:success",
                description = "Logged in"
            ),

            @SerializedName("auth:password-reset")
            AUTH_PASSWORD_RESET(
                value = "auth:password-reset",
                description = "Password reset"
            ),

            @SerializedName("auth:reset-password")
            AUTH_RESET_PASSWORD(
                value = "auth:reset-password",
                description = "Requested password reset"
            ),

            @SerializedName("auth:checkpoint")
            AUTH_CHECKPOINT(
                value = "auth:checkpoint",
                description = "Two-factor authentication requested"
            ),

            @SerializedName("auth:recovery-token")
            AUTH_RECOVERY_TOKEN(
                value = "auth:recovery-token",
                description = "Used two-factor recovery token"
            ),

            @SerializedName("auth:token")
            AUTH_TOKEN(
                value = "auth:token",
                description = "Solved two-factor challenge"
            ),

            @SerializedName("auth:ip-blocked")
            AUTH_IP_BLOCKED(
                value = "auth:ip-blocked",
                description = "Blocked request from unlisted IP address for {{identifier}}"
            ),

            @SerializedName("auth:sftp.fail")
            AUTH_SFTP_FAIL(
                value = "auth:sftp.fail",
                description = "Failed SFTP log in"
            ),

            @SerializedName("user:user.create")
            USER_CREATE(
                value = "user:user.create",
                description = "Created a new user {{email}}"
            ),

            @SerializedName("user:account.email-changed")
            USER_ACCOUNT_EMAIL_CHANGED(
                value = "user:account.email-changed",
                description = "Changed email from {{old}} to {{new}}"
            ),

            @SerializedName("user:account.password-changed")
            USER_ACCOUNT_PASSWORD_CHANGED(
                value = "user:account.password-changed",
                description = "Changed password"
            ),

            @SerializedName("user:api-key.create")
            USER_API_KEY_CREATE(
                value = "user:api-key.create",
                description = "Created new API key {{identifier}}"
            ),

            @SerializedName("user:api-key.delete")
            USER_API_KEY_DELETE(
                value = "user:api-key.delete",
                description = "Deleted API key {{identifier}}"
            ),

            @SerializedName("user:ssh-key.create")
            USER_SSH_KEY_CREATE(
                value = "user:ssh-key.create",
                description = "Added SSH key {{fingerprint}} to account"
            ),

            @SerializedName("user:ssh-key.delete")
            USER_SSH_KEY_DELETE(
                value = "user:ssh-key.delete",
                description = "Removed SSH key {{fingerprint}} from account"
            ),

            @SerializedName("user:two-factor.create")
            USER_TWO_FACTOR_CREATE(
                value = "user:two-factor.create",
                description = "Enabled two-factor auth"
            ),

            @SerializedName("user:two-factor.delete")
            USER_TWO_FACTOR_DELETE(
                value = "user:two-factor.delete",
                description = "Disabled two-factor auth"
            ),

            @SerializedName("server:reinstall")
            SERVER_REINSTALL(
                value = "server:reinstall",
                description = "Reinstalled server"
            ),

            @SerializedName("server:console.command")
            SERVER_CONSOLE_COMMAND(
                value = "server:console.command",
                description = "Executed \"{{command}}\" on the server"
            ),

            @SerializedName("server:power.start")
            SERVER_POWER_START(
                value = "server:power.start",
                description = "Started the server"
            ),

            @SerializedName("server:power.stop")
            SERVER_POWER_STOP(
                value = "server:power.stop",
                description = "Stopped the server"
            ),

            @SerializedName("server:power.restart")
            SERVER_POWER_RESTART(
                value = "server:power.restart",
                description = "Restarted the server"
            ),

            @SerializedName("server:power.kill")
            SERVER_POWER_KILL(
                value = "server:power.kill",
                description = "Killed the server process"
            ),

            @SerializedName("server:backup.download")
            SERVER_BACKUP_DOWNLOAD(
                value = "server:backup.download",
                description = "Downloaded the {{name}} backup"
            ),

            @SerializedName("server:backup.delete")
            SERVER_BACKUP_DELETE(
                value = "server:backup.delete",
                description = "Deleted the {{name}} backup"
            ),

            @SerializedName("server:backup.restore")
            SERVER_BACKUP_RESTORE(
                value = "server:backup.restore",
                description = "Restored the {{name}} backup (deleted files: {{truncate}})"
            ),

            @SerializedName("server:backup.restore-complete")
            SERVER_BACKUP_RESTORE_COMPLETE(
                value = "server:backup.restore-complete",
                description = "Completed restoration of the {{name}} backup"
            ),

            @SerializedName("server:backup.restore-failed")
            SERVER_BACKUP_RESTORE_FAILED(
                value = "server:backup.restore-failed",
                description = "Failed to complete restoration of the {{name}} backup"
            ),

            @SerializedName("server:backup.start")
            SERVER_BACKUP_START(
                value = "server:backup.start",
                description = "Started a new backup {{name}}"
            ),

            @SerializedName("server:backup.complete")
            SERVER_BACKUP_COMPLETE(
                value = "server:backup.complete",
                description = "Marked the {{name}} backup as complete"
            ),

            @SerializedName("server:backup.fail")
            SERVER_BACKUP_FAIL(
                value = "server:backup.fail",
                description = "Marked the {{name}} backup as failed"
            ),

            @SerializedName("server:backup.lock")
            SERVER_BACKUP_LOCK(
                value = "server:backup.lock",
                description = "Locked the {{name}} backup"
            ),

            @SerializedName("server:backup.unlock")
            SERVER_BACKUP_UNLOCK(
                value = "server:backup.unlock",
                description = "Unlocked the {{name}} backup"
            ),

            @SerializedName("server:database.create")
            SERVER_DATABASE_CREATE(
                value = "server:database.create",
                description = "Created new database {{name}}"
            ),

            @SerializedName("server:database.rotate-password")
            SERVER_DATABASE_ROTATE_PASSWORD(
                value = "server:database.rotate-password",
                description = "Password rotated for database {{name}}"
            ),

            @SerializedName("server:database.delete")
            SERVER_DATABASE_DELETE(
                value = "server:database.delete",
                description = "Deleted database {{name}}"
            ),

            @SerializedName("server:file.compress")
            SERVER_FILE_COMPRESS(
                value = "server:file.compress",
                description = "Compressed {{directory}}{{files.0}}",
                hasPluralVariation = true,
                descriptionPlural = "Compressed {{count}} files in {{directory}}"
            ),

            @SerializedName("server:file.read")
            SERVER_FILE_READ(
                value = "server:file.read",
                description = "Viewed the contents of {{file}}"
            ),

            @SerializedName("server:file.copy")
            SERVER_FILE_COPY(
                value = "server:file.copy",
                description = "Created a copy of {{file}}"
            ),

            @SerializedName("server:file.create-directory")
            SERVER_FILE_CREATE_DIRECTORY(
                value = "server:file.create-directory",
                description = "Created directory {{directory}}{{name}}"
            ),

            @SerializedName("server:file.decompress")
            SERVER_FILE_DECOMPRESS(
                value = "server:file.decompress",
                description = "Decompressed {{files}} in {{directory}}"
            ),

            @SerializedName("server:file.delete")
            SERVER_FILE_DELETE(
                value = "server:file.delete",
                description = "Deleted {{directory}}{{files.0}}",
                hasPluralVariation = true,
                descriptionPlural = "Deleted {{count}} files in {{directory}}"
            ),

            @SerializedName("server:file.download")
            SERVER_FILE_DOWNLOAD(
                value = "server:file.download",
                description = "Downloaded {{file}}"
            ),

            @SerializedName("server:file.pull")
            SERVER_FILE_PULL(
                value = "server:file.pull",
                description = "Downloaded a remote file from {{url}} to {{directory}}"
            ),

            @SerializedName("server:file.rename")
            SERVER_FILE_RENAME(
                value = "server:file.rename",
                description = "Renamed {{directory}}{{files.0.from}} to {{directory}}{{files.0.to}}",
                hasPluralVariation = true,
                descriptionPlural = "Renamed {{count}} files in {{directory}}"
            ),

            @SerializedName("server:file.write")
            SERVER_FILE_WRITE(
                value = "server:file.write",
                description = "Wrote new content to {{file}}"
            ),

            @SerializedName("server:file.upload")
            SERVER_FILE_UPLOAD(
                value = "server:file.upload",
                description = "Began a file upload"
            ),

            @SerializedName("server:file.uploaded")
            SERVER_FILE_UPLOADED(
                value = "server:file.uploaded",
                description = "Uploaded {{directory}}{{file}}"
            ),

            @SerializedName("server:sftp.denied")
            SERVER_SFTP_DENIED(
                value = "server:sftp.denied",
                description = "Blocked SFTP access due to permissions"
            ),

            @SerializedName("server:sftp.create")
            SERVER_SFTP_CREATE(
                value = "server:sftp.create",
                description = "Created {{files.0}}",
                hasPluralVariation = true,
                descriptionPlural = "Created {{count}} new files"
            ),

            @SerializedName("server:sftp.write")
            SERVER_SFTP_WRITE(
                value = "server:sftp.write",
                description = "Modified the contents of {{files.0}}",
                hasPluralVariation = true,
                descriptionPlural = "Modified the contents of {{count}} files"
            ),

            @SerializedName("server:sftp.delete")
            SERVER_SFTP_DELETE(
                value = "server:sftp.delete",
                description = "Deleted {{files.0}}",
                hasPluralVariation = true,
                descriptionPlural = "Deleted {{count}} files"
            ),

            @SerializedName("server:sftp.create-directory")
            SERVER_SFTP_CREATE_DIRECTORY(
                value = "server:sftp.create-directory",
                description = "Created the {{files.0}} directory",
                hasPluralVariation = true,
                descriptionPlural = "Created {{count}} directories"
            ),

            @SerializedName("server:sftp.rename")
            SERVER_SFTP_RENAME(
                value = "server:sftp.rename",
                description = "Renamed {{files.0.from}} to {{files.0.to}}",
                hasPluralVariation = true,
                descriptionPlural = "Renamed {{count}} files in {{directory}}"
            ),

            @SerializedName("server:allocation.create")
            SERVER_ALLOCATION_CREATE(
                value = "server:allocation.create",
                description = "Added {{allocation}} to the server"
            ),

            @SerializedName("server:allocation.notes")
            SERVER_ALLOCATION_NOTES(
                value = "server:allocation.notes",
                description = "Updated the notes for {{allocation}} from \"{{old}}\" to \"{{new}}\""
            ),

            @SerializedName("server:allocation.primary")
            SERVER_ALLOCATION_PRIMARY(
                value = "server:allocation.primary",
                description = "Set {{allocation}} as the primary server allocation"
            ),

            @SerializedName("server:allocation.delete")
            SERVER_ALLOCATION_DELETE(
                value = "server:allocation.delete",
                description = "Deleted the {{allocation}} allocation"
            ),

            @SerializedName("server:schedule.create")
            SERVER_SCHEDULE_CREATE(
                value = "server:schedule.create",
                description = "Created the {{name}} schedule"
            ),

            @SerializedName("server:schedule.update")
            SERVER_SCHEDULE_UPDATE(
                value = "server:schedule.update",
                description = "Updated the {{name}} schedule"
            ),

            @SerializedName("server:schedule.execute")
            SERVER_SCHEDULE_EXECUTE(
                value = "server:schedule.execute",
                description = "Manually executed the {{name}} schedule"
            ),

            @SerializedName("server:schedule.delete")
            SERVER_SCHEDULE_DELETE(
                value = "server:schedule.delete",
                description = "Deleted the {{name}} schedule"
            ),

            @SerializedName("server:task.create")
            SERVER_TASK_CREATE(
                value = "server:task.create",
                description = "Created a new \"{{action}}\" task for the {{name}} schedule"
            ),

            @SerializedName("server:task.update")
            SERVER_TASK_UPDATE(
                value = "server:task.update",
                description = "Updated the \"{{action}}\" task for the {{name}} schedule"
            ),

            @SerializedName("server:task.delete")
            SERVER_TASK_DELETE(
                value = "server:task.delete",
                description = "Deleted a task for the {{name}} schedule"
            ),

            @SerializedName("server:settings.rename")
            SERVER_SETTINGS_RENAME(
                value = "server:settings.rename",
                description = "Renamed the server from {{old}} to {{new}}"
            ),

            @SerializedName("server:settings.description")
            SERVER_SETTINGS_DESCRIPTION(
                value = "server:settings.description",
                description = "Changed the server description from {{old}} to {{new}}"
            ),

            @SerializedName("server:startup.edit")
            SERVER_STARTUP_EDIT(
                value = "server:startup.edit",
                description = "Changed the {{variable}} variable from \"{{old}}\" to \"{{new}}\""
            ),

            @SerializedName("server:startup.image")
            SERVER_STARTUP_IMAGE(
                value = "server:startup.image",
                description = "Updated the Docker Image for the server from {{old}} to {{new}}"
            ),

            @SerializedName("server:subuser.create")
            SERVER_SUBUSER_CREATE(
                value = "server:subuser.create",
                description = "Added {{email}} as a subuser"
            ),

            @SerializedName("server:subuser.update")
            SERVER_SUBUSER_UPDATE(
                value = "server:subuser.update",
                description = "Updated the subuser permissions for {{email}}"
            ),

            @SerializedName("server:subuser.delete")
            SERVER_SUBUSER_DELETE(
                value = "server:subuser.delete",
                description = "Removed {{email}} as a subuser"
            );

            override fun toString(): String = value
        }

        data class Relationships(
            val actor: Actor
        ) {
            data class Actor(
                val `object`: String = "user",
                val attributes: Attributes? = null
            ) {
                data class Attributes(
                    val uuid: String,
                    val identifier: String,
                    val username: String,
                    val email: String,
                    val image: String,
                    @SerializedName("2fa_enabled") val twoFactorAuthenticationEnabled: Boolean,
                    @SerializedName("created_at") val createdAt: String,
                )
            }
        }
    }
}
