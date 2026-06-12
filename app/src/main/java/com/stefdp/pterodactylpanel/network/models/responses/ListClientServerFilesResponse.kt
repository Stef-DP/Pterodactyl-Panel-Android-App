package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.pterodactylpanel.network.models.ServerFile

data class ListClientServerFilesResponse(
    val `object`: String = "list",
    val data: List<ServerFile>
)