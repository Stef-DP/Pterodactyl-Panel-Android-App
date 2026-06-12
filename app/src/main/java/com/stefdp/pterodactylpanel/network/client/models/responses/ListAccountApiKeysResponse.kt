package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ApiKey

data class ListAccountApiKeysResponse(
    val `object`: String = "list",
    val data: List<ApiKey>
)