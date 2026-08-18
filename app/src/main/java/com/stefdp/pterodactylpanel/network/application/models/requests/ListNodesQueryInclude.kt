package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListNodesQueryInclude(
    override val value: String
) : QueryInclude {
    SERVERS("servers"),
    ALLOCATIONS("allocations"),
    LOCATION("location");

    override fun toString(): String = value
}