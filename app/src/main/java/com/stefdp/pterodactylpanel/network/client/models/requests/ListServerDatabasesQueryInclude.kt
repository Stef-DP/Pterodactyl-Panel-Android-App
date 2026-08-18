package com.stefdp.pterodactylpanel.network.client.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListServerDatabasesQueryInclude(
    override val value: String
) : QueryInclude {
    PASSWORD("password");

    override fun toString(): String = value
}