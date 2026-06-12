package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.SshKey

data class GetClientSshKeysResponse(
    val `object`: String = "list",
    val data: List<SshKey>
)
