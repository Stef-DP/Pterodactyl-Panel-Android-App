package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

data class CreateClientServerBackupBody(
    val name: String? = null,
    val ignored: String? = null, // new line-separated list of file patterns to exclude
    @SerializedName("is_locked") val isLocked: Boolean? = null,
)
