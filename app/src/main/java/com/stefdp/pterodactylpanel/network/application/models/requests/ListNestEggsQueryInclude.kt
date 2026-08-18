package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListNestEggsQueryInclude(
    override val value: String
) : QueryInclude {
    NEST("nest"),
    SERVERS("servers"),
    CONFIG("config"),
    SCRIPT("script"),
    VARIABLES("variables");

    override fun toString(): String = value
}