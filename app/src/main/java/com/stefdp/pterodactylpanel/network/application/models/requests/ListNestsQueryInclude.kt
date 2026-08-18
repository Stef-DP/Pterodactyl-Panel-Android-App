package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListNestsQueryInclude(
    override val value: String
) : QueryInclude {
    EGGS("eggs"),
    SERVERS("servers");

    override fun toString(): String = value
}