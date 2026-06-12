package com.stefdp.pterodactylpanel.network.models.responses

import com.stefdp.pterodactylpanel.network.models.ServerAllocation

data class ListClientServerAllocationsResponse(
    val `object`: String = "list",
    val data: List<ServerAllocation>
)
