package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

data class ServerFile(
    val `object`: String = "file_object",
    val attributes: ServerFileAttributes
)

data class ServerFileAttributes(
    val name: String,
    val mode: String, // linux permissions like "-rw-r--r--"
    val size: Long,
    @SerializedName("is_file") val isFile: Boolean,
    @SerializedName("is_symlink") val isSymlink: Boolean,
    @SerializedName("is_editable") val isEditable: Boolean,
    val mimetype: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("modified_at") val modifiedAt: String
)
