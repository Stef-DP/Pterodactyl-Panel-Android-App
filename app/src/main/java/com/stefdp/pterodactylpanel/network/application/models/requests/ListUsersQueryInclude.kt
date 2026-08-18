package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListUsersQueryInclude(
    override val value: String
) : QueryInclude {
    SERVERS("servers");

    override fun toString(): String = value
}