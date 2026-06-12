package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerFile

data class ListServerFilesResponse(
    val `object`: String = "list",
    val data: List<ServerFile>
)