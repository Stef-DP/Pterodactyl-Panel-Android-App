package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.ServerAllocation

data class ListServerAllocationsResponse(
    val `object`: String = "list",
    val data: List<ServerAllocation>
)
