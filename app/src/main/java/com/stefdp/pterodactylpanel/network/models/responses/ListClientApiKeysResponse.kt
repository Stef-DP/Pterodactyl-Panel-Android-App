package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ApiKey

data class ListClientApiKeysResponse(
    val `object`: String = "list",
    val data: List<ApiKey>
)